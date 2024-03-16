

package mahjong;

import java.util.ArrayList;


public interface GameInterface {

    void Dash(int stage);

    boolean Select(Tiles tile);

    boolean[] freeTiles();

    ArrayList<Tiles> getDash();

    boolean[] getIndicated();

    float getMaxX();

    float getMaxY();

    float getMinX();

    float getMinY();

    int getScore();

    Tiles getSelected();

    void indicate();

    void rearrangement();

    boolean removePair();

    boolean suggestPair();

    boolean undo();
    
}
