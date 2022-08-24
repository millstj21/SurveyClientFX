package tim.survey.surveyclientfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;

public class SurveyApplication extends Application
{
    @Override
    public void start(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(SurveyApplication.class.getResource("surveyclient-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Survey Questions");
        stage.setScene(scene);
        stage.show();

        // Wire up the exitApp.  Consume the event otherwise it will exit anyway.
        stage.setOnCloseRequest(event ->
        {
            event.consume();
            exitApp(stage);
        });
    }

    void exitApp(Stage stage)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("You are about to exit the Survey Application.");
        alert.setContentText("Are you sure?");

        if(alert.showAndWait().get() == ButtonType.OK)
        {

            stage.close();
        }

    }

    public static void main(String[] args)
    {
        launch();
    }
}