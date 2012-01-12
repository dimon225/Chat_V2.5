/*
 * Description: Class Choose_Icon.java
 * Author: Dimtri Pankov
 * Date: 20-Jan-2011
 * Version: 1.0
 */
package ChessGameKenai;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

/**
 * The Choose_Icon class is a Simple JDialog that is used to
 * display the possible player icons to the user when the user
 * clicks on the icon then clicks next the icon he chose will represent him
 * This JDialog is only used if the user wants to choose another icon to represent himself
 * @author Dimitri Pankov
 * @see JDialog
 * @author 1.5
 */
public class Choose_Icon extends JDialog {

    private Container c;
    private JPanel southPanel;
    private JScrollPane scroll;
    private TreeMap<Integer, Thumbnail> map = new TreeMap<Integer, Thumbnail>();
    private JRadioButton pl1Button, pl2Button;
    private ButtonGroup btnGroup;
    private JPanel panel;
    private JButton applyButton;
    private Chess_Data data;
    private ConnectionBridge bridge;
    private String playerIconPath;

    /**
     * Overloaded constructor of the class
     * Has all needed GUI to represent itself graphically on the screen
     * @param data as Chess_Data
     * @param bridge as a ConnectionBridge
     */
    public Choose_Icon(Chess_Data data, ConnectionBridge bridge) {
        this.bridge = bridge;
        this.data = data;
        c = this.getContentPane();

        //CREATE AND INITIALIZE THE APPLY BUTTON
        applyButton = new JButton(new ImageIcon(getClass().getResource("Icons/next.gif")));
        applyButton.setBackground(new Color(139, 69, 19));
        applyButton.setPreferredSize(new Dimension(100, 30));
        applyButton.addActionListener(new ActionListener() {

            /**
             * The actionPerfomed method of our class
             * @param e ActionEvent object that is generated when button is clicked
             */
            public void actionPerformed(ActionEvent e) {
                if (Choose_Icon.this.data.isGameOnLine()) {
                    Choose_Icon.this.sendPlayerIcon();
                }
                Choose_Icon.this.dispose();
            }
        });

        btnGroup = new ButtonGroup();

        pl1Button = new JRadioButton("Player 1");
        pl1Button.setBackground(Color.GRAY);
        pl1Button.setFont(new Font("Verdana", Font.BOLD, 16));

        pl2Button = new JRadioButton("Player 2");
        pl2Button.setBackground(Color.GRAY);
        pl2Button.setFont(new Font("Verdana", Font.BOLD, 16));

        this.showPossiblePlayerIcons();

        btnGroup.add(pl1Button);
        btnGroup.add(pl2Button);

        panel = new JPanel();
        panel.setBackground(Color.GRAY);
        panel.add(pl1Button);
        panel.add(pl2Button);
        panel.add(applyButton);

        this.setModal(true);

        southPanel = new JPanel();
        southPanel.setBackground(Color.GRAY);

        //ADD THE SMILEYS TO THE DIALOG
        for (int i = 1; i < 16; i++) {
            map.put(new Integer(i), new Thumbnail("Icons/hercules" + i + ".gif"));
            southPanel.add(map.get(i));
            map.get(i).addMouseListener(new MouseAdapter() {

                /**
                 * The method mousePressed is overwritten in our class
                 * @param e MouseEvent object that is generated when mouse is clicked
                 */
                @Override
                public void mousePressed(MouseEvent e) {
                    Thumbnail thumbnail = (Thumbnail) e.getSource();
                    Set set = map.entrySet();
                    Iterator it = set.iterator();
                    while (it.hasNext()) {
                        Map.Entry me = (Map.Entry) it.next();
                        if (((Thumbnail) me.getValue()).getBorder() != null) {
                            ((Thumbnail) me.getValue()).setBorder(new LineBorder(Color.GRAY, 5));
                        }
                    }

                    if (pl1Button.isSelected()) {
                        playerIconPath = thumbnail.getImagePath();
                        Choose_Icon.this.data.getPlayers().get(0).setImagePath(thumbnail.getImagePath());
                    } else if (pl2Button.isSelected()) {
                        playerIconPath = thumbnail.getImagePath();
                        Choose_Icon.this.data.getPlayers().get(1).setImagePath(thumbnail.getImagePath());
                    }
                    Choose_Icon.this.data.setPlayers(Choose_Icon.this.data.getPlayers());
                    thumbnail.setBorder(new LineBorder(Color.GREEN, 5));
                }
            });
        }
        scroll = new JScrollPane(southPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        //ADD COMPONENTS TO THE CONTAINER
        c.add(scroll, BorderLayout.CENTER);
        c.add(panel, BorderLayout.SOUTH);

        //INITIALIZES JFRAMES' PROPERTIES
        this.setTitle("Choose Icon");
        this.setLocation(500, 300);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setSize(600, 200);
        this.setVisible(true);
    }

    /**
     * The method sendPlayerIcon sends the path to the icon
     * to the other player that he is playing against if the game is online
     * if the game is online of not his method is skipped
     */
    public void sendPlayerIcon() {
        try {
            Packet packet = new Packet();
            packet.setPlayerIconPath(playerIconPath);
            bridge.getOutputStream().writeObject(packet);
            bridge.getOutputStream().flush();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.toString());
        }
    }

    /**
     * The method showPossiblePlayerIcons is used if the game is online
     * so the player can only choose icon for him self that is done
     * by disabling the radio button of the other player
     */
    private void showPossiblePlayerIcons() {
        if (data.isGameOnLine()) {
            if (data.isServer()) {
                pl2Button.setEnabled(false);
                pl1Button.setSelected(true);
            } else {
                pl1Button.setEnabled(false);
                pl2Button.setSelected(true);
            }
        }
    }
}
