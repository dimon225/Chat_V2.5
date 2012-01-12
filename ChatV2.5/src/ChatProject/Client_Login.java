/* Description: Client_Login.java
 * Author: Dimitri Pankov
 * Date: 14-Feb-2011
 * Version: 1.0
 */
package ChatProject;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * The Client_Login class is a little JDialog that asks the user or IP and Port
 * When receives the IP and the port from the user it passes the information to the client class
 * which in turn uses that information to properly connect
 * @author Dimitri Pankov
 * @see JDialog
 * @version 1.0
 */
public class Client_Login extends JDialog {

    private JLabel lblName;
    private JTextField txtName;
    private Client client;
    private JButton btnApply, btnCancel;
    private CostumPanel pnlName, pnlButtons;

    /**
     * Overloaded constructor of the class receives client reference in the
     * constructor for later communication with the client object
     * @param client as a Client
     */
    public Client_Login(Client client) {
        this.client = client;
        lblName = new JLabel("Enter You Name");
        lblName.setForeground(Color.WHITE);
        txtName = new JTextField(12);
        txtName.addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when button is clicked
             */
            public void actionPerformed(ActionEvent e) {
                if (!txtName.getText().trim().equals("")) {
                    Client_Login.this.client.setClientName(txtName.getText().trim());
                }
                Client_Login.this.dispose();
            }
        });
        btnApply = new JButton("Apply");
        btnApply.addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when button is clicked
             */
            public void actionPerformed(ActionEvent e) {
                if (!txtName.getText().trim().equals("")) {
                    Client_Login.this.client.setClientName(txtName.getText().trim());
                }
                Client_Login.this.dispose();
            }
        });
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {

            /**
             * The actionPerformed method is called whenever the listener detects action
             * @param e ActionEvent object that is generated when button is clicked
             */
            public void actionPerformed(ActionEvent e) {
                Client_Login.this.dispose();
            }
        });
        pnlName = new CostumPanel("/Icons/back.jpg");
        pnlName.add(lblName);
        pnlName.add(txtName);
        pnlButtons = new CostumPanel("/Icons/back.jpg");
        pnlButtons.add(btnApply);
        pnlButtons.add(btnCancel);

        this.setModal(true);
        this.setIconImage(new ImageIcon(this.getClass().getResource("/Icons/login.png")).getImage());

        this.getContentPane().add(pnlName, BorderLayout.CENTER);
        this.getContentPane().add(pnlButtons, BorderLayout.SOUTH);

        //INITIALIZE JFRAMES'S PROPERTIES
        this.setSize(278, 115);
        this.setLocationRelativeTo(client);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setTitle("Chat Login");
        this.setVisible(true);
    }
}
