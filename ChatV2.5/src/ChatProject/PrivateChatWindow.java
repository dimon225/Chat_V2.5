/* Description: PrivateChatWindow.java
 * Author: Dimitri Pankov
 * Date: 15-Feb-2011
 * Version: 1.0
 */
package ChatProject;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * The PrivateChatWindow class is private chat window that pops up if the
 * user wants to talk privately to another user when the window is running my private chat is really private
 * the button start private chat should be clicked it sends a message to the server
 * to get the IP address of the user that you chose to privately chat with
 * The server has a map that maps the names to the IP addresses the IP address is extracted then
 * it is forwarded to the client that you invite in the private chat that IP in turn will be used
 * to create a socket with the IP received to connect to the private chat of the guy that invited the client
 * to private chat because as soon as the button start private chat is hit the inviter starts to listen to
 * connections in turn its name is forwarded to the server which extracts the IP sends it the other guy
 * which creates a socket and connects to already listening server of the private chat
 * @author Dimitri Pankov
 * @see Socket
 * @see ServerSocket
 * @version 1.2
 */
public class PrivateChatWindow extends JFrame {

    private JTextField txtSend;
    private JButton btnSend, btnStartChat;
    private JScrollPane scrollPane;
    private JTextPane txtPane;
    private JPanel eastPanel;
    private CostumPanel east;
    private SimpleAttributeSet localAttribute = new SimpleAttributeSet();
    private SimpleAttributeSet exceptionAttribute = new SimpleAttributeSet();
    private SimpleAttributeSet sendAttribute = new SimpleAttributeSet();
    private Client client;
    private JScrollPane emoticonsPanelScroll;
    private JMenuBar mBar;
    private JMenu menuFile, menuFont;
    private JMenu menuColor;
    private Color color = Color.RED;
    private JMenu menuHelp;
    private JScrollPane scroll;
    private CostumPanel pnlSouth;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Socket socket;
    private InetAddress ipAddress;
    private boolean isServer = true;
    private ServerSocket serverSocket;
    private ClientThread clientThread;
    private String name;
    private String iconPath;
    private String guestName;
    private FileTransferPanel fileTransferPanel;
    private String filePath;
    private String defaultDirectory = "downloads";
    private int fileSize;
    private File file = new File("directory.dat");
    private TitledBorder transferBorder;
    private InetAddress publicIpAddress;

    /**
     * Overloaded constructor of the class receives some needed references in the
     * constructor for communication purposes client object is needed to call its methods
     * InetAddress for creating a private chat client and isServer tells if the instance will server or client
     * This class is used for both server side private chat and the client side private chat
     * This constructor has only GUI stuff Graphical User Interface
     * @param client as a Client object
     * @param ipAddress as a InetAddress
     * @param isServer as a boolean argument
     */
    public PrivateChatWindow(Client client, InetAddress ipAddress, final boolean isServer) {
        this.client = client;
        this.ipAddress = ipAddress;
        this.isServer = isServer;
        mBar = new JMenuBar();
        mBar.setBackground(Color.ORANGE);
        this.setJMenuBar(mBar);
        this.addWindowListener(new WindowAdapter() {

            /**
             * The method windowClosing is needed to do
             * some special things when the user decides to close we need to
             * kill the thread and release resources
             * @param e WindowEvent object that is generated when window is closing
             */
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    clientThread.killThread();
                    socket.close();
                    oos.close();
                    ois.close();
                    if (isServer) {
                        serverSocket.close();
                    }

                } catch (Exception ed) {
                    PrivateChatWindow.this.appendString(ed.toString(), exceptionAttribute, color);
                }
            }
        });
        btnStartChat = new JButton("Start Private Chat");

        //IF NOT SERVER EXECUTE THE IF BLOCK
        if (!isServer) {
            btnStartChat.setEnabled(false);
        }

        btnStartChat.addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when start private chat button is clicked
             */
            public void actionPerformed(ActionEvent e) {
                try {
                    PrivateChatWindow.this.inviteToPrivateChat();
                    if (isServer) {

                        //THREAD THAT WILL LISTEN FOR THE CONNECTION
                        Thread t = new Thread(new Runnable() {

                            public void run() {
                                try {
                                    PrivateChatWindow.this.startServer();
                                } catch (Exception e) {
                                    PrivateChatWindow.this.appendString(e.toString(), exceptionAttribute, color);
                                }
                            }
                        });
                        t.setPriority(Thread.MAX_PRIORITY);
                        t.start();
                    }
                    btnStartChat.setEnabled(false);

                } catch (Exception ef) {
                    PrivateChatWindow.this.appendString(ef.toString(), exceptionAttribute, color);
                }
            }
        });

        txtPane = new JTextPane() {

            /**
             * The method paintComponent is overridden in our anonymous class
             * to paint some images onto swing components
             * @param g Graphics object used to paint this object
             */
            @Override
            public void paintComponent(Graphics g) {
                URL url = this.getClass().getResource("/Icons/backg.jpg");
                Toolkit toolkit = this.getToolkit();
                Image image = toolkit.getImage(url);
                g.drawImage(image, 0, 0, txtPane.getWidth(), txtPane.getHeight(), txtPane);
                super.paintComponent(g);
            }
        };

        //INIITALIZE TEXT PANE
        txtPane.setOpaque(false);
        txtPane.setEditable(false);
        TitledBorder border = new TitledBorder("Private Chat");
        border.setTitleFont(new Font("Serif", Font.PLAIN, 20));
        border.setTitleColor(Color.WHITE);
        txtPane.setBorder(border);
        txtPane.setFont(new Font("Serif", Font.PLAIN, 20));

        //SET THE CARET UPDATE POLICY
        DefaultCaret caret = (DefaultCaret) txtPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        //IF NOT SERVER EXECUTE THE IF BLOCK
        if (!isServer) {
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(ipAddress, 6666));
                clientThread = new ClientThread();
                clientThread.start();
                PrivateChatWindow.this.appendString("You been invited to Private chat ", exceptionAttribute, color);
            } catch (ConnectException ex) {
                PrivateChatWindow.this.appendString("\nClient is not responding", exceptionAttribute, color);
            } catch (IOException ex) {
                PrivateChatWindow.this.appendString(ex.toString(), exceptionAttribute, color);
            }
        }

        StyleConstants.setBold(sendAttribute, true);

        //CREATE AND INITIALIZE THE JFRAME'S PROPERTIES
        fileTransferPanel = new FileTransferPanel(this, client);
        transferBorder = new TitledBorder("File Transfer Panel");
        transferBorder.setTitleColor(Color.WHITE);
        transferBorder.setTitleFont(new Font("Serif", Font.PLAIN, 18));
        fileTransferPanel.setBorder(transferBorder);
        scrollPane = new JScrollPane(fileTransferPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        //CREATE JPANE SET ITS BORDER AND INITIALIZE
        eastPanel = new JPanel(new BorderLayout());
        east = new CostumPanel("/Icons/backg.jpg", new GridLayout(6, 4));
        TitledBorder emoticonsBorder = new TitledBorder("Choose Emoticons");
        emoticonsBorder.setTitleFont(new Font("Serif", Font.PLAIN, 19));
        emoticonsBorder.setTitleColor(Color.WHITE);
        east.setBorder(emoticonsBorder);

        //ADD SMILEYS TO THE EAST PANEL
        for (int i = 0; i < 24; i++) {
            east.add(new Smiley(this, "/smileys/smiley" + (i + 1) + ".gif"));
        }

        emoticonsPanelScroll = new JScrollPane(east, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        eastPanel.add(emoticonsPanelScroll, BorderLayout.CENTER);
        eastPanel.add(scrollPane, BorderLayout.SOUTH);

        menuFile = new JMenu("File");
        menuFile.add(new JMenuItem("Save as..")).addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when menu item is clicked
             */
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser(".");
                int value = chooser.showSaveDialog(PrivateChatWindow.this);
                if (value == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    PrivateChatWindow.this.saveConversation(file);
                }
            }
        });
        menuFile.add(new JMenuItem("Exit")).addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when menu item is clicked
             */
            public void actionPerformed(ActionEvent e) {
                try {
                    clientThread.killThread();
                    socket.close();
                    oos.close();
                    ois.close();
                    if (isServer) {
                        serverSocket.close();
                    }
                } catch (Exception ed) {
                    PrivateChatWindow.this.appendString(ed.toString(), exceptionAttribute, color);
                }
            }
        });


        menuFont = new JMenu("Font");
        menuFont.add(new JMenuItem("Italic")).addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when menu item is clicked
             */
            public void actionPerformed(ActionEvent e) {
                localAttribute = new SimpleAttributeSet();
                StyleConstants.setItalic(localAttribute, true);
                sendAttribute = localAttribute;
            }
        });
        menuFont.add(new JMenuItem("Plain")).addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when menu item is clicked
             */
            public void actionPerformed(ActionEvent e) {
                localAttribute = new SimpleAttributeSet();
                sendAttribute = localAttribute;
            }
        });
        menuFont.add(new JMenuItem("Bold")).addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when menu item is clicked
             */
            public void actionPerformed(ActionEvent e) {
                localAttribute = new SimpleAttributeSet();
                StyleConstants.setBold(localAttribute, true);
                StyleConstants.setForeground(localAttribute, color);
                sendAttribute = localAttribute;
            }
        });
        menuColor = new JMenu("Color");
        menuColor.add(new JMenuItem("Magenta")).addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when menu item is clicked
             */
            public void actionPerformed(ActionEvent e) {
                localAttribute = new SimpleAttributeSet();
                color = Color.MAGENTA;
                StyleConstants.setForeground(localAttribute, color);
                sendAttribute = localAttribute;
            }
        });
        menuColor.add(new JMenuItem("Red")).addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when menu item is clicked
             */
            public void actionPerformed(ActionEvent e) {
                localAttribute = new SimpleAttributeSet();
                color = Color.RED;
                StyleConstants.setForeground(localAttribute, color);
                sendAttribute = localAttribute;
            }
        });
        menuColor.add(new JMenuItem("Blue")).addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when menu item is clicked
             */
            public void actionPerformed(ActionEvent e) {
                localAttribute = new SimpleAttributeSet();
                color = Color.BLUE;
                StyleConstants.setForeground(localAttribute, color);
                sendAttribute = localAttribute;
            }
        });
        menuColor.add(new JMenuItem("Orange")).addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when menu item is clicked
             */
            public void actionPerformed(ActionEvent e) {
                localAttribute = new SimpleAttributeSet();
                color = Color.ORANGE;
                StyleConstants.setForeground(localAttribute, color);
                sendAttribute = localAttribute;
            }
        });
        menuColor.add(new JMenuItem("Choose color...")).addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when menu item is clicked
             */
            public void actionPerformed(ActionEvent e) {
                localAttribute = new SimpleAttributeSet();
                color = JColorChooser.showDialog(PrivateChatWindow.this, "Choose Color", Color.CYAN);
                if (color != null) {
                    StyleConstants.setForeground(localAttribute, color);
                } else {
                    color = Color.CYAN;
                }
                sendAttribute = localAttribute;
            }
        });

        menuHelp = new JMenu("Help");
        menuHelp.add(new JMenuItem("User Manual")).addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when menu item is clicked is clicked
             */
            public void actionPerformed(ActionEvent e) {
                new UserManual(PrivateChatWindow.this);
            }
        });

        //ADD ALL MENU TO THE MENU BAR
        mBar.add(menuFile);
        mBar.add(menuFont);
        mBar.add(menuColor);
        mBar.add(menuHelp);

        scroll = new JScrollPane(txtPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        txtSend = new JTextField(12);

        btnSend = new JButton("Send");
        btnSend.addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when send button is clicked
             */
            public void actionPerformed(ActionEvent e) {
                String message = "";
                if (!txtSend.getText().trim().equals("")) {
                    if (txtSend.getText().indexOf("~") >= 0) {
                        message = txtSend.getText().substring(0, txtSend.getText().indexOf("~"));
                        message += txtSend.getText().substring(txtSend.getText().indexOf("~") + 1);
                    }
                    PrivateChatWindow.this.sendPrivateMessage(message.trim());
                    PrivateChatWindow.this.appendString("\n" + PrivateChatWindow.this.client.getClientName() + ": " + message.trim(), sendAttribute, color);
                    txtSend.setText("");
                    if (iconPath != null) {
                        txtPane.insertIcon(new ImageIcon(this.getClass().getResource(iconPath)));
                    }
                    iconPath = null;
                }
            }
        });

        //ADD ALL THE COMPONENTS TO THE SOUTH PANEL
        pnlSouth = new CostumPanel("/Icons/backg.jpg");
        pnlSouth.setOpaque(false);
        pnlSouth.add(txtSend);
        pnlSouth.add(btnSend);
        pnlSouth.add(btnStartChat);
        txtSend.addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when enter is hit
             */
            public void actionPerformed(ActionEvent e) {
                String message = "";
                if (!txtSend.getText().trim().equals("")) {
                    if (txtSend.getText().indexOf("~") >= 0) {
                        message = txtSend.getText().substring(0, txtSend.getText().indexOf("~"));
                        message += txtSend.getText().substring(txtSend.getText().indexOf("~") + 1);
                    }
                    PrivateChatWindow.this.sendPrivateMessage(message.trim());
                    PrivateChatWindow.this.appendString("\n" + PrivateChatWindow.this.client.getClientName() + ": " + message.trim(), sendAttribute, color);
                    if (iconPath != null) {
                        txtPane.insertIcon(new ImageIcon(this.getClass().getResource(iconPath)));
                    }
                    iconPath = null;
                    txtSend.setText("");
                }
            }
        });

        //ADD ALL THE COMPONENTS TO THE CONTAINER
        this.getContentPane().add(scroll);
        this.getContentPane().add(pnlSouth, BorderLayout.SOUTH);
        this.getContentPane().add(eastPanel, BorderLayout.EAST);

        //INITIALIZE JFRAME'S COMPONENTS
        this.setTitle("Private Chat - " + client.getClientName());
        this.setIconImage(new ImageIcon(this.getClass().getResource("/Icons/client.gif")).getImage());
        this.setSize(650, 486);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(client);
        this.setVisible(true);
    }

    /**
     *The method appendString simply appends the String to the textPane
     * The way it does is pretty much straight forward it inserts
     * the String into the Document which in turn displays it on the Pane
     * I also used StyleConstants for color Font and etc.......
     * The menu items of the private chat window have font choose color chooser
     * when it appends it check the Attributes they can either be default like plain and black
     * or you can change them the way you want
     * @param srtingToBeAppended as a String
     * @param attributes as a SimpleAttributeSet which is a cool feature of the JTextPane no HTML required to costumize the text
     * @param color as a Color
     */
    public void appendString(String srtingToBeAppended, SimpleAttributeSet attributes, Color color) {
        Document document = txtPane.getDocument();
        try {
            StyleConstants.setForeground(attributes, color);
            document.insertString(document.getLength(), srtingToBeAppended, attributes);
        } catch (BadLocationException ex) {
            PrivateChatWindow.this.appendString(ex.toString(), exceptionAttribute, color);
        }
    }

    /**
     * The method getIconPath simply returns iconPath to the caller
     * @return iconPath as a String
     */
    public String getIconPath() {
        return iconPath;
    }

    /**
     * The method setIconPath simply sets the path to the icon
     * @param iconPath as a String
     */
    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    /**
     * The ClientThread class is a simple reader
     * inside the loop of the run method in the different thread it reads the ObjectInputStream and
     * forwards it to the method called analyzeAndDisplay which in turn analyzes and does what is needed
     * depending on the objects values this class is a thread so when it reads it does not bother anyone
     * @author Dimitri Pankov
     * @see Thread
     */
    public class ClientThread extends Thread {

        private volatile boolean isAlive = true;

        /**
         * Empty constructor of the ClientThread class
         * only creates both streams for communication purposes
         * @throws IOException when working with streams or sockets risks are unavoidable
         */
        public ClientThread() throws IOException {
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(socket.getInputStream());
        }

        /**
         *This run method only reads and then forwards the object to
         * the analyze and display method the run method is executed until
         * the Thread on this object is killed by changing isAlive to false
         */
        @Override
        public void run() {
            try {
                Packet packet = new Packet();
                if (publicIpAddress == null) {
                    packet.setIpAddress(socket.getLocalAddress());
                } else {
                    packet.setIpAddress(publicIpAddress);
                }
                oos.writeObject(packet);
                oos.flush();
                while (isAlive) {
                    Object object = ois.readObject();
                    PrivateChatWindow.this.analyzeAndDisplay(object);
                }
            } catch (EOFException e) {
                PrivateChatWindow.this.appendString("\n" + guestName + " has left the private chat", exceptionAttribute, color);
            } catch (Exception ex) {
                PrivateChatWindow.this.appendString(ex.toString(), exceptionAttribute, color);
            }
        }

        /**
         * The method killThread kills the thread
         * on this object simply by changing the boolean value to false
         */
        public void killThread() {
            isAlive = false;
        }
    }

    /**
     * The analyzeAndDisplay method simply receives an object as the parameter then analyzes it
     * then does what ever is needed depending on the object contents
     * Instead using strings with protocol its much easier to send whole objects and extract
     * the instance variables accordingly and do what is necessary depending on what variable is not null
     * @param object to analyze and extract the data from
     */
    public void analyzeAndDisplay(Object object) {
        if (object instanceof Packet) {
            if (((Packet) object).getPrivateMessage() != null) {
                this.appendString("\n" + ((Packet) object).getName() + ": " + ((Packet) object).getPrivateMessage() + "  ", ((Packet) object).getAttribute(), ((Packet) object).getAttributeColor());
                name = ((Packet) object).getName();
            }
            if (((Packet) object).getIconPath() != null) {
                txtPane.insertIcon(new ImageIcon(this.getClass().getResource(((Packet) object).getIconPath())));
                this.appendString("  ", exceptionAttribute, color);
            }
            if (((Packet) object).getTransferFile() != null) {
                fileSize = (int) (long) ((((Packet) object).getFileSize()));
                filePath = ((Packet) object).getFilePath();
                fileTransferPanel.connectAsClient(ipAddress);
            }
            if (((Packet) object).getIpAddress() != null) {
                ipAddress = ((Packet) object).getIpAddress();
            }
            if (((Packet) object).getCancelTransfer() != null && fileTransferPanel.getSocket() != null) {
                fileTransferPanel.cancelClientSideTransfer();
            }
            if (((Packet) object).getFileTransferDecline() != null) {
                try {
                    fileTransferPanel.getServerSocket().close();
                } catch (Exception e) {
                    this.appendString(e.toString(), exceptionAttribute, color);
                }
            }
        }
    }

    /**
     * The method startServer simply starts the server on port 6666 and after accepting a connection
     * starts the reading Thread then appends the text such as Connection is established
     * and then it is also disables start chat button
     * @throws IOException when working with streams risks are unavoidable
     */
    public void startServer() throws IOException {
        if (isServer) {
            serverSocket = new ServerSocket(6666);
            socket = serverSocket.accept();
            clientThread = new ClientThread();
            clientThread.start();
            PrivateChatWindow.this.appendString("Connection is established ", exceptionAttribute, color);
        }
    }

    /**
     * The method inviteToPrivatChat when the button startPrivateChat is clicked this method is called
     * the job of this method is simply first ask the user whether he is in a public or local network
     * if the client is chatting on the local network the mapped IP address of the client will be forwarded to
     * the client he wants to private chat with and the connection will be established immediately all needed information will
     * be also forwarded to that client but if the client is actually on the public network the IP will be taken off the special
     * web site we use URL object to connect to that web site readLine and that line is the public IP address and that address will be in turn
     * forwarded to the client to establish a connection for the private chat some other information is also sent to the client to use
     * such as name IPAddress etc........
     */
    public void inviteToPrivateChat() {
        try {
            Packet packet = new Packet();
            int returnValue = JOptionPane.showConfirmDialog(PrivateChatWindow.this, "Are You In The Public Network?", "IP Confirmation!", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (returnValue == JOptionPane.YES_OPTION) {
                URL whatismyip = new URL("http://www.whatismyip.com/automation/n09230945.asp");
                BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
                InetAddress publicIP = InetAddress.getByName(in.readLine());
                publicIpAddress = publicIP;
                packet.setName(name);
                packet.setPublicIP(publicIP);
                packet.setClientPositionForPrivateChat(PrivateChatWindow.this.client.getList().getSelectedIndex());
                packet.setCreatePrivateChat(PrivateChatWindow.this.client.getClientName());
                PrivateChatWindow.this.client.getOutputStream().writeObject(packet);
                PrivateChatWindow.this.client.getOutputStream().flush();
            } else {
                packet.setName(name);
                packet.setCreatePrivateChat(PrivateChatWindow.this.client.getClientName());
                packet.setClientPositionForPrivateChat(PrivateChatWindow.this.client.getList().getSelectedIndex());
                PrivateChatWindow.this.client.getOutputStream().writeObject(packet);
                PrivateChatWindow.this.client.getOutputStream().flush();
            }
        } catch (Exception e) {
            this.appendString(e.toString(), exceptionAttribute, color);
        }
    }

    /**
     * The sendPrivateMessage method simply sends a private message to the client
     * First it creates a new Packet object sets needed variables to what is needed
     * then the packet object is sent to the client for analyzing and extracting the message
     * the color of the message the iconPath if any the name of the person that send data a
     * and the SimpleSetAttribute is needed to append to the textPane the way it was typed on the other side
     * @param message
     */
    public void sendPrivateMessage(String message) {
        try {
            Packet packet = new Packet();
            packet.setPrivateMessage(message.trim());
            packet.setName(client.getClientName());
            packet.setIconPath(iconPath);
            packet.setAttribute(sendAttribute);
            packet.setAttributeColor(color);
            oos.writeObject(packet);
            oos.flush();
        } catch (Exception ex) {
            PrivateChatWindow.this.appendString(ex.toString(), exceptionAttribute, color);
        }
    }

    /**
     * The method getTextField simply returns the text field to the caller
     * @return as a JTextField
     */
    public JTextField getTextField() {
        return txtSend;
    }

    /**
     * The method setGuestName simply sets the guest name
     * The guest is the client on the other side of the socket
     * @param guestName as a String
     */
    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    /**
     * The method getOutputStream simply returns ObjectOutputStream
     * to the caller when called upon fro other class
     * @return oos as a ObjectOutputStream
     */
    public ObjectOutputStream getOutputStream() {
        return oos;
    }

    /**
     * The method getGuestName simply returns the guestName
     * @return guestName as a string
     */
    public String getGuestName() {
        return guestName;
    }

    /**
     * The method getClient name returns this client's name
     * @return name as a String
     */
    public String getClientName() {
        return name;
    }

    /**
     * The method getFileTransferPanel returns the fileTransferPanel
     * to the caller
     * @return fileTransferPanel as a FileTransferPanel
     */
    public FileTransferPanel getFileTransferPanel() {
        return fileTransferPanel;
    }

    /**
     * The method getIpAddress simply returns the IP address to the caller
     * @return ipAddress as a InetAddress
     */
    public InetAddress getIpAddress() {
        return ipAddress;
    }

    /**
     * The method getFilePath simply returns the file path to the caller
     * @return filePath as a String
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * The method getTextPane returns the JTextPane to the caller
     * @return txtPane as a JTextPane
     */
    public JTextPane getTextPane() {
        return txtPane;
    }

    /**
     * The method getFileOffSet simply returns the file
     * off set
     * @return fileSizeOffSet as an integer
     */
    public int getFileSize() {
        return fileSize;
    }

    /**
     * This method simply saves the conversation when asked upon
     * the conversation will be saved to the selected file by JFileChooser
     * @param file as a File
     */
    public void saveConversation(File file) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(txtPane.getText());
            bw.flush();
            bw.close();
        } catch (Exception e) {
            PrivateChatWindow.this.appendString(e.toString(), exceptionAttribute, color);
        }
    }
}
