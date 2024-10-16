public class Tile implements Constants, Comparable<Tile>
{
   private static final String SUIT_SYMBOLS = ".|#";
   private static final String[] SUIT_FILE_NAMES = {"dot", "bamboo", "char", "wind", "dragon"};
   private static final String[] DIRECTION_NAMES = {"East", "South", "West", "North"};
   private static final String DRAGON_SYMBOLS = "RGW";
   private static final String WIND_SYMBOLS = "ESWN";
   
   private int suit;
   private int rank;
   
   public Tile(int suit, int rank)
   {
      this.suit = suit;
      this.rank = rank;
   }
   
   public Tile(String s)
   {
      String first = s.substring(0, 1);
      String second = s.substring(1);
      if (second.equals("D"))
      {
         suit = DRAGONS;
         rank = DRAGON_SYMBOLS.indexOf(first);
      }
      else if (second.equals("W"))
      {
         suit = WINDS;
         rank = WIND_SYMBOLS.indexOf(first);
      }
      else
      {
         suit = SUIT_SYMBOLS.indexOf(second);
         rank = Integer.parseInt(first);
      }
   }
   
   public int getSuit()
   {
      return suit;
   }
   
   public int getRank()
   {
      return rank;
   }
   
   public String toString()
   {
      if (suit == DRAGONS)
         return DRAGON_SYMBOLS.charAt(rank) + "D";
      else if (suit == WINDS)
         return WIND_SYMBOLS.charAt(rank) + "W";
      else
         return "" + rank + SUIT_SYMBOLS.charAt(suit);
   }
   
   public boolean equals(Object obj)
   {
      Tile other = (Tile)obj;
      return suit == other.suit && rank == other.rank;
   }
   
   public int compareTo(Tile other)
   {
      if (suit == other.suit)
         return rank - other.rank;
      else
         return suit - other.suit;
   }
   
   public int hashCode()
   {
      return 10 * suit + rank;
   }
   
   public boolean isNumbered()
   {
      return isNumbered(suit);
   }
   
   public boolean isHonors()
   {
      return isHonors(suit);
   }
   
   public String getFileName()
   {
      return SUIT_FILE_NAMES[suit] + rank + ".png";
   }
   
   public static boolean isNumbered(int suit)
   {
      return suit == DOTS || suit == BAMBOO || suit == CHARACTERS;
   }
   
   public static boolean isHonors(int suit)
   {
      return suit == WINDS || suit == DRAGONS;
   }
   
   public static String getDirection(int seat)
   {
      return DIRECTION_NAMES[seat];
   }
}