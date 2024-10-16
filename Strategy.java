public interface Strategy extends Constants
{
   String getName();

   void gameStarted(Table table);

   Tile discardHiddenTile(int score);

   boolean declaresWin(Tile discardedTile, int score);
   boolean declaresTriple(Tile discardedTile);
   Tile declaresRun(Tile discardedTile, Tile[] startingTiles);

   void opponentDiscarded(int opponentSeat, Tile discardedTile);
}