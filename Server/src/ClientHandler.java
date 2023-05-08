// Handles a client
class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ArrayList<Question> questionList;
    private ArrayList<User> userList;
    Statement stmt;
    Connection conn;

    public ClientHandler(Socket clientSocket, ArrayList<Question> questionList, ArrayList<User> userList) {
        this.clientSocket = clientSocket;
        this.questionList = questionList;
        this.userList = userList;
    }

    @Override
    public void run(){
        String[] str;
        try {
            while (true) {
                // Send questions to client
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                
                str = in.readLine().split(",");  // Error
                int flag = 0;
                System.out.println(str[0] + ": " + str[1]);
                
                // Checks whether username and password send by user is in database
                for (int i = 0; i < userList.size(); i++) {
                    // System.out.print("User" + i + " " + (userList.get(i).getUser()));
                    // System.out.println("  "+ (userList.get(i).getPass()));
                    if (str[0].equals((userList.get(i)).getUser()) && str[1].equals((userList.get(i).getPass()))) {
                        flag = 1;
                        break;
                    }
                }
                
                // If right password
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                if (flag == 1) {
                    out.println("Correct");
                    ObjectOutputStream out2 = new ObjectOutputStream(clientSocket.getOutputStream());
                    out2.writeObject(questionList);
                    out2.flush();
                    break;
                } 
                else {
                    out.println("Incorrect");
                    clientSocket.close();
                    return;
                }
            }
            // Get score from user
            InputStream inputStream = clientSocket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            

            // Create output stream to send data to client
            DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            int count = 0;
            long startTime = System.currentTimeMillis();
            while (clientSocket.isConnected()) {
                // Wait for input from client for up to 11 min
                while(dataInputStream.available() == 0 && (System.currentTimeMillis() - startTime < 660000)) {
                    Thread.sleep(1000);  // Sleep for 1 second
                }
                conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/onlineexam", "user", "pass");
                stmt = conn.createStatement();
                // // Read input from client and send rank to client
                if (dataInputStream.available() > 0) {
                    int score = dataInputStream.readInt();
                    System.out.println(score);
                    if (count == 0){
                        String sql3 = "Insert INTO UserRank(UserName, Marks) Values('" + str[0] + "', '" + score + "')";
                        stmt.executeUpdate(sql3);
                        count++;
                    }
                        
                    // query to fetch rank from table when client asks for rank
                    String sql4 = "Select UserName, (SELECT COUNT(*) FROM UserRank s2 WHERE s2.Marks > s1.Marks) + 1 AS 'rank' FROM UserRank s1 ORDER BY Marks DESC";
                    ResultSet rs = stmt.executeQuery(sql4);
                    System.out.println("JE;;");
                    while(rs.next()) {
                        String s2 = rs.getString("UserName");
                        int rank  = rs.getInt("rank"); 
                        if (s2.equals(str[0]))
                        {
                            dataOutputStream.writeInt(rank);
                            dataOutputStream.flush();
                            break;
                        }
                    }
                }
                
            }
            dataInputStream.close();
            dataOutputStream.close();
            System.out.println("Client disconnected: " + clientSocket.getInetAddress().getHostAddress());
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
