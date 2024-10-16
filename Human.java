import java.util.*;

public class Human implements Strategy
{
   private Table table;
   private Scanner in;
   private Display display;
   
   public Human(Display display)
   {
      this.display = display;
   }
   
   public String getName()
   {
      return "You";
   }
   
   public void gameStarted(Table table)
   {
      this.table = table;
      in = new Scanner(System.in);
   }
   
   public Tile discardHiddenTile(int score)
   {
      return display.discardHiddenTile(table.getSeat(), score);
   }
   
   public void opponentDiscarded(int opponentSeat, Tile discardedTile)
   {
      //System.out.println("Seat " + opponentSeat + " discarded " + discardedTile);
   }

   public boolean declaresWin(Tile discardedTile, int score)
   {
      return display.declaresWin(discardedTile, score);
   }
   
   public boolean declaresTriple(Tile tile)
   {
      return display.declaresTriple(tile);
   }
   
   public Tile declaresRun(Tile tile, Tile[] startingTiles)
   {
      return display.declaresRun(tile, startingTiles);
   }
}