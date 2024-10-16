import java.awt.*;
import java.util.*;
import javax.swing.*;

public class DiscardPanel extends JComponent
{
   private static final int TILE_WIDTH = 33;
   private static final int TILE_HEIGHT = 40;
   private static final int NUM_ROWS = 11;
   private static final int NUM_COLS = 8;
   
   private Game game;
   
   public DiscardPanel()
   {
      setPreferredSize(new Dimension(NUM_COLS * TILE_WIDTH, NUM_ROWS * TILE_HEIGHT));
   }
   
   public void setGame(Game game)
   {
      this.game = game;
   }

   public void paintComponent(Graphics g)
   {
      if (game == null)
         return;
   
      //8 rows of 11 tiles
      g.setColor(Display.TABLE_COLOR);
      g.fillRect(0, 0, getWidth(), getHeight());
      ArrayList<Tile> tiles = game.getDiscardedTiles();
      for (int i = 0; i < tiles.size(); i++)
         g.drawImage(Display.getImage(tiles.get(i).getFileName()),
            TILE_WIDTH * (i % NUM_COLS), TILE_HEIGHT * (i / NUM_COLS), TILE_WIDTH, TILE_HEIGHT, null);
            
      g.setColor(Color.WHITE);
      String text = game.getWallSize() + " remaining";
      FontMetrics fm = g.getFontMetrics();
      g.drawString(text, getWidth() - fm.stringWidth(text), getHeight() - fm.getDescent());
   }
}