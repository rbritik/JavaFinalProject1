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
