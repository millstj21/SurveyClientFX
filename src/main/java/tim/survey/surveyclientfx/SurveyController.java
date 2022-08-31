package tim.survey.surveyclientfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tim.survey.surveyclientfx.ClientComs.Client;

public class SurveyController {

    Client comClient;
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
        comClient.connect();

    }

    @FXML
    void onSendClicked(ActionEvent event)
    {
        comClient.send(txtResponse.getText());
    }

}
