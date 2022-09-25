package tim.survey.surveyclientfx.ClientComs;

import SurveyMessagePacket.SurveyMessagePacket;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client
{
    private Socket socket = null;
    private ObjectInputStream console = null;
    private ObjectOutputStream streamOut = null;
    private ClientThread client = null;
    private String serverName;
    private int serverPort;
    TextField txtMessage;
    Logger logger = LogManager.getLogger();


    public Client(String server, int servPort, TextField txtMsg)
    {
        txtMessage = txtMsg;
        serverName = server;
        serverPort = servPort;
    }


    public void connect()
    {
        logger.debug("Establishing connection.");
        txtMessage.setText("Establishing connection. Please wait ...");
        try
        {
            socket = new Socket(serverName, serverPort);
            logger.debug("Connected: " + socket);
            txtMessage.setText("Connected: " + socket);
            open();
        }
        catch (UnknownHostException uhe)
        {
            logger.error("Host unknown: " + uhe.getMessage());
            txtMessage.setText("Host unknown: " + uhe.getMessage());
        }
        catch (IOException ioe)
        {
            logger.error("Unexpected exception: " + ioe.getMessage());
            txtMessage.setText("Unexpected exception: " + ioe.getMessage());
        }
    }

    public void send(SurveyMessagePacket response)
    {
        try
        {
            streamOut.writeObject(response);
            streamOut.flush();
            logger.debug("Sent");
            txtMessage.setText("SENT");
            // txtWord1.setText("");
        }
        catch (IOException ioe)
        {
            logger.error("Sending error: " + ioe.getMessage());
            txtMessage.setText("Sending error: " + ioe.getMessage());
            close();
        }
    }

    public void handle(SurveyMessagePacket inputPacket)
    {
        logger.debug("Entering handle.");
        switch(inputPacket.getMessageType())
        {
            case Answer:
                // We Should not get here
                logger.error("Message type = " + inputPacket.getMessageType());
                txtMessage.setText("ERROR - Message Type: " + inputPacket.getMessageType());
                break;
            case Disconnect:
                // TODO: Kill the Thread and send a message back to the GUI
                break;
            case Question:
                // TODO: Handle the question
                logger.debug("Question number: " + inputPacket.getQuestionNumber() + " received.");
                txtMessage.setText("Question number: " + inputPacket.getQuestionNumber() + " received.");
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
            client = ClientThread.createAndStartClientThread(this, socket);
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
        try
        {
            if (streamOut != null)
            {
                streamOut.close();
            }
            logger.debug("Closing Socket");
            if (socket != null)
            {
                socket.close();
            }
        }
        catch (IOException ioe)
        {
            logger.error("Error closing ..." + ioe);
            txtMessage.setText("Error closing ...");
        }
        client.close();
        client.stop();
    }

    void println(String msg)
    {
        //display.appendText(msg + "\n");
        //lblMessage.setText(msg);
    }


}
