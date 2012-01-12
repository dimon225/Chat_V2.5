/*
 * Description: Class CapturedPieces.java
 * Author: Dimtri Pankov
 * Date: 4-Feb-2011
 * Version: 1.0
 */
package ChessGameKenai;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 * The CapturedPieces class is the panel that our view
 * uses to show the captured pieces on. ChessBoardView class has two such panels
 * one is for displaying white captured pieces and the other one is for black captured pieces
 * This class is also an observer so each time any change happens to the Data which is CHess_Data class
 * it is notified by executing its update method in the update method capturedPieces object asks the data class
 * if any piece was captured if yes it takes it nd adds itself
 * @author Dimitri Pankov
 * @see Observer
 * @version 1.5
 */
public class CapturedPieces extends JPanel implements Observer {

    private Color color;
    private Board board;

    /**
     * Overloaded constructor of the class when the object
     * of this type is created it will contain this information such as Color and reference to the Board
     * Board holds all the pieces in the array list so each time the piece is added to the captured piece
     * panel it has to be removed from the board
     * @param color as a Color
     * @param board as a Board
     */
    public CapturedPieces(Color color, Board board) {
        this.color = color;
        this.board = board;
        this.setLayout(new FlowLayout());
        this.setPreferredSize(new Dimension(188, 750));
        this.setOpaque(false);

        //CREATE BORDER AND SET IT TO THE DETAILS PANEL
        TitledBorder border2 = new TitledBorder(this.getTitle());
        border2.setTitleFont(new Font("Times Roman", Font.PLAIN, 17));
        border2.setTitleColor(Color.WHITE);
        this.setBorder(border2);
        this.setOpaque(false);

    }

    /**
     * The method toString is overwritten by our class
     * this is how our object represents itself literally as a String
     * @return s as a String
     */
    @Override
    public String toString() {
        return "CapturedPieces{" + "color=" + color + '}';
    }

    /**
     * The method getColor simply returns the color to the caller
     * @return color as a Color
     */
    public Color getColor() {
        return color;
    }

    /**
     * The method setColor simply sets the color of this object
     * @param color as a Color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * The method getTitle simply returns the title to the caller
     * depending on its color black or white
     * @return title as a String
     */
    private String getTitle() {
        String title = "";
        if (color == Color.WHITE) {
            title = "White Captured Pieces";
            return title;
        } else {
            title = "Black Captured Pieces";
            return title;
        }
    }

    /**
     * The method painComponent of whiteCapturedPiecesPanel
     * is used here to paint the JPanel as we want
     * @param g Graphics object used to paint this object
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = this.getWidth();
        int h = this.getHeight();

        URL url = this.getClass().getResource("Icons/background.jpg");
        Toolkit toolkit = this.getToolkit();
        Image image = toolkit.getImage(url);
        g.drawImage(image, 0, 0, w, h, this);
    }

    /**
     * The method update is inherited from the Observer Interface
     * when this method is called it checks if there any captured pieces
     * if it finds any it adds them to the captured pieces panel depending on the color
     * of the pieces and itself. For example white captured pieces will be added to the instance of this class
     * which has an instance variable color that equals white or to the black if the pieces are of the black color
     * @param o as an Observable object
     * @param arg as an Object any object passed in
     */
    public void update(Observable o, Object arg) {
        Chess_Data data = (Chess_Data) o;
        VisualPiece pi = null;
        if (!data.getCapturedPieces().isEmpty()) {
            Non_Visual_Piece p = (Non_Visual_Piece) data.getCapturedPieces().get(data.getCapturedPieces().size() - 1);
            if (board.getSquares().get(p.getPosition() - 1).getComponentCount() > 0) {
                pi = (VisualPiece) board.getSquares().get(p.getPosition() - 1).getComponent(0);
                if (this.getColor() == Color.WHITE && p.getColor() == Color.WHITE && p.isCaptured() && pi.getColor() == Color.WHITE) {
                    pi.setPreferredSize(new Dimension(64, 64));
                    this.add(pi);
                } else if (this.getColor() == Color.BLACK && p.getColor() == Color.BLACK && p.isCaptured() && pi.getColor() == Color.BLACK) {
                    pi.setPreferredSize(new Dimension(64, 64));
                    this.add(pi);
                }
            }
        }
        this.revalidate();
        this.repaint();
    }
}
