package tim.survey.surveyclientfx.ClientComs;

import SurveyMessagePacket.SurveyMessagePacket;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tim.survey.surveyclientfx.SurveyController;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Manager for the client thread.  Handles received messages and sends responses.
 * Passes information back to the controller for gui update.
 */
public class ClientManager
{
    private Socket socket = null;
    private ObjectInputStream console = null;
    private ObjectOutputStream streamOut = null;
    private ClientThread clientThread = null;
    private String serverName;
    private int serverPort;

    // reference to the message field in the gui
    TextField txtMessage;
    // reference to the gui Controller
    SurveyController controller;
    Logger logger = LogManager.getLogger();


    /**
     * Constructor for the Client Thread Manager.
     * Sets up the connection information for the server and back to the controller.
     * @param server the address of the Survey Server
     * @param servPort the port for the Survey Server
     * @param txtMsg reference to the message field in the gui
     * @param guiController reference to the JavaFX Controller
     */
    public ClientManager(String server, int servPort, TextField txtMsg, SurveyController guiController)
    {
        txtMessage = txtMsg;
        serverName = server;
        serverPort = servPort;
        controller = guiController;
    }


    /**
     * Attempts to establish a connection to the Survey Server and calls open() to start outbound connection stream.
     * @return True on success, false on failure.
     */
    public boolean connect()
    {
        logger.debug("Establishing connection.");
        txtMessage.setText("Establishing connection. Please wait ...");
        try
        {
            socket = new Socket(serverName, serverPort);
            logger.debug("Connected: " + socket);
            txtMessage.setText("Connected to Question Server");
            open();
            return true;
        }
        catch (UnknownHostException uhe)
        {
            logger.error("Host unknown: " + uhe.getMessage());
            txtMessage.setText("Host unknown: " + uhe.getMessage());
            return false;
        }
        catch (IOException ioe)
        {
            logger.error("Unexpected exception: " + ioe.getMessage());
            txtMessage.setText("Unexpected exception: " + ioe.getMessage());
            return false;
        }
    }

    /**
     * Sends a response to the survey question
     * @param response A SurveyMessagePacket of type Answer
     */
    public void send(SurveyMessagePacket response)
    {
        try
        {
            streamOut.writeObject(response);
            streamOut.flush();
            logger.debug("Sent answer: " + response.getAnswer() + " to question " + response.getQuestionNumber());
            txtMessage.setText("Response to Q " + response.getQuestionNumber() + " sent.");

        }
        catch (IOException ioe)
        {
            logger.error("Sending error: " + ioe.getMessage());
            close();
        }
    }

    /**
     * Handles incoming message packets
     * @param inputPacket SurveyMessagePacket from the listener thread.
     */
    public synchronized void handle(SurveyMessagePacket inputPacket)
    {
        logger.debug("Entering message handler");
        switch(inputPacket.getMessageType())
        {
            case Answer:
                // We Should not get here
                logger.error("Message type = " + inputPacket.getMessageType());
                txtMessage.setText("ERROR - Message Type: " + inputPacket.getMessageType());
                break;
            case Disconnect:
                // Should not get here
                logger.debug("Disconnection Message received");
                Platform.runLater(()->
                {
                    controller.surveyStopped();
                });

                clientThread.stop();

                break;
            case Question:
                logger.debug("Question number: " + inputPacket.getQuestionNumber() + " received.");

                // need to return the value to the JavaFX thread - use Platform.runLater
                Platform.runLater(() ->
                {
                    controller.displayQuestion(inputPacket);
                });
                break;
        }
    }

    /**
     * Opens the output stream and starts the client listener thread.
     */
    public void open()
    {
        logger.debug("Opening StreamOut");
        try
        {
            streamOut = new ObjectOutputStream(socket.getOutputStream());
            logger.debug("Creating new ClientThread");
            clientThread = ClientThread.createAndStartClientThread(this, socket);
        }
        catch (IOException ioe)
        {
            logger.error("Error opening output stream: " + ioe);
            txtMessage.setText("Error opening output stream: " + ioe);
        }
    }

    /**
     * Sends a disconnection message to the server, closes the output stream and
     * shuts down the listener thread.
     */
    public void close()
    {
        logger.debug("Closing StreamOut");
        // Tell the SurveyServer we are disconnecting
        SurveyMessagePacket disconnectMessage = new SurveyMessagePacket();
        disconnectMessage.setMessageType(SurveyMessagePacket.MessageCodes.Disconnect);
        try
        {
            if(streamOut != null)
            {
                if(clientThread.t.isAlive())
                {
                    logger.debug("Sending: " + disconnectMessage.getMessageType());
                    streamOut.writeObject(disconnectMessage);
                    streamOut.flush();
                }
            }

        } catch (IOException e)
        {
            logger.error("Error sending disconnect message: "+ e.getMessage());
            //throw new RuntimeException(e);
        }

        try
        {
            if (clientThread != null)
            {
                clientThread.stop();
            }

            if (streamOut != null)
            {
                streamOut.close();
            }
        }
        catch (IOException ioe)
        {
            logger.error("Error closing ..." + ioe);
            txtMessage.setText("Error closing ...");
        }

    }
}
