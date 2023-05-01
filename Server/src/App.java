import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

public class App {
    private static final int PORT = 4444;
    private static ArrayList<Question> questionList = new ArrayList<>();
    private static ArrayList<User> userList = new ArrayList<>();

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        // Establish database connection
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/onlineexam", "user", "pass");
        Connection conn2 = DriverManager.getConnection("jdbc:mysql://localhost:3306/onlineexam", "user", "pass");
        // Fetch questions from database and store in array of objects
        String sql1 = "SELECT * FROM Question";
        String sql2 = "SELECT * FROM password_table";

        Statement stmt = conn.createStatement();
        Statement stmt2 = conn2.createStatement();

        ResultSet rs = stmt.executeQuery(sql1);
        ResultSet rs2 = stmt2.executeQuery(sql2);

        while (rs.next()) {
            int id = rs.getInt("QuestionID");
            String question = rs.getString("Question");
            String[] options = rs.getString("Options").split(",");
            String answer = rs.getString("Answer");
            Question q = new Question(id, question, options, answer);
            questionList.add(q);
        }

        while (rs2.next()) {
            String user = rs2.getString("UserName");
            String pass = rs2.getString("Password");
            User u = new User(user, pass);
            userList.add(u);
        }
        
        // Start server and listen for client connections
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started and waiting for connections...");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
            
            // Create new thread for each client connection
            ClientHandler clientHandler = new ClientHandler(clientSocket, questionList, userList);
            Thread clientThread = new Thread(clientHandler);
            clientThread.start();
        }
    }
}


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


