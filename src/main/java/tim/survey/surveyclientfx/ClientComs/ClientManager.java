package tim.survey.surveyclientfx.ClientComs;

import SurveyMessagePacket.SurveyMessagePacket;
import javafx.application.Platform;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tim.survey.surveyclientfx.SurveyController;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientManager
{
    private Socket socket = null;
    private ObjectInputStream console = null;
    private ObjectOutputStream streamOut = null;
    private ClientThread clientThread = null;
    private String serverName;
    private int serverPort;
    TextField txtMessage;
    SurveyController controller;
    Logger logger = LogManager.getLogger();


    public ClientManager(String server, int servPort, TextField txtMsg, SurveyController guiController)
    {
        txtMessage = txtMsg;
        serverName = server;
        serverPort = servPort;
        controller = guiController;
    }


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

    public void send(SurveyMessagePacket response)
    {
        try
        {
            streamOut.writeObject(response);
            streamOut.flush();
            logger.debug("Sent answer: " + response.getAnswer());
            txtMessage.setText("SENT: " + response.getAnswer());

        }
        catch (IOException ioe)
        {
            logger.error("Sending error: " + ioe.getMessage());
            close();
        }
    }

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
            throw new RuntimeException(e);
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
