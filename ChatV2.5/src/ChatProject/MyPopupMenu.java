/*
 * Description: Class MyPopupMenu.java
 * Author: Dimtri Pankov
 * Date: 14-Feb-2011
 * Version: 1.0
 */
package ChatProject;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.text.SimpleAttributeSet;
import ChessGameKenai.*;

/**
 * The MyPopup menu is a JPopup menu i have decided not to use
 * buttons but instead the right click of the mouse will show all choices to the user
 * This is the most simple class of my application it is brief and only creates JPopup menu
 * and then registers it with action listener
 * @author Dimitri Pankov
 * @see JPopupMenu
 * @see PrivateChatWindow
 * @version 1.2
 */
public class MyPopupMenu extends JPopupMenu {

    private JMenuItem item;
    private ActionListener listener;
    private JList list;
    private Client client;
    private PrivateChatWindow chatWindow;
    private JFileChooser chooser = new JFileChooser(".");
    private int returnValue;
    private PublicFileTransfer publicFileTransfer;
    private Start_Game start;

    /**
     * The overloaded constructor of the class has the references to the JList as well
     * as to the client for calling their methods inside here like JList's getSelected index etc....
     * and some of clients methods as well
     * @param list as a JList object
     * @param client as a Client Object
     */
    public MyPopupMenu(final JList list, final Client client) {
        this.list = list;
        this.client = client;
        listener = new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when menu item is clicked
             */
            public void actionPerformed(ActionEvent e) {
                if (list.getSelectedIndex() > -1 && !list.getSelectedValue().equals(client.getClientName())) {
                    if (e.getActionCommand().equals("Private Chat") && client.getPrivateChatWindow() == null && chatWindow == null) {
                        chatWindow = new PrivateChatWindow(client, client.getSocket().getInetAddress(), true);
                        chatWindow.setGuestName((String) list.getSelectedValue());
                    } else if (e.getActionCommand().equals("Transfer File")) {
                        if ((returnValue = chooser.showDialog(client, "Choose File To Send")) == JFileChooser.APPROVE_OPTION) {
                            File file = chooser.getSelectedFile();
                            MyPopupMenu.this.transferFile(file);
                        }
                    } else if (e.getActionCommand().equals("Play Chess Game")) {
                        MyPopupMenu.this.requestChessGame();
                    }
                }
                client.setGuestName((String) list.getSelectedValue());
            }
        };
        this.add(item = new JMenuItem("Private Chat"));
        item.addActionListener(listener);
        this.add(item = new JMenuItem("Transfer File"));
        item.addActionListener(listener);
        this.add(item = new JMenuItem("Play Chess Game"));
        item.addActionListener(listener);
    }

    /**
     * The method transferFile when the menu item is clicked this method is called
     * the job of this method is simply first ask the user whether he is in a public or local network
     * if the client is chatting on the local network the mapped IP address of the client will be forwarded to
     * the client he wants to send the file to and the connection will be established immediately all needed information will
     * be also forwarded to that client but if the client is actually on the public network the IP will be taken off the special
     * web site we use URL object to connect to that web site readLine and that line is the public IP address and that address will be in turn
     * forwarded to the client to establish a connection for sending the file some other information is also sent to the client to use
     * such as name IPAddress etc........
     * @param file as a File
     */
    public void transferFile(File file) {
        try {
            Packet packet = new Packet();
            returnValue = JOptionPane.showConfirmDialog(this, "Are You In The Public Network?", "IP Confirmation!", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (returnValue == JOptionPane.YES_OPTION) {
                this.getPublicIP(packet, file);
            } else {
                packet.setCreatePrivateChat(client.getClientName());
                packet.setClientPositionForPrivateChat(client.getList().getSelectedIndex());
                packet.setFileSize(file.length());
                packet.setFilePath(file.getName());
                packet.setPublicChatTransfer("public transfer");
                client.getOutputStream().writeObject(packet);
                client.getOutputStream().flush();
            }
            publicFileTransfer = new PublicFileTransfer(client, true, null, file, null, (int) file.length());
        } catch (Exception e) {
            client.appendString(e.toString(), new SimpleAttributeSet(), Color.RED);
        }
    }

    /**
     * The method getPublicFileTransfer returns PublicFileTransfer to the caller
     * @return as a PublicFileTransfer
     */
    public PublicFileTransfer getPublicFileTransfer() {
        return publicFileTransfer;
    }

    /**
     * The method requestChessGame simply sends a request for the chess game to
     * the other user and starts the server if the user that receives the request
     * replies yes th3 game will automatically starts if he replies no the message is sent back here
     * and closes the server socket on this side of the connection
     */
    public void requestChessGame() {
        Packet packet = new Packet();
        returnValue = JOptionPane.showConfirmDialog(this, "Are You In The Public Network?", "IP Confirmation!", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (returnValue != -1) {
            try {
                if (returnValue == JOptionPane.YES_OPTION) {
                    this.getPublicIP(packet);
                } else {
                    packet.setRequestChessGame(client.getClientName());
                    packet.setClientIndex(list.getSelectedIndex());
                    client.getOutputStream().writeObject(packet);
                    client.getOutputStream().flush();
                    client.appendString("\n Request is in proccess.......", new SimpleAttributeSet(), Color.RED);
                }
                start = new Start_Game();
                Thread t = new Thread(new Runnable() {

                    public void run() {
                        Start_Game.getInstance().startGameThroughChat(true, null, client);
                    }
                });
                t.setPriority(Thread.MAX_PRIORITY);
                t.start();

            } catch (Exception e) {
                client.appendString("\n" + e.toString(), new SimpleAttributeSet(), Color.RED);
            }
        }
    }

    /**
     * The method getPublicIP will get the public ipAdress if the use selects yes
     * of the confirm dialog which pops up when the client tries to connect
     * it asks him if he is in a local or public network if public chosen this method is executed
     * the public address will be found on the site http://www.whatismyip.com/automation/n09230945.asp
     * we use URL object to connect then readLine store it in the publicIP which will then be sent
     * to the user that will connect to u using this ipAddress you start the server right away
     * @param packet as a Packet
     * @param file as a File
     * @throws Exception when working with sockets risks are unavoidable
     */
    public void getPublicIP(Packet packet, File file) throws Exception {
        URL whatismyip = new URL("http://www.whatismyip.com/automation/n09230945.asp");
        BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
        InetAddress publicIP = InetAddress.getByName(in.readLine());
        packet.setPublicIP(publicIP);
        packet.setClientPositionForPrivateChat(client.getList().getSelectedIndex());
        packet.setCreatePrivateChat(client.getClientName());
        packet.setFileSize(file.length());
        packet.setFilePath(file.getName());
        packet.setPublicChatTransfer("public transfer");
        client.getOutputStream().writeObject(packet);
        client.getOutputStream().flush();
    }

    /**
     * The method getPublicIP will get the public ipAdress if the use selects yes
     * of the confirm dialog which pops up when the client tries to connect
     * it asks him if he is in a local or public network if public chosen this method is executed
     * the public address will be found on the site http://www.whatismyip.com/automation/n09230945.asp
     * we use URL object to connect then readLine store it in the publicIP which will then be sent
     * to the user that will connect to u using this ipAddress you start the server right away
     * @param packet as a Packet
     * @throws IOException when working with sockets risks are unavoidable
     */
    public void getPublicIP(Packet packet) throws IOException {
        URL whatismyip = new URL("http://www.whatismyip.com/automation/n09230945.asp");
        BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
        InetAddress publicIP = InetAddress.getByName(in.readLine());
        in.close();
        packet.setPublicIP(publicIP);
        packet.setClientIndex(client.getList().getSelectedIndex());
        packet.setRequestChessGame(client.getClientName());
        client.getOutputStream().writeObject(packet);
        client.getOutputStream().flush();
    }
}
