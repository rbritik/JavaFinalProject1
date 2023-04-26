import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

public class App {
    private static final int PORT = 4444;
    private static ArrayList<Question> questionList = new ArrayList<>();

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        // Establish database connection
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/onlineexam", "ritik", "Tz2@5c9tyo");
        
        // Fetch questions from database and store in array of objects
        String sql = "SELECT * FROM Question";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            int id = rs.getInt("QuestionID");
            String question = rs.getString("Question");
            String[] options = rs.getString("Options").split(",");
            String answer = rs.getString("Answer");
            Question q = new Question(id, question, options, answer);
            questionList.add(q);
        }
        
        // Start server and listen for client connections
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started and waiting for connections...");
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
            
            // Create new thread for each client connection
            ClientHandler clientHandler = new ClientHandler(clientSocket, questionList);
            Thread clientThread = new Thread(clientHandler);
            clientThread.start();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ArrayList<Question> questionList;

    public ClientHandler(Socket clientSocket, ArrayList<Question> questionList) {
        this.clientSocket = clientSocket;
        this.questionList = questionList;
    }

    @Override
    public void run() {
        try {
            // Send questions to client
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.writeObject(questionList);
            out.flush();
            
            // Close connection
            clientSocket.close();
            System.out.println("Client disconnected: " + clientSocket.getInetAddress().getHostAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Question implements Serializable{
    private int id;
    private String question;
    private String[] options;
    private String answer;

    public Question(int id, String question, String[] options, String answer) {
        this.id = id;
        this.question = question;
        this.options = options;
        this.answer = answer;
    }

    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getOptions() {
        return options;
    }

    public String getAnswer() {
        return answer;
    }
}
