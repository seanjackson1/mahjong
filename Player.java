import java.util.*;

public class Player implements Constants
{
   private int seat;
   private Strategy strategy;
   private ArrayList<Tile> exposedTiles;
   private ArrayList<Tile> hiddenTiles;
   private int[][] hidden;
   private Tile tileDrawn;
   private Game game;
   
   public Player(int seat, Game game)
   {
      this.seat = seat;
      this.strategy = null;
      exposedTiles = new ArrayList<Tile>(12);
      hiddenTiles = new ArrayList<Tile>(14);
      hidden = new int[5][10];
      tileDrawn = null;
      this.game = game;
   }
   
   public void setStrategy(Strategy strategy)
   {
      this.strategy = strategy;
   }
   
   public void addHiddenTile(Tile tile)
   {
      hiddenTiles.add(tile);
      hidden[tile.getSuit()][tile.getRank()]++;
   }
   
   public void gameStarted(Table table)
   {
      strategy.gameStarted(table);
   }
   
   public void drawWallTile(Tile wallTile)
   {
      tileDrawn = wallTile;
      hiddenTiles.add(wallTile);
      hidden[wallTile.getSuit()][wallTile.getRank()]++;
   }
   
   public Tile discardHiddenTile()
   {
      Score scoreObj = getScore();
      int score = scoreObj == null ? 0 : scoreObj.getScore();
      Tile tile = strategy.discardHiddenTile(6 * score);
      if (tile == null)
      {
         if (score > 0)
            return null;
         else
            error("discardTile returned null but cannot win with hand " + exposedTiles + " " + hiddenTiles);
      }
      
      int suit = tile.getSuit();
      int rank = tile.getRank();
      
      if (hidden[suit][rank] == 0)
         error("discardTile returned nonexistent " + tile + " for hidden tiles " + hiddenTiles);

      hiddenTiles.remove(hiddenTiles.lastIndexOf(tile));
      hidden[suit][rank]--;  
      tileDrawn = null;    
      return tile;
   }
   
   public void opponentDiscarded(int opponentSeat, Tile discardedTile)
   {
      strategy.opponentDiscarded(opponentSeat, discardedTile);
   }
   
   public boolean declaresWin(Tile discardedTile)
   {
//       System.out.println("seat: " + seat);
//       for (int suit = 0; suit < 5; suit++)
//          System.out.println("  hidden[" + suit + "]: " + Arrays.toString(hidden[suit]));
//       System.out.println("  hiddenTiles: " + hiddenTiles);
//       System.out.println("  discardedTile: " + discardedTile);
      int suit = discardedTile.getSuit();
      int rank = discardedTile.getRank();
      hidden[suit][rank]++;
      hiddenTiles.add(discardedTile);
      Score score = getScore();
      //System.out.println("score = " + score);
      hidden[suit][rank]--;
      hiddenTiles.remove(hiddenTiles.get(hiddenTiles.size() - 1));
      if (score != null && strategy.declaresWin(discardedTile, 4 * score.getScore()))
      {
         hiddenTiles.add(discardedTile);
         hidden[suit][rank]++;
         return true;
      }
      return false;
   }
   
   public boolean declaresTriple(Tile discardedTile)
   {
      int suit = discardedTile.getSuit();
      int rank = discardedTile.getRank();
      if (hidden[suit][rank] >= 2 && strategy.declaresTriple(discardedTile))
      {
         exposedTiles.add(hiddenTiles.remove(hiddenTiles.indexOf(discardedTile)));
         exposedTiles.add(hiddenTiles.remove(hiddenTiles.indexOf(discardedTile)));
         exposedTiles.add(discardedTile);
         hidden[suit][rank] -= 2;
         return true;
      }
      return false;
   }
   
   public Tile declaresRun(Tile discardedTile)
   {
      int suit = discardedTile.getSuit();
      if (!Tile.isNumbered(suit))
         return null;
      int rank = discardedTile.getRank();
      ArrayList<Tile> startingTileList = new ArrayList<Tile>(3);
      hidden[suit][rank]++;
      int min = Math.max(1, rank - 2);
      int max = Math.min(7, rank);
      for (int startingRank = min; startingRank <= max; startingRank++)
      {
         if (hidden[suit][startingRank] > 0 && hidden[suit][startingRank + 1] > 0 &&
             hidden[suit][startingRank + 2] > 0)
         {
            startingTileList.add(new Tile(suit, startingRank));
         }
      }
      hidden[suit][rank]--;
      if (startingTileList.size() == 0)
         return null;
      Tile[] startingTileArray = new Tile[startingTileList.size()];
      for (int i = 0; i < startingTileArray.length; i++)
         startingTileArray[i] = startingTileList.get(i);
      Tile claimedTile = strategy.declaresRun(discardedTile, startingTileArray);
      if (claimedTile == null)
         return null;
      int claimedRank = claimedTile.getRank();
      hiddenTiles.add(discardedTile);
      hidden[suit][rank]++;
      exposedTiles.add(hiddenTiles.remove(hiddenTiles.indexOf(new Tile(suit, claimedRank))));
      exposedTiles.add(hiddenTiles.remove(hiddenTiles.indexOf(new Tile(suit, claimedRank + 1))));
      exposedTiles.add(hiddenTiles.remove(hiddenTiles.indexOf(new Tile(suit, claimedRank + 2))));
      hidden[suit][claimedRank]--;
      hidden[suit][claimedRank + 1]--;
      hidden[suit][claimedRank + 2]--;
      return claimedTile;
   }
   
   private void error(String message)
   {
      throw new RuntimeException(seat + ":" + strategy.getClass().getName() +
         " " + message);
   }
      
   public ArrayList<Tile> getHiddenTiles()
   {
      return hiddenTiles;
   }
   
   public ArrayList<Tile> getExposedTiles()
   {
      return exposedTiles;
   }
   
   private boolean canWin(int suit, int tripleRank, int runRank)
   {
      //triples
      for (int rank = tripleRank; rank < 10; rank++)
      {
         if (hidden[suit][rank] >= 3)
         {
            hidden[suit][rank] -= 3;
            boolean win = canWin(suit, rank, 1);
            hidden[suit][rank] += 3;
            if (win) return true;
         }
      }
      //runs
      for (int rank = runRank; rank <= 7; rank++)
      {
         if (hidden[suit][rank] > 0 && hidden[suit][rank + 1] > 0 && hidden[suit][rank + 2] > 0)
         {
            hidden[suit][rank]--;
            hidden[suit][rank + 1]--;
            hidden[suit][rank + 2]--;
            boolean win = canWin(suit, 10, rank);
            hidden[suit][rank]++;
            hidden[suit][rank + 1]++;
            hidden[suit][rank + 2]++;
            if (win) return true;
         }
      }
      
      //pairs
      boolean pairFound = false;
      for (int rank = 0; rank < 10; rank++)
      {
         if (hidden[suit][rank] == 2)
         {
            if (pairFound)
               return false;  //2 pairs
            pairFound = true;
         }
         else if (hidden[suit][rank] != 0)
            return false;
      }
      return true;
   }
   
   //pre: known winning hand
   private Score getScore(int[] suits,
      int numTriples, int numRuns,
      int tripleSuit, int tripleRank, int runSuit, int runRank)
   {
      Score maxScore = null;
      //triples
      int suit = tripleSuit;
      int rank = tripleRank;
      while (suit < 5)
      {
         if (hidden[suit][rank] >= 3)
         {
            hidden[suit][rank] -= 3;
            Score score = getScore(suits,
               numTriples + 1, numRuns,
               suit, rank, 0, 1);
            hidden[suit][rank] += 3;
            if (maxScore == null || score != null && score.getScore() > maxScore.getScore())
               maxScore = score;
         }
         rank++;
         if (rank > 9)
         {
            suit++;
            rank = 0;
         }
      }
      //runs
      suit = runSuit;
      rank = runRank;
      while (suit < 3)
      {
         if (hidden[suit][rank] > 0 && hidden[suit][rank + 1] > 0 && hidden[suit][rank + 2] > 0)
         {
            hidden[suit][rank]--;
            hidden[suit][rank + 1]--;
            hidden[suit][rank + 2]--;
            Score score = getScore(suits,
               numTriples, numRuns + 1,
               5, 0, suit, rank);
            hidden[suit][rank]++;
            hidden[suit][rank + 1]++;
            hidden[suit][rank + 2]++;
            if (maxScore == null || score != null && score.getScore() > maxScore.getScore())
               maxScore = score;
         }
         rank++;
         if (rank > 7)
         {
            suit++;
            rank = 1;
         }
      }
      
      if (maxScore != null)
         return maxScore;
      
      //there should be nothing but a pair left
      for (suit = 0; suit < 5; suit++)
      {
         for (rank = 0; rank < 10; rank++)
         {
            int freq = hidden[suit][rank];
            if (freq != 0 && freq != 2)
               return null; //something other than a pair left
         }
      }
      
      //nothing but a pair left. ready to determine score.
      int score = 1;
      
      StringBuilder explanation = new StringBuilder();
      
      if (numRuns == 4)
      {
         score *= 2; // ×2   4 runs
         explanation.append("4 runs/chows (×2)\n");
      }

      if (numTriples == 4)
      {
         score *= 8; // ×8   4 triples
         explanation.append("4 triples/pongs (×8)\n");
      }

      int numberedSuits = (suits[0]>0?1:0) + (suits[1]>0?1:0) + (suits[2]>0?1:0);
      boolean honors = suits[DRAGONS] > 0 || suits[WINDS] > 0;
      
      if (numberedSuits == 1)
      {
         if (suits[DRAGONS] > 0 || suits[WINDS] > 0)
         {
            score *= 8;  // ×8   only 1 numbered suit with honors
            explanation.append("clean hand - only 1 numbered suit with honors (×8)\n");
         }
         else
         {
            score *= 64;  // ×64  only 1 numbered suit no honors
            explanation.append("pure hand - only 1 numbered suit no honors (×64)\n");
         }
      }
      else if (numberedSuits == 0)
      {
         score *= 8;  // ×8   all honors
         explanation.append("all honors (×8)\n");
      }
      
      int numDragonTriples = suits[DRAGONS] / 3;
      while (numDragonTriples > 0)
      {
         score *= 2;  // ×2   triple of dragons
         numDragonTriples--;
         explanation.append("triple/pong of dragons (×2)\n");
      }
      
      if (suits[5] >= 3)
      {
         score *= 2;  // ×2   triple of own wind
         explanation.append("triple/pong of own wind (×2)\n");
      }
      
      if (score == 1)
         explanation = new StringBuilder("chicken hand (1)\n");   
      
      return new Score(score, explanation.toString());
   }
   
   //pre: has 14 tiles
   //returns null if cannot win
   public Score getScore()
   {
//        for (int suit = 0; suit < 5; suit++)
//           System.out.println(Arrays.toString(hidden[suit]));
//        System.out.println(hiddenTiles);
   
      //stage 1: test if tile counts allow for a win
      int[] hiddenSuits = new int[5];
      for (Tile tile : hiddenTiles)
         hiddenSuits[tile.getSuit()]++;
      boolean pairFound = false;
      for (int count : hiddenSuits)
      {
         if (count % 3 == 1)
            return null;
         else if (count % 3 == 2)
         {
            if (pairFound)
               return null;  //too many pairs
            pairFound = true;
         }
      }
      
      //System.out.println("stage 2");
      //stage 2: test if can win
      for (int suit = 0; suit < 5; suit++)
      {
         if (!canWin(suit, 0, 1))
            return null;
      }
      
      //winning hand!
      
      //System.out.println("stage 3");
      //stage 3: find best score
      int[] suits = new int[6];  //suits[6] is number of own wind tiles
      for (Tile tile : exposedTiles)
      {
         int suit = tile.getSuit();
         suits[suit]++;
         if (suit == WINDS && tile.getRank() == seat)
            suits[5]++;
      }
      for (Tile tile : hiddenTiles)
      {
         int suit = tile.getSuit();
         suits[suit]++;
         if (suit == WINDS && tile.getRank() == seat)
            suits[5]++;
      }
      
      int numTriples = 0;
      int numRuns = 0;
      for (int i = 0; i < exposedTiles.size(); i += 3)
      {
         Tile tile = exposedTiles.get(i);
         if (tile.equals(exposedTiles.get(i + 1)))
            numTriples++;
         else
            numRuns++;
      }
      
      return getScore(suits, numTriples, numRuns, 0, 0, 0, 1);
   }
   
   public Tile getTileDrawn()
   {
      return tileDrawn;
   }
   
   public boolean hasTripleWith(Tile discardedTile)
   {
      //returns true if has 3 of this tile among hidden or revealed
      int count = 0;
      for (Tile tile : exposedTiles)
      {
         if (tile.equals(discardedTile))
            count++;
      }
      return count == 3 || hidden[discardedTile.getSuit()][discardedTile.getRank()] == 3;
   }
   
   public void revealQuad(Tile discardedTile)
   {
      int i = 0;
      while (i < hiddenTiles.size())
      {
         if (hiddenTiles.get(i).equals(discardedTile))
            exposedTiles.add(hiddenTiles.remove(i));
         else
            i++;
      }
      hidden[discardedTile.getSuit()][discardedTile.getRank()] = 0;
   }
   
   public String getName()
   {
      return Tile.getDirection(seat) + " (" + strategy.getName() + ")";
   }
}