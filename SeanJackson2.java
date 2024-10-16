import java.util.*;

public class SeanJackson2 implements Strategy {

    private Table table;
    private int seat;
    private ArrayList<Tile> hand;
    private HashSet<Tile> player1;
    private HashSet<Tile> player2;
    private HashSet<Tile> player3;

    private int numDots;
    private int numBamboo;
    private int numCharacters;

    public String getName() {
        return "MahSean";
    }

    public void gameStarted(Table table) {
        this.table = table;
        seat = table.getSeat();
        player1 = new HashSet<Tile>();
        player2 = new HashSet<Tile>();
        player3 = new HashSet<Tile>();

    }

    public Tile discardHiddenTile(int score) {
        if (score > 0)
            return null;

        int[] points = new int[table.getHiddenTiles().size()];

        ArrayList<Tile> possibilities = new ArrayList<Tile>(table.getHiddenTiles());
        Collections.sort(possibilities);

        numDots = 0;
        numBamboo = 0;
        numCharacters = 0;

        for (Tile tile : possibilities) {
            if (tile.getSuit() == DOTS)
                numDots++;
            else if (tile.getSuit() == BAMBOO)
                numBamboo++;
            else if (tile.getSuit() == CHARACTERS)
                numCharacters++;
        }

        int numBestSuit = Math.max(Math.max(numDots, numBamboo), numCharacters);

        ArrayList<Integer> bestSuit = new ArrayList,;
        
        if (numBestSuit == numDots)
            bestSuit.add(DOTS);
        if (numBestSuit == numBamboo)
            bestSuit.add(BAMBOO);
        if (numBestSuit == numCharacters)
            bestSuit.add(CHARACTERS);

        for (int i = 0; i < possibilities.size(); i++) {

            /*
             * To-do:
             * 
             * USE OPPONENT DISCARDED
             * 
             */

            if (numCardsInDiscard(possibilities.get(i)) >= 3)
                points[i] -= 100000;
            else if (numCardsInDiscard(possibilities.get(i)) == 2)
                points[i] -= 80;
            else if (numCardsInDiscard(possibilities.get(i)) == 1)
                points[i] -= 5;

            if (possibilities.get(i).isHonors())
                points[i] += 50;

            if (possibilities.get(i).getSuit() == DRAGONS
                    || (possibilities.get(i).getSuit() == WINDS) && possibilities.get(i).getRank() == table.getSeat())
                points[i] += 30;

            if (partOfTriple(possibilities.get(i), possibilities) && possibilities.get(i).isHonors())
                points[i] += 3000;

            else 
            {
                for(int a = 0; a < bestSuit.size(); a++) {
                    if (partOfTriple(possibilities.get(i), possibilities) && possibilities.get(i).getSuit() == bestSuit.get(a))
                    points[i] += 2020;
                }
            }
            

            else if (partOfTriple(possibilities.get(i), possibilities))
                points[i] += 2000;

            else if (partOfPair(possibilities.get(i), possibilities) && possibilities.get(i).isHonors())
                points[i] += 550;

            else if (partOfPair(possibilities.get(i), possibilities) && possibilities.get(i).getSuit() == bestSuit)
                points[i] += 520;

            else if (partOfPair(possibilities.get(i), possibilities))
                points[i] += 500;

            // if (partOfRun(possibilities.get(i), possibilities))
            // points[i] += 100;

        }

        int min = Integer.MAX_VALUE;
        int j = 0;

        for (int i = 0; i < points.length; i++) {
            if (points[i] < min) {
                j = i;
                min = points[i];
            } else if (points[i] == min && Math.random() > .25) {
                j = i;
                min = points[i];
            }
        }

        return possibilities.get(j);
    }

    public int numCardsInDiscard(Tile tile) {
        int count = 0;
        for (Tile t : table.getDiscardedTiles()) {
            if (t.equals(tile))
                count++;
        }
        for (int i = 0; i < 4; i++) {
            for (Tile t : table.getExposedTiles(i)) {
                if (t.equals(tile))
                    count++;
            }
        }

        return count;
    }

    public boolean partOfTriple(Tile tile, ArrayList<Tile> possibilities) {
        int count = 0;
        for (Tile t : table.getDiscardedTiles()) {
            if (t.equals(tile))
                count++;
        }
        for (int i = 0; i < 4; i++) {
            for (Tile t : table.getExposedTiles(i)) {
                if (t.equals(tile))
                    count++;
            }
        }
        if (count >= 2)
            return false;

        count = 0;
        for (int i = 0; i < possibilities.size(); i++) {
            if (possibilities.get(i).equals(tile))
                count++;
        }
        if (count >= 3 && count != 4)
            return true;
        return false;
    }

    // public boolean partOf2Of3Triple(Tile tile, ArrayList<Tile> possibilities) {
    // int count = 0;
    // for (Tile t : table.getDiscardedTiles()) {
    // if (t.equals(tile))
    // count++;
    // }
    // for (int i = 0; i < 4; i++) {
    // for (Tile t : table.getExposedTiles(i)) {
    // if (t.equals(tile))
    // count++;
    // }
    // }
    // if (count >= 2)
    // return false;

    // count = 0;
    // for (int i = 0; i < possibilities.size(); i++) {
    // if (possibilities.get(i).equals(tile))
    // count++;
    // }
    // if (count == 2)
    // return true;
    // return false;
    // }

    public boolean partOfPair(Tile tile, ArrayList<Tile> possibilities) {
        int count = 0;
        for (Tile t : table.getDiscardedTiles()) {
            if (t.equals(tile))
                count++;
        }
        for (int i = 0; i < 4; i++) {
            for (Tile t : table.getExposedTiles(i)) {
                if (t.equals(tile))
                    count++;
            }
        }
        if (count >= 3)
            return false;

        count = 0;
        for (int i = 0; i < possibilities.size(); i++) {
            if (possibilities.get(i).equals(tile))
                count++;
        }
        if (count == 2 && count != 4)
            return true;
        return false;
    }

    // public boolean partOf1Of3Triple(Tile tile, ArrayList<Tile> possibilities) {
    // int count = 0;
    // for (Tile t : table.getDiscardedTiles()) {
    // if (t.equals(tile))
    // count++;
    // }
    // for (int i = 0; i < 4; i++) {
    // for (Tile t : table.getExposedTiles(i)) {
    // if (t.equals(tile))
    // count++;
    // }
    // }
    // if (count >= 2)
    // return false;

    // return true;
    // }

    // public boolean partOf1Of2(Tile tile, ArrayList<Tile> possibilities) {
    // int count = 0;
    // for (Tile t : table.getDiscardedTiles()) {
    // if (t.equals(tile))
    // count++;
    // }
    // for (int i = 0; i < 4; i++) {
    // for (Tile t : table.getExposedTiles(i)) {
    // if (t.equals(tile))
    // count++;
    // }
    // }
    // if (count >= 3)
    // return false;

    // return true;
    // }

    // public boolean partOfRun(Tile tile, ArrayList<Tile> possibilities) {
    // if (tile.isNumbered()) {
    // if (tile.getRank() <= 7) {
    // Tile check1 = new Tile(tile.getSuit(), tile.getRank() + 1);
    // Tile check2 = new Tile(tile.getSuit(), tile.getRank() + 2);

    // int count1 = 0;
    // int count2 = 0;

    // for (Tile t : possibilities) {
    // if (t.equals(check1))
    // count1++;
    // if (t.equals(check2))
    // count2++;
    // }

    // if (count1 >= 1 && count2 >= 1)
    // return true;
    // }

    // if (tile.getRank() >= 3) {
    // Tile check1 = new Tile(tile.getSuit(), tile.getRank() - 1);
    // Tile check2 = new Tile(tile.getSuit(), tile.getRank() - 2);

    // int count1 = 0;
    // int count2 = 0;

    // for (Tile t : possibilities) {
    // if (t.equals(check1))
    // count1++;
    // if (t.equals(check2))
    // count2++;
    // }

    // if (count1 >= 1 && count2 >= 1)
    // return true;
    // }

    // if (tile.getRank() >= 2 && tile.getRank() <= 8) {
    // Tile check1 = new Tile(tile.getSuit(), tile.getRank() + 1);
    // Tile check2 = new Tile(tile.getSuit(), tile.getRank() - 1);

    // int count1 = 0;
    // int count2 = 0;

    // for (Tile t : possibilities) {
    // if (t.equals(check1))
    // count1++;
    // if (t.equals(check2))
    // count2++;
    // }

    // if (count1 >= 1 && count2 >= 1)
    // return true;
    // }
    // }

    // return false;
    // }

    // public boolean partOf2Of3Run(Tile tile, ArrayList<Tile> possibilities) {
    // if (tile.isNumbered()) {
    // if (tile.getRank() <= 7) {
    // Tile check1 = new Tile(tile.getSuit(), tile.getRank() + 1);
    // Tile check2 = new Tile(tile.getSuit(), tile.getRank() + 2);

    // int count1 = 0;
    // int count2 = 0;

    // for (Tile t : possibilities) {
    // if (t.equals(check1))
    // count1++;
    // if (t.equals(check2))
    // count2++;
    // }

    // if (count1 >= 1 || count2 >= 1)
    // return true;
    // }

    // if (tile.getRank() >= 3) {
    // Tile check1 = new Tile(tile.getSuit(), tile.getRank() - 1);
    // Tile check2 = new Tile(tile.getSuit(), tile.getRank() - 2);

    // int count1 = 0;
    // int count2 = 0;

    // for (Tile t : possibilities) {
    // if (t.equals(check1))
    // count1++;
    // if (t.equals(check2))
    // count2++;
    // }

    // if (count1 >= 1 || count2 >= 1)
    // return true;
    // }

    // if (tile.getRank() >= 2 && tile.getRank() <= 8) {
    // Tile check1 = new Tile(tile.getSuit(), tile.getRank() + 1);
    // Tile check2 = new Tile(tile.getSuit(), tile.getRank() - 1);

    // int count1 = 0;
    // int count2 = 0;

    // for (Tile t : possibilities) {
    // if (t.equals(check1))
    // count1++;
    // if (t.equals(check2))
    // count2++;
    // }

    // if (count1 >= 1 || count2 >= 1)
    // return true;
    // }
    // }

    // return false;
    // }

    public boolean declaresWin(Tile discardedTile, int score) {
        return true;
    }

    public boolean declaresTriple(Tile discardedTile) {
        if (partOfTriple(discardedTile, new ArrayList<Tile>(table.getHiddenTiles())))
            return false;

        return true;

    }

    public Tile declaresRun(Tile discardedTile, Tile[] startingTiles) {

        return null;
        // ArrayList<Tile> hand = new ArrayList<Tile>(table.getHiddenTiles());

        // hand.add(discardedTile);

        // Collections.sort(hand);

        // if (partOfRun(discardedTile, new ArrayList<Tile>(table.getHiddenTiles())))
        // return null;

        // int[] points = new int[startingTiles.length];

        // for (int i = 0; i < startingTiles.length; i++) {
        // if (partOfTriple(startingTiles[i], hand))
        // points[i] += 200;
        // else if (partOfPair(startingTiles[i], hand))
        // points[i] += 10;

        // if (partOfRun(startingTiles[i], hand))
        // points[i] += 100;

        // if (partOfTriple(new Tile(startingTiles[i].getSuit(),
        // startingTiles[i].getRank() + 1), hand))
        // points[i] += 200;
        // else if (partOfPair(new Tile(startingTiles[i].getSuit(),
        // startingTiles[i].getRank() + 1), hand))
        // points[i] += 10;

        // if (partOfRun(new Tile(startingTiles[i].getSuit(), startingTiles[i].getRank()
        // + 1), hand))
        // points[i] += 100;

        // if (partOfTriple(new Tile(startingTiles[i].getSuit(),
        // startingTiles[i].getRank() + 2), hand))
        // points[i] += 200;
        // else if (partOfPair(new Tile(startingTiles[i].getSuit(),
        // startingTiles[i].getRank() + 2), hand))
        // points[i] += 10;

        // if (partOfRun(new Tile(startingTiles[i].getSuit(), startingTiles[i].getRank()
        // + 2), hand))
        // points[i] += 100;

        // // if (partOfRun(startingTiles[i], new
        // // ArrayList<Tile>(table.getDiscardedTiles())))
        // // points[i] += 200;

        // // else if (partOf2Of3Run(startingTiles[i], new
        // // ArrayList<Tile>(table.getDiscardedTiles())))
        // // points[i] += 35;

        // // if (partOfTriple(startingTiles[i], new
        // // ArrayList<Tile>(table.getDiscardedTiles())))
        // // points[i] += 100;

        // // else if (partOf2Of3Triple(startingTiles[i], new
        // // ArrayList<Tile>(table.getDiscardedTiles())))
        // // points[i] += 20;

        // // else if (partOfPair(startingTiles[i], new
        // // ArrayList<Tile>(table.getDiscardedTiles())))
        // // points[i] += 10;

        // // else if (partOf1Of3Triple(startingTiles[i], new
        // // ArrayList<Tile>(table.getDiscardedTiles())))
        // // points[i] += 5;

        // // else if (partOf1Of2(startingTiles[i], new
        // // ArrayList<Tile>(table.getDiscardedTiles())))
        // // points[i] += 1;
        // }

        // int min = Integer.MAX_VALUE;
        // int j = 0;

        // for (int i = points.length - 1; i >= 0; i--) {
        // if (points[i] < min) {
        // j = i;
        // min = points[i];
        // }
        // }
        // return startingTiles[j];
    }

    public void opponentDiscarded(int opponentSeat, Tile discardedTile) {

    }
}