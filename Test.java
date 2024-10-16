import java.util.ArrayList;

public class Test {

    // Add discardTile to hand in callRun

    public static void main(String[] args) {

        // new RemoteTable("10.13.34.174", new SeanJackson());
        // new RemoteTable("10.13.34.174", new SeanJackson());

        int bigTotal = 0;
        int totalLostOrNothing = 0;
        for (int k = 0; k < 10; k++) {
            ArrayList<Integer> games = new ArrayList<>();
            int total = 0;
            int gamesLostOrNothing = 0;
            for (int i = 0; i < 2500; i++) {

                int[] pointsGained = new int[4];
                for (int j = 0; j < 4; j++) {
                    Strategy seat0 = new Marsha2();
                    Strategy seat1 = new Marsha2();
                    Strategy seat2 = new Marsha2();
                    Strategy seat3 = new SeanJackson2();
                    if (j == 0)
                        pointsGained = Game.play(null, seat3, seat0, seat1, seat2);
                    else if (j == 1)
                        pointsGained = Game.play(null, seat0, seat3, seat1, seat2);
                    else if (j == 2)
                        pointsGained = Game.play(null, seat0, seat1, seat3, seat2);
                    else if (j == 3)
                        pointsGained = Game.play(null, seat0, seat1, seat2, seat3);

                    total += pointsGained[j];

                    games.add(pointsGained[j]);

                    if (pointsGained[j] <= 0)
                        gamesLostOrNothing++;
                }

            }
            double sd = 0;

            double mean = total / 10000;

            for (int i : games) {
                sd += ((i - mean) * (i - mean));
            }

            sd /= 9999;

            sd = Math.sqrt(sd);

            System.out.println("Round " + k + " Points: " + total);

            System.out.println("Round " + k + " Average: " + (double) total / 10000);

            System.out.println("Round " + k + " Standard Deviation: " + sd);

            System.out.println("Round " + k + " Games Lost: " + gamesLostOrNothing);

            System.out.println();

            bigTotal += total;

            totalLostOrNothing += gamesLostOrNothing;
        }

        System.out.println("Average Points in 10,000: " + bigTotal / 10);
        System.out.println("Average Points: " + bigTotal / 100000);
        System.out.println("Average Games Lost: " + totalLostOrNothing / 10);

    }

}
