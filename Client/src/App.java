import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.awt.geom.*;
import java.util.ArrayList;


public class App extends JFrame implements ActionListener{
    JLabel l1, l2, l3;
    JTextField tf1;
    JPasswordField pf2;
    JButton b1, b2;
    Socket skt;
    private JPanel contentPane;
    App() throws IOException{
        setTitle("Login Form");
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();


        // Get size of each screen
        int screenWidth = 0;
        int screenHeight = 0;

        // Get size of each screen
        for (int i=0; i<gs.length; i++) {
            DisplayMode dm = gs[i].getDisplayMode();
            screenWidth = dm.getWidth();
            screenHeight += Math.max(screenHeight, dm.getHeight());
        }


        setLayout(null);

        ImageIcon imageIcon = new ImageIcon("");
              // Create a layered pane to hold the components
        JLayeredPane layeredPane = new JLayeredPane();
        setContentPane(layeredPane);

        // Add the image to the background
        JLabel backgroundLabel = new JLabel(imageIcon);
        backgroundLabel.setBounds(0, 0, screenWidth, screenHeight);
        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);


        // Add the components to the panel
        Font font = new Font("Arial", Font.ITALIC, 30); // create a new font object
        
        JLabel onlineExam = new JLabel("Online Exam");
        onlineExam.setFont(new Font("Arial",Font.HANGING_BASELINE, 150));
        onlineExam.setForeground(Color.BLUE);
        onlineExam.setBounds(540,140,1000, 150);
        layeredPane.add(onlineExam,JLayeredPane.PALETTE_LAYER);
        font = new Font("Arial", Font.BOLD, 25);
        l2 = new JLabel("Username:");
        l2.setForeground(Color.BLACK);
        l2.setFont(font);
        l2.setBounds(800,400, 200, 50);
        layeredPane.add(l2, JLayeredPane.PALETTE_LAYER);

        tf1 = new JTextField(20);
        tf1.setForeground(Color.BLUE);
        tf1.setBounds(950, 400, 200, 50);
        tf1.setFont(font);
        // Set shape to oval
        tf1.setBorder(new RoundBorder(10));
        layeredPane.add(tf1, JLayeredPane.PALETTE_LAYER);

        l3 = new JLabel("Password:");
        l3.setForeground(Color.BLACK);
        l3.setFont(new Font("Arial", Font.BOLD, 25));
        l3.setBounds(800, 500, 200, 50);
        layeredPane.add(l3, JLayeredPane.PALETTE_LAYER);

        pf2 = new JPasswordField(20);
        pf2.setBounds(950, 500, 200, 50);
        pf2.setForeground(Color.BLUE);
        pf2.setFont(font);
        // Set shape to oval
        pf2.setBorder(new RoundBorder(10));
        layeredPane.add(pf2, JLayeredPane.PALETTE_LAYER);

        b1 = new JButton("Login");
        b1.setBounds(890, 600, 80, 40);
        font = new Font("Arial", Font.BOLD, 17);
        b1.setFont(font);
        b1.setBorder(new RoundBorder(10));
        layeredPane.add(b1, JLayeredPane.PALETTE_LAYER);

        b2 = new JButton("Cancel");
        b2.setBounds(990, 600, 80, 40);
        b2.setFont(font);
        b2.setBorder(new RoundBorder(10));
        layeredPane.add(b2, JLayeredPane.PALETTE_LAYER);


        // Add action Listener
        b1.addActionListener(this);
        b2.addActionListener(this);
        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(screenWidth, screenHeight);
        setVisible(true);
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
        QuestionClient qc = new QuestionClient(ques,skt);
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

