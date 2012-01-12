/*
 * Description: Class Session.java
 * Author: Dimtri Pankov
 * Date: 11-Feb-2011
 * Version: 1.0
 */
package ChatProject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 *The Session class is the session between Client and Server this object is the Thread
 * Each time that the object of this type is created it runs in its own thread
 * It receives the object from the client analyzes it then does what is needed
 * if the object contains message it broadcasts the message to all connected clients
 * There also could be objects for private chat request and etc........
 * This class also holds all the information of the client like his IP,port number
 * name and when the object of this type is created it is analyzed
 * @author Dimitri Pankov
 * @see Thread
 * @version 1.1
 */
public class Session extends Thread {

    private ObjectInputStream ois;
    private Socket socket;
    private volatile boolean isAlive = true;
    private ServerGUI server;
    private String clientID;
    private ObjectOutputStream oos;
    private InetAddress ipAddress;
    private int port;
    private String clientName;

    /**
     * The Overloaded constructor of the class receives a ServerGUI class and the client socket
     * in the constructor ServerGUI reference is needed to access its methods the socket is needed for
     * communication with the client all  reading is don by this class the sending is done by the server
     * I still create here both input and output streams the output stream is needed in the server so i have method
     * that passes the output stream to server when needed when using objects its not as easy as Strings u cannot
     * create multiple input or output streams for one socket because they are kind of connected when i tried to
     * construct a new ObjectOutputStream in the server it always throws corrupted stream exception for one socket
     * with objects u can only use the stream one stream not multiple u have created
     * @param server as a ServerGUI needed to access its methods
     * @param socket as a Socket that the client is connected to
     * @throws IOException
     */
    public Session(ServerGUI server, Socket socket) throws IOException {
        this.socket = socket;
        this.server = server;

        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.flush();

        ois = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * The rum method of the class because this class is the Thread
     * This method is executed when start on the object is called
     * This particular run method first reads the packet object gets the client name\
     * makes sure the client name does not exists if exists throws close the client socket and streams
     * and also sends a message which notifies the client that he needs to choose another name and reconnect
     * if user name does not exists the session remembers clientID which is a name and IP combined
     * also gets the port number then updates all clients that there is a new connection in the list
     * also updates server list then adds the user name to the userName list
     * Then appends the message to the server's text area saying clientID has joined the session
     */
    @Override
    public void run() {
        try {
            Packet packet = (Packet) ois.readObject();
            clientName = packet.getName();
            if (!server.isUserNameExists(this, clientName)) {
                clientID = packet.getClientID();
                port = packet.getPort();
                ipAddress = socket.getInetAddress();
                server.getServer().updateList(this);
                server.getServer().updateClients();
                server.addUserName(clientName);
                server.getArea().append(clientName + ipAddress + " has joined the session\n");
                while (isAlive) {
                    server.getServer().analyzeAndDisplay(ois.readObject());
                }
            } else {
                server.getServer().removeSession(this);
                server.getServer().updateClients();
                this.getSocket().close();
                this.getOutputStream().close();
                this.ois.close();
            }

        } catch (SocketException e) {
            server.getArea().append(clientName + ipAddress + " has left the session\n");
            isAlive = false;
            server.getServer().removeSession(this);
            server.getServer().updateClients();
            server.removeClientName(clientName);
        } catch (Exception e) {
            server.getArea().append(e.toString());
            isAlive = false;
        }
    }

    /**
     * The method killThread kills the thread
     * on this object simply by changing the boolean value to false
     */
    public void killThread() {
        isAlive = false;
    }

    /**
     * The method getOutputStream simply returns the ObjectOutputStream
     * back to the caller this stream is used to send objects to the client that is connected
     * on this session
     * @return oos as a ObjectOutputStream to the caller
     */
    public ObjectOutputStream getOutputStream() {
        return oos;
    }

    /**
     * The method getSocket simply returns the socket back to the caller
     * it is used to get IP and some other information on the client
     * @return socket as Socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * The method getClientID simply returns the clientID to the caller
     * ClientID is a combination of name and IP address
     * @return clientID as a String
     */
    public String getClientID() {
        return clientID;
    }

    /**
     * The method getClientIP simply returns the ipAddress to the caller
     * @return clientIP as a String
     */
    public InetAddress getClientIP() {
        return ipAddress;
    }

    /**
     * The method getPort simply returns the port to the caller
     * @return clientID as a String
     */
    public int getPort() {
        return port;
    }

    /**
     * The method getClientName simply returns the client name to the caller
     * @return clientName as a String
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * The toString method is an overridden method
     * from the superclass of the Thread
     * @return clientID as a String
     */
    @Override
    public String toString() {
        return clientName + socket.getInetAddress();
    }
}
