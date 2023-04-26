import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
public class App extends JFrame implements ActionListener{
    JLabel l1, l2, l3;
    JTextField tf1;
    JPasswordField pf2;
    JButton b1, b2;
    Socket skt;
    App() {
        setTitle("Login Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 300);
        setResizable(false);
       
        // Create a panel to hold the components
        JPanel panel = new JPanel();
        panel.setBackground(Color.lightGray);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add the components to the panel
        l1 = new JLabel("Login Form");
        l1.setForeground(Color.blue);
        l1.setFont(new Font("Serif", Font.ROMAN_BASELINE, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(l1, gbc);

        l2 = new JLabel("Username:");
        l2.setFont(new Font("Arial", Font.BOLD, 25));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(l2, gbc);

        tf1 = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(tf1, gbc);

        l3 = new JLabel("Password:");
        l3.setFont(new Font("Arial", Font.BOLD, 25));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(l3, gbc);

        pf2 = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(pf2, gbc);

        b1 = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        panel.add(b1, gbc);

        b2 = new JButton("Cancel");
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        panel.add(b2, gbc);

        // Add the panel to the frame
        getContentPane().add(panel);

        setVisible(true);

        b1.addActionListener(this);
        b2.addActionListener(this);
        connectToServer();
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand() == "Cancel") {
            dispose();
            System.exit(0);
        }

        if (validatePass(tf1.getText(),pf2.getPassword())){
            try {
               
                DataExchange();
               
            } catch (Exception e) {
                System.out.println(e);
            }
          
        } else {
            JOptionPane.showMessageDialog(this, "Incorrect login or password", "Error", JOptionPane.ERROR_MESSAGE);
            tf1.setText("");
            pf2.setText("");
        }
    }

    private boolean validatePass(String username,char[] pass){

       try {
            if(username.equals("") || pass.length == 0)
                return false;
            
            String password = new String(pass);
            String details = username+","+password;
            PrintWriter output = new PrintWriter(skt.getOutputStream(),true);
            output.println(details);
            BufferedReader input = new BufferedReader(new InputStreamReader(skt.getInputStream()));
        
            String check = input.readLine();
            System.out.println(check);
            if(check.equals("Correct"))
                return true;
            else
                return false;
       } catch (Exception e) {
        System.out.println(e);
       }
       return false;
    }
private void DataExchange(){

    try {
        ObjectInputStream input = new ObjectInputStream(skt.getInputStream());
        ArrayList<Question> ques = (ArrayList<Question>)input.readObject();
        QuestionClient qc = new QuestionClient(ques);
        qc.setVisible(true);
        dispose();
    } catch (Exception e) {
        System.out.println(e);
    }
}
private JDialog waitingForServer(){
    JDialog waitingDialog = new JDialog(this, "Waiting for server to connect...", false);
    waitingDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    waitingDialog.setResizable(false);
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    JLabel label = new JLabel("Waiting for server to connect...");
    label.setHorizontalAlignment(SwingConstants.CENTER);
    panel.add(label, BorderLayout.NORTH);
    JProgressBar progressBar = new JProgressBar();
    progressBar.setIndeterminate(true);
    panel.add(progressBar, BorderLayout.CENTER);
    waitingDialog.getContentPane().add(panel);
    waitingDialog.pack();
    waitingDialog.setLocationRelativeTo(null);

    return waitingDialog;
}
    
void connectToServer(){
        
          
        JDialog waitingDialog = waitingForServer();  
        System.out.println("Waiting for server");
        try {

            waitingDialog.setVisible(true);
            skt = new Socket("192.168.128.230",4444);
            waitingDialog.dispose();
            JOptionPane.showMessageDialog(this,"Connection Established..", "Done",JOptionPane.INFORMATION_MESSAGE);
            System.out.println("Connected to Server");
            
        }
        catch(ConnectException e){

            waitingDialog.dispose();
            JOptionPane.showMessageDialog(this, "Connection timeout....Server didn't respond.", "Error", JOptionPane.ERROR_MESSAGE);

        }
        catch(Exception e){
            System.out.println(e);
        }
      
    }
    public static void main(String[] args) throws IOException{
        new App();
    }
}
