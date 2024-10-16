import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class PlayerPanel extends JComponent implements MouseListener, Constants
{
   private Game game;
   private int seat;
   private boolean showHiddenTiles;
   private int selectedIndex;
   private ArrayList<Tile> hiddenTiles;
   private String message;
   
   public PlayerPanel(boolean showHiddenTiles)
   {
      this.showHiddenTiles = showHiddenTiles;
      setPreferredSize(new Dimension(700, 80));
      addMouseListener(this);
      message = "";
   }
   
   public void paintComponent(Graphics g)
   {
      if (game == null)
         return;
      g.setColor(Display.TABLE_COLOR);
      g.fillRect(0, 0, 700, 80);
      Player player = game.getPlayers()[seat];
      ArrayList<Tile> exposedTiles = player.getExposedTiles();
      
      if (showHiddenTiles)
      {
         g.setColor(Color.BLACK);
         g.fillRect(exposedTiles.size() * 50, 0, hiddenTiles.size() * 50, 80);
      }
         
      //tile images are 100x121
      for (int i = 0; i < exposedTiles.size(); i++)
         showTile(g, exposedTiles.get(i), i * 50, 10);
      if (showHiddenTiles)
      {
         Tile tileDrawn = player.getTileDrawn();
         if (tileDrawn != null && hiddenTiles.get(hiddenTiles.size() - 1).equals(tileDrawn))
         {
            g.setColor(Color.YELLOW);
            g.fillRect(650, 0, 50, 80);
         }
         for (int i = 0; i < hiddenTiles.size(); i++)
            showTile(g, hiddenTiles.get(i), (exposedTiles.size() + i) * 50, 10);
      }
      
      g.setColor(Color.WHITE);
      ((Graphics2D)g).setStroke(new BasicStroke(2));
      for (int i = 0; i < exposedTiles.size(); i += 3)
         g.drawRect(50 * i, 10, 150, 60);
         
      g.setFont(new Font(null, Font.BOLD, 100));
      FontMetrics fm = g.getFontMetrics();
      int width = fm.stringWidth(message);
      int start = (700 - width) / 2;
      g.setColor(Color.BLACK);
      g.drawString(message, start + 1, 78);
      g.drawString(message, start + 0, 79);
      g.drawString(message, start + 2, 79);
      g.drawString(message, start + 1, 80);
      g.setColor(Color.CYAN);
      g.drawString(message, start + 1, 79);
   }
   
   private static void showTile(Graphics g, Tile tile, int x, int y)
   {
      g.drawImage(Display.getImage(tile.getFileName()), x, y, 50, 60, null);
      String text = "";
      if (tile.isNumbered())
         text = "" + tile.getRank();
      else if (tile.getSuit() == WINDS)
         text = Tile.getDirection(tile.getRank()).substring(0, 1);
      int textWidth = g.getFontMetrics().stringWidth(text);
      g.setColor(Color.BLACK);
      g.drawString(text, x + 42 - textWidth, 35);
   }
      
   public void setPlayer(Game game, int seat)
   {
      this.game = game;
      this.seat = seat;
      hiddenTiles = new ArrayList<Tile>(14);
      Player player = game.getPlayers()[seat];
      hiddenTiles.addAll(player.getHiddenTiles());
      Collections.sort(hiddenTiles);
      Tile tileDrawn = player.getTileDrawn();
      if (tileDrawn != null)
      {
         hiddenTiles.remove(tileDrawn);
         hiddenTiles.add(tileDrawn);
      }
   }
   
   public Tile discardTile()
   {
      selectedIndex = -1;
      while (selectedIndex == -1)
      {
         try{Thread.sleep(100);}catch(Exception e){}
      }
      return hiddenTiles.get(selectedIndex);
   }
   
   public void mouseClicked(MouseEvent e)
   {
   }
   
   public void mousePressed(MouseEvent e)
   {
      Player player = game.getPlayers()[seat];
      int index = e.getX() / 50 - player.getExposedTiles().size();
      if (index >= 0 && index < hiddenTiles.size())
         selectedIndex = index;
   }
   
   public void mouseReleased(MouseEvent e)
   {
   }
   
   public void mouseEntered(MouseEvent e)
   {
   }
   
   public void mouseExited(MouseEvent e)
   {
   }
   
   public void showTiles()
   {
      showHiddenTiles = true;
   }
   
   public void setMessage(String message)
   {
      this.message = message;
   }
}