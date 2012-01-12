/*
 * Description: Class Smiley.java
 * Author: Dimtri Pankov
 * Date: 10-Feb-2011
 * Version: 1.0
 */
package ChatProject;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * The Smiley class is basically Smiley i needed it to
 * make the chat a bit more fun and interacting you can insert smiley into the TextPane
 * Each Smiley has an ActionListener when it is clicked it sets the iconPath in Client or PrivateChat Window
 * @author Dimitri Pankov
 * @see JLabel
 * @see Client
 * @see PrivateChatWindow
 * @version 1.1
 */
public class Smiley extends JLabel {

    private String path;
    private Client client;
    private PrivateChatWindow privateChat;

    /**
     * The Overloaded constructor of the class receives iconPath and the client
     * object in the constructor
     * @param client as a Client object
     * @param path as a String
     */
    public Smiley(Client client, String path) {
        this.client = client;
        this.path = path;
        this.setPreferredSize(new Dimension(50, 50));
        this.setIcon(new ImageIcon(this.getClass().getResource(path)));
        this.addMouseListener(new MouseAdapter() {

            /**
             * The method mousePressed sets the iconPath to the smiley
             * @param e MouseEvent object that is generated when is clicked on the object
             */
            @Override
            public void mousePressed(MouseEvent e) {
                if (Smiley.this.client.getTextField().getText().indexOf("~") == -1) {
                    Smiley.this.client.getTextField().setText(Smiley.this.client.getTextField().getText() + "~");
                }
                Smiley.this.client.setIconPath(Smiley.this.path);
            }
        });
    }

    /**
     * The Overloaded constructor of the class receives iconPath and the privateChat
     * object in the constructor
     * @param privateChat as a PrivateChatWindow object
     * @param path as a String
     */
    public Smiley(PrivateChatWindow privateChat, String path) {
        this.privateChat = privateChat;
        this.path = path;
        this.setPreferredSize(new Dimension(50, 50));
        this.setIcon(new ImageIcon(this.getClass().getResource(path)));
        this.addMouseListener(new MouseAdapter() {

            /**
             * The method mousePressed sets the iconPath to the smiley
             * @param e MouseEvent object that is generated when is clicked on the object
             */
            @Override
            public void mousePressed(MouseEvent e) {
                if (Smiley.this.privateChat.getTextField().getText().indexOf("~") == -1) {
                    Smiley.this.privateChat.getTextField().setText(Smiley.this.privateChat.getTextField().getText() + "~");
                }
                Smiley.this.privateChat.setIconPath(Smiley.this.path);
            }
        });
    }
}
