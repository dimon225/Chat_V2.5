/*
 * Description: Class ConnectionBridge.java
 * Author: Dimtri Pankov
 * Date: 23-Feb-2011
 * Version: 1.0
 */
package ChessGameKenai;

import ChatProject.Client;
import java.awt.Color;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.text.SimpleAttributeSet;

/**
 * The ConnectionBridge class is the connection between two clients if the game is played online
 * using sockets. If the game is played online this class has responsibility to read data from the socket and
 * to establish a connection between clients in order for them to work as one. This class is an observer so each time
 * any change happens to our data class Chess_Data this class is notified by executing its update method data sends a list of
 * two value through notifyObservers method we extract them and send to the client that is connected when the client is connected
 * when the client receives a list he calls the move method with two arguments of the list that is sent so changes happen that way
 * This class also has an inner class ReadData which reads the data from the socket, analyzes it and calls the appropriate methods with it
 * It is not always move because we also have a chat so it could be a message, attributeSet, color or icon path etc......
 * @author Dimitri Pankov
 * @see Observer
 * @version 1.5
 */
public class ConnectionBridge implements Observer {

    private ServerSocket serverSocket;
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream dis;
    private boolean isServer = true;
    private InetAddress ipAddress;
    private ChessBoardView view;
    private ReadData readData;
    private Chess_Data data;
    private Color color = Color.ORANGE;
    private SimpleAttributeSet smpSet = new SimpleAttributeSet();
    private Chat chat;
    private Client client;

    /**
     * The overloaded constructor of the class creates server or client as well
     * as creates both stream ObjectInputStream and ObjectOutputStream and starts the inner class thread
     * @param data as Chess_Data
     * @param view as ChessBoardView
     * @param isServer as a boolean
     * @param ipAddress as an InetAddress
     * @param chat as a Chat
     * @param client as a Client exclusively for the chat
     */
    public ConnectionBridge(Chess_Data data, ChessBoardView view, boolean isServer, InetAddress ipAddress, Chat chat, Client client) {
        this.view = view;
        this.isServer = isServer;
        this.ipAddress = ipAddress;
        this.data = data;
        this.chat = chat;
        this.client = client;
        try {
            if (isServer) {
                chat.appendStr("\nSERVER ON LINE WAITING FOR CONNECTION\n", smpSet, color);
                serverSocket = new ServerSocket(8888);
                view.setConnectionBridge(this);
                socket = serverSocket.accept();
                chat.appendStr("CONNECTION IS ESTABLISHED\n", smpSet, color);
                chat.appendStr("GAME HAS BEEN STARTED!\n", smpSet, color);
                chat.setButtons(true);
                view.startTimer();
                data.isServer(true);
            } else {
                socket = new Socket(ipAddress, 8888);
                chat.appendStr("\nCONNECTED TO SERVER\n", smpSet, color);
                chat.appendStr("GAME HAS BEEN STARTED!\n", smpSet, color);
                chat.setButtons(true);
                view.startTimer();
                data.isServer(false);
                view.flipClientBoard();
            }
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            readData = new ReadData();
            readData.setPriority(Thread.MAX_PRIORITY);
            readData.start();

        } catch (ConnectException e) {
            chat.appendStr("\nSERVER NOT STARTED\n", smpSet, color);
            view.reEnableMenuItems(true);
            data.isGameOnLine(false);
        } catch (SocketException e) {
            client.appendString("\n" + client.getGuestName() + " has declined to play chess with you!", new SimpleAttributeSet(), Color.RED);
        } catch (Exception e) {
             Logger.getLogger(ConnectionBridge.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * The update method of the class is inherited from the Observer Interface
     * Each time any change happens to the data class Chess_Data this method is executed
     * in this case the update method receives Object that we use to extract the data from
     * The Chess_Data each time the move method is executed when the piece is moved it sends the ArrayList of two elements
     * first element is original position of the piece and the second is the destination. Update method sends the list to the other
     * client who upon receiving the list calls its move method with the arguments in the list
     * @param o as an Observable object
     * @param arg as an Object we send a list using this argument
     */
    public void update(Observable o, Object arg) {
        if (arg != null && socket != null) {
            try {
                oos.writeObject((ArrayList) arg);
                oos.flush();
            } catch (Exception e) {
                chat.appendStr(e.getMessage(), smpSet, color);
            }
        }
    }

    /**
     * The ReadData is a Thread object which reads data from the socket
     * then passes the read object to the analyzeAndExecute when the start method
     * is called on this object the run method is executed in our case it is inside the loop
     * so the run method will be executed until the boolean value is changed to false which will force the run
     * method to terminate thus killing the thread
     */
    public class ReadData extends Thread {

        private volatile boolean isAlive = true;

        /**
         * The empty constructor creates an input stream for reading data
         * @throws IOException when working with sockets risks are unavoidable
         */
        public ReadData() throws IOException {
            dis = new ObjectInputStream(socket.getInputStream());
        }

        /**
         * The run method of the class reads data from the socket
         * it is inside the loop so reads data until the run method terminates
         */
        @Override
        public void run() {
            try {
                while (isAlive) {
                    Object object = dis.readObject();
                    this.analyzeAndExecute(object);
                }
            } catch (EOFException e) {
                chat.appendStr("\n" + chat.getClientName() + " Has left the game", smpSet, color);
                chat.getTxtPane().setCaretPosition(chat.getTxtPane().getDocument().getLength());
                chat.setButtons(false);
                data.isGameOnLine(false);
                view.reEnableMenuItems(true);
                try {
                    socket.close();
                } catch (Exception d) {
                    chat.appendStr(d.toString(), smpSet, color);
                }
                socket = null;
                ConnectionBridge.this.killThread();
            } catch (Exception e) {
                chat.appendStr(e.toString(), smpSet, color);
            }
        }

        /**
         * The analyzeAndExecute method receives an object that the ReadData class has just read from
         * the socket this method analyzes it and calls appropriate methods to update the view like change icon,
         * display message, restart game or list of two integers that the move method will be called with
         * @param object as an Object
         */
        public void analyzeAndExecute(Object object) {
            if (object instanceof Packet) {
                Packet packet = (Packet) object;
                if (packet.getMessage() != null) {
                    chat.appendStr("\n" + chat.getClientName() + ": " + packet.getMessage(), packet.getSmpSet(), packet.getColor());
                    chat.getTxtPane().setCaretPosition(chat.getTxtPane().getDocument().getLength());
                }
                if (packet.getImgPath() != null) {
                    chat.getTxtPane().insertIcon(new ImageIcon(getClass().getResource(packet.getImgPath())));
                    chat.getTxtPane().setCaretPosition(chat.getTxtPane().getDocument().getLength());
                }
                if (packet.getPlayerIconPath() != null) {
                    ConnectionBridge.this.setPlayerIconPath(object);
                }
                if (packet.getGuestName() != null) {
                    ConnectionBridge.this.setGuestName(object);
                }
                if (packet.getRestartGame() != null) {
                    ConnectionBridge.this.restartGame();
                }
                if (packet.getConfirmRestart() != null) {
                    view.restartClientGame();
                }
            } else if (object instanceof ArrayList) {
                ArrayList list = (ArrayList) object;
                data.move((Integer) list.get(0), (Integer) list.get(1));
            }
        }
    }

    /**
     * The method getOutputStream returns the outputStream to the caller
     * @return oos as an ObjectOutputStream
     */
    public ObjectOutputStream getOutputStream() {
        return oos;
    }

    /**
     * The method restartGame simply restarts the client game
     * when it is needed it is calling the method from the view c;lass
     */
    public void restartGame() {
        int returnValue = JOptionPane.showConfirmDialog(view, chat.getClientName() + "would you like to restart the game", "Confirmation Message", JOptionPane.YES_NO_OPTION);
        if (returnValue == JOptionPane.YES_OPTION) {
            Packet packet = new Packet();
            packet.setConfirmRestart("restart game");
            try {
                this.oos.writeObject(packet);
                this.oos.flush();
            } catch (IOException ex) {
                chat.appendStr(ex.toString(), smpSet, color);
            }
            view.restartClientGame();
        }
    }

    /**
     * The method killThread simply sets the boolean value
     * isAlive to false this forces the run method to exit thus killing the thread
     */
    public void killThread() {
        if (readData != null) {
            readData.isAlive = false;
        }
    }

    /**
     * The method setPlayericonPath simply sets the iconPath
     * of the player image this method is used to update the view of the client
     * when a client changes his image the path to the image is send to the other client that is connected
     * so he would also see that image changed
     * @param object as an Object
     */
    public void setPlayerIconPath(Object object) {
        Packet packet = (Packet) object;
        if (data.isServer()) {
            data.getPlayers().get(1).setImagePath(packet.getPlayerIconPath());
        } else {
            data.getPlayers().get(0).setImagePath(packet.getPlayerIconPath());
        }
        data.notifyView();
    }

    /**
     * The method setGuestName simply sets the guest name
     * of the player this method is used to update the view of the client
     * when a client changes his name the name is send to the other client that is connected
     * so he would also see that name changed
     * @param object as an Object
     */
    public void setGuestName(Object object) {
        Packet packet = (Packet) object;
        if (data.isServer()) {
            data.getPlayers().get(1).setName(packet.getGuestName());
        } else {
            data.getPlayers().get(0).setName(packet.getGuestName());
        }
        data.notifyView();
    }

    /**
     * The method getServerSocket simply returns the serverSocket to the caller
     * @return serverSocket as a ServerSocket
     */
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    /**
     * The method closeSocket simply closes the socket
     * if the user no longer wants to play he exits we need to close the socket
     */
    public void closeSocket() {
        try {
            socket.close();
        } catch (Exception e) {
            chat.appendStr(e.toString(), smpSet, color);
        }
    }
}
