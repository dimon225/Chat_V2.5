/* Description: FileTransferPanel.java
 * Author: Dimitri Pankov
 * Date: 16-Feb-2011
 * Version: 1.0
 */
package ChatProject;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.text.SimpleAttributeSet;

/**
 * The FileTransferPanel is the panel on the bottom right corner of the
 * private chat window it basically needed to transfer files back and forth between clients
 * if one of the clients wants to send a file he/she must click on the send file button which in turn
 * pops up the JFileChooser the client selects the file he/she wants to send then clicks on the approve button
 * of the file chooser that will send a message to the other guy for approval if he wants to get file he clicks yes
 * otherwise he clicks no if yes is clicked the IP address of the client that is sending is extracted from the sent object
 * creates a socket and connects to already listening server which is the client that is sending the file if no is clicked the message indicating
 * of the disapproval is sent back to the client that sending the file in turn his socket will be closed and he will receive notification
 * that his file was refused for downloading
 * @author Dimitri Pankov
 * @see CostumPanel
 * @see JProgressBar
 * @version 1.2
 */
public class FileTransferPanel extends CostumPanel {

    private JProgressBar pBarFileTransferInfo;
    private JPanel pnlTransfer, pnlClear, pnlProgress, pnlPercent;
    private JButton transferButton, btnCancelTransfer, btnClear;
    private Socket socket;
    private ServerSocket serverSocket;
    private FileOutputStream fos;
    private FileInputStream fis;
    private OutputStream out;
    private InputStream in;
    private PrivateChatWindow chatWindow;
    private Color color = Color.RED;
    private SimpleAttributeSet attribute = new SimpleAttributeSet();
    private TransferINThread inThread;
    private TransferOUTThread outThread;
    private Client client;
    private boolean success;
    private JLabel lblPercent;
    private InetAddress ipAddress;
    private PromptFileTransfer transfer;
    private JButton btnAccept, btnDecline;
    private String filePath;
    private int fileSize;
    private byte[] array;
    private int intRead;

    /**
     * Overloaded Constructor of the class receives two references in the constructor
     * Client object and the PrivateChatWindow object for accessing methods of both classes
     * at runtime
     * @param chatWindow as a PrivateChatWindow object
     * @param client as a Client object
     */
    public FileTransferPanel(final PrivateChatWindow chatWindow, final Client client) {
        super("/Icons/backg.jpg", new GridLayout(4, 1));
        this.chatWindow = chatWindow;
        this.client = client;
        pnlPercent = new JPanel();
        pnlPercent.setOpaque(false);
        lblPercent = new JLabel("Transfer Progress!", SwingUtilities.CENTER);
        pnlPercent.add(lblPercent);
        lblPercent.setFont(new Font("Veradna", Font.PLAIN, 20));
        pnlTransfer = new JPanel();
        transferButton = new JButton("Send file");
        transferButton.addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when button is clicked
             */
            public void actionPerformed(ActionEvent e) {
                try {
                    if (chatWindow.getOutputStream() != null) {

                        //THE THREAD FOR LISTENING TO THE CONNECTIONS
                        new Thread(new Runnable() {

                            public void run() {
                                try {
                                    FileTransferPanel.this.connectAsServer();
                                } catch (SocketException se) {
                                    FileTransferPanel.this.chatWindow.appendString("\n" + chatWindow.getGuestName() + " has declined to receive your file", attribute, color);
                                } catch (Exception ex) {
                                    FileTransferPanel.this.chatWindow.appendString(ex.toString(), attribute, color);
                                }
                            }
                        }).start();
                    }

                } catch (Exception ex) {
                    FileTransferPanel.this.chatWindow.appendString(ex.toString(), attribute, color);
                }
            }
        });
        btnCancelTransfer = new JButton("Cancel Transfer");
        btnCancelTransfer.addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when button is clicked
             */
            public void actionPerformed(ActionEvent e) {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (Exception ex) {
                        FileTransferPanel.this.chatWindow.appendString(ex.toString(), attribute, color);
                    }
                }
            }
        });
        pnlTransfer.add(transferButton);
        pnlTransfer.add(btnCancelTransfer);
        pnlTransfer.setOpaque(false);

        //CREATE JPANEL AND INITIALIZE IT
        pnlClear = new JPanel();
        btnClear = new JButton("Clear Text Pane");
        btnClear.addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when button is clicked
             */
            public void actionPerformed(ActionEvent e) {
                chatWindow.getTextPane().setText("");
            }
        });
        pnlClear.setOpaque(false);
        pnlClear.add(btnClear);

        //CREATE JPANEL AND ADD JPROGRESS BAR TO IT
        pnlProgress = new JPanel();
        pBarFileTransferInfo = new JProgressBar();
        pBarFileTransferInfo.setStringPainted(true);
        pBarFileTransferInfo.setFont(new Font("Veradna", Font.BOLD, 18));
        pnlProgress.add(pBarFileTransferInfo);
        pnlProgress.setOpaque(false);

        //ADD ALL THE COMPONENTS TO THIS OBJECT WHICH IS A COSTUMPANEL
        this.add(pnlTransfer);
        this.add(pnlClear);
        this.add(pnlPercent);
        this.add(pnlProgress);

    }

    /**
     * The TransferOUTThread class is a simple reader object
     * it reads from the stream and in turn writes to the socket
     * simultaneously this object is the Thread object when the start method is called on
     * this object the run method is executed immediately it reads until the end of file is reached
     * -1 will indicate the end of file at the same time it increments the ProgressBar value
     * that is calculated at runtime it tells the user how much percent were uploaded
     * @author Dimitri Pankov
     * @see Thread
     * @version 1.1
     */
    public class TransferOUTThread extends Thread {

        private int intRead;
        private File file;
        private int bytesRead;

        /**
         * Overloaded constructor receives a file to write to the socket
         * creates both input and output streams for uploading the file
         * @param file as a File
         * @throws IOException when working with streams and sockets risks are unavoidable
         */
        public TransferOUTThread(File file) throws IOException {
            this.file = file;
            out = socket.getOutputStream();
            fis = new FileInputStream(file);
            array = new byte[4000];
            pBarFileTransferInfo.setMaximum((int) file.length());
            pBarFileTransferInfo.setValue(intRead);
        }

        /**
         * The rum method of the TransferOUTThread reads byte by byte
         * and simultaneously writes them to the socket
         * it is also calculates the file transfer percentage rate and displays the value in the
         * progress bar as well as the JLabel after the upload is done
         * it flushes and closes the output streams then closes the input stream
         * and that will in turn enable the transfer button
         */
        @Override
        public void run() {
            try {

                while ((bytesRead = fis.read(array, 0, 4000)) > 0) {
                    FileTransferPanel.this.calculateFileTransferPercentageRate(bytesRead);
                    out.write(array, 0, bytesRead);
                }
                Toolkit.getDefaultToolkit().beep();
                out.flush();
                out.close();
                fis.close();
                serverSocket.close();
                transferButton.setEnabled(true);
                FileTransferPanel.this.chatWindow.appendString("\nFile Transfer Completed.....", attribute, color);
            } catch (SocketException e) {
                FileTransferPanel.this.cancelServerSideTransfer();
            } catch (Exception e) {
                FileTransferPanel.this.chatWindow.appendString(e.toString(), attribute, color);
            }
        }
    }

    /**
     * The TransferINThread class is a simple writer object
     * it writes to the file and by reading from the socket
     * simultaneously this object is the Thread object when the start method is called on
     * this object the run method is executed immediately it reads until the end of file is reached
     * -1 will indicate the end of file at the same time it increments the ProgressBar value
     * that is calculated at runtime it tells the user how much percent were downloaded
     * @author Dimitri Pankov
     * @see Thread
     * @version 1.1
     */
    public class TransferINThread extends Thread {

        private int intRead;
        private File file;
        private int bytesRead;

        /**
         * Overloaded constructor receives a file to read from the socket
         * creates both input and output streams for downloading the file
         * @param file as a File
         * @throws IOException when working with streams and sockets risks are unavoidable
         */
        public TransferINThread(File file) throws IOException {
            this.file = file;
            in = socket.getInputStream();
            fos = new FileOutputStream(file);
            array = new byte[4000];
            pBarFileTransferInfo.setMaximum(fileSize);
            pBarFileTransferInfo.setValue(intRead);
        }

        /**
         * The rum method of the TransferINThread reads byte by byte
         * and simultaneously writes them to the file
         * it is also calculates the file transfer percentage rate and displays the value in the
         * progress bar as well as the JLabel after the download is done
         * it flushes and closes the output streams then closes the input stream
         * and that will in turn enable the transfer button and also removes the approve panel
         */
        @Override
        public void run() {
            try {
                while ((bytesRead = in.read(array, 0, 4000)) > 0) {
                    FileTransferPanel.this.calculateFileTransferPercentageRate(bytesRead);
                    fos.write(array, 0, bytesRead);
                }
                Toolkit.getDefaultToolkit().beep();
                fos.flush();
                fos.close();
                in.close();
                socket.close();
                transferButton.setEnabled(true);
                FileTransferPanel.this.chatWindow.appendString("\nFile Transfer Completed.....", attribute, color);
            } catch (SocketException e) {
                FileTransferPanel.this.cancelClientSideTransfer();
                FileTransferPanel.this.chatWindow.appendString("\nThe file transfer cancelled by the user", attribute, color);
            } catch (Exception e) {
                FileTransferPanel.this.chatWindow.appendString(e.toString(), attribute, color);
            }
        }
    }

    /**
     * The method calculateFileTransferPercentageRate calculates at runtime how
     * much was downloaded or uploaded by the user for having some sort of feedback
     * bytesRead is a length of the array that was read which represents a part of the file
     * the length is usually 4000 by when it gets to the last one it might be less
     * @param bytesRead as an integer length of the array that was read
     */
    public void calculateFileTransferPercentageRate(int bytesRead) {
        intRead += bytesRead;
        pBarFileTransferInfo.setValue(intRead);
    }

    /**
     * The method connectAsServer it called when user clicks send file
     * This will pop a JFileChooser the user will choose the file to send
     * the message will be sent to the other client asking for approval then it starts to listen
     * for the connections and the the other client if accepts the file will extract the IP and connect
     * to already listening server then it will start the thread and begin uploading file right away
     * @throws IOException when working with streams risks are unavoidable
     */
    public void connectAsServer() throws IOException {
        JFileChooser chooser = null;
        chooser = new JFileChooser(".");
        File file1 = null;
        int value = 0;
        if ((value = chooser.showDialog(chatWindow, "Choose File")) == JFileChooser.APPROVE_OPTION) {
            file1 = chooser.getSelectedFile();
            Packet packet = new Packet();
            packet.setTransferFile(client.getList().getSelectedIndex());
            packet.setName(chatWindow.getClientName());
            packet.setFilePath(file1.getName());
            packet.setFileSize(file1.length());
            chatWindow.getOutputStream().writeObject(packet);
            chatWindow.getOutputStream().flush();
            serverSocket = new ServerSocket(7777);
            socket = serverSocket.accept();
            FileTransferPanel.this.chatWindow.appendString("\nSending File............", attribute, color);
            outThread = new TransferOUTThread(file1);
            outThread.start();
            transferButton.setEnabled(false);
        }
    }

    /**
     * The method connectAsClient simply connects as a client this mean
     * that this guy will be receiving the file the extracted IP is passed
     * as a parameter to this method then the user is asked whether he wants to
     * download this file or not the rest of the connection is made in the
     * in the PromptFileTransfer after receiving the IP address
     * @param ipAddress as an InetAddress
     */
    public void connectAsClient(InetAddress ipAddress) {
        success = new File("downloads/").mkdir();
        this.ipAddress = ipAddress;
        transfer = new PromptFileTransfer();
        chatWindow.getTextPane().insertComponent(transfer);
        chatWindow.getTextPane().revalidate();
        chatWindow.repaint();
    }

    /**
     * The method getServerSocket simply returns the serverSocket to the caller
     * @return serverSocket as a ServerSocket
     */
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    /**
     * The method cancelServerSideTransfer is called when the cancel transfer button is clicked
     * Basically this method is needed to cancel the transfer if the client so chooses
     * First it send the notification to the client side connection to let him know that the transfer
     * is canceled so he as well will be able to close its socket and streams
     */
    public void cancelServerSideTransfer() {
        FileTransferPanel.this.chatWindow.appendString("\nThe file transfer cancelled by the user", attribute, color);
        Packet packet = new Packet();
        packet.setCancelTransfer("cancel");
        try {
            chatWindow.getOutputStream().writeObject(packet);
            chatWindow.getOutputStream().flush();
            out.close();
            fis.close();
            serverSocket.close();
            transferButton.setEnabled(true);
            pBarFileTransferInfo.setValue(0);
        } catch (Exception ex) {
            FileTransferPanel.this.chatWindow.appendString(ex.toString(), attribute, color);
        }
    }

    /**
     * The method getSocket simply returns the socket to the caller
     * @return socket as a Socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * The method cancelClientSideTransfer is needed to cancel the transfer
     * it just closes its socket and both streams as well
     */
    public void cancelClientSideTransfer() {
        try {
            fos.close();
            in.close();
            socket.close();
            transferButton.setEnabled(true);
            pBarFileTransferInfo.setValue(0);
        } catch (Exception e) {
            FileTransferPanel.this.chatWindow.appendString(e.toString(), attribute, color);
        }
    }

    /**
     * The PromptFileTransfer class is the panel with two buttons that are added
     * to the for confirming the file transfer this class is purely GUI
     * @author Dimitri Pankov
     * @see CostumPanel
     * @version 1.1
     */
    public class PromptFileTransfer extends CostumPanel {

        private JLabel lblPromptUser;

        /**
         * Empty constructor of the class
         * Has all needed GUI to represent itself graphically
         * on the screen it also has two buttons Accept and Decline
         * if the client click accept it connects directly to the server of the other client
         * and starts the reading thread with the specified file in the specified directory
         * if the client clicks decline the message is sent back to the client that wants to send the
         * the file to tell him of the disapproval and close its serverSocket
         */
        public PromptFileTransfer() {
            this.setLayout(new FlowLayout(FlowLayout.LEFT));
            btnAccept = new JButton("Accept");
            btnAccept.setPreferredSize(new Dimension(74, 25));
            btnAccept.addActionListener(new ActionListener() {

                /**
                 * The actionPerformed method is called whenever the listener detects action
                 * @param e ActionEvent object that is generated when button is clicked
                 */
                public void actionPerformed(ActionEvent e) {
                    try {
                        socket = new Socket(chatWindow.getIpAddress(), 7777);
                        FileTransferPanel.this.chatWindow.appendString("\nReceiving File............", attribute, color);
                        filePath = chatWindow.getFilePath();
                        fileSize = chatWindow.getFileSize();
                        inThread = new TransferINThread(new File("downloads/" + chatWindow.getFilePath()));
                        inThread.start();
                        transferButton.setEnabled(false);
                        btnAccept.setEnabled(false);
                        btnDecline.setEnabled(false);
                    } catch (Exception ex) {
                        FileTransferPanel.this.chatWindow.appendString(ex.toString(), attribute, color);
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
                        Packet packet = new Packet();
                        packet.setFileTransferDecline("decline");
                        chatWindow.getOutputStream().writeObject(packet);
                        chatWindow.getOutputStream().flush();
                        btnAccept.setEnabled(false);
                        btnDecline.setEnabled(false);
                    } catch (Exception ex) {
                        FileTransferPanel.this.chatWindow.appendString(ex.toString(), attribute, color);
                    }
                }
            });
            FileTransferPanel.this.chatWindow.appendString("\n" + chatWindow.getGuestName() + " wants to send you a file :\n", attribute, Color.BLACK);

            //CREATE AND INITIALIZE JLABEL
            lblPromptUser = new JLabel(chatWindow.getFilePath(), JLabel.LEFT);
            lblPromptUser.setForeground(Color.BLACK);
            lblPromptUser.setFont(new Font("Veradana", Font.PLAIN, 16));

            //ADD ALL COMPONENTS TO THE PANEL
            this.add(lblPromptUser);
            this.add(btnAccept);
            this.add(btnDecline);
            this.setOpaque(false);
        }
    }
}
