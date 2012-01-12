/*
 * Description: Class Chess_Data.java
 * Author: Dimtri Pankov
 * Date: 16-Dec-2010
 * Version: 1.0
 */
package ChessGameKenai;

import java.awt.Color;
import java.io.*;
import java.util.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * The Chess_Data Class are the model for our chess game
 * This model is an Observable object which when changed notifies the
 * observers in order to update observer views
 * The model contains all possible moves for the game
 * @author Dimitri Pankov
 * @see Observable Class
 * @see ArrayList object
 * @version 1.1
 */
public final class Chess_Data extends Observable {

    private ArrayList<Non_Visual_Piece> capturedPieces = new ArrayList<Non_Visual_Piece>();
    private ArrayList<Player> players = new ArrayList<Player>();
    private ArrayList<Player> loadedPlayer = new ArrayList<Player>();
    private int counter = 0;
    private ArrayList<Non_Visual_Piece> activePieces = new ArrayList<Non_Visual_Piece>();
    private boolean isWhiteTurn = true;
    private boolean isServer = true;
    private boolean isGameOnLine = false;

    /**
     * Empty Constructor of the Chess_Data Class
     * When the object of Chess_Data is created
     * it will contain the information that is specified inside
     * the constructor the constructor creates the non visual pieces and fills the array with them
     */
    public Chess_Data() {
        this.createNonVisualPieces();
    }

    /**
     * The method createNonVisualPieces simply creates non visual pieces
     * the pieces are stored in the ArrayList of activePieces
     */
    public void createNonVisualPieces() {
        if (!activePieces.isEmpty()) {
            activePieces.clear();
        }

        //FILL NON VISUAL PIECES INTO THE ARRAY LIST OF THE DATA CLASS
        for (int i = 0; i < 64; i++) {
            if (i == 0 || i == 7) {
                activePieces.add(new Non_Visual_Piece(this, "BRook", (i + 1), Color.BLACK));
            }
            if (i == 1 || i == 6) {
                activePieces.add(new Non_Visual_Piece(this, "BKnight", (i + 1), Color.BLACK));
            }
            if (i == 2 || i == 5) {
                activePieces.add(new Non_Visual_Piece(this, "BBishop", (i + 1), Color.BLACK));
            }
            if (i == 3) {
                activePieces.add(new Non_Visual_Piece(this, "BQueen", (i + 1), Color.BLACK));
            }
            if (i == 4) {
                activePieces.add(new Non_Visual_Piece(this, "BKing", (i + 1), Color.BLACK));
            }
            if (i > 7 && i < 16) {
                activePieces.add(new Non_Visual_Piece(this, "BPawn", (i + 1), Color.BLACK));
            }
            if (i > 47 && i < 56) {
                activePieces.add(new Non_Visual_Piece(this, "WPawn", (i + 1), Color.WHITE));
            }
            if (i == 56 || i == 63) {
                activePieces.add(new Non_Visual_Piece(this, "WRook", (i + 1), Color.WHITE));
            }
            if (i == 57 || i == 62) {
                activePieces.add(new Non_Visual_Piece(this, "WKnight", (i + 1), Color.WHITE));
            }
            if (i == 58 || i == 61) {
                activePieces.add(new Non_Visual_Piece(this, "WBishop", (i + 1), Color.WHITE));
            }
            if (i == 59) {
                activePieces.add(new Non_Visual_Piece(this, "WQueen", (i + 1), Color.WHITE));
            }
            if (i == 60) {
                activePieces.add(new Non_Visual_Piece(this, "WKing", (i + 1), Color.WHITE));
            } else if (i > 15 && i < 48) {
                activePieces.add(null);
            }
        }
    }

    /**
     * The method isGameOnLine simply sets the boolean value to true or false
     * depending if the game is online to true else to false
     * @param isGameOnLine as a boolean
     */
    public void isGameOnLine(boolean isGameOnLine) {
        this.isGameOnLine = isGameOnLine;
        this.notifyView();
    }

    /**
     * The method isGameOnLine returns true or false
     * depending if the game is online true else to false
     * @return as a boolean
     */
    public boolean isGameOnLine() {
        return isGameOnLine;
    }

    /**
     * The method isWhiteTurn simply returns true or false
     * if it is white turn to play true else false
     * @return isWhiteTurn as a boolean
     */
    public boolean isWhiteTurn() {
        return isWhiteTurn;
    }

    /**
     * The method isServer simply sets the boolean value to true or false
     * if the current player is server true else false
     * @param isServer as a boolean
     */
    public void isServer(boolean isServer) {
        this.isServer = isServer;
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * The method isServer simply returns true or false
     * if the current player is server true else false
     * @return isServer as a boolean
     */
    public boolean isServer() {
        return isServer;
    }

    /**
     * The method isWhiteTurn simply sets the boolean value to true or false
     * if it is the white turn to play true else false
     * @param isWhiteTurn as a boolean
     */
    public void isWhiteTurn(boolean isWhiteTurn) {
        this.isWhiteTurn = isWhiteTurn;
    }

    /**
     * The method getActivePieces simply returns the array list of
     * non visual pieces to the caller
     * @return activePieces as an ArrayList
     */
    public ArrayList<Non_Visual_Piece> getActivePieces() {
        return activePieces;
    }

    /**
     * The method setActivePieces simply sets the active pieces
     * @param activePieces as an ArrayList
     */
    public void setActivePieces(ArrayList<Non_Visual_Piece> activePieces) {
        this.activePieces = activePieces;
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * The method getLoadedPlayers simply returns the Player ArrayList that
     * just been loaded from the file to the user so he will choose what to do next what player he wants to load up
     * @return ArrayList<Player> loadedPlayer
     */
    public ArrayList<Player> getLoadedPlayers() {
        return loadedPlayer;
    }

    /**
     * The method savePlayer saves the player properties to a file
     * Using ObjectOutputStream it saves ArrayList<Player> players
     */
    public void savePlayer() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("player.dat")));
            oos.writeObject(players);
            oos.flush();
            oos.close();
        } catch (Exception ioe) {
            JOptionPane.showMessageDialog(null, ioe.toString(), "Error Message", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * The method loadPlayer loads the player properties from a file
     * Using ObjecInputStream it loads and stores player data to the ArrayList<Player> players
     */
    public void loadPlayer() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("player.dat")));
            loadedPlayer = (ArrayList<Player>) ois.readObject();
            players.clear();
            this.setPlayers(loadedPlayer);
            ois.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error " + e.toString(), "Error Message", JOptionPane.ERROR_MESSAGE);
        }

        this.setChanged();
        this.notifyObservers();
    }

    /**
     * The getPlayers method simply returns the
     * players ArrayList to the caller object
     * @return ArrayList<Player>
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * The method notifyView simply calls the view objects
     * And in turn their update method is executed
     */
    public void notifyView() {
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * The method setPlayers sets players
     * @param players Player objects in the ArrayList<Player> players
     */
    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * The method save simply saves the current game
     * Using ObjectOutputStream all Serialized objects are
     * able to be saved
     */
    public void save() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("saved_game.dat")));
            ObjectOutputStream oss = new ObjectOutputStream(new FileOutputStream(new File("saved_game_captured_pieces.dat")));
            oos.writeObject(activePieces);
            oss.writeObject(capturedPieces);
            oss.flush();
            oos.flush();
            oss.close();
            oos.close();
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, "Error " + ioe.toString(), "Error Message", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * The overloaded save method simply saves the current game
     * Using ObjectOutputStream all Serialized objects are
     * able to be saved
     * @param file is the file to save
     */
    public void save(File file) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            ObjectOutputStream oss = new ObjectOutputStream(new FileOutputStream(file.getName() + ".bak"));
            oos.writeObject(activePieces);
            oss.writeObject(capturedPieces);
            oss.flush();
            oos.flush();
            oss.close();
            oos.close();
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, "Error " + ioe.toString(), "Error Message", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * The load method simply loads the old game
     * Using ObjectInputStream it loads the game from the file
     * into an ArrayList and then notifies observers which in turn update their views
     */
    public void load() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("saved_game.dat")));
            ObjectInputStream iis = new ObjectInputStream(new FileInputStream(new File("saved_game_captured_pieces.dat")));
            activePieces = (ArrayList) ois.readObject();
            capturedPieces = (ArrayList) iis.readObject();
            this.setPieces();
            iis.close();
            ois.close();
        } catch (Exception ioe) {
            JOptionPane.showMessageDialog(null, "Error " + ioe.toString(), "Error Message", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * The overloaded load method simply loads the old game
     * Using ObjectInputStream it loads the game from the file
     * into an ArrayList and then notifies observers which in turn update their views
     * @param file to load from the System
     */
    public void load(File file) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            ObjectInputStream iis = new ObjectInputStream(new FileInputStream(file + ".bak"));
            activePieces = (ArrayList) ois.readObject();
            this.setPieces();
            capturedPieces = (ArrayList) iis.readObject();
            iis.close();
            ois.close();
        } catch (Exception ioe) {
            JOptionPane.showMessageDialog(null, "Error " + ioe.toString(), "Error Message", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * The method setPieces simply sets the pieces that were just loaded from the file
     * back on the board
     */
    private void setPieces() {
        for (int i = 0; i < activePieces.size(); i++) {
            if (activePieces.get(i) != null) {
                if (activePieces.get(i).getType().equals("WKing")) {
                    activePieces.set(i, new Non_Visual_Piece(this, "WKing", (i + 1), Color.WHITE));
                } else if (activePieces.get(i).getType().equals("BKing")) {
                    activePieces.set(i, new Non_Visual_Piece(this, "BKing", (i + 1), Color.BLACK));
                } else if (activePieces.get(i).getType().equals("BQueen")) {
                    activePieces.set(i, new Non_Visual_Piece(this, "BQueen", (i + 1), Color.BLACK));
                } else if (activePieces.get(i).getType().equals("WQueen")) {
                    activePieces.set(i, new Non_Visual_Piece(this, "WQueen", (i + 1), Color.WHITE));
                } else if (activePieces.get(i).getType().equals("WBishop")) {
                    activePieces.set(i, new Non_Visual_Piece(this, "WBishop", (i + 1), Color.WHITE));
                } else if (activePieces.get(i).getType().equals("BBishop")) {
                    activePieces.set(i, new Non_Visual_Piece(this, "BBishop", (i + 1), Color.BLACK));
                } else if (activePieces.get(i).getType().equals("WKnight")) {
                    activePieces.set(i, new Non_Visual_Piece(this, "WKnight", (i + 1), Color.WHITE));
                } else if (activePieces.get(i).getType().equals("BKnight")) {
                    activePieces.set(i, new Non_Visual_Piece(this, "BKnight", (i + 1), Color.BLACK));
                } else if (activePieces.get(i).getType().equals("WRook")) {
                    activePieces.set(i, new Non_Visual_Piece(this, "WRook", (i + 1), Color.WHITE));
                } else if (activePieces.get(i).getType().equals("BRook")) {
                    activePieces.set(i, new Non_Visual_Piece(this, "BRook", (i + 1), Color.BLACK));
                } else if (activePieces.get(i).getType().equals("WPawn")) {
                    activePieces.set(i, new Non_Visual_Piece(this, "WPawn", (i + 1), Color.WHITE));
                } else if (activePieces.get(i).getType().equals("BPawn")) {
                    activePieces.set(i, new Non_Visual_Piece(this, "BPawn", (i + 1), Color.BLACK));
                }
            }
        }
    }

    /**
     * The method isWinner checks if there is a winner in the game
     * @return boolean value true if isWinner and false otherwise
     */
    public boolean isWinner() {
        for (int i = 0; i < capturedPieces.size(); i++) {
            Non_Visual_Piece p = capturedPieces.get(i);
            if (p.getPieceType().equals("King")) {
                return true;
            }
        }
        return false;
    }

    /**
     * The method getCapturedPieces simply returns the ArrayList when called upon
     * @return ArrayList<Piece>
     */
    public synchronized ArrayList<Non_Visual_Piece> getCapturedPieces() {
        return capturedPieces;
    }

    /**
     * The method setCapturedPieces sets ArrayList<Piece>
     * @param capturedPieces ArrayList<Piece>
     */
    public void setCapturedPieces(ArrayList<Non_Visual_Piece> capturedPieces) {

        //ASSIGN LOCAL CAPTURED PIECES TO INSTANCE VARIABLE
        this.capturedPieces = capturedPieces;

        //SET CHANGED TO TRUE AND NOTIFY OBSERVERS
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * The getPiecePosition method of this class simply looks for the piece
     * thats been clicked when it finds the piece with the clickCount equal 1
     * it returns that piece position to the caller
     * @return clicked piece position as an integer
     */
    public int getPiecePosition() {
        Non_Visual_Piece p;
        for (int i = 0; i < activePieces.size(); i++) {
            if (activePieces.get(i) != null) {
                p = activePieces.get(i);
                if (p.getClickCount() == 1) {
                    return p.getPosition();
                }
            }
        }
        return 0;
    }

    /**
     * The method isMoveable will check if the current piece can be moved
     * To the specified position depending on which piece it is and the rules of the game
     * will be checked in order to play the game as it should be played.
     * This method will also ensure that pieces do not jump over other pieces except for the Knight_View which
     * can jump over any piece. This method will also ensure that pieces do not eat their own pieces only the opposite color
     * piece could be eaten by the current piece. The method will also verify that the pieces like Rook_View and Queen_View when going
     * left or right always stay on the same row which is very important because you do not want your piece jumping rows.
     * Different pieces have different move possibilities
     * @param x current piece position
     * @param y current piece destination
     * @return true if the piece can be moved false otherwise
     */
    public boolean isMoveable(int x, int y) {
        Non_Visual_Piece p = activePieces.get(x - 1);

        //IF PIECE IS KING GO INSIDE THE IF BLOCK
        if (p.getPieceType().equals("King")) {
            if (((y - 1) == (x) && (y - 1) % 8 != 0) || ((y - 1) == (x - 2) && (y) % 8 != 0) || (y - 1) == (x - 9) || (y - 1) == (x - 10) || (y - 1) == (x - 8)) {
                if (activePieces.get(y - 1) != null) {
                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                        return true;
                    }
                } else {
                    return true;
                }
            } else if ((y - 1) == (x + 6) || (y - 1) == (x + 7) || (y - 1) == (x + 8)) {
                if (activePieces.get(y - 1) != null) {
                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                        return true;
                    }
                } else {
                    return true;
                }
            } //CASTLE THE KING WITH THE ROOK ON THE RIGHT AND WHITE COLOR PIECE IS USED
            else if (activePieces.get(y - 1) != null) {
                counter = 0;
                if (activePieces.get(y - 1).getColor() == p.getColor() && p.getColor() == Color.WHITE) {
                    if (activePieces.get(y - 1).getPosition() == 64) {
                        if (activePieces.get(y - 1).getPieceType().equals("Rook") && !activePieces.get(y - 1).isMoved() && !p.isMoved()) {
                            if (activePieces.get(62) == null) {
                                if (activePieces.get(61) == null) {

                                    //CHECK IF THE OPPOSITE COLOR PIECES ARE MOVEABLE TO THE POSITON YOU NEED FOR CASTLING THE KING
                                    if (counter == 0) {
                                        for (int i = 0; i < activePieces.size(); i++) {
                                            if (activePieces.get(i) != null) {
                                                Non_Visual_Piece pi = activePieces.get(i);
                                                if (pi.getColor() != p.getColor()) {
                                                    if (this.isMoveable(pi.getPosition(), 63) || this.isMoveable(pi.getPosition(), 62)) {
                                                        counter++;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                //IF NONE OF THE OPPOSITE COLOR PIECES ARE MOVEABLE TO THE POSITIONS YOU NEED FOR CATSLING COUNTER WILL BE 0 GO INSIDE THE IF BLOCK
                                if (counter == 0) {
                                    return true;
                                }
                            }
                        }
                    }
                }

            }//CASTLE THE KING WITH THE ROOK ON THE LEFT AND WHITE COLOR PIECE IS USED
            else if (activePieces.get(y - 1) != null) {
                counter = 0;
                if (activePieces.get(y - 1).getColor() == p.getColor() && p.getColor() == Color.WHITE) {
                    if (activePieces.get(y - 1).getPieceType().equals("Rook") && !activePieces.get(y - 1).isMoved() && !p.isMoved()) {
                        if (activePieces.get(y - 1).getPosition() == 57) {
                            if (activePieces.get(59) == null) {
                                if (activePieces.get(58) == null) {
                                    if (activePieces.get(57) == null) {

                                        //CHECK IF THE OPPOSITE COLOR PIECES ARE MOVEABLE TO THE POSITONS YOU NEED FOR CASTLING THE KING
                                        if (counter == 0) {
                                            for (int i = 0; i < activePieces.size(); i++) {
                                                if (activePieces.get(i) != null) {
                                                    Non_Visual_Piece pi = activePieces.get(i);
                                                    if (pi.getColor() != p.getColor()) {
                                                        if (this.isMoveable(pi.getPosition(), 59) || this.isMoveable(pi.getPosition(), 58)) {
                                                            counter++;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    //IF NONE OF THE OPPOSITE COLOR PIECES ARE MOVEABLE TO THE POSITIONS YOU NEED FOR CATSLING COUNTER WILL BE 0 GO INSIDE THE IF BLOCK
                                    if (counter == 0) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }

            }//CASTLE THE KING WITH THE ROOK ON THE LEFT AND BLACK COLOR PIECE IS USED
            else if (activePieces.get(y - 1) != null) {
                counter = 0;
                if (activePieces.get(y - 1).getColor() == p.getColor() && p.getColor() == Color.BLACK) {
                    if (activePieces.get(y - 1).getPieceType().equals("Rook") && !activePieces.get(y - 1).isMoved() && !p.isMoved()) {
                        if (activePieces.get(y - 1).getPosition() == 8) {
                            if (activePieces.get(6) == null) {
                                if (activePieces.get(5) == null) {

                                    //CHECK IF THE OPPOSITE COLOR PIECES ARE MOVEABLE TO THE POSITONS YOU NEED FOR CASTLING THE KING
                                    if (counter == 0) {
                                        for (int i = 0; i < activePieces.size(); i++) {
                                            if (activePieces.get(i) != null) {
                                                Non_Visual_Piece pi = activePieces.get(i);
                                                if (pi.getColor() != p.getColor()) {
                                                    if (this.isMoveable(pi.getPosition(), 6) || this.isMoveable(pi.getPosition(), 7)) {
                                                        counter++;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                //IF NONE OF THE OPPOSITE COLOR PIECES ARE MOVEABLE TO THE POSITIONS YOU NEED FOR CATSLING COUNTER WILL BE 0 GO INSIDE THE IF BLOCK
                                if (counter == 0) {
                                    return true;
                                }
                            }
                        }
                    }
                }

            }//CASTLE THE KING WITH THE ROOK ON THE LEFT AND BLACK COLOR PIECE IS USED
            else if (activePieces.get(y - 1) != null) {
                counter = 0;
                if (activePieces.get(y - 1).getColor() == p.getColor() && p.getColor() == Color.BLACK) {
                    if (activePieces.get(y - 1).getPieceType().equals("Rook") && !activePieces.get(y - 1).isMoved() && !p.isMoved()) {
                        if (activePieces.get(y - 1).getPosition() == 1) {
                            if (activePieces.get(1) == null) {
                                if (activePieces.get(2) == null) {
                                    if (activePieces.get(3) == null) {

                                        //CHECK IF THE OPPOSITE COLOR PIECES ARE MOVEABLE TO THE POSITONS YOU NEED FOR CASTLING THE KING
                                        if (counter == 0) {
                                            for (int i = 0; i < activePieces.size(); i++) {
                                                if (activePieces.get(i) != null) {
                                                    Non_Visual_Piece pi = activePieces.get(i);
                                                    if (pi.getColor() != p.getColor()) {
                                                        if (this.isMoveable(pi.getPosition(), 2) || this.isMoveable(pi.getPosition(), 3)) {
                                                            counter++;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    //IF NONE OF THE OPPOSITE COLOR PIECES ARE MOVEABLE TO THE POSITIONS YOU NEED FOR CATSLING COUNTER WILL BE 0 GO INSIDE THE IF BLOCK
                                    if (counter == 0) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //IF PIECE IS QUEEN GO INSIDE THE IF BLOCK
        } else if (p.getType().equals("BQueen") || p.getType().equals("WQueen")) {

            //INSTRUCTIONS FOR THE QUEEN TO GO STRAIGHT UP AND NOT JUMPING OVER THE PIECES
            if ((y - 1) == (x - 9) || (y - 1) == (x - 17) || (y - 1) == (x - 25) || (y - 1) == (x - 33) || (y - 1) == (x - 41) || (y - 1) == (x - 49) || (y - 1) == (x - 57)) {
                if ((y - 1) == (x - 9)) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
                if ((y - 1) == (x - 17) && activePieces.get(x - 9) == null) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
                if ((y - 1) == (x - 25) && activePieces.get(x - 17) == null) {
                    if ((y - 1) == (x - 25) && activePieces.get(x - 9) == null) {
                        if (activePieces.get(y - 1) != null) {
                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                return true;
                            }
                        } else {
                            return true;
                        }
                    }
                }
                if ((y - 1) == (x - 33) && activePieces.get(x - 25) == null) {
                    if ((y - 1) == (x - 33) && activePieces.get(x - 17) == null) {
                        if ((y - 1) == (x - 33) && activePieces.get(x - 9) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                    }
                }
                if ((y - 1) == (x - 41) && activePieces.get(x - 33) == null) {
                    if ((y - 1) == (x - 41) && activePieces.get(x - 25) == null) {
                        if ((y - 1) == (x - 41) && activePieces.get(x - 17) == null) {
                            if ((y - 1) == (x - 41) && activePieces.get(x - 9) == null) {
                                if (activePieces.get(y - 1) != null) {
                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                    }
                }
                if ((y - 1) == (x - 49) && activePieces.get(x - 41) == null) {
                    if ((y - 1) == (x - 49) && activePieces.get(x - 33) == null) {
                        if ((y - 1) == (x - 49) && activePieces.get(x - 25) == null) {
                            if ((y - 1) == (x - 49) && activePieces.get(x - 17) == null) {
                                if ((y - 1) == (x - 49) && activePieces.get(x - 9) == null) {
                                    if (activePieces.get(y - 1) != null) {
                                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                            return true;
                                        }
                                    } else {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
                if ((y - 1) == (x - 57) && activePieces.get(x - 49) == null) {
                    if ((y - 1) == (x - 57) && activePieces.get(x - 41) == null) {
                        if ((y - 1) == (x - 57) && activePieces.get(x - 33) == null) {
                            if ((y - 1) == (x - 57) && activePieces.get(x - 25) == null) {
                                if ((y - 1) == (x - 57) && activePieces.get(x - 17) == null) {
                                    if ((y - 1) == (x - 57) && activePieces.get(x - 9) == null) {
                                        if (activePieces.get(y - 1) != null) {
                                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                return true;
                                            }
                                        } else {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                //INSTRUCTIONS FOR THE QUEEN TO GO STRAIGHT DOWN AND NOT JUMPING OVER THE PIECES
            } else if ((y - 1) == (x + 7) || (y - 1) == (x + 15) || (y - 1) == (x + 23) || (y - 1) == (x + 31) || (y - 1) == (x + 39) || (y - 1) == (x + 47) || (y - 1) == (x + 55)) {
                if ((y - 1) == (x + 7)) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
                if ((y - 1) == (x + 15) && activePieces.get(x + 7) == null) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
                if ((y - 1) == (x + 23) && activePieces.get(x + 15) == null) {
                    if ((y - 1) == (x + 23) && activePieces.get(x + 7) == null) {
                        if (activePieces.get(y - 1) != null) {
                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                return true;
                            }
                        } else {
                            return true;
                        }
                    }
                }
                if ((y - 1) == (x + 31) && activePieces.get(x + 23) == null) {
                    if ((y - 1) == (x + 31) && activePieces.get(x + 15) == null) {
                        if ((y - 1) == (x + 31) && activePieces.get(x + 7) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                    }
                }
                if ((y - 1) == (x + 39) && activePieces.get(x + 31) == null) {
                    if ((y - 1) == (x + 39) && activePieces.get(x + 23) == null) {
                        if ((y - 1) == (x + 39) && activePieces.get(x + 15) == null) {
                            if ((y - 1) == (x + 39) && activePieces.get(x + 7) == null) {
                                if (activePieces.get(y - 1) != null) {
                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                    }
                }
                if ((y - 1) == (x + 47) && activePieces.get(x + 39) == null) {
                    if ((y - 1) == (x + 47) && activePieces.get(x + 31) == null) {
                        if ((y - 1) == (x + 47) && activePieces.get(x + 23) == null) {
                            if ((y - 1) == (x + 47) && activePieces.get(x + 15) == null) {
                                if ((y - 1) == (x + 47) && activePieces.get(x + 7) == null) {
                                    if (activePieces.get(y - 1) != null) {
                                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                            return true;
                                        }
                                    } else {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
                if ((y - 1) == (x + 55) && activePieces.get(x + 47) == null) {
                    if ((y - 1) == (x + 55) && activePieces.get(x + 39) == null) {
                        if ((y - 1) == (x + 55) && activePieces.get(x + 31) == null) {
                            if ((y - 1) == (x + 55) && activePieces.get(x + 23) == null) {
                                if ((y - 1) == (x + 55) && activePieces.get(x + 15) == null) {
                                    if ((y - 1) == (x + 55) && activePieces.get(x + 7) == null) {
                                        if (activePieces.get(y - 1) != null) {
                                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                return true;
                                            }
                                        } else {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


                //INSTRUCTIONS FOR THE QUEEN TO GO DOWN IN THE DECLINING TO THE LEFT DIAGONAL AND NOT JUMPING OVER THE PIECES
            } else if ((y - 1) == (x + 6) || (y - 1) == (x + 13) || (y - 1) == (x + 20) || (y - 1) == (x + 27) || (y - 1) == (x + 34) || (y - 1) == (x + 41) || (y - 1) == (x + 48)) {
                if ((y - 1) == (x + 6)) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
                if ((y - 1) == (x + 13) && activePieces.get(x + 6) == null) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                } else if ((y - 1) == (x + 20) && activePieces.get(x + 13) == null) {
                    if ((y - 1) == (x + 20) && activePieces.get(x + 6) == null) {
                        if (activePieces.get(y - 1) != null) {
                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                return true;
                            }
                        } else {
                            return true;
                        }
                    }
                } else if ((y - 1) == (x + 27) && activePieces.get(x + 20) == null) {
                    if ((y - 1) == (x + 27) && activePieces.get(x + 13) == null) {
                        if ((y - 1) == (x + 27) && activePieces.get(x + 6) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                    }
                } else if ((y - 1) == (x + 34) && activePieces.get(x + 27) == null) {
                    if ((y - 1) == (x + 34) && activePieces.get(x + 20) == null) {
                        if ((y - 1) == (x + 34) && activePieces.get(x + 13) == null) {
                            if ((y - 1) == (x + 34) && activePieces.get(x + 6) == null) {
                                if (activePieces.get(y - 1) != null) {
                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                    }
                } else if ((y - 1) == (x + 41) && activePieces.get(x + 34) == null) {
                    if ((y - 1) == (x + 41) && activePieces.get(x + 27) == null) {
                        if ((y - 1) == (x + 41) && activePieces.get(x + 20) == null) {
                            if ((y - 1) == (x + 41) && activePieces.get(x + 13) == null) {
                                if ((y - 1) == (x + 41) && activePieces.get(x + 6) == null) {
                                    if (activePieces.get(y - 1) != null) {
                                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                            return true;
                                        }
                                    } else {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                } else if ((y - 1) == (x + 48) && activePieces.get(x + 48) == null) {
                    if ((y - 1) == (x + 48) && activePieces.get(x + 34) == null) {
                        if ((y - 1) == (x + 48) && activePieces.get(x + 27) == null) {
                            if ((y - 1) == (x + 48) && activePieces.get(x + 20) == null) {
                                if ((y - 1) == (x + 48) && activePieces.get(x + 13) == null) {
                                    if ((y - 1) == (x + 48) && activePieces.get(x + 6) == null) {
                                        if (activePieces.get(y - 1) != null) {
                                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                return true;
                                            }
                                        } else {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                //INSTRUCTIONS FOR THE QUEEN TO GO UP IN THE INCLINING TO THE RIGHT DIAGONAL AND NOT JUMPING OVER THE PIECES
            } else if ((y - 1) == (x - 8) || (y - 1) == (x - 15) || (y - 1) == (x - 22) || (y - 1) == (x - 29) || (y - 1) == (x - 36) || (y - 1) == (x - 43) || (y - 1) == (x - 50)) {
                if ((y - 1) == (x - 8)) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                } else if ((y - 1) == (x - 15) && activePieces.get(x - 8) == null) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }

                } else if ((y - 1) == (x - 22) && activePieces.get(x - 15) == null) {
                    if ((y - 1) == (x - 22) && activePieces.get(x - 8) == null) {
                        if (activePieces.get(y - 1) != null) {
                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                return true;
                            }
                        } else {
                            return true;
                        }
                    }
                } else if ((y - 1) == (x - 29) && activePieces.get(x - 22) == null) {
                    if ((y - 1) == (x - 29) && activePieces.get(x - 15) == null) {
                        if ((y - 1) == (x - 29) && activePieces.get(x - 8) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                    }
                } else if ((y - 1) == (x - 36) && activePieces.get(x - 29) == null) {
                    if ((y - 1) == (x - 36) && activePieces.get(x - 22) == null) {
                        if ((y - 1) == (x - 36) && activePieces.get(x - 15) == null) {
                            if ((y - 1) == (x - 36) && activePieces.get(x - 8) == null) {
                                if (activePieces.get(y - 1) != null) {
                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                    }
                } else if ((y - 1) == (x - 43) && activePieces.get(x - 36) == null) {
                    if ((y - 1) == (x - 43) && activePieces.get(x - 29) == null) {
                        if ((y - 1) == (x - 43) && activePieces.get(x - 22) == null) {
                            if ((y - 1) == (x - 43) && activePieces.get(x - 15) == null) {
                                if ((y - 1) == (x - 43) && activePieces.get(x - 8) == null) {
                                    if (activePieces.get(y - 1) != null) {
                                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                            return true;
                                        }
                                    } else {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                } else if ((y - 1) == (x - 50) && activePieces.get(x - 43) == null) {
                    if ((y - 1) == (x - 50) && activePieces.get(x - 36) == null) {
                        if ((y - 1) == (x - 50) && activePieces.get(x - 29) == null) {
                            if ((y - 1) == (x - 50) && activePieces.get(x - 22) == null) {
                                if ((y - 1) == (x - 50) && activePieces.get(x - 15) == null) {
                                    if ((y - 1) == (x - 50) && activePieces.get(x - 8) == null) {
                                        if (activePieces.get(y - 1) != null) {
                                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                return true;
                                            }
                                        } else {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                //INSTRUCTIONS FOR THE QUEEN TO GO UP IN THE INCLINING TO THE LEFT DIAGONAL AND NOT JUMPING OVER THE PIECES
            } else if ((y - 1) == (x - 10) || (y - 1) == (x - 19) || (y - 1) == (x - 28) || (y - 1) == (x - 37) || (y - 1) == (x - 46) || (y - 1) == (x - 55) || (y - 1) == (x - 64)) {
                if ((y - 1) == (x - 10)) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                } else if ((y - 1) == (x - 19) && activePieces.get(x - 10) == null) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }

                } else if ((y - 1) == (x - 28) && activePieces.get(x - 19) == null) {
                    if ((y - 1) == (x - 28) && activePieces.get(x - 10) == null) {
                        if (activePieces.get(y - 1) != null) {
                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                return true;
                            }
                        } else {
                            return true;
                        }
                    }
                } else if ((y - 1) == (x - 37) && activePieces.get(x - 28) == null) {
                    if ((y - 1) == (x - 37) && activePieces.get(x - 19) == null) {
                        if ((y - 1) == (x - 37) && activePieces.get(x - 10) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                    }
                } else if ((y - 1) == (x - 46) && activePieces.get(x - 37) == null) {
                    if ((y - 1) == (x - 46) && activePieces.get(x - 28) == null) {
                        if ((y - 1) == (x - 46) && activePieces.get(x - 19) == null) {
                            if ((y - 1) == (x - 46) && activePieces.get(x - 10) == null) {
                                if (activePieces.get(y - 1) != null) {
                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                    }
                } else if ((y - 1) == (x - 55) && activePieces.get(x - 46) == null) {
                    if ((y - 1) == (x - 55) && activePieces.get(x - 37) == null) {
                        if ((y - 1) == (x - 55) && activePieces.get(x - 28) == null) {
                            if ((y - 1) == (x - 55) && activePieces.get(x - 19) == null) {
                                if ((y - 1) == (x - 55) && activePieces.get(x - 10) == null) {
                                    if (activePieces.get(y - 1) != null) {
                                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                            return true;
                                        }
                                    } else {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                } else if ((y - 1) == (x - 64) && activePieces.get(x - 55) == null) {
                    if ((y - 1) == (x - 64) && activePieces.get(x - 46) == null) {
                        if ((y - 1) == (x - 64) && activePieces.get(x - 37) == null) {
                            if ((y - 1) == (x - 64) && activePieces.get(x - 28) == null) {
                                if ((y - 1) == (x - 64) && activePieces.get(x - 19) == null) {
                                    if ((y - 1) == (x - 64) && activePieces.get(x - 10) == null) {
                                        if (activePieces.get(y - 1) != null) {
                                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                return true;
                                            }
                                        } else {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                //INSTRUCTIONS FOR THE QUEEN TO GO DOWN IN THE DECLINING TO THE RIGHT DIAGONAL AND NOT JUMPING OVER THE PIECES
            } else if ((y - 1) == (x + 8) || (y - 1) == (x + 17) || (y - 1) == (x + 26) || (y - 1) == (x + 35) || (y - 1) == (x + 44) || (y - 1) == (x + 53) || (y - 1) == (x + 62)) {
                if ((y - 1) == (x + 8)) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                } else if ((y - 1) == (x + 17) && activePieces.get(x + 8) == null) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }

                } else if ((y - 1) == (x + 26) && activePieces.get(x + 17) == null) {
                    if ((y - 1) == (x + 26) && activePieces.get(x + 8) == null) {
                        if (activePieces.get(y - 1) != null) {
                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                return true;
                            }
                        } else {
                            return true;
                        }
                    }
                } else if ((y - 1) == (x + 35) && activePieces.get(x + 26) == null) {
                    if ((y - 1) == (x + 35) && activePieces.get(x + 17) == null) {
                        if ((y - 1) == (x + 35) && activePieces.get(x + 8) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                    }
                } else if ((y - 1) == (x + 44) && activePieces.get(x + 35) == null) {
                    if ((y - 1) == (x + 44) && activePieces.get(x + 26) == null) {
                        if ((y - 1) == (x + 44) && activePieces.get(x + 17) == null) {
                            if ((y - 1) == (x + 44) && activePieces.get(x + 8) == null) {
                                if (activePieces.get(y - 1) != null) {
                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                    }
                } else if ((y - 1) == (x + 53) && activePieces.get(x + 44) == null) {
                    if ((y - 1) == (x + 53) && activePieces.get(x + 35) == null) {
                        if ((y - 1) == (x + 53) && activePieces.get(x + 26) == null) {
                            if ((y - 1) == (x + 53) && activePieces.get(x + 17) == null) {
                                if ((y - 1) == (x + 53) && activePieces.get(x + 8) == null) {
                                    if (activePieces.get(y - 1) != null) {
                                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                            return true;
                                        }
                                    } else {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                } else if ((y - 1) == (x + 62) && activePieces.get(x + 53) == null) {
                    if ((y - 1) == (x + 62) && activePieces.get(x + 44) == null) {
                        if ((y - 1) == (x + 62) && activePieces.get(x + 35) == null) {
                            if ((y - 1) == (x + 62) && activePieces.get(x + 26) == null) {
                                if ((y - 1) == (x + 62) && activePieces.get(x + 17) == null) {
                                    if ((y - 1) == (x + 62) && activePieces.get(x + 8) == null) {
                                        if (activePieces.get(y - 1) != null) {
                                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                return true;
                                            }
                                        } else {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                //INSTRUCTIONS FOR THE QUEEN TO GO LEFT BUT IF AND ONLY IF IT'S ON THE SAME ROW
            } else if ((y) % 8 != 0 && (x - 1) > (y - 1)) {
                if ((x) % 8 == 0) {
                    if ((y - 1) == (x - 2) || (y - 1) == (x - 3) || (y - 1) == (x - 4) || (y - 1) == (x - 5) || (y - 1) == (x - 6) || (y - 1) == (x - 7) || (y - 1) == (x - 8)) {
                        if ((y - 1) == (x - 2)) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x - 3) && activePieces.get(x - 2) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x - 4) && activePieces.get(x - 3) == null) {
                            if ((y - 1) == (x - 4) && activePieces.get(x - 2) == null) {
                                if (activePieces.get(y - 1) != null) {
                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                        if ((y - 1) == (x - 5) && activePieces.get(x - 4) == null) {
                            if ((y - 1) == (x - 5) && activePieces.get(x - 3) == null) {
                                if ((y - 1) == (x - 5) && activePieces.get(x - 2) == null) {
                                    if (activePieces.get(y - 1) != null) {
                                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                            return true;
                                        }
                                    } else {
                                        return true;
                                    }
                                }
                            }
                        }
                        if ((y - 1) == (x - 6) && activePieces.get(x - 5) == null) {
                            if ((y - 1) == (x - 6) && activePieces.get(x - 4) == null) {
                                if ((y - 1) == (x - 6) && activePieces.get(x - 3) == null) {
                                    if ((y - 1) == (x - 6) && activePieces.get(x - 2) == null) {
                                        if (activePieces.get(y - 1) != null) {
                                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                return true;
                                            }
                                        } else {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                        if ((y - 1) == (x - 7) && activePieces.get(x - 6) == null) {
                            if ((y - 1) == (x - 7) && activePieces.get(x - 5) == null) {
                                if ((y - 1) == (x - 7) && activePieces.get(x - 4) == null) {
                                    if ((y - 1) == (x - 7) && activePieces.get(x - 3) == null) {
                                        if ((y - 1) == (x - 7) && activePieces.get(x - 2) == null) {
                                            if (activePieces.get(y - 1) != null) {
                                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                    return true;
                                                }
                                            } else {
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if ((y - 1) == (x - 8) && activePieces.get(x - 7) == null) {
                            if ((y - 1) == (x - 8) && activePieces.get(x - 6) == null) {
                                if ((y - 1) == (x - 8) && activePieces.get(x - 5) == null) {
                                    if ((y - 1) == (x - 8) && activePieces.get(x - 4) == null) {
                                        if ((y - 1) == (x - 8) && activePieces.get(x - 3) == null) {
                                            if ((y - 1) == (x - 8) && activePieces.get(x - 2) == null) {
                                                if (activePieces.get(y - 1) != null) {
                                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                        return true;
                                                    }
                                                } else {
                                                    return true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if ((x + 1) % 8 == 0) {
                    if ((y - 1) == (x - 2) || (y - 1) == (x - 3) || (y - 1) == (x - 4) || (y - 1) == (x - 5) || (y - 1) == (x - 6) || (y - 1) == (x - 7)) {
                        if ((y - 1) == (x - 2)) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x - 3) && activePieces.get(x - 2) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x - 4) && activePieces.get(x - 3) == null) {
                            if ((y - 1) == (x - 4) && activePieces.get(x - 2) == null) {
                                if (activePieces.get(y - 1) != null) {
                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                        if ((y - 1) == (x - 5) && activePieces.get(x - 4) == null) {
                            if ((y - 1) == (x - 5) && activePieces.get(x - 3) == null) {
                                if ((y - 1) == (x - 5) && activePieces.get(x - 2) == null) {
                                    if (activePieces.get(y - 1) != null) {
                                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                            return true;
                                        }
                                    } else {
                                        return true;
                                    }
                                }
                            }
                        }
                        if ((y - 1) == (x - 6) && activePieces.get(x - 5) == null) {
                            if ((y - 1) == (x - 6) && activePieces.get(x - 4) == null) {
                                if ((y - 1) == (x - 6) && activePieces.get(x - 3) == null) {
                                    if ((y - 1) == (x - 6) && activePieces.get(x - 2) == null) {
                                        if (activePieces.get(y - 1) != null) {
                                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                return true;
                                            }
                                        } else {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                        if ((y - 1) == (x - 7) && activePieces.get(x - 6) == null) {
                            if ((y - 1) == (x - 7) && activePieces.get(x - 5) == null) {
                                if ((y - 1) == (x - 7) && activePieces.get(x - 4) == null) {
                                    if ((y - 1) == (x - 7) && activePieces.get(x - 3) == null) {
                                        if ((y - 1) == (x - 7) && activePieces.get(x - 2) == null) {
                                            if (activePieces.get(y - 1) != null) {
                                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                    return true;
                                                }
                                            } else {
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if ((x + 2) % 8 == 0) {
                    if ((y - 1) == (x - 2) || (y - 1) == (x - 3) || (y - 1) == (x - 4) || (y - 1) == (x - 5) || (y - 1) == (x - 6)) {
                        if ((y - 1) == (x - 2)) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x - 3) && activePieces.get(x - 2) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x - 4) && activePieces.get(x - 3) == null) {
                            if ((y - 1) == (x - 4) && activePieces.get(x - 2) == null) {
                                if (activePieces.get(y - 1) != null) {
                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                        if ((y - 1) == (x - 5) && activePieces.get(x - 4) == null) {
                            if ((y - 1) == (x - 5) && activePieces.get(x - 3) == null) {
                                if ((y - 1) == (x - 5) && activePieces.get(x - 2) == null) {
                                    if (activePieces.get(y - 1) != null) {
                                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                            return true;
                                        }
                                    } else {
                                        return true;
                                    }
                                }
                            }
                        }
                        if ((y - 1) == (x - 6) && activePieces.get(x - 5) == null) {
                            if ((y - 1) == (x - 6) && activePieces.get(x - 4) == null) {
                                if ((y - 1) == (x - 6) && activePieces.get(x - 3) == null) {
                                    if ((y - 1) == (x - 6) && activePieces.get(x - 2) == null) {
                                        if (activePieces.get(y - 1) != null) {
                                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                return true;
                                            }
                                        } else {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if ((x + 3) % 8 == 0) {
                    if ((y - 1) == (x - 2) || (y - 1) == (x - 3) || (y - 1) == (x - 4) || (y - 1) == (x - 5)) {
                        if ((y - 1) == (x - 2)) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x - 3) && activePieces.get(x - 2) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x - 4) && activePieces.get(x - 3) == null) {
                            if ((y - 1) == (x - 4) && activePieces.get(x - 2) == null) {
                                if (activePieces.get(y - 1) != null) {
                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                        if ((y - 1) == (x - 5) && activePieces.get(x - 4) == null) {
                            if ((y - 1) == (x - 5) && activePieces.get(x - 3) == null) {
                                if ((y - 1) == (x - 5) && activePieces.get(x - 2) == null) {
                                    if (activePieces.get(y - 1) != null) {
                                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                            return true;
                                        }
                                    } else {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
                if ((x + 4) % 8 == 0) {
                    if ((y - 1) == (x - 2) || (y - 1) == (x - 3) || (y - 1) == (x - 4)) {
                        if ((y - 1) == (x - 2)) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x - 3) && activePieces.get(x - 2) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x - 4) && activePieces.get(x - 3) == null) {
                            if ((y - 1) == (x - 4) && activePieces.get(x - 2) == null) {
                                if (activePieces.get(y - 1) != null) {
                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                    }
                }
                if ((x + 5) % 8 == 0) {
                    if ((y - 1) == (x - 2) || (y - 1) == (x - 3)) {
                        if ((y - 1) == (x - 2)) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x - 3) && activePieces.get(x - 2) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                    }
                }
                if ((x + 6) % 8 == 0) {
                    if ((y - 1) == (x - 2)) {
                        if (activePieces.get(y - 1) != null) {
                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                return true;
                            }
                        } else {
                            return true;
                        }
                    }
                }

                //INSTRUCTIONS FOR THE QUEEN TO GO RIGHT BUT IF AND ONLY IF IT'S ON THE SAME ROW
            } else if ((y - 1) % 8 != 0 && (x - 1) < (y - 1)) {
                if ((x - 1) % 8 == 0) {
                    if ((y - 1) == (x) || (y - 1) == (x + 1) || (y - 1) == (x + 2) || (y - 1) == (x + 3) || (y - 1) == (x + 4) || (y - 1) == (x + 5) || (y - 1) == (x + 6)) {
                        if ((y - 1) == (x)) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x + 1) && activePieces.get(x) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x + 2) && activePieces.get(x + 1) == null) {
                            if ((y - 1) == (x + 2) && activePieces.get(x) == null) {
                                if (activePieces.get(y - 1) != null) {
                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                        if ((y - 1) == (x + 3) && activePieces.get(x + 2) == null) {
                            if ((y - 1) == (x + 3) && activePieces.get(x + 1) == null) {
                                if ((y - 1) == (x + 3) && activePieces.get(x) == null) {
                                    if (activePieces.get(y - 1) != null) {
                                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                            return true;
                                        }
                                    } else {
                                        return true;
                                    }
                                }
                            }
                        }
                        if ((y - 1) == (x + 4) && activePieces.get(x + 3) == null) {
                            if ((y - 1) == (x + 4) && activePieces.get(x + 2) == null) {
                                if ((y - 1) == (x + 4) && activePieces.get(x + 1) == null) {
                                    if ((y - 1) == (x + 4) && activePieces.get(x) == null) {
                                        if (activePieces.get(y - 1) != null) {
                                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                return true;
                                            }
                                        } else {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                        if ((y - 1) == (x + 5) && activePieces.get(x + 4) == null) {
                            if ((y - 1) == (x + 5) && activePieces.get(x + 3) == null) {
                                if ((y - 1) == (x + 5) && activePieces.get(x + 2) == null) {
                                    if ((y - 1) == (x + 5) && activePieces.get(x + 1) == null) {
                                        if ((y - 1) == (x + 5) && activePieces.get(x) == null) {
                                            if (activePieces.get(y - 1) != null) {
                                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                    return true;
                                                }
                                            } else {
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if ((y - 1) == (x + 6) && activePieces.get(x + 5) == null) {
                            if ((y - 1) == (x + 6) && activePieces.get(x + 4) == null) {
                                if ((y - 1) == (x + 6) && activePieces.get(x + 3) == null) {
                                    if ((y - 1) == (x + 6) && activePieces.get(x + 2) == null) {
                                        if ((y - 1) == (x + 6) && activePieces.get(x + 1) == null) {
                                            if ((y - 1) == (x + 6) && activePieces.get(x) == null) {
                                                if (activePieces.get(y - 1) != null) {
                                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                        return true;
                                                    }
                                                } else {
                                                    return true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if ((x - 2) % 8 == 0) {
                    if ((y - 1) == (x) || (y - 1) == (x + 1) || (y - 1) == (x + 2) || (y - 1) == (x + 3) || (y - 1) == (x + 4) || (y - 1) == (x + 5)) {
                        if ((y - 1) == (x)) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x + 1) && activePieces.get(x) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x + 2) && activePieces.get(x + 1) == null) {
                            if ((y - 1) == (x + 2) && activePieces.get(x) == null) {
                                if (activePieces.get(y - 1) != null) {
                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                        if ((y - 1) == (x + 3) && activePieces.get(x + 2) == null) {
                            if ((y - 1) == (x + 3) && activePieces.get(x + 1) == null) {
                                if ((y - 1) == (x + 3) && activePieces.get(x) == null) {
                                    if (activePieces.get(y - 1) != null) {
                                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                            return true;
                                        }
                                    } else {
                                        return true;
                                    }
                                }
                            }
                        }
                        if ((y - 1) == (x + 4) && activePieces.get(x + 3) == null) {
                            if ((y - 1) == (x + 4) && activePieces.get(x + 2) == null) {
                                if ((y - 1) == (x + 4) && activePieces.get(x + 1) == null) {
                                    if ((y - 1) == (x + 4) && activePieces.get(x) == null) {
                                        if (activePieces.get(y - 1) != null) {
                                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                return true;
                                            }
                                        } else {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                        if ((y - 1) == (x + 5) && activePieces.get(x + 4) == null) {
                            if ((y - 1) == (x + 5) && activePieces.get(x + 3) == null) {
                                if ((y - 1) == (x + 5) && activePieces.get(x + 2) == null) {
                                    if ((y - 1) == (x + 5) && activePieces.get(x + 1) == null) {
                                        if ((y - 1) == (x + 5) && activePieces.get(x) == null) {
                                            if (activePieces.get(y - 1) != null) {
                                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                    return true;
                                                }
                                            } else {
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if ((x - 3) % 8 == 0) {
                    if ((y - 1) == (x) || (y - 1) == (x + 1) || (y - 1) == (x + 2) || (y - 1) == (x + 3) || (y - 1) == (x + 4)) {
                        if ((y - 1) == (x)) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x + 1) && activePieces.get(x) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x + 2) && activePieces.get(x + 1) == null) {
                            if ((y - 1) == (x + 2) && activePieces.get(x) == null) {
                                if (activePieces.get(y - 1) != null) {
                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                        if ((y - 1) == (x + 3) && activePieces.get(x + 2) == null) {
                            if ((y - 1) == (x + 3) && activePieces.get(x + 1) == null) {
                                if ((y - 1) == (x + 3) && activePieces.get(x) == null) {
                                    if (activePieces.get(y - 1) != null) {
                                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                            return true;
                                        }
                                    } else {
                                        return true;
                                    }
                                }
                            }
                        }
                        if ((y - 1) == (x + 4) && activePieces.get(x + 3) == null) {
                            if ((y - 1) == (x + 4) && activePieces.get(x + 2) == null) {
                                if ((y - 1) == (x + 4) && activePieces.get(x + 1) == null) {
                                    if ((y - 1) == (x + 4) && activePieces.get(x) == null) {
                                        if (activePieces.get(y - 1) != null) {
                                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                return true;
                                            }
                                        } else {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if ((x - 4) % 8 == 0) {
                    if ((y - 1) == (x) || (y - 1) == (x + 1) || (y - 1) == (x + 2) || (y - 1) == (x + 3)) {
                        if ((y - 1) == (x)) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x + 1) && activePieces.get(x) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x + 2) && activePieces.get(x + 1) == null) {
                            if ((y - 1) == (x + 2) && activePieces.get(x) == null) {
                                if (activePieces.get(y - 1) != null) {
                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                        if ((y - 1) == (x + 3) && activePieces.get(x + 2) == null) {
                            if ((y - 1) == (x + 3) && activePieces.get(x + 1) == null) {
                                if ((y - 1) == (x + 3) && activePieces.get(x) == null) {
                                    if (activePieces.get(y - 1) != null) {
                                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                            return true;
                                        }
                                    } else {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
                if ((x - 5) % 8 == 0) {
                    if ((y - 1) == (x) || (y - 1) == (x + 1) || (y - 1) == (x + 2)) {
                        if ((y - 1) == (x)) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x + 1) && activePieces.get(x) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x + 2) && activePieces.get(x + 1) == null) {
                            if ((y - 1) == (x + 2) && activePieces.get(x) == null) {
                                if (activePieces.get(y - 1) != null) {
                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                    }
                }
                if ((x - 6) % 8 == 0) {
                    if ((y - 1) == (x) || (y - 1) == (x + 1)) {
                        if ((y - 1) == (x)) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x + 1) && activePieces.get(x) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                    }
                }
                if ((y - 1) == (x)) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
            }

            //IF PIECE IS PAWN GO INSIDE THE IF BLOCK
        } else if (p.getType().equals("BPawn") || p.getType().equals("WPawn")) {

            //IF PAWN IS AT FIRST ORIGINAL POSITION  IT HAS THE POSSIBILLITY TO JUMP ONE OR TWO SQUARES
            if ((x < 57 && x > 48) || (x < 17 && x > 7)) {

                //IF PIECE IS OF THE WHITE COLOR EXCUTE THE IF BLOCK
                if ((y - 1) == (x - 17) && p.getColor() == Color.WHITE) {
                    if (activePieces.get(x - 9) == null) {
                        if (activePieces.get(y - 1) == null) {
                            return true;
                        }
                    }
                }

                //IF PIECE IS OF THE BLACK COLOR EXCUTE THE IF BLOCK
                if (p.getColor() == Color.BLACK && (y - 1) == (x + 15)) {
                    if (activePieces.get(x + 7) == null) {
                        if (activePieces.get(y - 1) == null) {
                            return true;
                        }
                    }
                }
            }

            //IF PAWN IS NOT AT FIRST ORIGINAL POSITION AND ITS COLOR IS WHITE IT CAN ONLY MOVE ONE SQUARE AT A TIME
            if ((y - 1) == (x - 9) && p.getColor() == Color.WHITE) {
                if (activePieces.get(y - 1) == null) {
                    return true;
                }
            }

            //IF PAWN IS NOT AT FIRST ORIGINAL POSITION AND ITS COLOR IS BLACK IT CAN ONLY MOVE ONE SQUARE AT A TIME
            if ((y - 1) == (x + 7) && p.getColor() == Color.BLACK) {
                if (activePieces.get(y - 1) == null) {
                    return true;
                }
            }

            //IF PIECE IS OF A WHITE COLOR GO INSIDE THESE CONDITIONS ARE FOR CAPTURING PIECES
            if (p.getColor() == Color.WHITE) {
                if (((y - 1) == (x - 10)) || ((y - 1) == (x - 8))) {
                    if ((((y - 1) == (x - 10)) && (x - 1) % 8 != 0) || (((y - 1) == (x - 8)) && (x) % 8 != 0)) {
                        if (activePieces.get(y - 1) != null) {
                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                return true;
                            }
                        }
                    }
                }
            }

            //IF PIECE IS OF A BLACK COLOR GO INSIDE THESE CONDITIONS ARE FOR CAPTURING PIECES
            if (p.getColor() == Color.BLACK) {
                if (((y - 1) == (x + 6)) || ((y - 1) == (x + 8))) {
                    if (activePieces.get(y - 1) != null) {
                        if ((((y - 1) == (x + 6)) && (x - 1) % 8 != 0) || (((y - 1) == (x + 8)) && (x) % 8 != 0)) {
                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                return true;
                            }
                        }
                    }
                }
            }


            //THESE CONDITIONS ARE FOR PAWN EN PASSANT MOVE FOR A BLACK PIECE
            if (p.getColor() == Color.BLACK) {
                if ((x < 41 && x > 32) && activePieces.get(y - 1) == null) {
                    if ((y - 1) == (x + 6)) {
                        if (activePieces.get(y - 1) == null) {
                            if (activePieces.get(x - 2) != null && activePieces.get(x - 2).getColor() != p.getColor()) {
                                if (activePieces.get(x - 2).getType().equals("WPawn")) {
                                    return true;
                                }
                            }
                        }
                    } else if ((y - 1) == (x + 8)) {
                        if (activePieces.get(y - 1) == null) {
                            if (activePieces.get(x) != null && activePieces.get(x).getColor() != p.getColor()) {
                                if (activePieces.get(x).getType().equals("WPawn")) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
            //THESE CONDITIONS ARE FOR PAWN EN PASSANT MOVE FOR A WHITE PIECE
            if (p.getColor() == Color.WHITE) {
                if ((x < 33 && x > 24) && activePieces.get(y - 1) == null) {
                    if ((y - 1) == (x - 10)) {
                        if (activePieces.get(y - 1) == null) {
                            if (activePieces.get(x - 2) != null && activePieces.get(x - 2).getColor() != p.getColor()) {
                                if (activePieces.get(x - 2).getType().equals("BPawn")) {
                                    return true;
                                }
                            }
                        }
                    } else if ((y - 1) == (x - 8)) {
                        if (activePieces.get(y - 1) == null) {
                            if (activePieces.get(x) != null && activePieces.get(x).getColor() != p.getColor()) {
                                if (activePieces.get(x).getType().equals("BPawn")) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }


            //IF PIECE IS ROOK GO INSIDE THE IF BLOCK
        } else if (p.getPieceType().equals("Rook")) {

            //INSTRUCTIONS FOR THE ROOK TO GO STRAIGHT UP AND NOT JUMPING OVER THE PIECES
            if ((y - 1) == (x - 9) || (y - 1) == (x - 17) || (y - 1) == (x - 25) || (y - 1) == (x - 33) || (y - 1) == (x - 41) || (y - 1) == (x - 49) || (y - 1) == (x - 57)) {
                if ((y - 1) == (x - 9)) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
                if ((y - 1) == (x - 17) && activePieces.get(x - 9) == null) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
                if ((y - 1) == (x - 25) && activePieces.get(x - 17) == null) {
                    if ((y - 1) == (x - 25) && activePieces.get(x - 9) == null) {
                        if (activePieces.get(y - 1) != null) {
                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                return true;
                            }
                        } else {
                            return true;
                        }
                    }
                }
                if ((y - 1) == (x - 33) && activePieces.get(x - 25) == null) {
                    if ((y - 1) == (x - 33) && activePieces.get(x - 17) == null) {
                        if ((y - 1) == (x - 33) && activePieces.get(x - 9) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                    }
                }
                if ((y - 1) == (x - 41) && activePieces.get(x - 33) == null) {
                    if ((y - 1) == (x - 41) && activePieces.get(x - 25) == null) {
                        if ((y - 1) == (x - 41) && activePieces.get(x - 17) == null) {
                            if ((y - 1) == (x - 41) && activePieces.get(x - 9) == null) {
                                if (activePieces.get(y - 1) != null) {
                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                    }
                }
                if ((y - 1) == (x - 49) && activePieces.get(x - 41) == null) {
                    if ((y - 1) == (x - 49) && activePieces.get(x - 33) == null) {
                        if ((y - 1) == (x - 49) && activePieces.get(x - 25) == null) {
                            if ((y - 1) == (x - 49) && activePieces.get(x - 17) == null) {
                                if ((y - 1) == (x - 49) && activePieces.get(x - 9) == null) {
                                    if (activePieces.get(y - 1) != null) {
                                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                            return true;
                                        }
                                    } else {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
                if ((y - 1) == (x - 57) && activePieces.get(x - 49) == null) {
                    if ((y - 1) == (x - 57) && activePieces.get(x - 41) == null) {
                        if ((y - 1) == (x - 57) && activePieces.get(x - 33) == null) {
                            if ((y - 1) == (x - 57) && activePieces.get(x - 25) == null) {
                                if ((y - 1) == (x - 57) && activePieces.get(x - 17) == null) {
                                    if ((y - 1) == (x - 57) && activePieces.get(x - 9) == null) {
                                        if (activePieces.get(y - 1) != null) {
                                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                return true;
                                            }
                                        } else {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                //INSTRUCTIONS FOR THE ROOK TO GO STRAIGHT DOWN AND NOT JUMPING OVER THE PIECES
            } else if ((y - 1) == (x + 7) || (y - 1) == (x + 15) || (y - 1) == (x + 23) || (y - 1) == (x + 31) || (y - 1) == (x + 39) || (y - 1) == (x + 47) || (y - 1) == (x + 55)) {
                if ((y - 1) == (x + 7)) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
                if ((y - 1) == (x + 15) && activePieces.get(x + 7) == null) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
                if ((y - 1) == (x + 23) && activePieces.get(x + 15) == null) {
                    if ((y - 1) == (x + 23) && activePieces.get(x + 7) == null) {
                        if (activePieces.get(y - 1) != null) {
                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                return true;
                            }
                        } else {
                            return true;
                        }
                    }
                }
                if ((y - 1) == (x + 31) && activePieces.get(x + 23) == null) {
                    if ((y - 1) == (x + 31) && activePieces.get(x + 15) == null) {
                        if ((y - 1) == (x + 31) && activePieces.get(x + 7) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                    }
                }
                if ((y - 1) == (x + 39) && activePieces.get(x + 31) == null) {
                    if ((y - 1) == (x + 39) && activePieces.get(x + 23) == null) {
                        if ((y - 1) == (x + 39) && activePieces.get(x + 15) == null) {
                            if ((y - 1) == (x + 39) && activePieces.get(x + 7) == null) {
                                if (activePieces.get(y - 1) != null) {
                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                    }
                }
                if ((y - 1) == (x + 47) && activePieces.get(x + 39) == null) {
                    if ((y - 1) == (x + 47) && activePieces.get(x + 31) == null) {
                        if ((y - 1) == (x + 47) && activePieces.get(x + 23) == null) {
                            if ((y - 1) == (x + 47) && activePieces.get(x + 15) == null) {
                                if ((y - 1) == (x + 47) && activePieces.get(x + 7) == null) {
                                    if (activePieces.get(y - 1) != null) {
                                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                            return true;
                                        }
                                    } else {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
                if ((y - 1) == (x + 55) && activePieces.get(x + 47) == null) {
                    if ((y - 1) == (x + 55) && activePieces.get(x + 39) == null) {
                        if ((y - 1) == (x + 55) && activePieces.get(x + 31) == null) {
                            if ((y - 1) == (x + 55) && activePieces.get(x + 23) == null) {
                                if ((y - 1) == (x + 55) && activePieces.get(x + 15) == null) {
                                    if ((y - 1) == (x + 55) && activePieces.get(x + 7) == null) {
                                        if (activePieces.get(y - 1) != null) {
                                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                return true;
                                            }
                                        } else {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                //INSTRUCTIONS FOR THE ROOK TO GO LEFT BUT IF AND ONLY IF IT'S ON THE SAME ROW
            } else if ((y) % 8 != 0 && (x - 1) > (y - 1)) {
                if ((x) % 8 == 0) {
                    if ((y - 1) == (x - 2) || (y - 1) == (x - 3) || (y - 1) == (x - 4) || (y - 1) == (x - 5) || (y - 1) == (x - 6) || (y - 1) == (x - 7) || (y - 1) == (x - 8)) {
                        if ((y - 1) == (x - 2)) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x - 3) && activePieces.get(x - 2) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x - 4) && activePieces.get(x - 3) == null) {
                            if ((y - 1) == (x - 4) && activePieces.get(x - 2) == null) {
                                if (activePieces.get(y - 1) != null) {
                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                        if ((y - 1) == (x - 5) && activePieces.get(x - 4) == null) {
                            if ((y - 1) == (x - 5) && activePieces.get(x - 3) == null) {
                                if ((y - 1) == (x - 5) && activePieces.get(x - 2) == null) {
                                    if (activePieces.get(y - 1) != null) {
                                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                            return true;
                                        }
                                    } else {
                                        return true;
                                    }
                                }
                            }
                        }
                        if ((y - 1) == (x - 6) && activePieces.get(x - 5) == null) {
                            if ((y - 1) == (x - 6) && activePieces.get(x - 4) == null) {
                                if ((y - 1) == (x - 6) && activePieces.get(x - 3) == null) {
                                    if ((y - 1) == (x - 6) && activePieces.get(x - 2) == null) {
                                        if (activePieces.get(y - 1) != null) {
                                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                return true;
                                            }
                                        } else {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                        if ((y - 1) == (x - 7) && activePieces.get(x - 6) == null) {
                            if ((y - 1) == (x - 7) && activePieces.get(x - 5) == null) {
                                if ((y - 1) == (x - 7) && activePieces.get(x - 4) == null) {
                                    if ((y - 1) == (x - 7) && activePieces.get(x - 3) == null) {
                                        if ((y - 1) == (x - 7) && activePieces.get(x - 2) == null) {
                                            if (activePieces.get(y - 1) != null) {
                                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                    return true;
                                                }
                                            } else {
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if ((y - 1) == (x - 8) && activePieces.get(x - 7) == null) {
                            if ((y - 1) == (x - 8) && activePieces.get(x - 6) == null) {
                                if ((y - 1) == (x - 8) && activePieces.get(x - 5) == null) {
                                    if ((y - 1) == (x - 8) && activePieces.get(x - 4) == null) {
                                        if ((y - 1) == (x - 8) && activePieces.get(x - 3) == null) {
                                            if ((y - 1) == (x - 8) && activePieces.get(x - 2) == null) {
                                                if (activePieces.get(y - 1) != null) {
                                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                        return true;
                                                    }
                                                } else {
                                                    return true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if ((x + 1) % 8 == 0) {
                    if ((y - 1) == (x - 2) || (y - 1) == (x - 3) || (y - 1) == (x - 4) || (y - 1) == (x - 5) || (y - 1) == (x - 6) || (y - 1) == (x - 7)) {
                        if ((y - 1) == (x - 2)) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x - 3) && activePieces.get(x - 2) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x - 4) && activePieces.get(x - 3) == null) {
                            if ((y - 1) == (x - 4) && activePieces.get(x - 2) == null) {
                                if (activePieces.get(y - 1) != null) {
                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                        if ((y - 1) == (x - 5) && activePieces.get(x - 4) == null) {
                            if ((y - 1) == (x - 5) && activePieces.get(x - 3) == null) {
                                if ((y - 1) == (x - 5) && activePieces.get(x - 2) == null) {
                                    if (activePieces.get(y - 1) != null) {
                                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                            return true;
                                        }
                                    } else {
                                        return true;
                                    }
                                }
                            }
                        }
                        if ((y - 1) == (x - 6) && activePieces.get(x - 5) == null) {
                            if ((y - 1) == (x - 6) && activePieces.get(x - 4) == null) {
                                if ((y - 1) == (x - 6) && activePieces.get(x - 3) == null) {
                                    if ((y - 1) == (x - 6) && activePieces.get(x - 2) == null) {
                                        if (activePieces.get(y - 1) != null) {
                                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                return true;
                                            }
                                        } else {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                        if ((y - 1) == (x - 7) && activePieces.get(x - 6) == null) {
                            if ((y - 1) == (x - 7) && activePieces.get(x - 5) == null) {
                                if ((y - 1) == (x - 7) && activePieces.get(x - 4) == null) {
                                    if ((y - 1) == (x - 7) && activePieces.get(x - 3) == null) {
                                        if ((y - 1) == (x - 7) && activePieces.get(x - 2) == null) {
                                            if (activePieces.get(y - 1) != null) {
                                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                    return true;
                                                }
                                            } else {
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if ((x + 2) % 8 == 0) {
                    if ((y - 1) == (x - 2) || (y - 1) == (x - 3) || (y - 1) == (x - 4) || (y - 1) == (x - 5) || (y - 1) == (x - 6)) {
                        if ((y - 1) == (x - 2)) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x - 3) && activePieces.get(x - 2) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x - 4) && activePieces.get(x - 3) == null) {
                            if ((y - 1) == (x - 4) && activePieces.get(x - 2) == null) {
                                if (activePieces.get(y - 1) != null) {
                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                        if ((y - 1) == (x - 5) && activePieces.get(x - 4) == null) {
                            if ((y - 1) == (x - 5) && activePieces.get(x - 3) == null) {
                                if ((y - 1) == (x - 5) && activePieces.get(x - 2) == null) {
                                    if (activePieces.get(y - 1) != null) {
                                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                            return true;
                                        }
                                    } else {
                                        return true;
                                    }
                                }
                            }
                        }
                        if ((y - 1) == (x - 6) && activePieces.get(x - 5) == null) {
                            if ((y - 1) == (x - 6) && activePieces.get(x - 4) == null) {
                                if ((y - 1) == (x - 6) && activePieces.get(x - 3) == null) {
                                    if ((y - 1) == (x - 6) && activePieces.get(x - 2) == null) {
                                        if (activePieces.get(y - 1) != null) {
                                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                return true;
                                            }
                                        } else {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if ((x + 3) % 8 == 0) {
                    if ((y - 1) == (x - 2) || (y - 1) == (x - 3) || (y - 1) == (x - 4) || (y - 1) == (x - 5)) {
                        if ((y - 1) == (x - 2)) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x - 3) && activePieces.get(x - 2) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x - 4) && activePieces.get(x - 3) == null) {
                            if ((y - 1) == (x - 4) && activePieces.get(x - 2) == null) {
                                if (activePieces.get(y - 1) != null) {
                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                        if ((y - 1) == (x - 5) && activePieces.get(x - 4) == null) {
                            if ((y - 1) == (x - 5) && activePieces.get(x - 3) == null) {
                                if ((y - 1) == (x - 5) && activePieces.get(x - 2) == null) {
                                    if (activePieces.get(y - 1) != null) {
                                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                            return true;
                                        }
                                    } else {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
                if ((x + 4) % 8 == 0) {
                    if ((y - 1) == (x - 2) || (y - 1) == (x - 3) || (y - 1) == (x - 4)) {
                        if ((y - 1) == (x - 2)) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x - 3) && activePieces.get(x - 2) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x - 4) && activePieces.get(x - 3) == null) {
                            if ((y - 1) == (x - 4) && activePieces.get(x - 2) == null) {
                                if (activePieces.get(y - 1) != null) {
                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                    }
                }
                if ((x + 5) % 8 == 0) {
                    if ((y - 1) == (x - 2) || (y - 1) == (x - 3)) {
                        if ((y - 1) == (x - 2)) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x - 3) && activePieces.get(x - 2) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                    }
                }
                if ((x + 6) % 8 == 0) {
                    if ((y - 1) == (x - 2)) {
                        if (activePieces.get(y - 1) != null) {
                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                return true;
                            }
                        } else {
                            return true;
                        }
                    }
                }

                //INSTRUCTIONS FOR THE ROOK TO GO RIGHT BUT IF AND ONLY IF IT'S ON THE SAME ROW
            } else if ((y - 1) % 8 != 0 && (x - 1) < (y - 1)) {
                if ((x - 1) % 8 == 0) {
                    if ((y - 1) == (x) || (y - 1) == (x + 1) || (y - 1) == (x + 2) || (y - 1) == (x + 3) || (y - 1) == (x + 4) || (y - 1) == (x + 5) || (y - 1) == (x + 6)) {
                        if ((y - 1) == (x)) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x + 1) && activePieces.get(x) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x + 2) && activePieces.get(x + 1) == null) {
                            if ((y - 1) == (x + 2) && activePieces.get(x) == null) {
                                if (activePieces.get(y - 1) != null) {
                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                        if ((y - 1) == (x + 3) && activePieces.get(x + 2) == null) {
                            if ((y - 1) == (x + 3) && activePieces.get(x + 1) == null) {
                                if ((y - 1) == (x + 3) && activePieces.get(x) == null) {
                                    if (activePieces.get(y - 1) != null) {
                                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                            return true;
                                        }
                                    } else {
                                        return true;
                                    }
                                }
                            }
                        }
                        if ((y - 1) == (x + 4) && activePieces.get(x + 3) == null) {
                            if ((y - 1) == (x + 4) && activePieces.get(x + 2) == null) {
                                if ((y - 1) == (x + 4) && activePieces.get(x + 1) == null) {
                                    if ((y - 1) == (x + 4) && activePieces.get(x) == null) {
                                        if (activePieces.get(y - 1) != null) {
                                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                return true;
                                            }
                                        } else {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                        if ((y - 1) == (x + 5) && activePieces.get(x + 4) == null) {
                            if ((y - 1) == (x + 5) && activePieces.get(x + 3) == null) {
                                if ((y - 1) == (x + 5) && activePieces.get(x + 2) == null) {
                                    if ((y - 1) == (x + 5) && activePieces.get(x + 1) == null) {
                                        if ((y - 1) == (x + 5) && activePieces.get(x) == null) {
                                            if (activePieces.get(y - 1) != null) {
                                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                    return true;
                                                }
                                            } else {
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if ((y - 1) == (x + 6) && activePieces.get(x + 5) == null) {
                            if ((y - 1) == (x + 6) && activePieces.get(x + 4) == null) {
                                if ((y - 1) == (x + 6) && activePieces.get(x + 3) == null) {
                                    if ((y - 1) == (x + 6) && activePieces.get(x + 2) == null) {
                                        if ((y - 1) == (x + 6) && activePieces.get(x + 1) == null) {
                                            if ((y - 1) == (x + 6) && activePieces.get(x) == null) {
                                                if (activePieces.get(y - 1) != null) {
                                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                        return true;
                                                    }
                                                } else {
                                                    return true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if ((x - 2) % 8 == 0) {
                    if ((y - 1) == (x) || (y - 1) == (x + 1) || (y - 1) == (x + 2) || (y - 1) == (x + 3) || (y - 1) == (x + 4) || (y - 1) == (x + 5)) {
                        if ((y - 1) == (x)) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x + 1) && activePieces.get(x) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x + 2) && activePieces.get(x + 1) == null) {
                            if ((y - 1) == (x + 2) && activePieces.get(x) == null) {
                                if (activePieces.get(y - 1) != null) {
                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                        if ((y - 1) == (x + 3) && activePieces.get(x + 2) == null) {
                            if ((y - 1) == (x + 3) && activePieces.get(x + 1) == null) {
                                if ((y - 1) == (x + 3) && activePieces.get(x) == null) {
                                    if (activePieces.get(y - 1) != null) {
                                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                            return true;
                                        }
                                    } else {
                                        return true;
                                    }
                                }
                            }
                        }
                        if ((y - 1) == (x + 4) && activePieces.get(x + 3) == null) {
                            if ((y - 1) == (x + 4) && activePieces.get(x + 2) == null) {
                                if ((y - 1) == (x + 4) && activePieces.get(x + 1) == null) {
                                    if ((y - 1) == (x + 4) && activePieces.get(x) == null) {
                                        if (activePieces.get(y - 1) != null) {
                                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                return true;
                                            }
                                        } else {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                        if ((y - 1) == (x + 5) && activePieces.get(x + 4) == null) {
                            if ((y - 1) == (x + 5) && activePieces.get(x + 3) == null) {
                                if ((y - 1) == (x + 5) && activePieces.get(x + 2) == null) {
                                    if ((y - 1) == (x + 5) && activePieces.get(x + 1) == null) {
                                        if ((y - 1) == (x + 5) && activePieces.get(x) == null) {
                                            if (activePieces.get(y - 1) != null) {
                                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                    return true;
                                                }
                                            } else {
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if ((x - 3) % 8 == 0) {
                    if ((y - 1) == (x) || (y - 1) == (x + 1) || (y - 1) == (x + 2) || (y - 1) == (x + 3) || (y - 1) == (x + 4)) {
                        if ((y - 1) == (x)) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x + 1) && activePieces.get(x) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x + 2) && activePieces.get(x + 1) == null) {
                            if ((y - 1) == (x + 2) && activePieces.get(x) == null) {
                                if (activePieces.get(y - 1) != null) {
                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                        if ((y - 1) == (x + 3) && activePieces.get(x + 2) == null) {
                            if ((y - 1) == (x + 3) && activePieces.get(x + 1) == null) {
                                if ((y - 1) == (x + 3) && activePieces.get(x) == null) {
                                    if (activePieces.get(y - 1) != null) {
                                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                            return true;
                                        }
                                    } else {
                                        return true;
                                    }
                                }
                            }
                        }
                        if ((y - 1) == (x + 4) && activePieces.get(x + 3) == null) {
                            if ((y - 1) == (x + 4) && activePieces.get(x + 2) == null) {
                                if ((y - 1) == (x + 4) && activePieces.get(x + 1) == null) {
                                    if ((y - 1) == (x + 4) && activePieces.get(x) == null) {
                                        if (activePieces.get(y - 1) != null) {
                                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                return true;
                                            }
                                        } else {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if ((x - 4) % 8 == 0) {
                    if ((y - 1) == (x) || (y - 1) == (x + 1) || (y - 1) == (x + 2) || (y - 1) == (x + 3)) {
                        if ((y - 1) == (x)) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x + 1) && activePieces.get(x) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x + 2) && activePieces.get(x + 1) == null) {
                            if ((y - 1) == (x + 2) && activePieces.get(x) == null) {
                                if (activePieces.get(y - 1) != null) {
                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                        if ((y - 1) == (x + 3) && activePieces.get(x + 2) == null) {
                            if ((y - 1) == (x + 3) && activePieces.get(x + 1) == null) {
                                if ((y - 1) == (x + 3) && activePieces.get(x) == null) {
                                    if (activePieces.get(y - 1) != null) {
                                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                            return true;
                                        }
                                    } else {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
                if ((x - 5) % 8 == 0) {
                    if ((y - 1) == (x) || (y - 1) == (x + 1) || (y - 1) == (x + 2)) {
                        if ((y - 1) == (x)) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x + 1) && activePieces.get(x) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x + 2) && activePieces.get(x + 1) == null) {
                            if ((y - 1) == (x + 2) && activePieces.get(x) == null) {
                                if (activePieces.get(y - 1) != null) {
                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                    }
                }
                if ((x - 6) % 8 == 0) {
                    if ((y - 1) == (x) || (y - 1) == (x + 1)) {
                        if ((y - 1) == (x)) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        if ((y - 1) == (x + 1) && activePieces.get(x) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                    }
                }
                if ((y - 1) == (x)) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
            }
            //IF PIECE IS KNIGHT GO INSIDE THE IF BLOCK
        } else if (p.getType().equals("BKnight") || p.getType().equals("WKnight")) {

            //INSTRUCTIONS FOR THE KNIGHT TO MOVE DOWNWARDS LIMIT MOVES WHEN ON THE LAST SQUARE OF THE ROW
            if (((y - 1) == (x + 16))) {
                if ((x) % 8 != 0) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
            }

            //INSTRUCTIONS FOR THE KNIGHT TO MOVE DOWNWARDS LIMIT MOVES WHEN ON THE NEXT LAST SQUARE OF THE ROW
            if (((y - 1) == (x + 9))) {
                if ((x + 1) % 8 != 0) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
            }

            //INSTRUCTIONS FOR THE KNIGHT TO MOVE DOWNWARDS LIMIT MOVES WHEN ON THE FIRST SQUARE OF THE ROW
            if ((y - 1) == (x + 14)) {
                if ((x - 1) % 8 != 0) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
            }

            //INSTRUCTIONS FOR THE KNIGHT TO MOVE DOWNWARDS LIMIT MOVES WHEN ON THE FIRST SQUARE OF THE ROW
            if ((y - 1) == (x + 5)) {
                if ((x - 1) % 8 != 0 && (x - 2) % 8 != 0) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }

                //INSTRUCTIONS FOR THE KNIGHT TO MOVE UPWARDS LIMIT MOVES IF ON THE LAST SQUARE OF THE ROW
            } else if ((y - 1) == (x - 16)) {
                if (x % 8 != 0) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }

                //INSTRUCTIONS FOR THE KNIGHT TO MOVE UPWARDS LIMIT MOVES IF ON THE LAST SQUARE OF THE ROW
            } else if ((y - 1) == (x - 18)) {
                if ((x - 1) % 8 != 0) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
            } else if ((y - 1) == (x - 11)) {
                if ((x - 1) % 8 != 0 && (x - 2) % 8 != 0) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }

                //INSTRUCTIONS FOR THE KNIGHT TO MOVE UPWARDS LIMIT MOVES IF ON THE LAST SQUARE OF THE ROW
            } else if ((y - 1) == (x - 7)) {
                if (x % 8 != 0 && (x + 1) % 8 != 0) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
            }

            //IF PIECE IS BISHOP GO INSIDE THE IF BLOCK
        } else if (p.getType().equals("BBishop") || p.getType().equals("WBishop")) {

            //INSTRUCTIONS FOR THE BISHOP TO GO DOWN IN THE DECLINING TO THE LEFT DIAGONAL AND NOT JUMPING OVER THE PIECES
            if ((y - 1) == (x + 6) || (y - 1) == (x + 13) || (y - 1) == (x + 20) || (y - 1) == (x + 27) || (y - 1) == (x + 34) || (y - 1) == (x + 41) || (y - 1) == (x + 48)) {
                if ((y - 1) == (x + 6)) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
                if ((y - 1) == (x + 13) && activePieces.get(x + 6) == null) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                } else if ((y - 1) == (x + 20) && activePieces.get(x + 13) == null) {
                    if ((y - 1) == (x + 20) && activePieces.get(x + 6) == null) {
                        if (activePieces.get(y - 1) != null) {
                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                return true;
                            }
                        } else {
                            return true;
                        }
                    }
                } else if ((y - 1) == (x + 27) && activePieces.get(x + 20) == null) {
                    if ((y - 1) == (x + 27) && activePieces.get(x + 13) == null) {
                        if ((y - 1) == (x + 27) && activePieces.get(x + 6) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                    }
                } else if ((y - 1) == (x + 34) && activePieces.get(x + 27) == null) {
                    if ((y - 1) == (x + 34) && activePieces.get(x + 20) == null) {
                        if ((y - 1) == (x + 34) && activePieces.get(x + 13) == null) {
                            if ((y - 1) == (x + 34) && activePieces.get(x + 6) == null) {
                                if (activePieces.get(y - 1) != null) {
                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                    }
                } else if ((y - 1) == (x + 41) && activePieces.get(x + 34) == null) {
                    if ((y - 1) == (x + 41) && activePieces.get(x + 27) == null) {
                        if ((y - 1) == (x + 41) && activePieces.get(x + 20) == null) {
                            if ((y - 1) == (x + 41) && activePieces.get(x + 13) == null) {
                                if ((y - 1) == (x + 41) && activePieces.get(x + 6) == null) {
                                    if (activePieces.get(y - 1) != null) {
                                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                            return true;
                                        }
                                    } else {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                } else if ((y - 1) == (x + 48) && activePieces.get(x + 48) == null) {
                    if ((y - 1) == (x + 48) && activePieces.get(x + 34) == null) {
                        if ((y - 1) == (x + 48) && activePieces.get(x + 27) == null) {
                            if ((y - 1) == (x + 48) && activePieces.get(x + 20) == null) {
                                if ((y - 1) == (x + 48) && activePieces.get(x + 13) == null) {
                                    if ((y - 1) == (x + 48) && activePieces.get(x + 6) == null) {
                                        if (activePieces.get(y - 1) != null) {
                                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                return true;
                                            }
                                        } else {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                //INSTRUCTIONS FOR THE BISHOP TO GO UP IN THE INCLINING TO THE RIGHT DIAGONAL AND NOT JUMPING OVER THE PIECES
            } else if ((y - 1) == (x - 8) || (y - 1) == (x - 15) || (y - 1) == (x - 22) || (y - 1) == (x - 29) || (y - 1) == (x - 36) || (y - 1) == (x - 43) || (y - 1) == (x - 50)) {
                if ((y - 1) == (x - 8)) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                } else if ((y - 1) == (x - 15) && activePieces.get(x - 8) == null) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                } else if ((y - 1) == (x - 22) && activePieces.get(x - 15) == null) {
                    if ((y - 1) == (x - 22) && activePieces.get(x - 8) == null) {
                        if (activePieces.get(y - 1) != null) {
                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                return true;
                            }
                        } else {
                            return true;
                        }
                    }
                } else if ((y - 1) == (x - 29) && activePieces.get(x - 22) == null) {
                    if ((y - 1) == (x - 29) && activePieces.get(x - 15) == null) {
                        if ((y - 1) == (x - 29) && activePieces.get(x - 8) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                    }
                } else if ((y - 1) == (x - 36) && activePieces.get(x - 29) == null) {
                    if ((y - 1) == (x - 36) && activePieces.get(x - 22) == null) {
                        if ((y - 1) == (x - 36) && activePieces.get(x - 15) == null) {
                            if ((y - 1) == (x - 36) && activePieces.get(x - 8) == null) {
                                if (activePieces.get(y - 1) != null) {
                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                    }
                } else if ((y - 1) == (x - 43) && activePieces.get(x - 36) == null) {
                    if ((y - 1) == (x - 43) && activePieces.get(x - 29) == null) {
                        if ((y - 1) == (x - 43) && activePieces.get(x - 22) == null) {
                            if ((y - 1) == (x - 43) && activePieces.get(x - 15) == null) {
                                if ((y - 1) == (x - 43) && activePieces.get(x - 8) == null) {
                                    if (activePieces.get(y - 1) != null) {
                                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                            return true;
                                        }
                                    } else {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                } else if ((y - 1) == (x - 50) && activePieces.get(x - 43) == null) {
                    if ((y - 1) == (x - 50) && activePieces.get(x - 36) == null) {
                        if ((y - 1) == (x - 50) && activePieces.get(x - 29) == null) {
                            if ((y - 1) == (x - 50) && activePieces.get(x - 22) == null) {
                                if ((y - 1) == (x - 50) && activePieces.get(x - 15) == null) {
                                    if ((y - 1) == (x - 50) && activePieces.get(x - 8) == null) {
                                        if (activePieces.get(y - 1) != null) {
                                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                return true;
                                            }
                                        } else {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                //INSTRUCTIONS FOR THE BISHOP TO GO UP IN THE INCLINING TO THE LEFT DIAGONAL AND NOT JUMPING OVER THE PIECES
            } else if ((y - 1) == (x - 10) || (y - 1) == (x - 19) || (y - 1) == (x - 28) || (y - 1) == (x - 37) || (y - 1) == (x - 46) || (y - 1) == (x - 55) || (y - 1) == (x - 64)) {
                if ((y - 1) == (x - 10)) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                } else if ((y - 1) == (x - 19) && activePieces.get(x - 10) == null) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                } else if ((y - 1) == (x - 28) && activePieces.get(x - 19) == null) {
                    if ((y - 1) == (x - 28) && activePieces.get(x - 10) == null) {
                        if (activePieces.get(y - 1) != null) {
                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                return true;
                            }
                        } else {
                            return true;
                        }
                    }
                } else if ((y - 1) == (x - 37) && activePieces.get(x - 28) == null) {
                    if ((y - 1) == (x - 37) && activePieces.get(x - 19) == null) {
                        if ((y - 1) == (x - 37) && activePieces.get(x - 10) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                    }
                } else if ((y - 1) == (x - 46) && activePieces.get(x - 37) == null) {
                    if ((y - 1) == (x - 46) && activePieces.get(x - 28) == null) {
                        if ((y - 1) == (x - 46) && activePieces.get(x - 19) == null) {
                            if ((y - 1) == (x - 46) && activePieces.get(x - 10) == null) {
                                if (activePieces.get(y - 1) != null) {
                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                    }
                } else if ((y - 1) == (x - 55) && activePieces.get(x - 46) == null) {
                    if ((y - 1) == (x - 55) && activePieces.get(x - 37) == null) {
                        if ((y - 1) == (x - 55) && activePieces.get(x - 28) == null) {
                            if ((y - 1) == (x - 55) && activePieces.get(x - 19) == null) {
                                if ((y - 1) == (x - 55) && activePieces.get(x - 10) == null) {
                                    if (activePieces.get(y - 1) != null) {
                                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                            return true;
                                        }
                                    } else {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                } else if ((y - 1) == (x - 64) && activePieces.get(x - 55) == null) {
                    if ((y - 1) == (x - 64) && activePieces.get(x - 46) == null) {
                        if ((y - 1) == (x - 64) && activePieces.get(x - 37) == null) {
                            if ((y - 1) == (x - 64) && activePieces.get(x - 28) == null) {
                                if ((y - 1) == (x - 64) && activePieces.get(x - 19) == null) {
                                    if ((y - 1) == (x - 64) && activePieces.get(x - 10) == null) {
                                        if (activePieces.get(y - 1) != null) {
                                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                return true;
                                            }
                                        } else {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                //INSTRUCTIONS FOR THE BISHOP TO GO DOWN IN THE DECLINING TO THE RIGHT DIAGONAL AND NOT JUMPING OVER THE PIECES
            } else if ((y - 1) == (x + 8) || (y - 1) == (x + 17) || (y - 1) == (x + 26) || (y - 1) == (x + 35) || (y - 1) == (x + 44) || (y - 1) == (x + 53) || (y - 1) == (x + 62)) {
                if ((y - 1) == (x + 8)) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                } else if ((y - 1) == (x + 17) && activePieces.get(x + 8) == null) {
                    if (activePieces.get(y - 1) != null) {
                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                } else if ((y - 1) == (x + 26) && activePieces.get(x + 17) == null) {
                    if ((y - 1) == (x + 26) && activePieces.get(x + 8) == null) {
                        if (activePieces.get(y - 1) != null) {
                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                return true;
                            }
                        } else {
                            return true;
                        }
                    }
                } else if ((y - 1) == (x + 35) && activePieces.get(x + 26) == null) {
                    if ((y - 1) == (x + 35) && activePieces.get(x + 17) == null) {
                        if ((y - 1) == (x + 35) && activePieces.get(x + 8) == null) {
                            if (activePieces.get(y - 1) != null) {
                                if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                    }
                } else if ((y - 1) == (x + 44) && activePieces.get(x + 35) == null) {
                    if ((y - 1) == (x + 44) && activePieces.get(x + 26) == null) {
                        if ((y - 1) == (x + 44) && activePieces.get(x + 17) == null) {
                            if ((y - 1) == (x + 44) && activePieces.get(x + 8) == null) {
                                if (activePieces.get(y - 1) != null) {
                                    if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                    }
                } else if ((y - 1) == (x + 53) && activePieces.get(x + 44) == null) {
                    if ((y - 1) == (x + 53) && activePieces.get(x + 35) == null) {
                        if ((y - 1) == (x + 53) && activePieces.get(x + 26) == null) {
                            if ((y - 1) == (x + 53) && activePieces.get(x + 17) == null) {
                                if ((y - 1) == (x + 53) && activePieces.get(x + 8) == null) {
                                    if (activePieces.get(y - 1) != null) {
                                        if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                            return true;
                                        }
                                    } else {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                } else if ((y - 1) == (x + 62) && activePieces.get(x + 53) == null) {
                    if ((y - 1) == (x + 62) && activePieces.get(x + 44) == null) {
                        if ((y - 1) == (x + 62) && activePieces.get(x + 35) == null) {
                            if ((y - 1) == (x + 62) && activePieces.get(x + 26) == null) {
                                if ((y - 1) == (x + 62) && activePieces.get(x + 17) == null) {
                                    if ((y - 1) == (x + 62) && activePieces.get(x + 8) == null) {
                                        if (activePieces.get(y - 1) != null) {
                                            if (activePieces.get(y - 1).getColor() != p.getColor()) {
                                                return true;
                                            }
                                        } else {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * The method move simply takes two arguments the first one(x) is the current piece
     * position and the second(y) is the destination of the current piece
     * The piece will be moved if and only if the isMoveable method will return true if the method isMoveable
     * returns false that means the piece is not Movable list array that we have declared in the method is used to
     * wrap the x and y position to send it to the client or server through socket if and only if the game is played online
     * @param x current piece position
     * @param y current piece destination
     */
    public void move(int x, int y) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        if (activePieces.get(x - 1) == null) {
            return;
        }

        //BEFORE MOVING FIRST CHECK IF THE PIECE IS MOVEABLE
        if (this.isMoveable(x, y)) {
            Non_Visual_Piece p = activePieces.get(x - 1);

            //IF KING EXECUTE THE IF BLOCK AND CHECK THE FOLLOWING CONDITIONS
            if (p.getPieceType().equals("King")) {
                if (activePieces.get(y - 1) != null && activePieces.get(y - 1).getPieceType().equals("Rook") && p.getColor() == activePieces.get(y - 1).getColor()) {

                    //CASTLE THE KING WITH THE ROOK ON THE LEFT AND PIECE IS A BLACK COLOR
                    if (activePieces.get(y - 1) != null && (y - 1) == 7) {
                        if (p.getColor() == Color.BLACK && (activePieces.get(y - 1).getType()).equals("BRook")) {
                            activePieces.set(6, p);
                            activePieces.set(5, activePieces.get(y - 1));
                            activePieces.get(5).setPosition(6);
                            activePieces.get(5).setPreviousPosition(8);
                            activePieces.set((x - 1), null);
                            activePieces.set((y - 1), null);
                            p.setPosition(7);
                            p.setClickCount(0);
                            p.setPreviousPosition(x);
                        }
                    }

                    //CASTLE THE KING WITH THE ROOK ON THE LEFT AND PIECE IS A BLACK COLOR
                    if (activePieces.get(y - 1) != null && (y - 1) == 0) {
                        if (p.getColor() == Color.BLACK && (activePieces.get(y - 1).getType()).equals("BRook")) {
                            activePieces.set(1, p);
                            activePieces.set(2, activePieces.get(y - 1));
                            activePieces.get(2).setPosition(3);
                            activePieces.get(2).setPreviousPosition(1);
                            activePieces.set((x - 1), null);
                            activePieces.set((y - 1), null);
                            p.setPosition(2);
                            p.setClickCount(0);
                            p.setPreviousPosition(x);
                        }
                    }

                    //CASTLE THE KING WITH THE ROOK ON THE LEFT AND PIECE IS A WHITE COLOR
                    if (activePieces.get(y - 1) != null && (y - 1) == 63) {
                        if (p.getColor() == Color.WHITE && (activePieces.get(y - 1).getType()).equals("WRook")) {
                            activePieces.set(62, p);
                            activePieces.set(61, activePieces.get(y - 1));
                            activePieces.get(61).setPosition(62);
                            activePieces.get(61).setPreviousPosition(64);
                            activePieces.set((x - 1), null);
                            activePieces.set((y - 1), null);
                            p.setPosition(63);
                            p.setClickCount(0);
                            p.setPreviousPosition(x);
                        }
                    }

                    //CASTLE THE KING WITH THE ROOK ON THE LEFT AND PIECE IS A WHITE COLOR
                    if (activePieces.get(y - 1) != null && (y - 1) == 56) {
                        if (p.getColor() == Color.WHITE && (activePieces.get(y - 1).getType()).equals("WRook")) {
                            activePieces.set(57, p);
                            activePieces.set(58, activePieces.get(y - 1));
                            activePieces.get(58).setPosition(59);
                            activePieces.get(58).setPreviousPosition(57);
                            activePieces.set((x - 1), null);
                            activePieces.set((y - 1), null);
                            p.setPosition(58);
                            p.setClickCount(0);
                            p.setPreviousPosition(x);
                        }
                    }
                } else {
                    if (activePieces.get(y - 1) == null || activePieces.get(y - 1).getColor() != p.getColor()) {
                        if (activePieces.get(y - 1) != null) {
                            activePieces.get(y - 1).isCaptured(true);
                            capturedPieces.add(activePieces.get(y - 1));
                            activePieces.set((y - 1), null);
                        }
                        activePieces.set((y - 1), p);
                        activePieces.set((x - 1), null);
                        p.setPosition(y);
                        p.setClickCount(0);
                        p.setPreviousPosition(x);
                        p.isMoved(true);
                    }
                }

                //IF QUEEN EXECUTE THE IF BLOCK AND CHECK THE FOLLOWING CONDITIONS
            } else if (p.getPieceType().equals("Queen")) {
                if (activePieces.get(y - 1) != null) {
                    activePieces.get(y - 1).isCaptured(true);
                    capturedPieces.add(activePieces.get(y - 1));
                    activePieces.set((y - 1), null);
                }
                activePieces.set((y - 1), p);
                activePieces.set((x - 1), null);
                p.setPosition(y);
                p.setClickCount(0);
                p.setPreviousPosition(x);

                //IF BISHOP EXECUTE THE IF BLOCK AND CHECK THE FOLLOWING CONDITIONS
            } else if (p.getPieceType().equals("Bishop")) {
                if (activePieces.get(y - 1) != null) {
                    activePieces.get(y - 1).isCaptured(true);
                    capturedPieces.add(activePieces.get(y - 1));
                    activePieces.set((y - 1), null);
                }
                activePieces.set((y - 1), p);
                activePieces.set((x - 1), null);
                p.setPosition(y);
                p.setClickCount(0);
                p.setPreviousPosition(x);

                //IF KNIGHT EXECUTE THE IF BLOCK AND CHECK THE FOLLOWING CONDITIONS
            } else if (p.getPieceType().equals("Knight")) {
                if (activePieces.get(y - 1) != null) {
                    activePieces.get(y - 1).isCaptured(true);
                    capturedPieces.add(activePieces.get(y - 1));
                    activePieces.set((y - 1), null);
                }
                activePieces.set((y - 1), p);
                activePieces.set((x - 1), null);
                p.setPosition(y);
                p.setClickCount(0);
                p.setPreviousPosition(x);

                //IF ROOK EXECUTE THE IF BLOCK AND CHECK THE FOLLOWING CONDITIONS
            } else if (p.getPieceType().equals("Rook")) {
                if (activePieces.get(y - 1) != null) {
                    capturedPieces.add(activePieces.get(y - 1));
                    activePieces.get(y - 1).isCaptured(true);
                    activePieces.set((y - 1), null);
                }
                activePieces.set((y - 1), p);
                activePieces.set((x - 1), null);
                p.setPosition(y);
                p.setClickCount(0);
                p.setPreviousPosition(x);
                p.isMoved(true);

                //IF PAWN EXECUTE THE IF BLOCK AND CHECK THE FOLLOWING CONDITIONS
            } else if (p.getPieceType().equals("Pawn")) {

                //INSTRUCTIONS FOR CAPTURING PAWN EN PASSANT IF P IS WHITE PAWN
                if (p.getColor() == Color.WHITE) {
                    if ((x - 1) < 32 && (x - 1) > 23) {
                        if ((y - 1) == (x - 10) && activePieces.get(x - 2) != null && activePieces.get(y - 1) == null && activePieces.get(x - 2).getPieceType().equals("Pawn")) {
                            activePieces.get(x - 2).isCaptured(true);
                            capturedPieces.add(activePieces.get(x - 2));
                            activePieces.set((x - 2), null);
                        } else if ((y - 1) == (x - 8) && activePieces.get(x) != null && activePieces.get(y - 1) == null && activePieces.get(x).getPieceType().equals("Pawn")) {
                            activePieces.get(x).isCaptured(true);
                            capturedPieces.add(activePieces.get(x));
                            activePieces.set(x, null);
                        }
                    }

                    //INSTRUCTIONS FOR CAPTURING PAWN EN PASSANT IF P IS BLACK PAWN
                } else if (p.getColor() == Color.BLACK) {
                    if ((x - 1) < 40 && (x - 1) > 31) {
                        if ((y - 1) == (x + 6) && activePieces.get(x - 2) != null && activePieces.get(y - 1) == null && activePieces.get(x - 2).getPieceType().equals("Pawn")) {
                            activePieces.get(x - 2).isCaptured(true);
                            capturedPieces.add(activePieces.get(x - 2));
                            activePieces.set((x - 2), null);
                        } else if ((y - 1) == (x + 8) && activePieces.get(x) != null && activePieces.get(y - 1) == null && activePieces.get(x).getPieceType().equals("Pawn")) {
                            activePieces.get(x).isCaptured(true);
                            capturedPieces.add(activePieces.get(x));
                            activePieces.set(x, null);
                        }
                    }
                }

                //IF WHITE PAWN HAS REACHED THE LAST SQUARE IT BECOMES A QUEEN
                if (((y - 1) < 8 && (y - 1) >= 0) && p.getColor() == Color.WHITE) {
                    if (activePieces.get(y - 1) != null) {
                        activePieces.get(y - 1).isCaptured(true);
                        capturedPieces.add(activePieces.get(y - 1));
                        activePieces.set((y - 1), null);
                    }
                    activePieces.set((y - 1), new Non_Visual_Piece(this, "WQueen", y, Color.WHITE));
                    activePieces.get(y - 1).setPreviousPosition(x);
                    activePieces.set((x - 1), null);
                    activePieces.get(y - 1).isQueenFromPawn(true);
                    activePieces.remove(p);

                    //IF WBLACK PAWN HAS REACHED LAST SQUARE IT BECOMES BLACK QUEEN
                } else if (((y - 1) < 65 && (y - 1) >= 56) && p.getColor() == Color.BLACK) {
                    if (activePieces.get(y - 1) != null) {
                        activePieces.get(y - 1).isCaptured(true);
                        capturedPieces.add(activePieces.get(y - 1));
                        activePieces.set((y - 1), null);
                    }
                    activePieces.set((y - 1), new Non_Visual_Piece(this, "BQueen", y, Color.BLACK));
                    activePieces.get(y - 1).setPreviousPosition(x);
                    activePieces.set((x - 1), null);
                    activePieces.get(y - 1).isQueenFromPawn(true);
                    activePieces.remove(p);
                }
                if (y > 8 && y < 57) {
                    if (activePieces.get(y - 1) != null) {
                        activePieces.get(y - 1).isCaptured(true);
                        capturedPieces.add(activePieces.get(y - 1));
                        activePieces.set((y - 1), null);
                    }
                    activePieces.set((y - 1), p);
                    activePieces.set((x - 1), null);
                    p.setPosition(y);
                    p.setClickCount(0);
                    p.setPreviousPosition(x);
                }
            }

            if (p.getColor() == Color.BLACK) {
                isWhiteTurn = true;
            } else {
                isWhiteTurn = false;
            }

            list.add(x);
            list.add(y);
       
            //NOTIFY OBSERVERS IF IT THE STATE HAS CHANGED
            this.setChanged();
            this.notifyObservers(list);
        }
    }
}
