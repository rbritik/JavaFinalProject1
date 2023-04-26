import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
// import java.util.*;

public class QuestionForm extends JFrame implements ActionListener {
    JLabel l1, l2, l3, l4, l5, l6, l7, l8;
    JTextField tf1, tf2, tf3, tf4, tf5, tf6, tf7, tf8;
    JButton b1, b2, b3, b4;
    JRadioButton r1, r2, r3, r4;
    int count = 0;

    QuestionForm() {
        setVisible(true);
        setSize(800, 800);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Question Form");

        l1 = new JLabel("Question Form");
        l1.setForeground(Color.blue);
        l1.setFont(new Font("Serif", Font.BOLD, 20));

        l2 = new JLabel("Question:");
        l3 = new JLabel("Option 1:");
        l4 = new JLabel("Option 2:");
        l5 = new JLabel("Option 3:");
        l6 = new JLabel("Option 4:");
        tf1 = new JTextField();
        tf2 = new JTextField();
        tf3 = new JTextField();
        tf4 = new JTextField();
        tf5 = new JTextField();
        tf6 = new JTextField();
        tf7 = new JTextField();
        tf8 = new JTextField();
        r1 = new JRadioButton("Option 1");
        r2 = new JRadioButton("Option 2");
        r3 = new JRadioButton("Option 3");
        r4 = new JRadioButton("Option 4");
        ButtonGroup bg = new ButtonGroup();
        bg.add(r1);
        bg.add(r2);
        bg.add(r3);
        bg.add(r4);
        b1 = new JButton("Create Question");
        b2 = new JButton("Edit Question");
        b3 = new JButton("Next Question");
        b4 = new JButton("Finish Exam");

        l1.setBounds(100, 30, 400, 30);
        l2.setBounds(80, 70, 200, 30);
        l3.setBounds(80, 110, 200, 30);
        l4.setBounds(80, 150, 200, 30);
        l5.setBounds(80, 190, 200, 30);
        l6.setBounds(80, 230, 200, 30);
        tf1.setBounds(300, 70, 400, 30);
        tf2.setBounds(300, 110, 400, 30);
        tf3.setBounds(300, 150, 400, 30);
        tf4.setBounds(300, 190, 400, 30);
        tf5.setBounds(300, 230, 400, 30);
        r1.setBounds(300, 270, 200, 30);
        r2.setBounds(300, 310, 200, 30);
        r3.setBounds(300, 350, 200, 30);
        r4.setBounds(300, 390, 200, 30);
        b1.setBounds(100, 470, 200, 30);
        b2.setBounds(350, 470, 200, 30);
        b3.setBounds(100, 550, 200, 30);
        b4.setBounds(350, 550, 200, 30);

        add(l1);
        add(l2);
        add(l3);
        add(l4);
        add(l5);
        add(l6);
        add(tf1);
        add(tf2);
        add(tf3);
        add(tf4);
        add(tf5);
        add(b1);
        add(b2);
        add(b3);
        add(b4);
        add(r1);
        add(r2);
        add(r3);
        add(r4);

        b1.addActionListener(this);
        b2.addActionListener(this);
        b3.addActionListener(this);
        b4.addActionListener(this);
    }
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == b1) {
            String question = tf1.getText().trim();
            String option1 = tf2.getText().trim();
            String option2 = tf3.getText().trim();
            String option3 = tf4.getText().trim();
            String option4 = tf5.getText().trim();
            boolean answer1 = r1.isSelected();
            boolean answer2 = r2.isSelected();
            boolean answer3 = r3.isSelected();
            boolean answer4 = r4.isSelected();
    
            if (!question.equals("") && !option1.equals("") && !option2.equals("") && !option3.equals("") && !option4.equals("") && (answer1 || answer2 || answer3 || answer4)) {
                String[] options = {option1, option2, option3, option4};
                count++;
                Question q = new Question(count, question, options);
                QuestionDAO c = new QuestionDAO();
                c.addQuestion(q);
    
                JOptionPane.showMessageDialog(this, "Question added successfully");
                tf1.setText("");
                tf2.setText("");
                tf3.setText("");
                tf4.setText("");
                tf5.setText("");
                // bg.clearSelection();
            } else {
                JOptionPane.showMessageDialog(this, "Please fill all the fields and select the correct answer");
            }
        } else if (e.getSource() == b2) {
            // code to edit a question
        } else if (e.getSource() == b3) {
            // code to move to the next question
        } else if (e.getSource() == b4) {
            // code to finish the exam
        }
    }
    
}
