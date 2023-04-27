import java.io.*;
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
