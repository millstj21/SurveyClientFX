package SurveyMessagePacket;

import java.io.Serializable;

public class SurveyMessagePacket implements Serializable
{
    public enum MessageCodes
    {
        Question,
        Answer,
        Disconnect
    }
    // Message type: 1 = Question, 2 = Answer, 3 = Disconnect
    private MessageCodes messageType;
    private String questionNumber;
    private String topic;
    private String question;

    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private String answer5;

    private int answer;

    public MessageCodes getMessageType()
    {
        return messageType;
    }

    public void setMessageType(MessageCodes messageType)
    {
        this.messageType = messageType;
    }

    public String getQuestionNumber()
    {
        return questionNumber;
    }

    public void setQuestionNumber(String questionNumber)
    {
        this.questionNumber = questionNumber;
    }

    public String getTopic()
    {
        return topic;
    }

    public void setTopic(String topic)
    {
        this.topic = topic;
    }

    public String getQuestion()
    {
        return question;
    }

    public void setQuestion(String question)
    {
        this.question = question;
    }

    public String getAnswer1()
    {
        return answer1;
    }

    public void setAnswer1(String answer1)
    {
        this.answer1 = answer1;
    }

    public String getAnswer2()
    {
        return answer2;
    }

    public void setAnswer2(String answer2)
    {
        this.answer2 = answer2;
    }

    public String getAnswer3()
    {
        return answer3;
    }

    public void setAnswer3(String answer3)
    {
        this.answer3 = answer3;
    }

    public String getAnswer4()
    {
        return answer4;
    }

    public void setAnswer4(String answer4)
    {
        this.answer4 = answer4;
    }

    public String getAnswer5()
    {
        return answer5;
    }

    public void setAnswer5(String answer5)
    {
        this.answer5 = answer5;
    }

    public int getAnswer()
    {
        return answer;
    }

    public void setAnswer(int answer)
    {
        this.answer = answer;
    }
}
