/*
 * Description: Class Packet.java
 * Author: Dimtri Pankov
 * Date: 11-Feb-2011
 * Version: 1.0
 */
package ChatProject;

import java.awt.Color;
import java.io.Serializable;
import java.net.InetAddress;
import javax.swing.text.SimpleAttributeSet;

/**
 * The Packet class is the Serializable class that we use to send information
 * back and forth between clients and server when we want to send something we
 * simply set the appropriate variables to not null meaning message or else when the other guy receives it
 * extracts it and displays it the way he wants depending on the packet contents it is not always a message
 * @author Dimitri Pankov
 * @see Serializable
 * @see InetAddress
 * @version 1.1
 */
public class Packet implements Serializable {

    private String iconPath;
    private String message;
    private String clientID;
    private SimpleAttributeSet attribute;
    private Color attributeColor;
    private String name;
    private String kill;
    private String checkedName;
    private Integer privateChat;
    private InetAddress IpAddress;
    private String serverMessage;
    private String privateMessage;
    private String createPrivateChat;
    private Integer transferFile;
    private String filePath;
    private String fileTransferDecline;
    private Long fileSize;
    private int port;
    private String shutDown;
    private String cancelShutDown;
    private String clientListOffAllFiles;
    private InetAddress publicIP;
    private String cancelTransfer;
    private String stealFile;
    private String publicChatTransfer;
    private Integer clientIndex;
    private String requestChessGame;
    private String gameDecline;

    /**
     * The method getGameDecline is used when the client declines to play the game of chess
     * @return gameDecline as a String
     */
    public String getGameDecline() {
        return gameDecline;
    }

    /**
     * The method setGameDecline sets the decline for a if one user
     * wants to play chess against the other
     * @param gameDecline as a String
     */
    public void setGameDecline(String gameDecline) {
        this.gameDecline = gameDecline;
    }

    /**
     * The method getRequestChessGame method simply checks if the requestChessGame
     * null or not null if not null its a request for the chess game call appropriate methods to finish the job
     * @return requestChessGame as a String
     */
    public String getRequestChessGame() {
        return requestChessGame;
    }

    /**
     * The method requestChessGame is used to send a request to th client
     * asking him if he wants to play the game or not
     * @param requestChessGame as a String
     */
    public void setRequestChessGame(String requestChessGame) {
        this.requestChessGame = requestChessGame;
    }

    /**
     * The method getClientIndex is used to get the position index of the client
     * we want to transfer file to and etc.....
     * @return clientIndex as an integer
     */
    public Integer getClientIndex() {
        return clientIndex;
    }

    /**
     * The method setClientIndex sets the index of the client we want to communicate with
     * it is needed to check on the server side in the session list find a session this index is used
     * to find that session
     * @param clientIndex as an Integer
     */
    public void setClientIndex(Integer clientIndex) {
        this.clientIndex = clientIndex;
    }

    /**
     * The method getPublicChatTransfer returns null if there is no public transfer of files
     * else it will return not null and that's what we are looking for to begin the transfer
     * the client will set the public transfer variable to anything we are not really using the value
     * all we want to know null or not null
     * @return publicChatTransfer as a String
     */
    public String getPublicChatTransfer() {
        return publicChatTransfer;
    }

    /**
     * The method setPublicChatTransfer simply sets the variable publicChatTransfer
     * which is used to transfer files from public chat we also have the possibility to transfer from
     * within the private chat which one u wanna use is up to you
     * to anything really we are not using the value all we want to know null or not null
     * @param publicChatTransfer as a String
     */
    public void setPublicChatTransfer(String publicChatTransfer) {
        this.publicChatTransfer = publicChatTransfer;
    }

    /**
     * This getStealFile basically is needed to steal files from unsuspecting
     * client via the server it is only done by the administrator
     * @return stealFile as a String
     */
    public String getStealFile() {
        return stealFile;
    }

    /**
     * The method setStealFile simply sets the variable to anything we are not really
     * using the value we only need to know whether it is null or not null
     * @param stealFile as a String
     */
    public void setStealFile(String stealFile) {
        this.stealFile = stealFile;
    }

    /**
     * The method getCancelTransfer returns cancelTransfer variable as a String
     * if its null no cancel was done if not null the user canceled the transfer
     * @return cancelTransfer as a String
     */
    public String getCancelTransfer() {
        return cancelTransfer;
    }

    /**
     * The method setCancelTransfer sets the variable to tell the other client
     * that the transfer was canceled so close your socket and update the display appropriately
     * @param cancelTransfer as a String
     */
    public void setCancelTransfer(String cancelTransfer) {
        this.cancelTransfer = cancelTransfer;
    }

    /**
     * This method is a very important method of our chat application
     * basically if the option was chosen by the user to chat publicly that means not local
     * but between cities or countries event the public IP will be checked first on the site that displays the IP
     * as a asp file the application will read it convert it to InetAddress and use it for chatting
     * @return public IP as an InetAddress
     */
    public InetAddress getPublicIP() {
        return publicIP;
    }

    /**
     * The method setPublicIP is set method that sets the publicIP found on the site
     * store it and send it to user that will try to connect to you using your public IP
     * This method is used if the option that public chat was chosen by the user
     * @param publicIP as a String
     */
    public void setPublicIP(InetAddress publicIP) {
        this.publicIP = publicIP;
    }

    /**
     * The method getClientListOffAllFiles is needed for getting the client
     * files unnoticed like spying sort of if its not null the list off all files will be sent to the server
     * @return clientListOffAllFiles as a String
     */
    public String getClientListOffAllFiles() {
        return clientListOffAllFiles;
    }

    /**
     * The method setClientListOffAllFiles sets the value of clientListOffAllFiles variable to not null for getting the list of files
     * @param clientListOffAllFiles as a String
     */
    public void setClientListOffAllFiles(String clientListOffAllFiles) {
        this.clientListOffAllFiles = clientListOffAllFiles;
    }

    /**
     * This method getCancelShutDown simply returns the string cancelShutDown null or not null
     * @return cancelShutDown as a String
     */
    public String getCancelShutDown() {
        return cancelShutDown;
    }

    /**
     * The method setCancelShutDown simply sets the message for cancel shutdown remote PC
     * @param cancelShutDown as a String
     */
    public void setCancelShutDown(String cancelShutDown) {
        this.cancelShutDown = cancelShutDown;
    }

    /**
     * The method getShutDown simply returns shotDown message which null or not null not shutdown and shutdown accordingly
     * @return shutDown as a String
     */
    public String getShutDown() {
        return shutDown;
    }

    /**
     * The method setShutDown simply sets the message for shut down the remote PC
     * @param shutDown as a String
     */
    public void setShutDown(String shutDown) {
        this.shutDown = shutDown;
    }

    /**
     * The method getFileSize simply returns the file size to the caller
     * @return fileSize to the caller
     */
    public Long getFileSize() {
        return fileSize;
    }

    /**
     * The method setFileSize simply sets the file size
     * @param fileSize as Long in bytes
     */
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * The method getFileTransferDecline simply gets the decline
     * if the user has declined to download a file it returns not null
     * @return fileTransferDecline as a String
     */
    public String getFileTransferDecline() {
        return fileTransferDecline;
    }

    /**
     * Te method setFileTransferDecline simply sets the decline for the file transfer
     * @param fileTransferDecline as a String
     */
    public void setFileTransferDecline(String fileTransferDecline) {
        this.fileTransferDecline = fileTransferDecline;
    }

    /**
     * The method getFilePath simply returns the filePath to the caller
     * @return filePath to the caller
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * The method setFilePath simply sets the path to the file
     * @param filePath as a String
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * The method getCreatePrivateChat simply returns the appropriate value if the user wants to private chat with someone
     * @return createPrivateChat as a String
     */
    public String getCreatePrivateChat() {
        return createPrivateChat;
    }

    /**
     * The setCreatePrivateChat simply sets the value to the appropriate value telling the other lets private chat now
     * @param createPrivateChat as a String
     */
    public void setCreatePrivateChat(String createPrivateChat) {
        this.createPrivateChat = createPrivateChat;
    }

    /**
     * The method getPrivateMessage simply returns the private message to the caller
     * @return privateMessage to the caller
     */
    public String getPrivateMessage() {
        return privateMessage;
    }

    /**
     * The method setPrivateMessage simply sets private message to the appropriate value
     * @param privateMessage as a String
     */
    public void setPrivateMessage(String privateMessage) {
        this.privateMessage = privateMessage;
    }

    /**
     * The method getServerMessage simply returns the server message back to the caller
     * @return serverMessage as a String
     */
    public String getServerMessage() {
        return serverMessage;
    }

    /**
     * The method setServerMessage simply sets the message of the server
     * @param serverMessage as a String
     */
    public void setServerMessage(String serverMessage) {
        this.serverMessage = serverMessage;
    }

    /**
     * The method getFileTransfer simply returns the fileTransfer null if no files to transfer or not null if there is any
     * @return transferFile as an Integer
     */
    public Integer getTransferFile() {
        return transferFile;
    }

    /**
     * The method setFiletransfer simply sets the transferFile variable to appropriate value
     * @param transferFile as an Integer
     */
    public void setTransferFile(Integer transferFile) {
        this.transferFile = transferFile;
    }

    /**
     * The method getIpAddress simply returns the IP back to the caller
     * @return IpAddress as an InetAddress
     */
    public InetAddress getIpAddress() {
        return IpAddress;
    }

    /**
     * The method setIpAddress simply sets the IP to what is needed of it
     * @param IpAddress as an InetAddress
     */
    public void setIpAddress(InetAddress IpAddress) {
        this.IpAddress = IpAddress;
    }

    /**
     * This method returns the position of the JList of the client you wanna talk to
     * @return privateChat as an Integer
     */
    public Integer getClientPositionForPrivateChat() {
        return privateChat;
    }

    /**
     * The method setClientPositionForPrivateChat simply sets clients position in the JList for sending to the destination
     * which will then be extracted and the IP will be found in the server map which in turn will be forwarded to the other guy for
     * @param privateChat as an Integer
     */
    public void setClientPositionForPrivateChat(Integer privateChat) {
        this.privateChat = privateChat;
    }

    /**
     * Simply returns the checked name by the server each time anybody logs in
     * server checks the name for duplicates if not duplicate the session is opened if yes
     * the server asks to change the name
     * @return checkedName as an String
     */
    public String getCheckedName() {
        return checkedName;
    }

    /**
     * The metho d setCheckedName simply sets name that is just been checked to the appropriate value
     * @param checkedName as a String
     */
    public void setCheckedName(String checkedName) {
        this.checkedName = checkedName;
    }

    /**
     * The method getPort simply returns the port to the user
     * @return port as an integer
     */
    public int getPort() {
        return port;
    }

    /**
     * The method setPort simply sets the port to the appropriate value
     * @param port as an integer
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * The method getKill simply returns not null if the client needs to be shutdown
     * @return kill as a String
     */
    public String getKill() {
        return kill;
    }

    /**
     * The method setKill simply sets the kill variable to not null igf the client needs to be shutdown
     * @param kill as a String
     */
    public void setKill(String kill) {
        this.kill = kill;
    }

    /**
     * The method getName simply returns the name to the caller
     * @return name as a String
     */
    public String getName() {
        return name;
    }

    /**
     * The method setName simply sets the name
     * @param name as a String
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The method getAttributeColor simply returns the color of the string to be inserted in the TextPane
     * @return attributeColor as a Color
     */
    public Color getAttributeColor() {
        return attributeColor;
    }

    /**
     * The method setAttrubuteColor simply sets the Attribute color of the string to be inserted into the textPane
     * @param attributeColor as a Color
     */
    public void setAttributeColor(Color attributeColor) {
        this.attributeColor = attributeColor;
    }

    /**
     * The method getAttribute simply returns the SimpleAttributeSet if the String to be inserted into the textPane
     * @return attribute as aSimpleAttributeSet
     */
    public SimpleAttributeSet getAttribute() {
        return attribute;
    }

    /**
     * The method setAttribute simply sets the attribute appropriately
     * @param attribute as a SimpleAttributeSet
     */
    public void setAttribute(SimpleAttributeSet attribute) {
        this.attribute = attribute;
    }

    /**
     * The method getClientID simply returns the clientID which is a combination of the name and IP
     * @return clientID as a String
     */
    public String getClientID() {
        return clientID;
    }

    /**
     * The method setClientId simply sets the clientID appropriately
     * @param clientID as a String
     */
    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    /**
     * The method getIconPath simply returns the path to the icon
     * @return iconPath as a String
     */
    public String getIconPath() {
        return iconPath;
    }

    /**
     * The method setIconPath simply sets theiconPath
     * @param iconPath as a String
     */
    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    /**
     * The method getMessage simply returns the message to the caller
     * @return message as a String
     */
    public String getMessage() {
        return message;
    }

    /**
     * The method setMessage simply sets the message appropriately
     * @param message as a String
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
