/*
 * Description: Chat.java
 * Author: Mario Bruno
 * Date: 28-Feb-2011
 * Version: 2.0
 */
package ChessGameKenai;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * Chat class is extends JPanel and is the part of the chess game where the two
 * players may communicate with each other. The chat includes basic message communication
 * which works with the Packet class to send the messages to each other. This chat also
 * includes smilies which the users can send to each other. The user can also chat certain
 * font setting like bold/italic and the font color. The users can also save the conversation
 * by typing *save in the text field and clear the text area with *clear.
 * @author Mario Bruno
 * @version 2.0
 */
public class Chat extends JPanel {

    //INSTNACE VARIABLES
    private JTextPane chatA;
    private JPanel chatBox, sendPnl, allChatPanel;
    private JTextField chatF;
    private JButton btnSend, btnFont;
    private JScrollPane scroll;
    private Font font;
    private TitledBorder border;
    private ActionListener enter;
    private String imgPath;
    private String imgSet = " :image ";
    private ChessBoardView view;
    private SimpleAttributeSet smpSet, smpSetUnderline;
    private Color color = Color.ORANGE;
    private ObjectOutputStream out;
    private Chess_Data data;
    private String name = "";
    private DateFormat format = new SimpleDateFormat("MM/dd/yy");
    private Date dateNow = new Date();

    /**
     * Overloaded constructor which receives references to other classes to be
     * able to pass messages between them. It gets the ChessBoardView class only for
     * setting the location of the Font chooser dialog. It also receives the Chess_Data
     * class for getting the player names.
     * @param view as a ChessBoardView object
     * @param data as a Chess_Data object
     */
    public Chat(ChessBoardView view, Chess_Data data) {

        this.view = view;
        this.data = data;


        smpSet = new SimpleAttributeSet();
        smpSetUnderline = new SimpleAttributeSet();
        StyleConstants.setUnderline(smpSetUnderline, true);

        //chatBox JPanel is the panel which contains the JTextPane
        chatBox = new JPanel(new BorderLayout()) {

            /**
             * The method painComponent of chatBox
             * is used here to paint the JPanel as we want
             * @param g Graphics object used to paint this object
             */
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                int w = chatBox.getWidth();
                int h = chatBox.getHeight();

                URL url = chatA.getClass().getResource("Icons/background.jpg");
                Toolkit toolkit = this.getToolkit();
                Image image = toolkit.getImage(url);
                g.drawImage(image, 0, 0, w, h, chatBox);
            }
        };

        chatBox.setOpaque(false);

        //chatA is the JTextPane where the messages are seen
        chatA = new JTextPane() {

            /**
             * The method painComponent of chatA
             * is used here to paint the JTexchatA as we want
             * @param g Graphics object used to paint this object
             */
            @Override
            public void paintComponent(Graphics g) {
                int w = chatA.getWidth();
                int h = chatA.getHeight();

                URL url = chatA.getClass().getResource("Icons/background.jpg");
                Toolkit toolkit = this.getToolkit();
                Image image = toolkit.getImage(url);
                g.drawImage(image, 0, 0, w, h, chatA);
                super.paintComponent(g);
            }
        };


        chatA.setFont(new Font("Dialog", Font.PLAIN, 16));
        appendStr("Chess chat", smpSetUnderline, Color.BLACK);

        btnFont = new JButton("Font");

        btnFont.addActionListener(new ActionListener() {

            /**
             * The method actionPerformed needs to be overridden in our class
             * @param e ActionEvent object that is generated when listener detects an action
             */
            public void actionPerformed(ActionEvent e) {
                new FontDialog();
            }
        });




        chatA.setOpaque(false);
        chatA.setEditable(false);

        scroll = new JScrollPane(chatA, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        //SET BORDER PROPERTIES
        border = new TitledBorder("Chat");
        border.setTitleFont(font);
        border.setTitleColor(Color.WHITE);
        chatBox.setBorder(border);
        chatBox.add(scroll);

        chatF = new JTextField(30);

        //sendPnl is the panel which has the JTextArea and two JButtons
        sendPnl = new JPanel(new FlowLayout(FlowLayout.LEFT));

        btnSend = new JButton("Send");

        btnSend.addActionListener(new ActionListener() {

            /**
             * The method actionPerformed needs to be overridden in our class
             * @param e ActionEvent object that is generated when listener detects action
             */
            public void actionPerformed(ActionEvent e) {
                if (!chatF.getText().trim().equals("") || imgPath != null) {
                    if (chatF.getText().trim().equals("*save")) {
                        saveChat();
                        chatF.setText("");
                    } else if (chatF.getText().trim().equals("*clear")) {
                        chatA.setText("");
                        appendStr("Chess chat", smpSetUnderline, Color.BLACK);
                        chatF.setText("");
                    } else {
                        try {
                            sendMsg();
                            chatA.setCaretPosition(chatA.getDocument().getLength());
                        } catch (IOException ex) {
                            Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        });

        //Add the JComponents to the panel
        sendPnl.add(chatF);
        sendPnl.add(btnSend);
        sendPnl.add(btnFont);
        sendPnl.setOpaque(false);

        enter = (new ActionListener() {

            /**
             * The method actionPerformed needs to be overridden in our class
             * @param e ActionEvent object that is generated when listener detects an action
             */
            public void actionPerformed(ActionEvent e) {
                if (!chatF.getText().trim().equals("") || imgPath != null) {
                    if (chatF.getText().trim().equals("*save")) {
                        saveChat();
                        chatF.setText("");
                    } else if (chatF.getText().trim().equals("*clear")) {
                        chatA.setText("");
                        appendStr("Chess chat", smpSetUnderline, Color.BLACK);
                        chatF.setText("");
                    } else {
                        try {
                            sendMsg();
                            chatA.setCaretPosition(chatA.getDocument().getLength());
                        } catch (IOException ex) {
                            Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        });

        chatF.addActionListener(enter);

        //allChatPanel is the panel which has all the other panels
        allChatPanel = new JPanel(new BorderLayout()) {

            /**
             * The method painComponent of allChatPanel
             * is used here to paint the JPanel as we want
             * @param g Graphics object used to paint this object
             */
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                int w = allChatPanel.getWidth();
                int h = allChatPanel.getHeight();

                URL url = chatA.getClass().getResource("Icons/background.jpg");
                Toolkit toolkit = this.getToolkit();
                Image image = toolkit.getImage(url);
                g.drawImage(image, 0, 0, w, h, allChatPanel);
            }
        };

        setButtons(false);

        //Add all the panels to the main allChatPanel and set their borderLayout
        allChatPanel.add(chatBox, BorderLayout.CENTER);
        allChatPanel.add(sendPnl, BorderLayout.SOUTH);
        allChatPanel.setPreferredSize(new Dimension(757, 175));

        //Set the JFrame properties
        this.add(allChatPanel);
        this.setPreferredSize(new Dimension(757, 175));
        this.setVisible(true);

    }

    //*****************************************************************\\
    //                      SAVE DIALOG                                 \\-------------------------------------------------------------------
    //*******************************************************************\\
    /**
     * saveChat method is used to save the conversation to a text file.
     * The user is given the choice where to save the file with a JFileChooser.
     * It also contains a file filter and automatically saves the file as a .txt file
     */
    public void saveChat() {

        String path = null;

        //Create the JFileChooser
        JFileChooser saveDialog = new JFileChooser(".");

        //Add a .txt filter
        saveDialog.setFileFilter(new javax.swing.filechooser.FileFilter() {

            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".txt");
            }

            public String getDescription() {
                return "Text Document (*txt files)";
            }
        });

        //Remove the all file type option
        saveDialog.setAcceptAllFileFilterUsed(false);

        // Display the dialog and get the filename from the user
        int returnVal = saveDialog.showDialog(null, "save");

        // Check whether user canceled the save
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        } else {
            File file = saveDialog.getSelectedFile();
            path = file.getPath();

            if (!path.endsWith(".txt")) {
                path = path + ".txt";
            }
        }

        try {

            //Create the PrintWriter and save the file as data.txt
            PrintWriter pr = new PrintWriter(new BufferedWriter(new FileWriter(path)));


            pr.println("*****" + format.format(dateNow) + "*****");
            pr.print(chatA.getText());
            pr.flush();
            pr.close();

        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, ioe.getMessage());
        }
    }//******************END OF SAVE DIALOG****************************\\

    /**
     * setImgPath is used to set where the images are on the harddrive
     * @param imgPath the path of where the images are stored
     */
    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    /**
     * getImgPath is used to simply get the string imgPath
     * so it can be used in any other class
     * @return imgPath as a String
     */
    public String getImgPath() {
        return imgPath;
    }

    /**
     * getTxtxPane is used to get the JTextPane
     * so it can be access in any other class
     * @return chatA as a JTextPane
     */
    public JTextPane getTxtPane() {
        return chatA;
    }

    /**
     * getTxtField is used to get the JTextField
     * so it can be access in any other class
     * @return chatF as a JTextField
     */
    public JTextField getTxtField() {
        return chatF;
    }

    /**
     * appendStr is used to append messages, this method had to be made because
     * JTextPane doesn't include a append() method
     * @param s The message you want to send
     * @param smpSet the SimpleAttributeSet used
     * @param color Color of the String
     */
    public final void appendStr(String s, SimpleAttributeSet smpSet, Color color) {

        try {
            Document doc = chatA.getDocument();
            StyleConstants.setForeground(smpSet, color);
            doc.insertString(doc.getLength(), s, smpSet);

        } catch (BadLocationException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * replaceStr is used to check for a certain string and can replace that string
     * with another string. ( Used for when smileys )
     * @param original The entire entered message
     * @param find The String in which to replace
     * @param replacement The replacement String
     * @return The new message with the replaced String
     */
    public String replaceStr(String original, String find, String replacement) {
        int i = original.indexOf(find);
        if (i < 0) {
            return original;  // return original if 'find' is not in it.
        }
        String partBefore = original.substring(0, i);
        String partAfter = original.substring(i + find.length());
        return partBefore + replacement + partAfter;

    }

    /**
     * sendMsg method is used to send the message to the protocol so the
     * other user can receive it. It first gets the outputstream, then checks
     * if the user has entered a smiley, if there is a smiley the word :image is
     * removed from the message. It then sends the font style,color,message
     * to the protocol. It also checks if the image path is null or not, if it is
     * not null that means the user has entered a smiley and it sends the smiley to
     * the protocol. Finally it gets the users users name and inputs his own message
     * to his JTextPane.
     * @throws IOException
     */
    public void sendMsg() throws IOException {
        out = ChessBoardView.getConnectionInstance().getOutputStream();

        if (imgPath != null && chatF.getText().indexOf(" :image ") >= 0) {
            chatF.setText(replaceStr(chatF.getText(), imgSet, ""));
        }
        Packet packet = new Packet();
        packet.setSmpSet(smpSet);
        packet.setColor(color);
        packet.setMessage(chatF.getText().trim());
        if (imgPath != null) {
            packet.setImgPath(imgPath);
        }
        out.writeObject(packet);
        out.flush();
        if (data.isServer()) {
            name = data.getPlayers().get(0).getName();
        } else {
            name = data.getPlayers().get(1).getName();
        }
        if (!chatF.getText().trim().equals("") || imgPath != null) {
            appendStr("\n" + name + ": " + chatF.getText(), smpSet, color);
            if (imgPath != null) {
                chatA.insertIcon(new ImageIcon(getClass().getResource(imgPath)));
            }
        }
        chatF.setText("");
        imgPath = null;
    }

    /**
     * setButtons method is used to set up the buttons
     * settings. When the chat starts the buttons are disabled. Once
     * a connection has been made with another user, the buttons become enabled.
     * @param isOn boolean
     */
    public final void setButtons(boolean isOn) {
        if (isOn == true) {
            chatF.setEditable(true);
            chatF.setEnabled(true);
            btnSend.setEnabled(true);
            btnFont.setEnabled(true);

        } else {
            chatF.setEditable(false);
            chatF.setEnabled(false);
            btnSend.setEnabled(false);
            btnFont.setEnabled(false);
        }
    }

    /**
     * getClientName method checks which user is the server and which is the client
     * it then sets the player name accordingly.
     * @return name as a String
     */
    public String getClientName() {
        if (!data.isServer()) {
            name = data.getPlayers().get(0).getName();
        } else {
            name = data.getPlayers().get(1).getName();
        }
        return name;
    }

    //*****************************************************************\\
    //                      FONT DIALOG                                 \\--------------------------------------------------------
    //*******************************************************************\\
    /**
     * FontDialog class extends JDialog and is used so the user can change their
     * font settings. It allows the font to be changed to Bold or Italic and also
     * be able to change the font color.
     */
    public class FontDialog extends JDialog {

        //INSTANCE VARIABLES
        private JCheckBox chkItalic, chkBold;
        private JButton btnOk, btnColor;
        private JPanel pnlChk, pnlBtn;
        private SimpleAttributeSet smpSet1;

        /**
         * empty constructor which has the components to create its self graphically
         */
        public FontDialog() {

            chkItalic = new JCheckBox("Italic");
            chkBold = new JCheckBox("Bold");

            btnOk = new JButton("Ok");
            btnColor = new JButton("Change Color");

            btnColor.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    color = JColorChooser.showDialog(view, "Choose Color", Color.BLUE);
                    if (color != null) {
                        StyleConstants.setForeground(smpSet, color);
                    } else {
                        color = Color.BLUE;
                    }
                }
            });

            btnOk.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    smpSet1 = new SimpleAttributeSet();
                    if (!chkBold.isSelected() && chkItalic.isSelected()) {
                        StyleConstants.setItalic(smpSet1, true);
                        StyleConstants.setForeground(smpSet1, color);
                        Chat.this.smpSet = smpSet1;
                    } else if (chkBold.isSelected() && chkItalic.isSelected()) {
                        StyleConstants.setItalic(smpSet1, true);
                        StyleConstants.setBold(smpSet1, true);
                        StyleConstants.setForeground(smpSet1, color);
                        Chat.this.smpSet = smpSet1;
                    }
                    if (chkBold.isSelected() && !chkItalic.isSelected()) {
                        StyleConstants.setBold(smpSet1, true);
                        StyleConstants.setForeground(smpSet1, color);
                        Chat.this.smpSet = smpSet1;
                    } else if (!chkBold.isSelected() && !chkItalic.isSelected()) {
                        Chat.this.smpSet = smpSet1;
                    }
                    FontDialog.this.dispose();
                }
            });

            //Add the components directly to the frame
            this.add(chkItalic);
            this.add(chkBold);
            this.add(btnColor);
            this.add(btnOk);

            // set the view
            this.setLayout(new FlowLayout());
            this.setTitle("Change Font");
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            this.setSize(200, 100);
            this.setResizable(false);
            this.setLocationRelativeTo(view);
            this.setModal(true);
            this.setVisible(true);


        }
    }//************************END OF FONT DIALOG************************************
}
