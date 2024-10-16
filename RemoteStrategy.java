import java.io.*;
import java.net.*;

public class RemoteStrategy implements Strategy
{
   private static ServerSocket server;
   
   private Socket socket;
   private BufferedReader in;
   private PrintWriter out;
   private String name;
   private Table table;
   
   public RemoteStrategy()
   {
      try
      {
         if (server == null)
            server = new ServerSocket(9216);
         socket = server.accept();
         in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         out = new PrintWriter(socket.getOutputStream(), true);
         name = in.readLine() + " - Remote";
      }
      catch(IOException e)
      {
         throw new RuntimeException(e);
      }
   }
   
   public String getName()
   {
      return name;
   }
   
   public void gameStarted(Table table)
   {
      this.table = table;
      sendTable();
      out.println("gameStarted " + table.getSeat());
   }

   public Tile discardHiddenTile(int score)
   {
      try
      {
         sendTable();
         out.println("discardHiddenTile " + score);
         return new Tile(in.readLine());
      }
      catch(IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   public boolean declaresWin(Tile discardedTile, int score)
   {
      try
      {
         sendTable();
         out.println("declaresWin " + discardedTile + " " + score);
         String line = in.readLine();
         if (line.equals("true"))
            return true;
         if (line.equals("false"))
            return false;
         throw new RuntimeException("invalid response to declaresWin:  \"" + line + "\"");
      }
      catch(IOException e)
      {
         throw new RuntimeException(e);
      }
   }
   
   public boolean declaresTriple(Tile discardedTile)
   {
      try
      {
         sendTable();
         out.println("declaresTriple " + discardedTile);
         String line = in.readLine();
         if (line.equals("true"))
            return true;
         if (line.equals("false"))
            return false;
         throw new RuntimeException("invalid response to declaresTriple:  \"" + line + "\"");
      }
      catch(IOException e)
      {
         throw new RuntimeException(e);
      }
   }
   
   public Tile declaresRun(Tile discardedTile, Tile[] startingTiles)
   {
      try
      {
         sendTable();
         String startingTileString = "";
         for (Tile tile : startingTiles)
            startingTileString += " " + tile;
         out.println("declaresRun " + discardedTile + startingTileString);
         String line = in.readLine();
         if (line.equals("null"))
            return null;
         else
            return new Tile(line);
      }
      catch(IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   public void opponentDiscarded(int opponentSeat, Tile discardedTile)
   {
      sendTable();
      out.println("opponentDiscarded " + opponentSeat + " " + discardedTile);
   }
  
   private void sendTable()
   {
      StringBuilder sb = new StringBuilder("hidden");
      for (Tile tile : table.getHiddenTiles())
      {
         sb.append(" ");
         sb.append(tile);
      }
      out.println(sb);
      for (int seat = 0; seat < 4; seat++)
      {
         sb = new StringBuilder("exposed " + seat);
         for (Tile tile : table.getExposedTiles(seat))
         {
            sb.append(" ");
            sb.append(tile);
         }
         out.println(sb);
      }
      sb = new StringBuilder("discarded");
      for (Tile tile : table.getDiscardedTiles())
      {
         sb.append(" ");
         sb.append(tile);
      }
      out.println(sb);
   }
}