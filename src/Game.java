import java.util.Scanner;

public class Game {


    public int mapSize;
    public int hp;
    public Game() {
        System.out.println("Game created..");
    }
    Scanner userInput = new Scanner(System.in);  // Create a Scanner object

    public void startGame(){
        setMapSize();
        Spieler spieler1 = new Spieler("Name1", mapSize, hp);
        Spieler spieler2 = new Spieler("Name2", mapSize, hp);
        spieler1.printBoth(spieler1, spieler2);
        spieler1.manualShipPlacement(1,1,2,2);
        spieler1.manualShipPlacement(7,7,4,3);
        spieler1.manualShipPlacement(3,3,6,2);
        spieler1.manualShipPlacement(7,6,4,2);
        spieler1.printBoth(spieler1, spieler2);

        System.out.println("Schiffe platzieren oder auf Schiffe schiessen? (1 oder 2): ...");
        int modus = userInput.nextInt();
        if (modus == 1) {
            while (true) {
                spieler1.placeShipRequest();
                spieler1.printBoth(spieler1, spieler2);
            }
        } else if (modus == 2) {
            while (true) {
                spieler1.shootrequest();
                spieler1.printBoth(spieler1, spieler2);
                spieler2.shootrequest();
                spieler1.printBoth(spieler1, spieler2);
            }
        } else {
            System.out.println("1 ODER 2, nicht " + modus);
        }
    }

    public void setMapSize(){
        boolean breakout = false;
        int size;
        while (!breakout) {
            System.out.println("Spielfeldgroesse zwischen 5 und 35 auswaehlen:");
            size = userInput.nextInt();
            System.out.println("SIZE"+size);
            if  (size < 31 && size > 4) {
                mapSize=size;
                hp= mapSize;
                hp*=30;
                System.out.println("Health: "+hp);
                breakout = true;
            } else if  (size > 30) {
                System.out.println("Maximale Spielfeldgröße überschritten");
            } else /*if (size<5)*/ {
                System.out.println("Spielfeld zu klein!");
            }
        }
        System.out.println("BREAKOUT");
    }
}
