package tim.survey.surveyclientfx.ClientComs;

import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client
{
    private Socket socket = null;
    private DataInputStream console = null;
    private DataOutputStream streamOut = null;
    private ClientThread client = null;
    private String serverName;
    private int serverPort;
    TextField txtMessage;


    public Client(String server, int servPort, TextField txtMsg)
    {
        txtMessage = txtMsg;
        serverName = server;
        serverPort = servPort;
    }


    public void connect()
    {
        txtMessage.setText("Establishing connection. Please wait ...");
        try
        {
            socket = new Socket(serverName, serverPort);
            txtMessage.setText("Connected: " + socket);
            open();
        }
        catch (UnknownHostException uhe)
        {
            txtMessage.setText("Host unknown: " + uhe.getMessage());
        }
        catch (IOException ioe)
        {
            txtMessage.setText("Unexpected exception: " + ioe.getMessage());
        }
    }

    public void send(String response)
    {
        try
        {
            streamOut.writeUTF(response);
            streamOut.flush();
            txtMessage.setText("SENT");
            // txtWord1.setText("");
        }
        catch (IOException ioe)
        {
            txtMessage.setText("Sending error: " + ioe.getMessage());
            close();
        }
    }

    public void handle(String msg)
    {
        if (msg.equals(".bye"))
        {
            txtMessage.setText("Good bye. Press EXIT button to exit ...");
            close();
        }
        else
        {
            System.out.println("Handle: " + msg);
            println(msg);
        }
    }

    public void open()
    {
        try
        {
            streamOut = new DataOutputStream(socket.getOutputStream());
            client = new ClientThread(this, socket);
        }
        catch (IOException ioe)
        {
            txtMessage.setText("Error opening output stream: " + ioe);
        }
    }

    public void close()
    {
        try
        {
            if (streamOut != null)
            {
                streamOut.close();
            }
            if (socket != null)
            {
                socket.close();
            }
        }
        catch (IOException ioe)
        {
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
