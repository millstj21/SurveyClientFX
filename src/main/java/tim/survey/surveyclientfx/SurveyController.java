package tim.survey.surveyclientfx;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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




    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        logger.debug("Starting Controller initialization.");


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
            if (comClientManager !=null)
            {
                comClientManager.close();
            }

            stage = (Stage) scenePane.getScene().getWindow();
            stage.close();
        }

    }
    @FXML
    void onConnectClicked(ActionEvent event)
    {
        comClientManager = new ClientManager("localhost", 4444, txtMessage, this);
        logger.debug("Connecting....");
        comClientManager.connect();

    }

    @FXML
    void onSendClicked(ActionEvent event)
    {
        msgPacket = new SurveyMessagePacket();
        msgPacket.setQuestionNumber(lblNum.getText());
        msgPacket.setMessageType(SurveyMessagePacket.MessageCodes.Answer);
        msgPacket.setAnswer(Integer.parseInt(txtResponse.getText()));
        comClientManager.send(msgPacket);
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




    }


}
