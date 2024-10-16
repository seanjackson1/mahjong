import java.io.*;
import java.net.*;
import java.util.*;

public class RemoteTable extends Thread implements Table
{
   private Strategy strategy;
   private Socket socket;
   private BufferedReader in;
   private PrintWriter out;
   private int seat;
   private List<Tile> hiddenTiles;
   private List<Tile>[] exposedTiles;
   private List<Tile> discardedTiles;
   
   public RemoteTable(String hostIPAddress, Strategy strategy)
   {
      try
      {
         this.strategy = strategy;
         exposedTiles = (List<Tile>[])new List[4];
         socket = new Socket(hostIPAddress, 9216);
         in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         out = new PrintWriter(socket.getOutputStream(), true);
         out.println(strategy.getName());
         start();
      }
      catch(IOException e)
      {
         throw new RuntimeException(e);
      }
   }
   
   public void run()
   {
      try
      {
         while (true)
         {
            String line = in.readLine();
            String[] tokens = line.split(" ");
            String command = tokens[0];
            if (command.equals("gameStarted"))
            {
               seat = Integer.parseInt(tokens[1]);
               strategy.gameStarted(this);
            }
            else if (command.equals("discardHiddenTile"))
               out.println(strategy.discardHiddenTile(Integer.parseInt(tokens[1])));
            else if (command.equals("declaresWin"))
               out.println(strategy.declaresWin(new Tile(tokens[1]), Integer.parseInt(tokens[2])));
            else if (command.equals("declaresTriple"))
               out.println(strategy.declaresTriple(new Tile(tokens[1])));
            else if (command.equals("declaresRun"))
            {
               Tile[] startingTiles = new Tile[tokens.length - 2];
               for (int i = 0; i < startingTiles.length; i++)
                  startingTiles[i] = new Tile(tokens[i + 2]);
               out.println(strategy.declaresRun(new Tile(tokens[1]), startingTiles));
            }
            else if (command.equals("opponentDiscarded"))
               strategy.opponentDiscarded(Integer.parseInt(tokens[1]), new Tile(tokens[2]));
            else if (command.equals("hidden"))
            {
               ArrayList<Tile> tiles = new ArrayList<Tile>(14);
               for (int i = 1; i < tokens.length; i++)
                  tiles.add(new Tile(tokens[i]));
               hiddenTiles = Collections.unmodifiableList(tiles);
            }
            else if (command.equals("exposed"))
            {
               int seat = Integer.parseInt(tokens[1]);
               ArrayList<Tile> tiles = new ArrayList<Tile>(12);
               for (int i = 2; i < tokens.length; i++)
                  tiles.add(new Tile(tokens[i]));
               exposedTiles[seat] = Collections.unmodifiableList(tiles);
            }
            else if (command.equals("discarded"))
            {
               ArrayList<Tile> tiles = new ArrayList<Tile>();
               for (int i = 1; i < tokens.length; i++)
                  tiles.add(new Tile(tokens[i]));
               discardedTiles = Collections.unmodifiableList(tiles);
            }
            else
               throw new RuntimeException("invalid command:  " + command);
         }
      }
      catch(IOException e)
      {
         throw new RuntimeException(e);
      }
   }
   
   public int getSeat()
   {
      return seat;
   }
   
   public List<Tile> getHiddenTiles()
   {
      return hiddenTiles;
   }
   
   public List<Tile> getExposedTiles(int seat)
   {
      return exposedTiles[seat];
   }
   
   public List<Tile> getDiscardedTiles()
   {
      return discardedTiles;
   }
}