/*
 * Description: Class Packet.java
 * Author: Dimtri Pankov
 * Date: 23-Feb-2011
 * Version: 1.0
 */
package ChessGameKenai;

import java.awt.Color;
import java.io.Serializable;
import javax.swing.text.SimpleAttributeSet;

/**
 * The Packet class is a serialized object that we use to communicate with the client
 * and the server we simply set needed instance variables and then we extract them on the other side
 * This class is solely for communication purposes over network as serialized objects
 * @author Dimitri Pankov
 * @see Serializable
 * @version 1.5
 */
public class Packet implements Serializable {

    private String message;
    private String guestName;
    private String playerIconPath;
    private String restartGame;
    private String imgPath;
    private SimpleAttributeSet smpSet;
    private Color color;
    private String confirmRestart;

    /**
     * The method getConfirmRestart is used to confirm if the user
     * wants to restart the game or not when the user clicks restart game the confirmation
     * will be sent to the other user if he really wants to restart the game or not
     * @return confirmRestart as a String
     */
    public String getConfirmRestart() {
        return confirmRestart;
    }

    /**
     * The method setConfirmRestart sets the confirmation for restart game
     * @param confirmRestart as a String
     */
    public void setConfirmRestart(String confirmRestart) {
        this.confirmRestart = confirmRestart;
    }

    /**
     * The method getRestartGame simply returns the null or not null
     * if not null the game is restarted if null not restarted
     * @return as a String
     */
    public String getRestartGame() {
        return restartGame;
    }

    /**
     * The method getColor simply returns the color to the caller
     * it is used in the TextPane for coloring strings
     * @return as a Color
     */
    public Color getColor() {
        return color;
    }

    /**
     * The method setColor simply sets the color of the string
     * @param color as a Color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * The method getImgPath simply returns the image path to the caller
     * @return imgPath as a String
     */
    public String getImgPath() {
        return imgPath;
    }

    /**
     * The setImgPath method simply sets the image path
     * @param imgPath as a string
     */
    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    /**
     * The method getSmpSet simple returns the SimpleAttributeSet to the caller
     * this object is used by the TextPane to insert strings with different color and attributes
     * @return smpSet as a SimpleAttributeSet
     */
    public SimpleAttributeSet getSmpSet() {
        return smpSet;
    }

    /**
     * The method setSmpSet simply sets the SimpleAttributeSet of the current string
     * @param smpSet as a SimpleAttributeSet
     */
    public void setSmpSet(SimpleAttributeSet smpSet) {
        this.smpSet = smpSet;
    }

    /**
     * The method setRestartGame simply sets the value to not null when
     * the game needs to be restarted
     * @param restartGame as a String
     */
    public void setRestartGame(String restartGame) {
        this.restartGame = restartGame;
    }

    /**
     * The getPlayerIconPath simply returns the icon path to the caller
     * @return playerIconPath as a String
     */
    public String getPlayerIconPath() {
        return playerIconPath;
    }

    /**
     * The method setPlayerIconPath sets the needed iconPath
     * @param playerIconPath as a String
     */
    public void setPlayerIconPath(String playerIconPath) {
        this.playerIconPath = playerIconPath;
    }

    /**
     * The getGuestGame returns the guest name to the caller
     * @return as a String
     */
    public String getGuestName() {
        return guestName;
    }

    /**
     * The method setGuestName sets the guest name
     * @param guestName as a String
     */
    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    /**
     * The method getMessage simply returns the message to the caller
     * @return as a String
     */
    public String getMessage() {
        return message;
    }

    /**
     * The setMessage the method setMessage simply sets the message to
     * needed staring for extraction by the receiving end server or client exchange message through sockets
     * @param message as a String
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
