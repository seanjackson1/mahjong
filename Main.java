public class Main {
    public static void main(String[] args) {
        int delayInMilliseconds = 500;
        boolean showAllTilesSeat0 = true;
        boolean showAllTilesSeat1 = true;
        boolean showAllTilesSeat2 = true;
        boolean showAllTilesSeat3 = true;
        Display display = new Display(delayInMilliseconds, showAllTilesSeat0,
                showAllTilesSeat1,
                showAllTilesSeat2, showAllTilesSeat3);
        Strategy seat0 = new Marsha2();
        Strategy seat1 = new Marsha2();
        Strategy seat2 = new Marsha2();
        Strategy seat3 = new SeanJackson();

        int[] pointsGained = Game.play(display, seat0, seat1, seat2, seat3);

        System.out.println("Seat 0 gained " + pointsGained[0] + " points");
        System.out.println("Seat 1 gained " + pointsGained[1] + " points");
        System.out.println("Seat 2 gained " + pointsGained[2] + " points");
        System.out.println("Seat 3 gained " + pointsGained[3] + " points");

        // Display display = new Display(300, true, true, true, true);
        // int[] gains = Game.play(display, new SeanJackson(), new RemoteStrategy(), new
        // SeanJackson(),
        // new RemoteStrategy());
        // System.out.println(java.util.Arrays.toString(gains));

    }
}