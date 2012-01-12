/*
 * Description: About.java
 * Author: Mario Bruno
 * Date: 28-Feb-2011
 * Version: 2.0
 */
package ChessGameKenai;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * About class extends JDialog and is used to simply display the about window.
 * The use will see this window when they click Help-About in the game. The about dialog
 * contains simple scrolling animation made in a thread. It draws String and 2 images
 * on the center panel. It also has a close button.
 * @author Mario
 */
public class About extends JDialog {

    //INSTANCE VARIABLES
    private JButton btnClose = new JButton("Close");
    private CostumPanel pnlClose, pnlText, pnlTop, pnlAll;
    private JLabel lblTitle;
    private Container c;
    private int Ypos = 200;
    private volatile boolean isKill = true;
    private ChessBoardView view;

    /**
     * Overloaded constructor which receives a reference to the ChessBoardView class
     * only to set the location of the dialog in front of the main game. The constructor
     * also has all the components needed to create its gui.
     * @param view ChessBoardView object
     */
    public About(ChessBoardView view) {
        this.view = view;

        c = this.getContentPane();


        //Start the animation thread
        Animation animate = new Animation();
        animate.makePanel();
        animate.start();

        pnlClose = new CostumPanel();
        pnlClose.setOpaque(false);
        pnlTop = new CostumPanel();
        pnlTop.setOpaque(false);
        pnlAll = new CostumPanel("Icons/background.jpg", new BorderLayout());

        //Window listener that kills the thread once you close the window.
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                isKill = false;
            }
        });

        lblTitle = new JLabel("Chess'N'Chat");
        lblTitle.setFont(new Font("Verdana", Font.BOLD, 23));
        lblTitle.setForeground(Color.WHITE);

        //Action listener when the user clicks the close button. It closed the dialog and kills the thread
        btnClose.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                About.this.dispose();
                isKill = false;

            }
        });

        //Add the components to thier panel
        pnlTop.add(lblTitle);
        pnlClose.add(btnClose);
        pnlAll.add(pnlTop, BorderLayout.NORTH);
        pnlAll.add(pnlText, BorderLayout.CENTER);
        pnlAll.add(pnlClose, BorderLayout.SOUTH);

        //Add main panel to the container
        c.add(pnlAll);

        //Set the JDialog properties
        this.setSize(350, 300);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(view);
        this.setTitle("About");
        this.setVisible(true);


    }

    /**
     * Animation class is a thread used for the vertical scrolling animation.
     * It works by repainting the panel every 44 milliseconds
     */
    class Animation extends Thread {

        @Override
        public void run() {
            while (isKill) {
                repaint();
                try {
                    Thread.sleep(44);
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                    isKill = false;
                }
            }
        }

        /**
         * makePanel is the method that creates the center panel. It uses the
         * paintComponent method to draw the strings and images on the panel.
         * Once the text has reached the top of the panel, it just stops there.
         */
        public void makePanel() {
            final Image image = new ImageIcon(getClass().getResource("Icons/logo.gif")).getImage();
            final Image image2 = new ImageIcon(getClass().getResource("Icons/logo.gif")).getImage();

            pnlText = new CostumPanel() {

                @Override
                public void paintComponent(Graphics g) {

                    g.setFont(new Font("Arial", Font.PLAIN, 18));
                    g.setColor(Color.ORANGE);
                    g.drawString("Created By:", 120, Ypos);
                    g.drawImage(image, 30, Ypos + 35, this);
                    g.drawImage(image2, 230, Ypos + 35, this);
                    g.drawString("Dimitri", 135, Ypos + 35);
                    g.drawString("Mario", 135, Ypos + 65);
                    g.drawString("Val", 135, Ypos + 95);
                    g.drawString("Version 2.0", 118, Ypos + 135);
                    Ypos--;

                    //Once the texts have reached the top of the panel, keep the Y position
                    if (Ypos <= 15) {
                        Ypos = 15;
                        isKill = false;

                    }
                }
            };
            pnlText.setOpaque(false);
        }
    }
}
