/*
 * Description: Class Thumbnail.java
 * Author: Dimtri Pankov
 * Date: 19-Jan-2011
 * Version: 1.0
 */
package ChessGameKenai;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import javax.swing.JComponent;

/**
 * The Thumbnail class is needed for the ChooseIcon class in the ChessBoardView
 * The user will be able to click on the Thumbnail for changing the icon
 * @author Dimitri Pankov
 * @see Comparable
 * @see JComponent
 * @version 1.0
 */
public class Thumbnail extends JComponent implements Comparable {

    private String imagePath;

    /**
     * Overloaded constructor receives the path to the image
     * @param imagePath as String the path for the image
     */
    public Thumbnail(String imagePath) {
        this.imagePath = imagePath;
        this.setPreferredSize(new Dimension(100, 100));
    }

    /**
     * The method painComponent of Thumbnail
     * is used here to paint the JComponent as we want
     * @param g Graphics object used to paint this object
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintChildren(g);
        URL url = this.getClass().getResource(imagePath);
        Toolkit toolkit = this.getToolkit();
        Image image = toolkit.getImage(url);
        g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
    }

    /**
     * The method getImagePath simply returns the image path
     * @return the image path as String
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * The method setImagePath simply sets the path to the image
     * @param imagePath as a String
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * The method compareTo is inherited from Comparable Interface
     * @param o Object to compare
     * @return an integer 0 if equal and -1 or 1 if this object is less or greater respectively
     */
    public int compareTo(Object o) {
        if (!(o instanceof Thumbnail)) {
            throw new ClassCastException("Thumbnail Object Expected");
        }
        return this.getImagePath().compareTo(((Thumbnail) o).getImagePath());
    }
}
