package tim.survey.surveyclientfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class SurveyApplication extends Application
{
    private static final Logger logger = LogManager.getLogger(SurveyApplication.class);
    @Override
    public void start(Stage stage) throws IOException
    {
        logger.debug("************************************************");
        logger.debug("Application Starting");
        logger.debug("************************************************");

        FXMLLoader fxmlLoader = new FXMLLoader(SurveyApplication.class.getResource("surveyclient-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Survey Questions");
        stage.setScene(scene);
        stage.show();

        SurveyController controller = fxmlLoader.getController();
        // Wire up the exitApp.  Consume the event otherwise it will exit anyway.
        stage.setOnCloseRequest(event ->
        {
            event.consume();
            exitApp(stage, controller);
        });
    }

    /**
     * Initiates the orderly shutdown of the application
     * @param stage Application stage
     * @param controller Survey Controller
     */
    void exitApp(Stage stage, SurveyController controller)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("You are about to exit the Survey Application.");
        alert.setContentText("Are you sure?");

        if(alert.showAndWait().get() == ButtonType.OK)
        {
            controller.shutdown();

            stage.close();
        }

    }

    public static void main(String[] args)
    {
        launch();
    }
}