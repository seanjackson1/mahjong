import java.awt.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

public class Display
{
   public static final Color TABLE_COLOR = new Color(0, 127, 0);

   private static Map<String,Image> imageMap = new HashMap<String,Image>();

   private Game game;
   private JFrame frame;
   private PlayerPanel[] playerPanels;
   private JPanel[] titledPlayerPanels;
   private DiscardPanel discardPanel;
   private int delay;
   
   public Display(int delay, boolean ... showTiles)
   {
      this.delay = delay;
      frame = new JFrame("Mahjong");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setResizable(false);
      frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.LINE_AXIS));
      
      discardPanel = new DiscardPanel();
      frame.getContentPane().add(withTitle(discardPanel, "Discarded Tiles"));
      
      JPanel playerArea = new JPanel(new GridLayout(4, 1));
      playerArea.setBackground(TABLE_COLOR);
      frame.getContentPane().add(playerArea);
      
      playerPanels = new PlayerPanel[4];
      titledPlayerPanels = new JPanel[4];
      for (int seat = 0; seat < 4; seat++)
      {
         playerPanels[seat] = new PlayerPanel(showTiles[seat]);
         titledPlayerPanels[seat] = withTitle(playerPanels[seat], Tile.getDirection(seat));
         playerArea.add(titledPlayerPanels[seat]);
      }
      frame.pack();
      frame.setVisible(true);
   }
   
   private static JPanel withTitle(JComponent comp, String title)
   {
      JPanel panel = new JPanel();
      panel.setBorder(BorderFactory.createTitledBorder(null, title,
         TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, Color.WHITE));
      panel.setBackground(TABLE_COLOR);
      panel.add(comp);
      return panel;
   }
   
   public void setGame(Game game)
   {
      this.game = game;
      discardPanel.setGame(game);
      for (int seat = 0; seat < 4; seat++)
      {
         playerPanels[seat].setPlayer(game, seat);
         ((TitledBorder)titledPlayerPanels[seat].getBorder()).setTitle(game.getPlayers()[seat].getName());
         titledPlayerPanels[seat].repaint();
      }
   }
   
   public static Image getImage(String fileName)
   {
      fileName = "images/" + fileName;
      Image image = imageMap.get(fileName);
      if (image == null)
      {
         URL url = Display.class.getResource(fileName);
         if (url == null)
            throw new RuntimeException("file not found:  " + fileName);
         image = new ImageIcon(url).getImage();
         imageMap.put(fileName, image);
      }
      return image;      
   }
   
   public void update()
   {
      for (int seat = 0; seat < 4; seat++)
      {
         playerPanels[seat].setPlayer(game, seat);
         Font font;
         if (seat == game.getCurrentSeat())
            font = new Font(null, Font.BOLD, 16);
         else
            font = new Font(null, Font.PLAIN, 14);
         ((TitledBorder)titledPlayerPanels[seat].getBorder()).setTitleFont(font);
         titledPlayerPanels[seat].repaint();
      }
      discardPanel.repaint();
      for (PlayerPanel p : playerPanels)
         p.repaint();
      
      try{Thread.sleep(delay);}catch(Exception e){}
      
      //new Scanner(System.in).nextLine();
   }
   
   public Tile discardHiddenTile(int seat, int score)
   {
      if (score > 0)
      {
         int option = JOptionPane.showConfirmDialog(frame, "Win with " + score + "?", "Win",
            JOptionPane.YES_NO_OPTION);
         if (option == JOptionPane.YES_OPTION)
            return null;
      }
      return playerPanels[seat].discardTile();
   }
   
   public boolean declaresTriple(Tile tile)
   {
      return JOptionPane.showConfirmDialog(
         frame,
         "Declare triple?",
         "Pong",
         JOptionPane.YES_NO_OPTION,
         JOptionPane.QUESTION_MESSAGE,
         new ImageIcon(getImage(tile.getFileName()))) == JOptionPane.YES_OPTION;
   }
   
   public Tile declaresRun(Tile discardedTile, Tile[] startingTiles)
   {
      int option = JOptionPane.showConfirmDialog(
         frame,
         "Declare run?",
         "Chow",
         JOptionPane.YES_NO_OPTION,
         JOptionPane.QUESTION_MESSAGE,
         new ImageIcon(getImage(discardedTile.getFileName())));
         
      if (option != JOptionPane.YES_OPTION)
         return null;

      if (startingTiles.length == 1)
         return startingTiles[0];
      
      Object[] selectionValues = new Object[startingTiles.length + 2];
      for (int i = 0; i < startingTiles.length; i++)
      {
         int rank = startingTiles[i].getRank();
         selectionValues[i] = rank + " - " + (rank + 1) + " - " + (rank + 2);
      }
      Object selectedObject = JOptionPane.showInputDialog(
         frame,
         "Choose run",
         "Chow",
         JOptionPane.PLAIN_MESSAGE,
         new ImageIcon(getImage(discardedTile.getFileName())),
         selectionValues,
         selectionValues[0]);
      if (selectedObject == null)
         return null;
      for (int i = 0; i < startingTiles.length; i++)
      {
         if (selectionValues[i].equals(selectedObject))
            return startingTiles[i];
      }
      throw new RuntimeException("should never get here");
   }
   
   public boolean declaresWin(Tile tile, int score)
   {
      return JOptionPane.showConfirmDialog(
         frame,
         "Declare win with " + score + " points?",
         "Win",
         JOptionPane.YES_NO_OPTION,
         JOptionPane.QUESTION_MESSAGE,
         new ImageIcon(getImage(tile.getFileName()))) == JOptionPane.YES_OPTION;
   }
   
   public void showTiles()
   {
      for (PlayerPanel playerPanel : playerPanels)
         playerPanel.showTiles();
      update();
   }
   
   public void showScores(int[] gains, String explanation)
   {
      String message;
      if (explanation == null)
         message = "Tie Game: no more tiles to draw";
      else
      {
         int winningSeat = 0;
         int losingSeat = 0;
         for (int seat = 0; seat < gains.length; seat++)
         {
            if (gains[seat] > gains[winningSeat])
               winningSeat = seat;
            if (losingSeat != -1 && gains[seat] == gains[losingSeat])
               losingSeat = -1;
            if (losingSeat != -1 && gains[seat] < gains[losingSeat])
               losingSeat = seat;
         }
         message = game.getPlayers()[winningSeat].getName() + " wins with\n\n" + explanation + "\n";
         if (losingSeat != -1)
            message +=  game.getPlayers()[losingSeat].getName() + " pays double for discarding winning tile\n\n";
         for (int seat = 0; seat < gains.length; seat++)
         {
            message += game.getPlayers()[seat].getName() + ":  ";
            if (gains[seat] > 0)
               message += "+";
            message += gains[seat] + "\n";
         }
      }
      JOptionPane.showMessageDialog(frame, message);
   }
   
   public void declare(int seat, String message)
   {
      for (int i = 0; i < 4; i++)
      {
         if (i == seat)
            playerPanels[i].setMessage(message);
         else
            playerPanels[i].setMessage("");
      }
      update();
      for (PlayerPanel playerPanel : playerPanels)
         playerPanel.setMessage("");
   }
}