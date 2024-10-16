import java.util.*;

public class LocalTable implements Table
{
   private Game game;
   private int seat;
   private List<Tile> hiddenTiles;
   private List<Tile>[] exposedTiles;
   private List<Tile> discardedTiles;
   
   
   public LocalTable(Game game, int seat)
   {
      this.game = game;
      this.seat = seat;
      hiddenTiles = Collections.unmodifiableList(game.getPlayers()[seat].getHiddenTiles());
      exposedTiles = (List<Tile>[])new List[4];
      for (int i = 0; i < 4; i++)
         exposedTiles[i] = Collections.unmodifiableList(game.getPlayers()[i].getExposedTiles());
      discardedTiles = Collections.unmodifiableList(game.getDiscardedTiles());
   }
   
   public int getSeat()
   {
      return seat;
   }

   public List<Tile> getHiddenTiles()
   {
      return Collections.unmodifiableList(game.getPlayers()[seat].getHiddenTiles());
   }
   
   public List<Tile> getExposedTiles(int seat)
   {
      return Collections.unmodifiableList(game.getPlayers()[seat].getExposedTiles());
   }
   
   public List<Tile> getDiscardedTiles()
   {
      return Collections.unmodifiableList(game.getDiscardedTiles());
   }
}