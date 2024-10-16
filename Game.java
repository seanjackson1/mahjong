import java.util.*;

public class Game implements Constants
{
   public static int[] play(Display display, Strategy ... strategies)
   {
      return new Game().playRound(display, strategies);
   }

   private Player[] players;
   private int currentSeat;
   private ArrayList<Tile> discardedTiles;
   private ArrayList<Tile> wall;
   
   //sets up tiles and deals
   public Game()
   {
      //create tile set
      wall = new ArrayList<Tile>(136);
      for (int i = 0; i < 4; i++)
      {
         for (int rank = 1; rank <= 9; rank++)
         {
            wall.add(new Tile(DOTS, rank));
            wall.add(new Tile(BAMBOO, rank));
            wall.add(new Tile(CHARACTERS, rank));
         }
         wall.add(new Tile(WINDS, EAST));
         wall.add(new Tile(WINDS, SOUTH));
         wall.add(new Tile(WINDS, WEST));
         wall.add(new Tile(WINDS, NORTH));
         wall.add(new Tile(DRAGONS, RED));
         wall.add(new Tile(DRAGONS, GREEN));
         wall.add(new Tile(DRAGONS, WHITE));
      }
      
      Collections.shuffle(wall);
      
      //set up and deal
      players = new Player[4];
      for (int seat = 0; seat < 4; seat++)
      {
         players[seat] = new Player(seat, this);
         for (int i = 0; i < 13; i++)
            players[seat].addHiddenTile(wall.remove(wall.size() - 1));
      }
         
      discardedTiles = new ArrayList<Tile>(136);

      currentSeat = 0;  
   }
   
   //returns change in score for each player
   public int[] playRound(Display display, Strategy[] strategies)
   {
      if (strategies.length != 4)
         throw new RuntimeException("require 4 strategies but given " + strategies.length);
   
      for (int seat = 0; seat < 4; seat++)
         players[seat].setStrategy(strategies[seat]);
         
      for (int seat = 0; seat < 4; seat++)
         players[seat].gameStarted(new LocalTable(this, seat));
         
      boolean meldDeclared = false;
      
      if (display != null)
         display.setGame(this);
      
      //game starts
      
      while (wall.size() > 0 || meldDeclared)
      {
         Tile tilePickedUp;
      
         if (meldDeclared)
            tilePickedUp = discardedTiles.remove(discardedTiles.size() - 1);
         else
         {
            tilePickedUp = wall.remove(wall.size() - 1);
            players[currentSeat].drawWallTile(tilePickedUp);
         }
         if (display != null) display.update();
         
         //discard tile
         Tile discardedTile = players[currentSeat].discardHiddenTile();
         if (discardedTile == null)
         {
            if (display != null) display.showTiles();
            //player won by drawing from the wall
            Score score = players[currentSeat].getScore();
            int[] gains = new int[4];
            for (int seat = 0; seat < 4; seat++)
               gains[seat] = -2 * score.getScore();
            gains[currentSeat] = 6 * score.getScore();
            String explanation = score.getExplanation() + "winning tile drawn from wall (×2)\n";
            if (display != null) display.showScores(gains, explanation);
            return gains;
         }
         
         discardedTiles.add(discardedTile);
                  
         if (display != null) display.update();
         for (int i = 0; i < 4; i++)
         {
            if (i != currentSeat)
               players[i].opponentDiscarded(currentSeat, discardedTile);
         }
         
//          //quad/kong scenarios
//          if (players[currentSeat].hasTripleWith(discardedTile))
//          {
//             //kong
//             if (display != null) display.declare(currentSeat, "KONG!");
//             players[currentSeat].revealQuad(discardedTile);
//             meldDeclared = false;
//             //leave kong tile in discard, for simplicity
//             continue;  //no seat change
//          }
                  
         int nextSeat = (currentSeat + 1) % 4;

         for (int i = nextSeat; i != currentSeat; i = (i + 1) % 4)
         {
            if (players[i].declaresWin(discardedTiles.get(discardedTiles.size() - 1)))
            {
               if (display != null) display.showTiles();
               //player won by claiming a tile discarded by an opponent
               Score score = players[i].getScore();
               int[] gains = new int[4];
               for (int seat = 0; seat < 4; seat++)
                  gains[seat] = -score.getScore();
               gains[currentSeat] = -2 * score.getScore();
               gains[i] = 4 * score.getScore();
               if (display != null) display.showScores(gains, score.getExplanation());
               return gains;
            }
         }
         
         meldDeclared = false;
         
         for (int i = nextSeat; i != currentSeat; i = (i + 1) % 4)
         {
            if (players[i].declaresTriple(discardedTiles.get(discardedTiles.size() - 1)))
            {
               if (display != null) display.declare(i, "PONG!");
               meldDeclared = true;
               nextSeat = i;
               break;
            }
         }
         
         if (!meldDeclared)
         {
            Tile startingTile = players[nextSeat].declaresRun(discardedTiles.get(discardedTiles.size() - 1));
            if (startingTile == null)
               discardedTile = null;
            else
            {
               if (display != null) display.declare(nextSeat, "CHOW!");
               meldDeclared = true;
            }
         }
         
         currentSeat = nextSeat;
      }
      
      int[] gains = {0, 0, 0, 0};
      
      if (display != null)
      {
         display.showTiles();
         display.showScores(gains, null);
      }
      
      return gains;
   }
   
   public Player[] getPlayers()
   {
      return players;
   }
   
   public int getCurrentSeat()
   {
      return currentSeat;
   }
   
   public ArrayList<Tile> getDiscardedTiles()
   {
      return discardedTiles;
   }
   
   public int getWallSize()
   {
      return wall.size();
   }
}