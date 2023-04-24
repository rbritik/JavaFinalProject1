import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class App extends JFrame implements ActionListener{
    JLabel l1, l2, l3;
    JTextField tf1;
    JPasswordField pf2;
    JButton b1, b2;

    App() {
        setVisible(true);
        setSize(800, 800);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Login Form");

        l1 = new JLabel("Login Form");
        l1.setForeground(Color.blue);
        l1.setFont(new Font("Serif", Font.BOLD, 20));

        l2 = new JLabel("Username:");
        l3 = new JLabel("Password:");
        tf1 = new JTextField();
        pf2 = new JPasswordField();
        b1 = new JButton("Login");
        b2 = new JButton("Cancel");

        l1.setBounds(100, 30, 400, 30);
        l2.setBounds(80, 70, 200, 30);
        l3.setBounds(80, 110, 200, 30);
        tf1.setBounds(300, 70, 200, 30);
        pf2.setBounds(300, 110, 200, 30);
        b1.setBounds(150, 160, 100, 30);
        b2.setBounds(270, 160, 100, 30);

        add(l1);
        add(l2);
        add(tf1);
        add(l3);
        add(pf2);
        add(b1);
        add(b2);

        b1.addActionListener(this);
        b2.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand() == "Cancel") {
            dispose();
            System.exit(0);
        }
        String uname = tf1.getText();
        String pass = new String(pf2.getPassword());

        if (uname.equals("admin") && pass.equals("admin123")) {
            QuestionForm qf = new QuestionForm();
            qf.setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Incorrect login or password", "Error", JOptionPane.ERROR_MESSAGE);
            tf1.setText("");
            pf2.setText("");
        }
    }

    public static void main(String[] args) {
        new App();
    }
}
