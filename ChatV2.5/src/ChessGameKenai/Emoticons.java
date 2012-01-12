/*
 * Description: Emoticons.java
 * Author: Mario Bruno
 * Date: 15-Feb-2011
 * Version: 1.0
 */
package ChessGameKenai;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Emoticons class extends JLabel and is used for the chat part of the
 * game. It is a panel which receives the path to the images and paints them on
 * the panel. When a user clicks on a smiley a border is draw around the selected
 * smiley.
 * @author Mario Bruno
 */
public class Emoticons extends JLabel {

    //INSTANCE VARIABLES
    private String imgPath;
    private Chat user;
    private ChessBoardView view;

    /**
     * Overloaded constructor which receives a reference to the chess board view
     * for getting some methods from the class, the image path for setting the smiley
     * gifs on the panel and a reference to the chat class for setting the image on the
     * JTextPane
     * @param view ChessBoardView object
     * @param imgPath String object
     * @param user Chat object
     */
    public Emoticons(ChessBoardView view, String imgPath, Chat user) {
        this.view = view;
        this.imgPath = imgPath;
        this.user = user;
        this.setPreferredSize(new Dimension(50, 50));
        this.setIcon(new ImageIcon(getClass().getResource(imgPath)));



        //Mouse listener for selecting a smiley
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                Emoticons.this.user.setImgPath(Emoticons.this.imgPath);
                Emoticons.this.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 3, 3, 3, new java.awt.Color(255, 153, 0)));
                if (ChessBoardView.getConnectionInstance() != null && Emoticons.this.user.getTxtField().getText().indexOf(" :image ") == -1) {
                    Emoticons.this.user.getTxtField().setText(Emoticons.this.user.getTxtField().getText() + " :image ");

                }
                removeBorder();

            }
        });
    }

    /**
     * remove border method is used to remove the border around the selected smiley.
     * Each time a smiley is selected it removes the border from the previous smiley
     * and then a new one is created on the current smiley.
     */
    public void removeBorder() {
        for (int i = 0; i < view.getPanel().getComponents().length; i++) {
            if (((JLabel) (view.getPanel().getComponents()[i])).getBorder() != null && ((JLabel) (view.getPanel().getComponents()[i])).hashCode() != this.hashCode()) {
                ((JLabel) (view.getPanel().getComponents()[i])).setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 0, 0, new ImageIcon(getClass().getResource("Icons/nothing.gif"))));
            }

        }
    }
}
