import java.util.*;

public class Marsha implements Strategy
{
   private Table table;

   public String getName()
   {
      return "Marsha Mahjong";
   }

   public void gameStarted(Table table)
   {
      this.table = table;
   }

   public Tile discardHiddenTile(int score)
   {
      //Mahjong Marsha don't care what score she can win with.

      List<Tile> hiddenTiles = table.getHiddenTiles();

      //Mahjong Marsha always discards a random hidden tile.
      return hiddenTiles.get((int)(Math.random() * hiddenTiles.size()));
   }
   
   public boolean declaresWin(Tile discardedTile, int score)
   {
      return false; //Marsha Mahjong never declares a win.
   }
   
   public boolean declaresTriple(Tile discardedTile)
   {
      return false; //Marsha Mahjong never declares a triple.
   }
   
   public Tile declaresRun(Tile discardedTile, Tile[] startingTiles)
   {
      return null; //Marsha Mahjong never declares a run.
   }
   
   public void opponentDiscarded(int opponentSeat, Tile discardedTile)
   {
      //Mahjong Marsha don't care what her opponent discarded.
   }
}