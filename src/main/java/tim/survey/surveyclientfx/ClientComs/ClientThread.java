package tim.survey.surveyclientfx.ClientComs;

import SurveyMessagePacket.SurveyMessagePacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientThread implements Runnable
{
    private Socket socket = null;
    private Client client = null;
    private ObjectInputStream streamIn = null;
    Thread t;
    private String name;
    private int ID =-1;
    boolean stopFlag;
    Logger logger;



    public static ClientThread createAndStartClientThread(Client inClient, Socket inSocket)
    {
        ClientThread newThread = new ClientThread(inClient, inSocket);
        newThread.t.start();
        return newThread;
    }

    public ClientThread(Client inClient, Socket inSocket)
    {
        logger = LogManager.getLogger(ClientThread.class);
        client = inClient;
        socket = inSocket;
        ID = socket.getPort();
        name = "Client Thread " + ID;
        t = new Thread(this, name);
        stopFlag = false;
        //openSocket();

    }

    public void openSocket()
    {
        try
        {
            logger.debug("Opening InputStream.");
            streamIn = new ObjectInputStream(socket.getInputStream());
        }
        catch (IOException ioe)
        {
            logger.error("Error getting input stream: " + ioe.getMessage());

            //client2.stop();
            client.close();
        }
    }

    public void close()
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
                client.handle((SurveyMessagePacket) streamIn.readObject());
            }
            catch (IOException ioe)
            {
                logger.error("Listening error: " + ioe.getMessage());
                //client2.stop();
                client.close();
            } catch (ClassNotFoundException e)
            {
                logger.error("Class not found error: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
        logger.debug("Exiting Thread.");
    }
}
