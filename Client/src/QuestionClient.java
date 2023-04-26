import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import java.util.*;

public class QuestionClient extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private JLabel[] questionLabels;
    private JRadioButton[][] optionButtons;
    private ButtonGroup bg[];
    private JButton submitButton;
    private int numQuestions;
    private String[] correctAnswers;
    private int score = 0;

    public QuestionClient(ArrayList<Question> questions) {
        // Set up the GUI
        super("Questions");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        numQuestions = questions.size();
        correctAnswers = new String[numQuestions];
        
        JPanel questionPanel = new JPanel(new GridLayout(numQuestions, 1));
        questionLabels = new JLabel[numQuestions];
        optionButtons = new JRadioButton[numQuestions][4];
        bg = new ButtonGroup[4];
        
        // Add each question to the panel
        for (int i = 0; i < numQuestions; i++) {
            questionLabels[i] = new JLabel((i + 1) + ". " + questions.get(i).getQuestion());
            questionPanel.add(questionLabels[i]);
            bg[i] = new ButtonGroup();
            for (int j = 0; j < 4; j++) {
                optionButtons[i][j] = new JRadioButton(questions.get(i).getOptions()[j]);
                bg[i].add(optionButtons[i][j]);
                questionPanel.add(optionButtons[i][j]);
            }

            correctAnswers[i] = questions.get(i).getAnswer();
            

        }

        JPanel buttonPanel = new JPanel(new BorderLayout());
        submitButton = new JButton("Submit");
        submitButton.addActionListener(this);
        buttonPanel.add(submitButton, BorderLayout.CENTER);

        getContentPane().add(questionPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            // Calculate the score
    
            for (int i = 0; i < numQuestions; i++) {
                for (int j = 0; j < 4; j++) {
                    
                    String str1 = optionButtons[i][j].getText().trim();
                    String str2 = correctAnswers[i].trim();
                    if (optionButtons[i][j].isSelected() && str1.equals(str2)) {
                       
                        score++;
                    }
                }
            }

            // Display the result
            getContentPane().removeAll();
            getContentPane().setLayout(new GridLayout(2, 1));
            JLabel resultLabel = new JLabel("Your score is " + score + " out of " + numQuestions);
            getContentPane().add(resultLabel);
            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
            getContentPane().add(closeButton);
            getContentPane().validate();
            getContentPane().repaint();
        }
    }
}
