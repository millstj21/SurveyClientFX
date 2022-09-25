package tim.survey.surveyclientfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tim.survey.surveyclientfx.ClientComs.Client;
import SurveyMessagePacket.SurveyMessagePacket;

import java.net.URL;
import java.util.ResourceBundle;

public class SurveyController implements Initializable
{

    Client comClient;
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
            stage = (Stage) scenePane.getScene().getWindow();
            stage.close();
        }

    }
    @FXML
    void onConnectClicked(ActionEvent event)
    {
        comClient = new Client("localhost", 4444, txtMessage);
        logger.debug("Connecting....");
        comClient.connect();

    }

    @FXML
    void onSendClicked(ActionEvent event)
    {
        msgPacket = new SurveyMessagePacket();
        // msgPacket.setQuestionNumber( something );
        msgPacket.setAnswer(Integer.parseInt(txtResponse.getText()));
        comClient.send(msgPacket);
    }


}
