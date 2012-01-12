/*
 * Description: Class CostumPanel.java
 * Author: Dimtri Pankov
 * Date: 13-Feb-2011
 * Version: 1.0
 */
package ChessGameKenai;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.net.URL;
import javax.swing.JPanel;

/**
 * The CostumPanel class is JPanel and a bit more
 * instead overriding the paintComponent method of each panel i want to paint
 * i have decided to have the CostumPanel that will receive path to the image
 * and paint itself accordingly
 * @author Dimitri Pankov
 * @see JPanel
 * @version 1.1
 */
public class CostumPanel extends JPanel {

    private String path;
    private LayoutManager layout;

    /**
     * Empty Constructor of the class
     * sets the layout to default FlowLayout
     */
    public CostumPanel() {
        this.setLayout(new FlowLayout());
    }

    /**
     * The overloaded constructor of the class receives path to the mage
     * as well as the LayoutManager to use
     * @param path as a String
     * @param layout as a layoutManager
     */
    public CostumPanel(String path, LayoutManager layout) {
        this.path = path;
        this.layout = layout;
        this.setLayout(layout);
    }

    /**
     * Overloaded constructor of the class receives the path
     * to the image
     * @param path as a String
     */
    public CostumPanel(String path) {
        this.path = path;
        this.setLayout(new FlowLayout());
    }

    /**
     * Overloaded constructor of the class receives a LayoutManager in the constructor
     * @param layout as a LayoutManager
     */
    public CostumPanel(LayoutManager layout) {
        this.layout = layout;
        this.setLayout(layout);
    }

    /**
     * The paintComponent method that is needed when painting costumPanel
     * the path to the image is checked and if the path is not null paints the panel accordingly
     * @param g as Graphics object is used to paint this panel
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (path != null) {
            URL url = this.getClass().getResource(path);
            Toolkit toolkit = this.getToolkit();
            Image image = toolkit.getImage(url);
            g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
        }
    }

    /**
     * The method getLayoutManager simply returns the LayoutManager of the CostumPanel to the caller
     * @return layout as a LayoutManager
     */
    public LayoutManager getLayoutManager() {
        return layout;
    }
}
