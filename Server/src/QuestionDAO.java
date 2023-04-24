import java.sql.*;

public class QuestionDAO {

    // Database credentials
    final String username = "yourusername";
    final String password = "passwd"; 

    public void addQuestion(Question question) {
        Connection conn = null;
        Statement stmt = null;
        int count = question.getCount();
        String ques = question.getQuestion();
        String [] options = question.getOptions();
        String choice1 = options[0];
        String choice2 = options[1];
        String choice3 = options[2];
        String choice4 = options[3];
        try {
            // Register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Hello world");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/onlineexam", username, password);
            stmt = conn.createStatement();
            String sql;
            sql = "INSERT INTO question_form (QuestionID, Question, Choice1, Choice2, Choice3, Choice4) " +
                    "VALUES ('" + count + "', '" + ques + "', '"+ choice1 + "', '"+ choice2 + "', '"+ choice3 + "', '"+ choice4 + "')";
            stmt.executeUpdate(sql);

            // Clean-up environment
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            // Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            // Finally block used to close resources
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            } // nothing we can do
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            } // end finally try
        } // end try
    }
}
