import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.awt.*;
import javax.swing.Timer;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.metal.MetalButtonUI;
import javax.swing.border.*;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import java.util.*;
import java.lang.Math;
import java.net.Socket;
import java.text.DecimalFormat;

public class QuestionClient extends JFrame implements ActionListener{

    private static final long serialVersionUID = 1L; 
    private JLabel[] questionLabels;
    private JRadioButton[][] optionButtons;
    private ButtonGroup bg[];
    private JButton submitButton;
    private int numQuestions;
    private String[] correctAnswers;
    private int score = 0;
    private Timer timer;
    private static final int EXAM_TIME = 600;
    private JLabel timerLabel;
    private int remaningTime = EXAM_TIME;
    private Container cpane;
    private CardLayout card;
    private int cardNum = 1;
    private Socket skt;
    private int rank;
    private int attemptCount = 0;

    public QuestionClient(ArrayList<Question> questions,Socket skt) {
        // Set up the GUI
        super("Online Exam");
        this.skt = skt;
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
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
        getContentPane().setSize(screenSize.width, screenSize.height);
        ImageIcon img = new ImageIcon("onlineExamBackgroundImage.jpg");
        JLabel background = new JLabel(img);
        background.setBounds(0,0,2000,800);
        add(background,BorderLayout.CENTER);

        JPanel timerPanel = new JPanel();
        timerPanel.setOpaque(false);
        timerLabel = new JLabel("");
        timerPanel.add(timerLabel);
        getContentPane().add(timerPanel,BorderLayout.WEST);
        timerForExam();

        cpane = getContentPane();
        card = new CardLayout();
        Font questionFont = new Font("Arial",Font.BOLD,40);
        Font optionFont = new Font("Arial",Font.BOLD,40);
        JPanel mainPanel = new  JPanel();
        mainPanel.setLayout(card);

        mainPanel.setOpaque(false);
        numQuestions = questions.size();
        correctAnswers = new String[numQuestions];
        
        JPanel[] questionPanel = new JPanel[numQuestions];
        for(int i=0; i<numQuestions; i++)
            questionPanel[i] = new JPanel(new GridLayout(5,1));

        questionLabels = new JLabel[numQuestions];
        optionButtons = new JRadioButton[numQuestions][4];
        bg = new ButtonGroup[numQuestions];
        
        // Add each question to the panel
        Dimension size = new Dimension(300, 70); // width, height
       
        Icon unselectedIcon = new ImageIcon("unselectedRadio.png");
        Icon selectedIcon = new ImageIcon("selectedRadio.png");
        for (int i = 0; i < numQuestions; i++) {
            questionLabels[i] = new JLabel((i + 1) + ". " + questions.get(i).getQuestion());
            questionLabels[i].setFont(questionFont);
            questionLabels[i].setPreferredSize(size);
            questionPanel[i].add(questionLabels[i]);
            bg[i] = new ButtonGroup();
            for (int j = 0; j < 4; j++) {
                optionButtons[i][j] = new JRadioButton(questions.get(i).getOptions()[j]);
                optionButtons[i][j].setIcon(unselectedIcon);
                optionButtons[i][j].setSelectedIcon(selectedIcon);
                optionButtons[i][j].setFont(optionFont);
                optionButtons[i][j].setOpaque(false);
                bg[i].add(optionButtons[i][j]);
                questionPanel[i].add(optionButtons[i][j]);
                questionPanel[i].setOpaque(false);
            }
            correctAnswers[i] = questions.get(i).getAnswer();

        }
      
        JPanel buttonPanel = new JPanel();
        submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Arial",Font.BOLD,20));
        submitButton.setBorder(new RoundBorder(30));
        submitButton.setSize(100,50);
        submitButton.addActionListener(this);
        buttonPanel.add(submitButton);
        buttonPanel.setOpaque(false);
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() instanceof JButton) {
                    String text = ((JButton) e.getSource()).getText();
                    cardNum = Integer.parseInt(text);
                    card.show(mainPanel, text);
                }
            }
        };

      
        JPanel buttonPanel2 = new JPanel(new GridLayout((int)Math.ceil((float)numQuestions/3), 3));
        buttonPanel2.setPreferredSize(new Dimension(300, 500));
        buttonPanel2.setOpaque(false);
        JButton[] arrayButton = new JButton[numQuestions];
        for(int i=0; i<numQuestions; i++){
            String cardNumber = Integer.toString(i + 1);
            mainPanel.add(cardNumber,questionPanel[i]);
            arrayButton[i] = new JButton(String.valueOf(i + 1));
            arrayButton[i].setBorder(new RoundBorder(20));
            arrayButton[i].setSize(50, 30);
            arrayButton[i].addActionListener(listener);
            arrayButton[i].setFont(new Font("Arial",Font.BOLD,40));
            buttonPanel2.add(arrayButton[i]);
        }

        Font buttonFont = new Font("Arial",Font.PLAIN,20);
        Dimension buttonSize = new Dimension(80, 50);
        JPanel navigationButtonPanel = new JPanel();
        JButton first = new JButton("First");
        first.setPreferredSize(buttonSize);
        first.setFont(buttonFont);
        first.setBorder(new RoundBorder(10));

        JButton last = new JButton("Last");
        last.setPreferredSize(buttonSize);
        last.setBorder(new RoundBorder(10));
        last.setFont(buttonFont);
        JButton next = new JButton("Next");
        next.setPreferredSize(buttonSize);
        next.setBorder(new RoundBorder(10));
        next.setFont(buttonFont);
        JButton prev = new JButton("Prev");
        prev.setPreferredSize(buttonSize);
        prev.setBorder(new RoundBorder(10));
        prev.setFont(buttonFont);

        navigationButtonPanel.add(first);
        navigationButtonPanel.add(prev);
        navigationButtonPanel.add(next);
        navigationButtonPanel.add(last);
        navigationButtonPanel.setOpaque(false);
        first.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                card.first(mainPanel);
                cardNum = 1;

            }
        });

        next.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                if(cardNum < numQuestions)
                    cardNum++;
                
                card.show(mainPanel,""+cardNum);

            }
        });

        prev.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                if(cardNum > 1)
                    cardNum--;
                
                card.show(mainPanel, ""+cardNum);

            }
        });

        last.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e){

                card.last(mainPanel);
                cardNum = numQuestions;

            }
        });
        getContentPane().add(navigationButtonPanel,BorderLayout.NORTH);
        getContentPane().add(mainPanel,BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        getContentPane().add(buttonPanel2, BorderLayout.EAST);
        setVisible(true);
    }

    public void timerForExam(){

        int delay = 1000;
        timer = new Timer(delay, this);
        timer.start();

    }
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            showResult();
        }
        else if(e.getSource() == timer){

            updateTimerLabel();
            if(remaningTime == 0){
                updateTimerLabel();
                showResult();
            }
        }
    }

public void updateTimerLabel(){

    remaningTime--;
    if(remaningTime>=0){
        DecimalFormat formatter = new DecimalFormat("00");
        int minutes = remaningTime/60;
        int seconds = remaningTime%60;
        String timeString = formatter.format(minutes) + " : "+formatter.format(seconds);
        timerLabel.setForeground(Color.red);
        timerLabel.setFont(new Font("Arial",Font.BOLD,30));
        
        timerLabel.setText(timeString);
    }
    else{

        timer.stop();

    }    

}    
public void countScoreAndAttempt(){

    for (int i = 0; i < numQuestions; i++) {
        for (int j = 0; j < 4; j++) {
            
            String str1 = optionButtons[i][j].getText().trim();
            String str2 = correctAnswers[i].trim();
            if (optionButtons[i][j].isSelected()) {
                
                attemptCount++;
                if(str1.equals(str2))
                    score++;
            }
        }
    }

}
public void showResult(){

        // Calculate the score
        countScoreAndAttempt();

        try {
            DataOutputStream resultOut = new DataOutputStream(skt.getOutputStream());
            resultOut.writeInt(score);

            DataInputStream rankIn = new DataInputStream(skt.getInputStream());
            rank = rankIn.readInt();

            rankIn.close();
            resultOut.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        getContentPane().removeAll();
        
        //Calculating result and showing it.
        ImageIcon img = new ImageIcon("onlineExamBackgroundImage.jpg");
        JLabel background = new JLabel(img);
        background.setBounds(0, 0, 900,800);
        getContentPane().add(background,BorderLayout.CENTER);

        JPanel last = new JPanel(new GridBagLayout());
        last.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel resultLabel = new JLabel("Your score is " + score + " out of " + numQuestions);
        resultLabel.setFont(new Font("Arial",Font.BOLD,40));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10); // Add 10 pixels of padding on all sides
        last.add(resultLabel, gbc);

        JLabel rankLabel = new JLabel("Your rank : "+rank);
        rankLabel.setFont(new Font("Arial", Font.BOLD, 40));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 10, 10, 10);
        last.add(rankLabel,gbc);

        JLabel totalAttempt = new JLabel("Total attempts : "+attemptCount);
        totalAttempt.setFont(new Font("Arial",Font.BOLD,40));
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.insets = new Insets(10, 10, 10, 10);
        last.add(totalAttempt,gbc);

        JButton update = new JButton("Update Result");
        update.setSize(100,50);
        update.setFont(new Font("Arial", Font.BOLD, 20));
        update.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                showResult();

            }
        });
        gbc.gridx = 0;
        gbc.gridy = 12;
        last.add(update,gbc);

        JButton closeButton = new JButton("Close");
        closeButton.setSize(100,50);
        closeButton.setFont(new Font("Arial",Font.BOLD,20));
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 16;
        last.add(closeButton, gbc);

        getContentPane().add(last, BorderLayout.CENTER);
        getContentPane().validate();
        getContentPane().repaint();

    }
}
class RoundBorder extends AbstractBorder {
    private int radius;

    public RoundBorder(int radius) {
        this.radius = radius;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        ((Graphics2D) g).setColor(Color.BLACK);
        Shape border = new RoundRectangle2D.Double(x, y, width - 1, height - 1, radius, radius);
        ((Graphics2D) g).draw(border);
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(radius, radius, radius, radius);
    }

    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.right = insets.bottom = insets.top = radius;
        return insets;
    }
}
