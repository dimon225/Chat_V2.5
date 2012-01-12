package ChatProject;

import java.awt.Component;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTML;

/**
 * UserManual class extends JDialog and is used to display the User manual in a
 * JEditorPane. If the user clicks Help--User Manual a new JDialog pops up with the
 * HTML file loaded.
 * @author Dimitri Pankov
 * @see HTML
 * @version 2.2
 */
public class UserManual extends JDialog {

    //INSTANCE VARIABLES
    private JEditorPane pane;
    private JScrollPane scroll;
    private Component component;

    /**
     * Overloaded constructor which receives a reference to the component which could be any GUI object
     * we use it to set its location on the screen to set the location of the dialog in front of the main game. The constructor
     * also has all the components needed to create its GUI.
     * @param component Component object to set location relative to that component
     */
    public UserManual(Component component) {

        this.component = component;

        pane = new JEditorPane();

        scroll = new JScrollPane(pane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pane.setEditable(false);
        pane.setOpaque(false);

        //Create a URL object to store the file location
        URL url = UserManual.class.getResource("HTML/userguide.htm");


        //Set the html file to the JEditorPane
        try {
            pane.setPage(url);
        } catch (FileNotFoundException ex) {
            pane.setText("userguide.htm File Not Found");
        } catch (IOException ee) {
            JOptionPane.showMessageDialog(this, ee.getMessage());
        }

        //Add the scrollpane to the JDialog
        this.add(scroll, "Center");

        //Set the JDialog properties
        this.setSize(700, 650);
        this.setTitle("User Manual");
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(component);
        this.setVisible(true);
    }
}
