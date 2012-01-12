/*
 * Description: Class PromptUser.java
 * Author: Dimtri Pankov
 * Date: 5-Mar-2011
 * Version: 1.0
 */
package ChatProject;

import ChessGameKenai.Start_Game;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.SimpleAttributeSet;

/**
 * The PromptPublicFileTransfer class is the panel with two buttons that are added
 * to the for confirming the file transfer this class is purely GUI
 * @author Dimitri Pankov
 * @see CostumPanel
 * @version 1.1
 */
public class PromptUser extends CostumPanel {

    private JLabel lblPromptUser;
    private JPanel pnlCenter, pnlSouth;
    private JButton btnAccept, btnDecline;
    private Client client;
    private InetAddress ipAddress;

    /**
     * Empty constructor of the class
     * Has all needed GUI to represent itself graphically
     * on the screen it also has two buttons Accept and Decline
     * if the client click accept it connects directly to the server of the other client
     * and starts the reading thread with the specified file in the specified directory
     * if the client clicks decline the message is sent back to the client that wants to send the
     * the file to tell him of the disapproval and close its serverSocket
     * @param client as a Client
     * @param ipAddress as an InetAddress
     */
    public PromptUser(final Client client, final InetAddress ipAddress) {
        super(new BorderLayout());
        this.client = client;
        this.ipAddress = ipAddress;
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
                    new Start_Game();
                    btnAccept.setEnabled(false);
                    btnDecline.setEnabled(false);
                    Thread t = new Thread(new Runnable() {

                        public void run() {
                            Start_Game.getInstance().startGameThroughChat(false, ipAddress, client);
                        }
                    });
                    t.setPriority(Thread.MAX_PRIORITY);
                    t.start();
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
                    PromptUser.this.declineGame();
                    btnAccept.setEnabled(false);
                    btnDecline.setEnabled(false);
                    client.appendString("\nTransfer canceled by the user", new SimpleAttributeSet(), Color.RED);
                } catch (Exception ex) {
                    client.appendString(ex.toString(), new SimpleAttributeSet(), Color.RED);
                }
            }
        });
        lblPromptUser = new JLabel("Accept/Decline", JLabel.LEFT);
        lblPromptUser.setForeground(Color.BLACK);
        lblPromptUser.setFont(new Font("Veradana", Font.PLAIN, 16));

        pnlCenter.add(lblPromptUser);
        pnlCenter.add(btnAccept);
        pnlCenter.add(btnDecline);

        pnlSouth.add(btnAccept);
        pnlSouth.add(btnDecline);

        this.add(pnlCenter, BorderLayout.CENTER);
        this.add(pnlSouth, BorderLayout.SOUTH);

        this.setOpaque(false);
    }

    /**
     * The method declineGame simply declines the game of chess
     * if the user received a request to play chess against someone and he declines
     * this method is executed it sends a messages back to the user that send a request
     * and tells him that the game was declined
     * @throws IOException when working with sockets risks are unavoidable
     */
    public void declineGame() throws IOException {
        Packet packet = new Packet();
        packet.setGameDecline("no game");
        packet.setClientIndex(client.getUserNames().indexOf(client.getGuestName()));
        client.getOutputStream().writeObject(packet);
        client.getOutputStream().flush();
    }
}
