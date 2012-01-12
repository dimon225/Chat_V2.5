/* Description: Main.java
 * Author: Dimitri Pankov
 * Date: 18-Feb-2011
 * Version: 1.0
 */
package ChatProject;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JRadioButton;

/**
 * The Main class is the class that runs the whole application
 * Its a frame that has two radio buttons client/server you can choose
 * either client or a server to run the application by clicking the button go
 * the chosen application will pop either client or server
 * @author Dimitri Pankov
 * @see JRadioButton
 * @version 1.1
 */
public class Main extends JFrame {

    /**
     * The empty constructor of the class
     * has all GUI to make up a single small frame
     * this object is needed to call instances of either server or client on demand
     */
    public Main() {
        final JRadioButton jbtnServer = new JRadioButton("Server");
        final JRadioButton jbtnClient = new JRadioButton("Client");
        ButtonGroup btnGroup = new ButtonGroup();

        jbtnClient.setOpaque(false);
        jbtnServer.setOpaque(false);

        jbtnClient.setForeground(Color.WHITE);
        jbtnServer.setForeground(Color.WHITE);

        jbtnClient.setFont(new Font("Verdana", Font.PLAIN, 18));
        jbtnServer.setFont(new Font("Verdana", Font.PLAIN, 18));

        btnGroup.add(jbtnClient);
        btnGroup.add(jbtnServer);
        JButton btnGo = new JButton("Go");
        btnGo.setBackground(Color.ORANGE);
        btnGo.setPreferredSize(new Dimension(100, 25));
        btnGo.addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when button is clicked
             */
            public void actionPerformed(ActionEvent e) {
                if (jbtnClient.isSelected()) {
                    new Client();
                    Main.this.dispose();
                } else if (jbtnServer.isSelected()) {
                    new ServerGUI().updateView();
                    Main.this.dispose();
                }
            }
        });
        JButton btnCancel = new JButton("Cancel");
        btnCancel.setBackground(Color.ORANGE);
        btnCancel.setPreferredSize(new Dimension(100, 25));

        btnCancel.addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when button is clicked
             */
            public void actionPerformed(ActionEvent e) {
                Main.this.dispose();
            }
        });
        CostumPanel mainPanel = new CostumPanel("/Icons/blueswirls.jpg", new BorderLayout());
        CostumPanel pnlJButtons = new CostumPanel();
        pnlJButtons.setOpaque(false);
        CostumPanel pnlButtons = new CostumPanel();
        pnlButtons.setOpaque(false);

        pnlJButtons.add(jbtnClient);
        pnlJButtons.add(jbtnServer);
        pnlButtons.add(btnGo);
        pnlButtons.add(btnCancel);

        //ADD ALL COMPONENTS TO THE MAINPANEL
        mainPanel.add(pnlJButtons, BorderLayout.CENTER);
        mainPanel.add(pnlButtons, BorderLayout.SOUTH);

        //ADD ALL THE COMPONENTS TO THE CONTAINER
        this.add(mainPanel, BorderLayout.CENTER);

        //INITIALIZE JFRAME'S PROPERTIES
        this.setLocation(555, 300);
        this.setResizable(false);
        this.setTitle("Make Your Choice");
        this.setIconImage(new ImageIcon(this.getClass().getResource("/Icons/choose.png")).getImage());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(325, 120);
        this.setVisible(true);

    }

    /**
     * The main method of the class has all GUI components here
     * @param args command line arguments if any
     */
    public static void main(String args[]) {
        new Main();
    }
}
