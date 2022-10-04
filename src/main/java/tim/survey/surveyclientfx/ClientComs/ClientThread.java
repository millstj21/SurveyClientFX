package tim.survey.surveyclientfx.ClientComs;

import SurveyMessagePacket.SurveyMessagePacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * The listener thread for the client.
 */
public class ClientThread implements Runnable
{
    private Socket socket = null;
    private ClientManager clientManager = null;
    private ObjectInputStream streamIn = null;
    Thread t;
    private String name;
    private int ID =-1;
    boolean stopFlag;
    Logger logger;


    /**
     * Factory method for creating and starting the thread
     * @param inClientManager a reference to the client manager - the calling class
     * @param inSocket the socket to listen to
     * @return Returns a reference to a running listener thread
     */
    public static ClientThread createAndStartClientThread(ClientManager inClientManager, Socket inSocket)
    {
        ClientThread newThread = new ClientThread(inClientManager, inSocket);
        newThread.t.start();
        return newThread;
    }

    /**
     * Constructor for the listener thread.  Should only be called by the factory method -
     * createAndStartClientThread(ClientManager inClientManager, Socket inSocket)
     * @param inClientManager a reference to the client manager - the calling class
     * @param inSocket the socket to listen to
     */
    public ClientThread(ClientManager inClientManager, Socket inSocket)
    {
        logger = LogManager.getLogger(ClientThread.class);
        clientManager = inClientManager;
        socket = inSocket;
        ID = socket.getPort();
        name = "Client Thread " + ID;
        t = new Thread(this, name);
        stopFlag = false;
        //openSocket();

    }

    /**
     * Opens a socket as an ObjectInputStream.
     */
    private void openSocket()
    {
        try
        {
            logger.debug("Opening InputStream.");
            streamIn = new ObjectInputStream(socket.getInputStream());
        }
        catch (IOException ioe)
        {
            logger.error("Error getting input stream: " + ioe.getMessage());
        }
    }

    /**
     * Closes the input stream socket.
     */
    private void close()
    {
        logger.debug("Closing InputStream.");
        try
        {
            if (streamIn != null)
            {
                streamIn.close();
            }
        }
        catch (IOException ioe)
        {
            logger.error("Error closing input stream: " + ioe.getMessage());
        }
    }

    /**
     * Thread shutdown logic
     */
    public synchronized void stop()
    {
        stopFlag = true;
    }

    /**
     * Thread startup method.
     * Opens the socket and listens for a broadcast from the Server.
     */
    @Override
    public void run()
    {
        logger = LogManager.getLogger("Thread:" + Thread.currentThread().getName());
        logger.debug("Starting Thread.");
        openSocket();

        while (!stopFlag)
        {
            try
            {
                clientManager.handle((SurveyMessagePacket) streamIn.readObject());
            }
            catch (IOException ioe)
            {
                logger.error("Listening error: " + ioe.getMessage());
                //client2.stop();
                clientManager.close();
            } catch (ClassNotFoundException e)
            {
                logger.error("Class not found error: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }

        logger.debug("Exiting Thread.");
    }
}
