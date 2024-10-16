public class Score
{
   private int score;
   private String explanation;
   
   public Score(int score, String explanation)
   {
      this.score = score;
      this.explanation = explanation;
   }
   
   public int getScore()
   {
      return score;
   }
   
   public String getExplanation()
   {
      return explanation;
   }
   
   public String toString()
   {
      return "" + score;
   }
}