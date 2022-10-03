package tim.survey.surveyclientfx;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tim.survey.surveyclientfx.ClientComs.ClientManager;
import SurveyMessagePacket.SurveyMessagePacket;

import java.net.URL;
import java.util.ResourceBundle;

public class SurveyController implements Initializable
{

    ClientManager comClientManager;
    SurveyMessagePacket msgPacket;
    Logger logger = LogManager.getLogger();

    // Indicates if a question is waiting to br answered
    boolean questionWaiting = false;
    int answerNumber;
    boolean isConnected = false;


    //<editor-fold desc="FXML Declarations">
    @FXML
    private Button btnExit;
    @FXML
    private Button btnConnect;
    @FXML
    private Button btnSend;
    @FXML
    private TextField txtMessage;
    @FXML
    private TextField txtResponse;
    Stage stage;
    @FXML
    private VBox scenePane;

    // Question fields
    @FXML
    private TextField txtTopic;
    @FXML
    private Label lblNum;
    @FXML
    private TextField txtAnswer3;

    @FXML
    private TextField txtAnswer1;

    @FXML
    private TextField txtAnswer2;

    @FXML
    private TextField txtAnswer4;

    @FXML
    private TextField txtAnswer5;
    @FXML
    private TextArea txtAreaQuestionText;
    //</editor-fold>




    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        logger.debug("Starting Controller initialization.");
        // disable send button
        btnSend.setDisable(true);

        // disable text fields for editing
        txtTopic.setEditable(false);
        txtAreaQuestionText.setEditable(false);
        txtAnswer1.setEditable(false);
        txtAnswer2.setEditable(false);
        txtAnswer3.setEditable(false);
        txtAnswer4.setEditable(false);
        txtAnswer5.setEditable(false);

        logger.debug("Finishing Controller initialization.");

    }

    @FXML
    void onExitClicked(ActionEvent event)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("You are about to exit the Survey Application.");
        alert.setContentText("Are you sure?");

        if(alert.showAndWait().get() == ButtonType.OK)
        {
            shutdown();

            stage = (Stage) scenePane.getScene().getWindow();
            stage.close();
        }

    }
    @FXML
    void onConnectClicked(ActionEvent event)
    {
        if (isConnected)
        {
            if (comClientManager !=null)
            {
                comClientManager.close();
            }
            btnConnect.setText("Connect");
            isConnected = false;
        }
        else
        {
            comClientManager = new ClientManager("localhost", 4444, txtMessage, this);
            logger.debug("Connecting....");
            isConnected = comClientManager.connect();
            if (isConnected)
            {
                btnConnect.setText("Disconnect");
            }
        }

    }

    @FXML
    void onSendClicked(ActionEvent event)
    {
        msgPacket = new SurveyMessagePacket();
        msgPacket.setQuestionNumber(lblNum.getText());
        msgPacket.setMessageType(SurveyMessagePacket.MessageCodes.Answer);
        msgPacket.setAnswer(answerNumber);
        comClientManager.send(msgPacket);
        setAnswerFieldsBackground();
        answerNumber = 0;
        btnSend.setDisable(true);
        questionWaiting = false;
    }

    @FXML
    void onAnswerClicked(MouseEvent event)
    {
        // if a questions is waiting:
        // Set all fields clear
        // set this field highlighted
        // set the answer number
        // Enable send button

        if(questionWaiting)
        {
            if (event.getSource() == txtAnswer1)
            {
                setAnswerFieldsBackground();
                txtAnswer1.setStyle("-fx-background-color: red");
                answerNumber = 1;
                btnSend.setDisable(false);
            }
            if (event.getSource() == txtAnswer2)
            {
                setAnswerFieldsBackground();
                txtAnswer2.setStyle("-fx-background-color: red");
                answerNumber = 2;
                btnSend.setDisable(false);
            }
            if (event.getSource() == txtAnswer3)
            {
                setAnswerFieldsBackground();
                txtAnswer3.setStyle("-fx-background-color: red");
                answerNumber = 3;
                btnSend.setDisable(false);
            }
            if (event.getSource() == txtAnswer4)
            {
                setAnswerFieldsBackground();
                txtAnswer4.setStyle("-fx-background-color: red");
                answerNumber = 4;
                btnSend.setDisable(false);
            }
            if (event.getSource() == txtAnswer5)
            {
                setAnswerFieldsBackground();
                txtAnswer5.setStyle("-fx-background-color: red");
                answerNumber = 5;
                btnSend.setDisable(false);
            }
        }

    }

    public synchronized void displayQuestion(SurveyMessagePacket questionPacket)
    {

            txtTopic.setText(questionPacket.getTopic());
            txtAreaQuestionText.setText(questionPacket.getQuestion());
            lblNum.setText(questionPacket.getQuestionNumber());
            txtAnswer1.setText(questionPacket.getAnswer1());
            txtAnswer2.setText(questionPacket.getAnswer2());
            txtAnswer3.setText(questionPacket.getAnswer3());
            txtAnswer4.setText(questionPacket.getAnswer4());
            txtAnswer5.setText(questionPacket.getAnswer5());

            questionWaiting = true;

    }

    public void shutdown()
    {
        if (isConnected)
        {
            if (comClientManager !=null)
            {
                comClientManager.close();
            }
        }
    }
    private void setAnswerFieldsBackground()
    {
        var defaultStyle = txtTopic.getStyle();
        txtAnswer1.setStyle(defaultStyle);
        txtAnswer2.setStyle(defaultStyle);
        txtAnswer3.setStyle(defaultStyle);
        txtAnswer4.setStyle(defaultStyle);
        txtAnswer5.setStyle(defaultStyle);
    }


}
