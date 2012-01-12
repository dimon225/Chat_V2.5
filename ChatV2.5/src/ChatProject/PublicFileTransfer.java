/*
 * Description: Class PublicFileTransfer.java
 * Author: Dimtri Pankov
 * Date: 22-Feb-2011
 * Version: 1.0
 */
package ChatProject;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.text.SimpleAttributeSet;

/**
 * The PublicFileTransfer class is used to transfer file through the public chat
 * my chat application has two possibilities you can either transfer files through a private chat
 * or you can transfer through the public chat which one you use its upto you as a user
 * This class has a simple GUI graphical user interface to let the user know that the file is being transfered
 * and how many percent are left. The files are all downloaded into a default folder called downloads which
 * is on the same root level as the jar file or if you are running the code directly it's on the same root level as source
 * folder. I use this class for either server side or a client side public file transfer so my constructor is overloaded with 5
 * arguments so it would be easier to create both instances of both sides when i create the instance of this class it already knows
 * the arguments and works with them instead having other classes to upload/download the data
 * @author Dimitri Pankov
 * @see JFrame
 * @version 1.4
 */
public class PublicFileTransfer {

    private Client client;
    private JProgressBar pBarFileTransferInfo;
    private Socket socket;
    private ServerSocket serverSocket;
    private FileOutputStream fos;
    private InputStream is;
    private FileInputStream fis;
    private OutputStream os;
    private boolean isServer = true;
    private InetAddress ipAddress;
    private File file;
    private int fileSize;
    private String filePath;
    private boolean success;
    private JButton btnAccept, btnDecline;
    private PromptPublicFileTransfer publicFileTransfer;
    private JPanel pnlProgress;
    private JLabel lblProgress;

    /**
     * Overloaded constructor of the class receives 6 arguments creates itself according to those arguments
     * then establishes a connection with the other side then uploads or downloads data
     * @param client as a Client object this object needs to know where the client is
     * @param isServer as a boolean tells the class to either create server or client
     * @param ipAddress as a String only used by the client side to connect to the waiting server
     * @param file as a File only used by the server side to upload chosen file
     * @param filePath as a String only used by the client side to know the name of the file
     * @param fileSize as an integer only used by the client side to know the file size
     */
    public PublicFileTransfer(final Client client, final boolean isServer, InetAddress ipAddress, File file, String filePath, int fileSize) {
        this.client = client;
        this.isServer = isServer;
        this.ipAddress = ipAddress;
        this.file = file;
        this.filePath = filePath;
        this.fileSize = fileSize;

        pBarFileTransferInfo = new JProgressBar();
        pBarFileTransferInfo.setMaximum(fileSize);
        pBarFileTransferInfo.setStringPainted(true);

        pnlProgress = new JPanel(new BorderLayout());
        pnlProgress.setOpaque(false);
        lblProgress = new JLabel("Upload Progress", SwingUtilities.CENTER);
        lblProgress.setForeground(Color.WHITE);
        lblProgress.setFont(new Font("Verdana", Font.BOLD, 18));
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.add(pBarFileTransferInfo);
        pnlProgress.add(lblProgress, BorderLayout.CENTER);
        pnlProgress.add(panel, BorderLayout.SOUTH);

        new Thread(new Runnable() {

            public void run() {
                if (isServer) {
                    try {
                        serverSocket = new ServerSocket(11111);
                        socket = serverSocket.accept();
                        client.appendString("\nSending File...........\n", new SimpleAttributeSet(), Color.RED);
                        client.getTextPane().insertComponent(pnlProgress);
                        new PublicFileOUTTransfer().start();
                    } catch (SocketException e) {
                        client.appendString("\n" + client.getGuestName() + " has refused to download your file", new SimpleAttributeSet(), Color.RED);
                    } catch (Exception e) {
                        client.appendString("\n" + e.toString(), new SimpleAttributeSet(), Color.RED);
                    }
                }
            }
        }).start();
    }

    /**
     * The PublicFileOUTTransfer class is used for the server side
     * of the transfer file connection which uploads the chosen file to the
     * selected user this job is executed in its own thread it does not interfere with anything
     * @author Dimitri Pankov
     * @see Thread
     * @version 1.4
     */
    public class PublicFileOUTTransfer extends Thread {

        private byte[] array;
        private int bytesRead;
        private int intRead;

        /**
         * Empty constructor of the class creates needed objects
         * for uploading the file. The socket output stream and file input stream are created then the byte array
         * that we use to upload the data then the progress bar is set to the maximum value which is file size
         * and that value is 100% as we send data we update the progress bar with received data it does the math for us
         * and tells the user how much percent is uploaded
         * @throws FileNotFoundException if file is not found this is the exception that will be thrown
         * @throws IOException when working with stream and sockets risks are unavoidable
         */
        public PublicFileOUTTransfer() throws FileNotFoundException, IOException {
            fis = new FileInputStream(new File(file.getPath()));
            os = socket.getOutputStream();
            array = new byte[4000];
            pBarFileTransferInfo.setMaximum((int) file.length());
        }

        /**
         * The run method of the class only reads the from the file
         * and writes the data to the socket.Uses byte array to read data
         * and write data the progress bar is updated accordingly to let the user
         * know the upload progress after the file is finished uploading
         * the file input stream closed and a socket stream is flushed then closed
         * the socket is closed right after that as well as serverSocket
         */
        @Override
        public void run() {
            try {
                while ((bytesRead = fis.read(array, 0, 4000)) > 0) {
                    os.write(array, 0, bytesRead);
                    intRead += bytesRead;
                    pBarFileTransferInfo.setValue(intRead);
                }
                client.appendString("\nFile Transfer Completed............", new SimpleAttributeSet(), Color.RED);
                Toolkit.getDefaultToolkit().beep();
                os.flush();
                os.close();
                fis.close();
                socket.close();
                serverSocket.close();
            } catch (Exception e) {
                client.appendString("\n" + e.toString(), new SimpleAttributeSet(), Color.RED);
            }
        }
    }

    /**
     * The PublicFileINTransfer class is used for the client side
     * that receives data and writes it to the file. The instance of this class
     * has its own thread so does not interfere with the chat or other stuff you might be doing
     * with the application. All files are downloaded to a default folder called downloads
     * @author Dimitri Pankov
     * @see Thread
     * @version 1.4
     */
    public class PublicFileINTransfer extends Thread {

        private int bytesRead;
        private byte[] array;
        private int intRead;

        /**
         * Empty constructor of the class creates needed objects
         * for downloading the file. If downloads directory does not exist it creates one otherwise
         * it skips that line the socket input stream and file output stream are created then the byte array
         * that we use to upload the data then the progress bar is set to the maximum value which is file size
         * and that value is 100% as we receive data we update the progress bar with received data it does the math for us
         * and tells the user how much percent is downloaded
         * @throws IOException when working with stream and sockets risks are unavoidable
         */
        public PublicFileINTransfer() throws IOException {
            success = new File("downloads/").mkdir();
            is = socket.getInputStream();
            fos = new FileOutputStream(new File("downloads/" + filePath));
            array = new byte[4000];
            pBarFileTransferInfo.setMaximum(fileSize);
        }

        /**
         * The run method of the class only reads the from the socket
         * and writes the data to the file.Uses byte array to read data
         * and write data the progress bar is updated accordingly to let the user
         * know the download progress after the file is finished downloading
         * the file output stream is flushed then closed in which a socket stream is closed
         * the socket is closed right after that
         */
        @Override
        public void run() {
            try {
                while ((bytesRead = is.read(array, 0, 4000)) > 0) {
                    fos.write(array, 0, bytesRead);
                    intRead += bytesRead;
                    pBarFileTransferInfo.setValue(intRead);
                }
                client.appendString("\nFile Transfer Completed............", new SimpleAttributeSet(), Color.RED);
                Toolkit.getDefaultToolkit().beep();
                fos.flush();
                fos.close();
                is.close();
                socket.close();
            } catch (Exception e) {
                client.appendString("\n" + e.toString(), new SimpleAttributeSet(), Color.RED);
            }
        }
    }

    /**
     * The method acceptFile is called when the user clicks accepts the file
     * download the socket will be created with the IPAddress of the client that wants to send
     * he already has  a ServerSocket object that is waiting for the connection when the connection
     * is established transfer begins right away
     */
    public void acceptFile() {
        try {
            socket = new Socket(ipAddress, 11111);
            client.appendString("\nReceiving File............", new SimpleAttributeSet(), Color.RED);
            new PublicFileINTransfer().start();
        } catch (Exception e) {
            client.appendString("\n" + e.toString(), new SimpleAttributeSet(), Color.RED);
        }
    }

    /**
     * The method declineFile is called when the user declines the file
     * download. It is done by sending a message to the client that wants to send the file
     * to this client and tell him that the transfer was refused that will in turn close his socket
     */
    public void declineFile() {
        try {
            Packet packet = new Packet();
            packet.setFileTransferDecline("decline");
            packet.setClientIndex(client.getUserNames().indexOf(client.getGuestName()));
            client.getOutputStream().writeObject(packet);
            client.getOutputStream().flush();
        } catch (Exception e) {
            client.appendString("\n" + e.toString(), new SimpleAttributeSet(), Color.RED);
        }
    }

    /**
     * The method promptUserForDownload asks the user whether he wants
     * to download a file or not. It is done by inserting a JPanel with two buttons
     * into the TextPane user clicks either accept button or decline button
     */
    public void promptUserForDownload() {
        publicFileTransfer = new PromptPublicFileTransfer();
        client.getTextPane().insertComponent(publicFileTransfer);
    }

    /**
     * The method getServerSocket returns ServerSocket to the caller
     * @return serverSocket as a ServerSocket
     */
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    /**
     * The PromptPublicFileTransfer class is the panel with two buttons that are added
     * to the for confirming the file transfer this class is purely GUI
     * @author Dimitri Pankov
     * @see CostumPanel
     * @version 1.1
     */
    public class PromptPublicFileTransfer extends CostumPanel {

        private JLabel lblPromptUser;
        private JPanel pnlCenter, pnlSouth;

        /**
         * Empty constructor of the class
         * Has all needed GUI to represent itself graphically
         * on the screen it also has two buttons Accept and Decline
         * if the client click accept it connects directly to the server of the other client
         * and starts the reading thread with the specified file in the specified directory
         * if the client clicks decline the message is sent back to the client that wants to send the
         * the file to tell him of the disapproval and close its serverSocket
         */
        public PromptPublicFileTransfer() {
            super(new BorderLayout());
            pnlSouth = new JPanel(new FlowLayout(FlowLayout.LEFT));
            pnlSouth.setOpaque(false);
            pnlCenter = new JPanel(new FlowLayout(FlowLayout.LEFT));
            pnlCenter.setOpaque(false);
            btnAccept = new JButton("Accept");
            btnAccept.setPreferredSize(new Dimension(74, 25));
            btnAccept.addActionListener(new ActionListener() {

                /**
                 * The actionPerformed method is called whenever the listener detects action
                 * @param e ActionEvent object that is generated when button is clicked
                 */
                public void actionPerformed(ActionEvent e) {
                    try {
                        PublicFileTransfer.this.acceptFile();
                        btnAccept.setEnabled(false);
                        btnDecline.setEnabled(false);
                    } catch (Exception ex) {
                        client.appendString(ex.toString(), new SimpleAttributeSet(), Color.RED);
                    }
                }
            });
            btnDecline = new JButton("Decline");
            btnDecline.setPreferredSize(new Dimension(77, 25));
            btnDecline.addActionListener(new ActionListener() {

                /**
                 * The actionPerformed method is called whenever the listener detects action
                 * @param e ActionEvent object that is generated when button is clicked
                 */
                public void actionPerformed(ActionEvent e) {
                    try {
                        PublicFileTransfer.this.declineFile();
                        btnAccept.setEnabled(false);
                        btnDecline.setEnabled(false);
                        client.appendString("\nTransfer canceled by the user", new SimpleAttributeSet(), Color.RED);
                    } catch (Exception ex) {
                        client.appendString(ex.toString(), new SimpleAttributeSet(), Color.RED);
                    }
                }
            });
            client.appendString("\n" + client.getGuestName() + " wants to send you a file :\n", new SimpleAttributeSet(), Color.BLACK);
            lblPromptUser = new JLabel(filePath, JLabel.LEFT);
            lblPromptUser.setForeground(Color.BLACK);
            lblPromptUser.setFont(new Font("Veradana", Font.PLAIN, 16));

            lblProgress = new JLabel("Download Progress", SwingUtilities.CENTER);
            lblProgress.setForeground(Color.WHITE);
            lblProgress.setFont(new Font("Verdana", Font.BOLD, 18));

            pnlCenter.add(lblPromptUser);
            pnlCenter.add(btnAccept);
            pnlCenter.add(btnDecline);

            pnlSouth.add(lblProgress);
            pnlSouth.add(pBarFileTransferInfo);

            this.add(pnlCenter, BorderLayout.CENTER);
            this.add(pnlSouth, BorderLayout.SOUTH);

            this.setOpaque(false);
        }
    }
}
