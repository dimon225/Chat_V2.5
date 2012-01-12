/*
 * Class is a JFrame, a scrollabel txtArea that displays a file with the rules
 * of chess whenever the correspondent menuitem is selected from the Chess Game
 * View.
 * Author: Dimitri, Mario, Val
 * Date: 26/1/2011
 */
package ChessGameKenai;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;

/**
 * HelpTopics class extends JDialog and is the class which creates the Chess Rules
 * windows that you see when the user clicks Chess Rules in the Help menu. It contains
 * a JTextArea and it gets the text from the file ChessRules.txt and appends it to the text area.
 * @author Val
 */
public class HelpTopics extends JDialog {

    //INSTANCE VARIABLES
    private Container c;
    private JTextArea txtArea;
    private JScrollPane scrollPane;
    private ChessBoardView view;

    /**
     * Empty constructor that creates the graphically objects and has a reference
     * to the ChessBoardView to set the location of the JDialog.
     * @param view as a ChessBoardView object
     */
    public HelpTopics(ChessBoardView view) {
        this.view = view;

        c = this.getContentPane();
        txtArea = new JTextArea() {

            /**
             * The method painComponent of chatA
             * is used here to paint the JTexchatA as we want
             * @param g Graphics object used to paint this object
             */
            @Override
            public void paintComponent(Graphics g) {
                int w = txtArea.getWidth();
                int h = txtArea.getHeight();

                URL url = txtArea.getClass().getResource("Icons/background.jpg");
                Toolkit toolkit = this.getToolkit();
                Image image = toolkit.getImage(url);
                g.drawImage(image, 0, 0, w, h, txtArea);
                super.paintComponent(g);
            }
        };

        txtArea.setForeground(Color.WHITE);
        txtArea.setFont(new Font("Verdana", Font.PLAIN, 16));
        txtArea.setOpaque(false);
        txtArea.setLineWrap(true);
        //create the file object to contain the text file with the rules.


        try {
            //use the bufferedreader. File will be "read" one line at a time.
            BufferedReader input = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("TxtFiles/chessrules.txt")));

            try {
                //create a string to contain the reading lines
                String line = null;
                //append what is read to the line as long as the readLine method returns something.
                while ((line = input.readLine()) != null) {

                    txtArea.append(line + "\n");
                }
            } finally {
                input.close();
            }
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(view, "chessrules.txt Not Found", "File Not Found", JOptionPane.ERROR_MESSAGE);
            HelpTopics.this.dispose();
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
        //makes sure the user cannot make changes to the text
        txtArea.setEditable(false);

        scrollPane = new JScrollPane(txtArea);
        txtArea.setCaretPosition(0);

        c.add(scrollPane);

        // set the view
        this.setTitle(" CHESS RULES ");
        this.setSize(800, 400);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(view);
        this.setVisible(true);
    }
}
