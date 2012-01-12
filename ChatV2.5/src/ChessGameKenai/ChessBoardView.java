/*
 * Description: Class ChessBoardView.java
 * Author: Dimtri Pankov
 * Date: 26-Dec-2010
 * Version: 1.0
 */
package ChessGameKenai;

import ChatProject.Client;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.SimpleAttributeSet;

/**
 * The ChessBoardView Class is main view of the chess game it consists of the
 * chess board, squares and pieces.This view is an observer so each time the change
 * occurs in the model(Chess_Data) it is notified immediately and changes its view accordingly
 * This class is pure GUI of our chess game it interacts with the user by showing him the changes he
 * made like piece move game won and etc.. This view has a lot of options user can change his name
 * or he can change his icon, there is also possible to save the game as well as the player
 * @author Dimitri Pankov
 * @see Observer Interface
 * @see JFrame
 * @version 1.1
 */
public final class ChessBoardView extends JFrame implements Observer {

    private Container container;
    private JComponent mainPanel, northPanel, pl1IconPanel, pl2IconPanel, pl1PropetiesPanel, pl2PropetiesPanel;
    private Chess_Data data;
    private CostumPanel mainNorthPanel, mainEastPanel, eastPanel3, eastPanel2;
    private JMenuBar menuBar;
    private JMenu fileMenu, helpMenu, optionsMenu;
    private JScrollPane whiteCapturedPiecesScroll, blackCapturedPiecesScroll;
    private JComponent eastPanel1, northButtonPanel, buttonPanel;
    private CapturedPieces whiteCapturedPiecesPanel, blackCapturedPiecesPanel;
    private JLabel whoseTurnLabel, player1IconLabel, player2IconLabel, player1NbrWinsLabel, player2NbrWinsLabel, player1TimerLabel, player2TimerLabel;
    private JLabel player1NameLabel, player2NameLabel;
    private javax.swing.Timer player1Timer, player2Timer;
    private ActionListener player1TimerAction, player2TimerAction;
    private int hours, minutes, seconds;
    private int hour, minute, second;
    private int numberOfWins;
    private JButton restartButton;
    private JTabbedPane tabs;
    private JFileChooser fileChooser = new JFileChooser(".");
    private int returnValue = 0;
    private Board board;
    private static ConnectionBridge bridge;
    private Chat chat;
    private Color color = Color.ORANGE;
    private SimpleAttributeSet smpSet = new SimpleAttributeSet();
    private JMenuItem optionsMenuConnectAsServer, optionsMenuConnectAsClient;
    private JTextArea tArea = new JTextArea();
    private JScrollPane scroll = new JScrollPane(tArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    /**
     * OverLoaded constructor that receives Chess_Data object's address in the system
     * for later communication with the object
     * This constructor is purely GUI side of the chess game
     * @param data address of the Chess_Data object
     */
    public ChessBoardView(Chess_Data data) {
        this.data = data;
        this.setLayout(null);
        this.container = this.getContentPane();

        tArea.setText("--------Moves Made-------\n");
        tArea.setFont(new Font("Verdana", Font.PLAIN, 16));
        tArea.setOpaque(false);
        board = new Board(data, ChessBoardView.this);
        data.addObserver(board);
        data.addObserver(this);

        chat = new Chat(this, data);

        //ADD A WINDOW LISTENER TO THE JFRAME
        this.addWindowListener(new WindowAdapter() {

            /**
             * The method windowClosing is executed when the user
             * clicks on the window closing button at the top right corner
             * @param e WindowEvent object that is generated when window is being closed
             */
            @Override
            public void windowClosing(WindowEvent e) {
                ChessBoardView.this.promptUserOnExit();
            }
        });

        //LOADS THE ARRAY LIST WITH TWO PLAYER OBJECTS AND GIVES THEM INITIAL VALUES
        data.getPlayers().add(new Player("Player1", "Icons/hercules10.gif"));
        data.getPlayers().add(new Player("Player2", "Icons/hercules12.gif"));

        //CREATE JBUTTON RESTART AND ADD ACTION LISTENER TO IT THROUGH THE ANONYMOUS CLASS
        restartButton = new JButton("Restart", new ImageIcon(getClass().getResource("Icons/start.png")));
        restartButton.addActionListener(new ActionListener() {

            /**
             * The method actionPerformed is the method
             * that is inherited from ActionListener Interface
             * @param e ActionEvent object that is generated when listener detects an action
             */
            public void actionPerformed(ActionEvent e) {
                if (!ChessBoardView.this.data.isGameOnLine()) {
                    ChessBoardView.this.restartLocalGame();
                } else {
                    ChessBoardView.this.restartOnlineGame();
                }
            }
        });

        //SET BUTTON'S PREFERRED SIZE
        restartButton.setPreferredSize(new Dimension(114, 44));

        //CREATE JPANEL
        buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        //CREATE JPANEL
        northButtonPanel = new JPanel(new GridLayout(1, 2));
        northButtonPanel.setOpaque(false);

        //CREATE AN ANONYMOUS CLASS THAT IMPLEMENTS ACTION LISTENER
        player1TimerAction = new ActionListener() {

            /**
             * The method actionPerformed is the method
             * that is inherited from ActionListener Interface
             * @param e ActionEvent object that is generated when listener detects an action
             */
            public void actionPerformed(ActionEvent e) {

                //IF IT IS WHITE TURN EXECUTE THE IF BLOCK
                if (ChessBoardView.this.data.isWhiteTurn()) {

                    //IF SECONDS VARIABLE IS LESS THAN 60 EXECUTE THE IF BLOCK
                    if (seconds < 59) {
                        seconds++;

                        //IF SECONDS VARIABLE IS EQUAL TO 60 EXECUTE THE ELSE IF BLOCK
                    } else if (seconds == 59) {
                        seconds = 0;
                        minutes++;

                        //IF MINUTES VARIABLE IS EQUAL TO 60 EXECUTE THE ELSE F BLOCK
                    } else if (minutes == 59) {
                        minutes = 0;
                        hours++;
                    }

                    //IF SECONDS LESS THAN 10 AND MINUTES LESS THAN 10 AND HOURS LESS THAN 10 EXECUTE THE IF BLOCK
                    if (seconds < 10 && minutes < 10 && hours < 10) {

                        //SET APROPRIATE TEXT TO THE LABEL
                        player1TimerLabel.setText("0" + hours + ":" + "0" + minutes + ":" + "0" + seconds);

                        //IF SECONDS LESS OR EQUAL TO 10 MINUTES LESS THAN 10 AND HOURS LESS THAN 10 EXECUTE THE IF BLOCK
                    } else if (minutes < 10 && hours < 10 && seconds >= 10) {

                        //SET APROPRIATE TEXT TO THE LABEL
                        player1TimerLabel.setText("0" + hours + ":" + "0" + minutes + ":" + seconds);

                        //IF SECONDS LESS OR EQUAL TO 10 MINUTES LESS OR EQUAL TO 10 AND HOURS LESS THAN 10 EXECUTE THE IF BLOCK
                    } else if (minutes >= 10 && hours < 10 && seconds >= 10) {

                        //SET APROPRIATE TEXT TO THE LABEL
                        player1TimerLabel.setText("0" + hours + ":" + minutes + ":" + seconds);
                    }
                } //IF NOT WHITE TURN TO PLAY EXECUTE THE IF BLOCK
                else if (!ChessBoardView.this.data.isWhiteTurn()) {

                    //STOP PLAYER 1 TIMER
                    player1Timer.stop();

                    //START PLAYER 2 TIMER
                    player2Timer.start();
                }
            }
        };

        //CREATE AN ANONYMOUS CLASS THAT IMPLEMENTS ACTION LISTENER
        player2TimerAction = new ActionListener() {

            /**
             * The method actionPerformed is the method
             * that is inherited from ActionListener Interface
             * @param e ActionEvent object that is generated when listener detects an action
             */
            public void actionPerformed(ActionEvent e) {
                //IF IS WHITE TURN EXECUTE THE IF BLOCK
                if (!ChessBoardView.this.data.isWhiteTurn()) {

                    //IF SECOND LESS THAN 60 EXECUTE THE IF BLOCK
                    if (second < 59) {
                        second++;

                        //IF SECOND IS EQUAL TO 60
                    } else if (second == 59) {
                        second = 0;
                        minute++;

                        //IF MINUTE IS EQUAL TO 60
                    } else if (minute == 59) {
                        minute = 0;
                        hour++;
                    }

                    //IF SECONDS LESS THAN 10 AND MINUTES LESS THAN 10 AND HOURS LESS THAN 10 EXECUTE THE IF BLOCK
                    if (second < 10 && minute < 10 && hour < 10) {

                        //SET APROPRIATE TEXT TO THE LABEL
                        player2TimerLabel.setText("0" + hour + ":" + "0" + minute + ":" + "0" + second);

                        //IF SECONDS LESS OR EQUAL TO 10 AND MINUTES LESS THAN 10 AND HOURS LESS THAN 10 EXECUTE THE IF BLOCK
                    } else if (minute < 10 && hour < 10 && second >= 10) {

                        //SET APROPRIATE TEXT TO THE LABEL
                        player2TimerLabel.setText("0" + hour + ":" + "0" + minute + ":" + second);

                        //IF SECONDS LESS OR EQUAL TO 10 AND MINUTES LESS OR EQUAL TO 10 AND HOURS LESS THAN 10 EXECUTE THE IF BLOCK
                    } else if (minute >= 10 && hour < 10 && second >= 10) {

                        //SET APROPRIATE TEXT TO THE LABEL
                        player2TimerLabel.setText("0" + hour + ":" + minute + ":" + second);
                    }

                    //IF IS WHITE TURN
                } else if (ChessBoardView.this.data.isWhiteTurn()) {

                    //STOP PLAYER2 TIMER AND START PLAYER1 TIMER
                    player2Timer.stop();
                    player1Timer.start();
                }
            }
        };

        //CREATE AND START PLAYER2 TIMER
        player2Timer = new javax.swing.Timer(1000, player2TimerAction);

        //CREATE AND START PLAYER1 TIMER
        player1Timer = new javax.swing.Timer(1000, player1TimerAction);

        //CREATE AND INITILAIZE MAIN NORTH PANEL
        mainNorthPanel = new CostumPanel("Icons/background.jpg", new BorderLayout());

        //CREATE AND INITILALIZE WHOSE TURN LABEL
        whoseTurnLabel = new JLabel(data.getPlayers().get(0).getName(), SwingConstants.CENTER);
        whoseTurnLabel.setFont(new Font("Times Roman", Font.PLAIN, 30));
        whoseTurnLabel.setForeground(Color.WHITE);

        //CREATE AND INITIALIZE PLAYER 1 ICON LABEL
        player1IconLabel = new JLabel(new ImageIcon(getClass().getResource(data.getPlayers().get(0).getIconPath())));

        //CREATE AND INITIALIZE PL1 ICON PANEL
        pl1IconPanel = new JPanel();
        pl1IconPanel.setOpaque(false);
        pl1IconPanel.add(player1IconLabel);

        //CREATE AND INITIALIZE PLAYER 2 ICON LABEL
        player2IconLabel = new JLabel(new ImageIcon(getClass().getResource(data.getPlayers().get(1).getIconPath())));

        //CREATE AND INITIALIZE PL2 ICON PANEL
        pl2IconPanel = new JPanel();
        pl2IconPanel.setOpaque(false);
        pl2IconPanel.add(player2IconLabel);

        //CREATE AND INITILAIZE PLAYER 1 PROPERTIES LABELS
        player1NameLabel = new JLabel(data.getPlayers().get(0).getName(), SwingConstants.LEFT);
        player1NameLabel.setFont(new Font("Times Roman", Font.PLAIN, 20));
        player1NameLabel.setForeground(Color.WHITE);
        player1NbrWinsLabel = new JLabel("Number of wins: " + data.getPlayers().get(0).getNumberOfWins(), SwingConstants.LEFT);
        player1NbrWinsLabel.setFont(new Font("Times Roman", Font.PLAIN, 20));
        player1NbrWinsLabel.setForeground(Color.WHITE);
        player1TimerLabel = new JLabel("00:00:00", SwingConstants.LEFT);
        player1TimerLabel.setFont(new Font("Times Roman", Font.PLAIN, 20));
        player1TimerLabel.setForeground(Color.WHITE);

        //CREATE AND INITILAIZE PL1 PROPERTIES PANEL
        pl1PropetiesPanel = new JPanel(new GridLayout(3, 1));
        pl1PropetiesPanel.setOpaque(false);

        //ADD ALL THE COMPONENTS TO THE PL1 PROPERTIES PANEL
        pl1PropetiesPanel.add(player1NameLabel);
        pl1PropetiesPanel.add(player1NbrWinsLabel);
        pl1PropetiesPanel.add(player1TimerLabel);

        //CREATE AND INITILAIZE PLAYER 2 PROPERTIES LABELS
        player2NameLabel = new JLabel(data.getPlayers().get(1).getName(), SwingConstants.LEFT);
        player2NameLabel.setForeground(Color.WHITE);
        player2NameLabel.setFont(new Font("Times Roman", Font.PLAIN, 20));
        player2NbrWinsLabel = new JLabel("Number of wins: " + data.getPlayers().get(1).getNumberOfWins(), SwingConstants.LEFT);
        player2NbrWinsLabel.setFont(new Font("Times Roman", Font.PLAIN, 20));
        player2NbrWinsLabel.setForeground(Color.WHITE);
        player2TimerLabel = new JLabel("00:00:00", SwingConstants.LEFT);
        player2TimerLabel.setFont(new Font("Times Roman", Font.PLAIN, 20));
        player2TimerLabel.setForeground(Color.WHITE);

        //CREATE AND INITILAIZE PL2 PROPERTIES PANEL
        pl2PropetiesPanel = new JPanel(new GridLayout(3, 1));
        pl2PropetiesPanel.setOpaque(false);

        //ADD ALL THE COMPONENTS TO THE PL2 PROPERTIES PANEL
        pl2PropetiesPanel.add(player2NameLabel);
        pl2PropetiesPanel.add(player2NbrWinsLabel);
        pl2PropetiesPanel.add(player2TimerLabel);

        //CREATE AND INITIALIZE NORTH PANEL
        northPanel = new JPanel(new GridLayout(1, 4));
        northPanel.setOpaque(false);
        //northPanel.setBackground(Color.WHITE);

        //ADD ALL THE COMPONENTS TO THE NORTH PANEL
        northPanel.add(pl1IconPanel);
        northPanel.add(pl1PropetiesPanel);
        northPanel.add(pl2IconPanel);
        northPanel.add(pl2PropetiesPanel);

        buttonPanel.add(restartButton);

        //ADD ALL THE COMPONENTS TO THE NORTH BUTTON PANEL
        northButtonPanel.add(whoseTurnLabel);
        northButtonPanel.add(buttonPanel);

        //ADD ALL THE COMPONENTS TO THE MAIN NORTH PANEL
        mainNorthPanel.add(northPanel, BorderLayout.CENTER);
        mainNorthPanel.add(northButtonPanel, BorderLayout.SOUTH);

        //CREATE JMENUBAR AND SET IT TO THE JFRAME
        menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);


        //CREATE JMENU CALLED FILE
        fileMenu = new JMenu("File");

        //ADD AN ACTIONLISTENER TO THE JMENU ITEM NEW GAME
        fileMenu.add(new JMenuItem("New Game")).addActionListener(new ActionListener() {

            /**
             * The method actionPerformed needs to be overridden in our class
             * @param e ActionEvent object that is generated when listener detects action
             */
            public void actionPerformed(ActionEvent e) {
                if (ChessBoardView.this.data.isGameOnLine()) {
                    int returnValue = JOptionPane.showConfirmDialog(ChessBoardView.this, "The Connection will be lost would you like to proceed?Y/N", "Confirmation Message", JOptionPane.YES_NO_OPTION);
                    if (returnValue == JOptionPane.YES_OPTION) {
                        ChessBoardView.this.dispose();
                        EventQueue.invokeLater(new Runnable() {

                            public void run() {
                                new Start_Game();
                            }
                        });
                    }
                } else {
                    ChessBoardView.this.dispose();
                    EventQueue.invokeLater(new Runnable() {

                        public void run() {
                            new Start_Game();
                        }
                    });
                }

            }
        });

        //ADD SEPARATOR
        fileMenu.addSeparator();

        //ADD AN ACTIONLISTENER TO THE JMENU ITEM SAVE PLAYER
        fileMenu.add(new JMenuItem("Save Player")).addActionListener(new ActionListener() {

            /**
             * The method actionPerformed needs to be overridden in our class
             * @param e ActionEvent object that is generated when listener detects action
             */
            public void actionPerformed(ActionEvent e) {
                ChessBoardView.this.data.savePlayer();
            }
        });

        //ADD AN ACTIONLISTENER TO THE JMENU ITEM LOAD PLAYER
        fileMenu.add(new JMenuItem("Load Player")).addActionListener(new ActionListener() {

            /**
             * The method actionPerformed needs to be overridden in our class
             * @param e ActionEvent object that is generated when listener detects action
             */
            public void actionPerformed(ActionEvent e) {
                ChessBoardView.this.data.loadPlayer();

            }
        });

        fileMenu.addSeparator();

        //ADD AN ACTIONLISTENER TO THE JMENU ITEM LOAD
        fileMenu.add(new JMenuItem("Save Game")).addActionListener(new ActionListener() {

            /**
             * The method actionPerformed needs to be overridden in our class
             * @param e ActionEvent object that is generated when listener detects action
             */
            public void actionPerformed(ActionEvent e) {
                ChessBoardView.this.data.save();
            }
        });

        //ADD AN ACTIONLISTENER TO THE JMENU ITEM SAVE
        fileMenu.add(new JMenuItem("Load Game")).addActionListener(new ActionListener() {

            /**
             * The method actionPerformed needs to be overridden in our class
             * @param e ActionEvent object that is generated when listener detects action
             */
            public void actionPerformed(ActionEvent e) {
                ChessBoardView.this.data.load();
                ChessBoardView.this.board.removeAllPieces();
                ChessBoardView.this.board.getPieces().clear();
                ChessBoardView.this.board.populateBoard();
                ChessBoardView.this.loadCapturedPieces();
            }
        });

        fileMenu.addSeparator();

        fileMenu.add(new JMenuItem("Save As...")).addActionListener(new ActionListener() {

            /**
             * The method actionPerformed needs to be overridden in our class
             * @param e ActionEvent object that is generated when listener detects action
             */
            public void actionPerformed(ActionEvent e) {
                if ((returnValue = fileChooser.showSaveDialog(ChessBoardView.this)) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    ChessBoardView.this.data.save(file);
                }
            }
        });

        fileMenu.add(new JMenuItem("Load from file")).addActionListener(new ActionListener() {

            /**
             * The method actionPerformed needs to be overridden in our class
             * @param e ActionEvent object that is generated when listener detects action
             */
            public void actionPerformed(ActionEvent e) {
                if ((returnValue = fileChooser.showOpenDialog(ChessBoardView.this)) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    ChessBoardView.this.data.load(file);
                    ChessBoardView.this.board.removeAllPieces();
                    ChessBoardView.this.board.getPieces().clear();
                    ChessBoardView.this.board.populateBoard();
                    ChessBoardView.this.loadCapturedPieces();
                }
            }
        });

        fileMenu.addSeparator();

        //ADD AN ACTIONLISTENER TO THE JMENU ITEM EXIT
        fileMenu.add(new JMenuItem("Exit")).addActionListener(new ActionListener() {

            /**
             * The method actionPerformed needs to be overridden in our class
             * @param e ActionEvent object that is generated when listener detects action
             */
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        optionsMenu = new JMenu("Options");

        optionsMenu.add(optionsMenuConnectAsClient = new JMenuItem("Connect As Client")).addActionListener(new ActionListener() {

            /**
             * The method actionPerformed needs to be overridden in our class
             * @param e ActionEvent object that is generated when listener detects action
             */
            public void actionPerformed(ActionEvent e) {

                try {
                    new Thread(new Runnable() {

                        InetAddress ipAddress = InetAddress.getByName(JOptionPane.showInputDialog("Enter Server IP"));

                        public void run() {
                            ChessBoardView.this.playOnLine();

                            bridge = new ConnectionBridge(ChessBoardView.this.data, ChessBoardView.this, false, ipAddress, chat, null);
                            ChessBoardView.this.data.addObserver(bridge);
                        }
                    }).start();
                } catch (UnknownHostException ex) {
                    JOptionPane.showMessageDialog(ChessBoardView.this, ex.toString());
                    ChessBoardView.this.reEnableMenuItems(true);
                }
            }
        });
        optionsMenu.add(optionsMenuConnectAsServer = new JMenuItem("Connect As Server")).addActionListener(new ActionListener() {

            /**
             * The method actionPerformed needs to be overridden in our class
             * @param e ActionEvent object that is generated when listener detects action
             */
            public void actionPerformed(ActionEvent e) {

                new Thread(new Runnable() {

                    public void run() {
                        ChessBoardView.this.playOnLine();
                        ChessBoardView.this.reEnableMenuItems(false);
                        bridge = new ConnectionBridge(ChessBoardView.this.data, ChessBoardView.this, true, null, chat, null);
                        ChessBoardView.this.data.addObserver(bridge);
                    }
                }).start();
            }
        });

        this.reEnableMenuItems(false);

        //ADD AN ACTIONLISTENER TO THE JMENU ITEM FLIP BOARD
        optionsMenu.add(new JMenuItem("Flip Board")).addActionListener(new ActionListener() {

            /**
             * The method actionPerformed needs to be overridden in our class
             * @param e ActionEvent object that is generated when listener detects action
             */
            public void actionPerformed(ActionEvent e) {
                if (board.getCurrentBoard() == Board.NORMAL_BOARD) {
                    board.setBoard(Board.FLIPPED_BOARD);
                } else {
                    board.setBoard(Board.NORMAL_BOARD);
                }
                board.flipBoard();
            }
        });

        //ADD AN ACTIONLISTENER TO THE JMENU ITEM NEW GAME
        optionsMenu.add(new JMenuItem("Choose Icon")).addActionListener(new ActionListener() {

            /**
             * The method actionPerformed needs to be overridden in our class
             * @param e ActionEvent object that is generated when listener detects action
             */
            public void actionPerformed(ActionEvent e) {
                new Choose_Icon(ChessBoardView.this.data, bridge);
            }
        });

        //ADD AN ACTIONLISTENER TO THE JMENU ITEM SET PLAYER NAMES
        optionsMenu.add(new JMenuItem("Set Player Names")).addActionListener(new ActionListener() {

            /**
             * The method actionPerformed needs to be overridden in our class
             * @param e ActionEvent object that is generated when listener detects action
             */
            public void actionPerformed(ActionEvent e) {
                if (!ChessBoardView.this.data.isGameOnLine()) {
                    ChessBoardView.this.chooseNameLocal();
                } else {
                    ChessBoardView.this.chooseNameOnLine();
                }
            }
        });

        //CREATE JMENU
        helpMenu = new JMenu("Help");

        //ADD JMENUITEMS TO THE HELP MENU
        helpMenu.add(new JMenuItem("Chess Rules")).addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                new HelpTopics(ChessBoardView.this);
            }
        });

        helpMenu.add(new JMenuItem("User Manual")).addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                new UserManual(ChessBoardView.this);
            }
        });

        helpMenu.add(new JMenuItem("About")).addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                new About(ChessBoardView.this);
            }
        });

        //ADD MENUS TO THE MENUBAR
        menuBar.add(fileMenu);
        menuBar.add(optionsMenu);
        menuBar.add(helpMenu);


        whiteCapturedPiecesPanel = new CapturedPieces(Color.WHITE, board);
        whiteCapturedPiecesScroll = new JScrollPane(whiteCapturedPiecesPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        whiteCapturedPiecesScroll.setOpaque(false);

        //CREATE AND SET JPANEL'S LAYOUT
        mainPanel = new JPanel(new GridLayout(1, 1));
        mainPanel.add(board);

        blackCapturedPiecesPanel = new CapturedPieces(Color.BLACK, board);

        //CREATE JSCROLLPANE AND ADD JPANEL TO IT
        blackCapturedPiecesScroll = new JScrollPane(blackCapturedPiecesPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        //ADD TWO OBSERVERS TO THE DATA WHICH ARE CAPTURED PIECES PANELS
        data.addObserver(whiteCapturedPiecesPanel);
        data.addObserver(blackCapturedPiecesPanel);

        mainEastPanel = new CostumPanel("Icons/background.jpg", new GridLayout(1, 1));

        //CREATE JTABBED PANE
        tabs = new JTabbedPane();
        eastPanel3 = new CostumPanel("Icons/background.jpg", new BorderLayout());

        //SET BORDER FOR EAST PANEL 3
        TitledBorder t = new TitledBorder("History Tab");
        t.setTitleColor(Color.WHITE);
        t.setTitleFont(new Font("New Times Roman", Font.PLAIN, 18));
        eastPanel3.setBorder(t);
        eastPanel3.add(scroll);
        eastPanel2 = new CostumPanel("Icons/background.jpg", new GridLayout(8, 4));

        //SET BORDER  FOR EAST PANEL 2
        TitledBorder t1 = new TitledBorder("Smilies");
        t1.setTitleColor(Color.WHITE);
        t1.setTitleFont(new Font("New Times Roman", Font.PLAIN, 18));
        eastPanel2.setBorder(t1);

        //ADD SMILIES TO THE PANEL
        for (int i = 0; i < 32; i++) {
            eastPanel2.add(new Emoticons(ChessBoardView.this, "smileys/smiley" + (i + 1) + ".gif", chat));
        }

        //CREATE EAST PANEL
        eastPanel1 = new JPanel(new GridLayout(2, 1));
        eastPanel1.add(whiteCapturedPiecesScroll);
        eastPanel1.add(blackCapturedPiecesScroll);

        //ADD COMPONENTS TO ALL THE TABS
        tabs.addTab("Captured", eastPanel1);
        tabs.addTab("Smilies", eastPanel2);
        tabs.addTab("History", eastPanel3);
        tabs.setBackgroundAt(0, Color.ORANGE);
        tabs.setBackgroundAt(1, Color.ORANGE);
        tabs.setBackgroundAt(2, Color.ORANGE);
        mainEastPanel.add(tabs);

        this.setIconImage(new ImageIcon("ChessPieces/wking46.gif").getImage());

        //ADD EVERYTHING TO THE CONTAINER
        container.add(board);
        container.add(chat);
        container.add(mainEastPanel);
        container.add(mainNorthPanel);

        mainNorthPanel.setBounds(0, 0, 757, 170);
        mainNorthPanel.repaint();

        mainEastPanel.setBounds(520, 170, 230, 531);
        mainEastPanel.repaint();

        board.setBounds(0, 170, 583, 592);
        board.repaint();

        chat.setBounds(0, 693, 757, 211);
        chat.repaint();

        //INITIALIZES JFRAMES' PROPERTIES
        this.setTitle("Chess Game V 2.0");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setLocation(333, 6);
        this.setResizable(false);
        this.setSize(757, 933);
        this.setVisible(true);
    }

    /**
     * The method update is inherited from Observer Interface
     * needs to be implemented in our class
     * @param o Observable object
     * @param arg any object
     */
    public void update(Observable o, Object arg) {

        //SET APROPRIATE LABEL TEXT DEPENDING ON WHOSE TURN IT IS
        whoseTurnLabel.setText(data.isWhiteTurn() ? (ChessBoardView.this.data.getPlayers().get(0).getName() + " turn to play") : (ChessBoardView.this.data.getPlayers().get(1).getName() + " turn to play"));

        if (data.isWinner()) {
            if (data.getCapturedPieces().get(data.getCapturedPieces().size() - 1).getColor() == Color.BLACK) {
                whoseTurnLabel.setText(data.getPlayers().get(0).getName() + " has won the game!");
                data.getPlayers().get(0).setNumberOfWins(++numberOfWins);
            } else {
                whoseTurnLabel.setText(data.getPlayers().get(1).getName() + " has won the game!");
                data.getPlayers().get(1).setNumberOfWins(++numberOfWins);
            }
            board.removeListeners(Color.WHITE);
            board.removeListeners(Color.BLACK);
            this.stopTimer();
        }
        player1NbrWinsLabel.setText("Number of wins: " + data.getPlayers().get(0).getNumberOfWins());
        player2NbrWinsLabel.setText("Number of wins: " + data.getPlayers().get(1).getNumberOfWins());
        player1NameLabel.setText(data.getPlayers().get(0).getName());
        player2NameLabel.setText(data.getPlayers().get(1).getName());
        player1IconLabel.setIcon(new ImageIcon(getClass().getResource(data.getPlayers().get(0).getIconPath())));
        player2IconLabel.setIcon(new ImageIcon(getClass().getResource(data.getPlayers().get(1).getIconPath())));
    }

    /**
     * The method chooseNameLocal is used if the user
     * wants to change its name and the game is local game
     * this method is used when the user is playing the game locally
     */
    public void chooseNameLocal() {
        if (!data.isGameOnLine()) {
            String player1Name = "";
            String player2Name = "";

            //SET PLAYER NAMES APROPRIATLY
            player1Name = JOptionPane.showInputDialog("Enter First Player Name");
            player2Name = JOptionPane.showInputDialog("Enter Second Player Name");

            if (player1Name != null && player2Name != null) {

                data.getPlayers().get(0).setName(player1Name);
                data.getPlayers().get(1).setName(player2Name);

                //SET APROPRIATE LABEL TEXT DEPENDING ON WHOSE TURN IT IS
                whoseTurnLabel.setText(data.isWhiteTurn() ? (data.getPlayers().get(0).getName() + " turn to play") : (data.getPlayers().get(1).getName() + " turn to play"));
                player1NameLabel.setText(data.getPlayers().get(0).getName());
                player2NameLabel.setText(data.getPlayers().get(1).getName());
            }
        }
    }

    /**
     * The method chooseNameOnLine is used when the user
     * is playing online so the name he chose for himself will be shown
     * on the other client view it is done by sending the name through a socket
     * and setting it in the players ArrayList in the data each time the view is notified
     * it extracts the name from the data class and displays it
     */
    public void chooseNameOnLine() {
        String playerName = "";
        if (data.isServer()) {
            playerName = JOptionPane.showInputDialog("Enter Your Name");
            if (playerName != null) {
                data.getPlayers().get(0).setName(playerName);
                player1NameLabel.setText(data.getPlayers().get(0).getName());
            }
        } else {
            playerName = JOptionPane.showInputDialog("Enter Your Name");
            if (playerName != null) {
                data.getPlayers().get(1).setName(playerName);
                player2NameLabel.setText(data.getPlayers().get(1).getName());
            }
        }
        try {
            Packet packet = new Packet();
            packet.setGuestName(playerName);
            bridge.getOutputStream().writeObject(packet);
            bridge.getOutputStream().flush();
        } catch (Exception e) {
            chat.appendStr(e.getMessage(), smpSet, color);
        }

        //SET APROPRIATE LABEL TEXT DEPENDING ON WHOSE TURN IT IS
        whoseTurnLabel.setText(data.isWhiteTurn() ? (data.getPlayers().get(0).getName() + " turn to play") : (data.getPlayers().get(1).getName() + " turn to play"));
    }

    /**
     * This method simply starts the timer when it is called
     * by starting the first player timer then the first player timer
     * will start the second player timer an so on.....
     */
    public void startTimer() {
        data.isWhiteTurn(true);
        player1Timer.start();
    }

    /**
     * The method loadCapturedPieces is used when the saved game is loaded back and
     * the capturedPieces ArrayList is not empty it takes the captured pieces and adds them to the appropriate
     * captured pieces panel white or black
     */
    public void loadCapturedPieces() {
        for (int i = 0; i < data.getCapturedPieces().size(); i++) {
            if (data.getCapturedPieces().get(i).getType().startsWith("W")) {
                whiteCapturedPiecesPanel.add(new VisualPiece(board.getImageMap().get(data.getCapturedPieces().get(i).getType())));
                whiteCapturedPiecesPanel.revalidate();
                whiteCapturedPiecesPanel.repaint();
            } else {
                blackCapturedPiecesPanel.add(new VisualPiece(board.getImageMap().get(data.getCapturedPieces().get(i).getType())));
                blackCapturedPiecesPanel.revalidate();
                blackCapturedPiecesPanel.repaint();
            }
        }
    }

    /**
     * The method restartLocalGame restarts local game that means
     * that if the user plays locally we use this method if the user plays
     * on the same computer locally
     */
    public void restartLocalGame() {
        data.getActivePieces().clear();
        data.getCapturedPieces().clear();
        data.createNonVisualPieces();
        board.removeAllPieces();
        board.getPieces().clear();
        board.populateBoard();
        hours = minutes = seconds = hour = minute = second = 0;
        player1TimerLabel.setText("00:00:00");
        player2TimerLabel.setText("00:00:00");
        player1Timer.stop();
        player2Timer.stop();
        tArea.setText("");
        whoseTurnLabel.setText(data.getPlayers().get(0).getName() + " turn to play");
        this.resetAllSquares();
        chat.getTxtPane().setText("");
        data.getCapturedPieces().clear();
        board.removeListeners(Color.BLACK);
        board.addListeners(Color.WHITE);
        whiteCapturedPiecesPanel.removeAll();
        blackCapturedPiecesPanel.removeAll();
    }

    /**
     * The method restartOnlineGame is used if the user plays online
     * against an opponent and wants to restart the game
     * The information is sent to the client on the other side and restarts its game as well
     */
    public void restartOnlineGame() {
        if (data.isGameOnLine()) {
            try {
                Packet packet = new Packet();
                packet.setRestartGame("restart game");
                bridge.getOutputStream().writeObject(packet);
                bridge.getOutputStream().flush();
            } catch (Exception e) {
                chat.appendStr(e.getMessage(), smpSet, color);
            }
        }
    }

    /**
     * The method restartClientGame online game
     * appropriately removes captured pieces creates new visual and non visual pieces
     * adds and removes listeners depending on who's server or a client
     */
    public void restartClientGame() {
        this.resetAllSquares();
        data.getActivePieces().clear();
        data.getCapturedPieces().clear();
        data.createNonVisualPieces();
        board.removeAllPieces();
        board.getPieces().clear();
        board.populateBoard();
        player1Timer.start();
        player2Timer.stop();
        tArea.setText("");
        hours = minutes = seconds = hour = minute = second = 0;
        player1TimerLabel.setText("00:00:00");
        player2TimerLabel.setText("00:00:00");
        whoseTurnLabel.setText(data.getPlayers().get(0).getName() + " turn to play");
        if (data.isServer()) {
            board.removeListeners(Color.BLACK);
            board.addListeners(Color.WHITE);
        } else {
            board.removeListeners(Color.WHITE);
            board.removeListeners(Color.BLACK);
        }
        whiteCapturedPiecesPanel.removeAll();
        blackCapturedPiecesPanel.removeAll();
    }

    /**
     * The method getConnectionInstance is a static method here
     * we use a Singleton pattern to get the instance of the ConnectionBrodge class
     * @return bridge as a ConnectionBridge
     */
    public static ConnectionBridge getConnectionInstance() {
        return bridge;
    }

    /**
     * getPanel simply returns a JPanel to the caller
     * @return eastPanel2 as a JPanel
     */
    public JPanel getPanel() {
        return eastPanel2;
    }

    /**
     * The method reEnableMenuItems simply set enable false or true
     * to the options menu item connect as server and connect as client
     * @param enable as a boolean
     */
    public void reEnableMenuItems(boolean enable) {
        optionsMenuConnectAsServer.setEnabled(enable);
        optionsMenuConnectAsClient.setEnabled(enable);
    }

    /**
     * The method stopTimer stops both timers
     * whenever we need we could call this method and both timers would
     * In our case we would use this method if the game was won by either party
     */
    public void stopTimer() {
        player1Timer.stop();
        player2Timer.stop();
    }

    /**
     * The method promptUserOnExit simply asks the user if he wants to save the game or not
     * if yes the JFileChoose pops up to save the game if no or yes was pressed the program will exit
     * if the user wants to cancel the exit there is a third option cancel which disposes of the dialog
     * but the game is still there
     */
    public void promptUserOnExit() {
        returnValue = JOptionPane.showConfirmDialog(ChessBoardView.this, "Would you like to save the game?".toUpperCase(), "Information Message", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(this.getClass().getResource("Icons/save.png")));
        if (returnValue == JOptionPane.YES_OPTION) {
            if ((returnValue = fileChooser.showSaveDialog(ChessBoardView.this)) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    ChessBoardView.this.data.save(file);
                } catch (Exception er) {
                    JOptionPane.showMessageDialog(ChessBoardView.this, "Error: " + er.toString(), "Error Message", JOptionPane.ERROR_MESSAGE);
                }
                this.dispose();
            }
        } else if (returnValue == JOptionPane.NO_OPTION) {
            this.dispose();
        }
        if (bridge != null) {
            bridge.killThread();
            bridge.closeSocket();
        }
    }

    /**
     * The method resetAllSquares simply resets all squares to their
     * original color checks each square for their background color
     * and if the background is blue then it switches the background back to original color
     */
    public void resetAllSquares() {
        for (Square s : board.getSquares()) {
            if (s.getBackground() == Color.BLUE) {
                s.setBackground(s.getColor());
            }
        }
    }

    /**
     * The method playOnLine simply starts the game on line
     * it restarts the game clears the captured pieces sets the isServer and isGameOnLine to true
     * and calls another method of this class which is restartLocalGame to complete the job
     */
    public void playOnLine() {
        player1Timer.stop();
        data.isGameOnLine(true);
        data.isWhiteTurn(true);
        board.isFirstTime(true);
        this.restartClientGame();
    }

    /**
     * The method flipClientBoard automatically flips the
     * board for the client side if the game is played online
     * for easier view on the client side however if the client decides to switch
     * back the view he would simply go in the options menu and change the flip back
     */
    public void flipClientBoard() {
        if (!data.isServer()) {
            board.removeAll();
            board.setBoard(Board.FLIPPED_BOARD);
            board.flipBoard();
        }
    }

    /**
     * The method startGameThroughPrivateChat simply starts the chess game through
     * private chat the parameters will decide whether to create a server or a client
     * @param isServer as a boolean
     * @param ipAdress as an InetAddress object
     * @param client as a Client object
     */
    public void startGameThroughChat(boolean isServer, InetAddress ipAdress, Client client) {
        if (isServer) {
            bridge = new ConnectionBridge(data, this, true, null, chat, client);
        } else {
            bridge = new ConnectionBridge(data, this, false, ipAdress, chat, client);
        }
        data.addObserver(bridge);
        this.playOnLine();

    }

    /**
     * The method getMoves simply returns the text area of moves to the caller
     * @return as a JTextArea
     */
    public JTextArea getMoves() {
        return tArea;
    }

    /**
     * The method setConnectionBridge simply sets the ConnectionBridge to what the
     * programmer desires
     * @param bridge as a ConnectionBridge
     */
    public void setConnectionBridge(ConnectionBridge bridge) {
        ChessBoardView.bridge = bridge;
    }
}
