import java.util.*;

public interface Table
{
    int getSeat();
    List<Tile> getHiddenTiles();
    List<Tile> getExposedTiles(int seat);
    List<Tile> getDiscardedTiles();
}