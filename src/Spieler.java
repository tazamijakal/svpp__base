import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Die Klasse Spieler beinhaltet momentan Methoden zur Spieler- und Spielfelderzeugung, Schiffplatzierung (und Kollisionsabfrage).
 */
public class Spieler {
    private final String name;

    int x, y, xd, yd, direction, length; //wird fuer Schiffplatzierung benutzt
    private final int hp;          //capacity = felder die schiffe sind bzw. HP welche 0 erreichen wenn alle Schiffe zerstoert sind
    public int mapSize;
    static int playerNumber = 1;         //currently not used, automatically incremented on player creation
    public final Object[][] board;      //Spielfeld ist eine Matrix und kann leicht navigiert und bearbeitet werden
    private int[][] collisionMap;        //
    private int[][] hitMap;
    public ArrayList<com.company.Ship> shipList = new ArrayList<>();
    Scanner userinput = new Scanner(System.in); //wird fuer Userinput benoetigt

    public static class TrefferObject{ public TrefferObject(){}}
    TrefferObject trefferObject = new TrefferObject();
    public static class MisfireObject{ public MisfireObject(){}}
    MisfireObject misfireObject = new MisfireObject();


    /**
     * Konstruktor erzeugt neuen Spieler mit eigener Map.
     *
     * @param name      Name des Spielers
     * @param mapSize   Groesse der Map (mapSize*mapSize == x*y)
     * @param hp        Legt fest wie viele Felder mit Schiffen belegt sein koennen-
     */
    public Spieler(String name, int mapSize, int hp) {     //move mapSize out?? capacity and name not used
        this.name = name;
        this.hp = hp;
        this.mapSize = mapSize;
        playerNumber++;                     //der 2te Spieler der erstellt wird bekommt automatisch die #2 zugewiesen
        collisionMap = new int[mapSize][mapSize];
        board = new Object[mapSize][mapSize];       //Spielfeld
        for (int y = 0; y < mapSize; y++) {         //fuellt die map mit Z, soll Wasser darstellen
            for (int x = 0; x < mapSize; x++) {
                board[x][y] = null;
                collisionMap[x][y] = 0;
            }
        }
    }

    /**
     * Printet nur das Spielfeld des Objekts, welches die Funktion aufruft.
     */
    public void printField() {
        for (int y = 0; y < mapSize; y++) {
            for (int x = 0; x < mapSize; x++) {
                System.out.print(board[x][y] + "  ");
            }
        }
    }

    /**
     * Printet beide Spielfelder nebeneinander. Da die Reihenfolge gleich bleibt kann man immer abwechselnd one und two abrufen.
     *
     * @param one Spieler 1
     * @param two Spieler 2
     */
    public void printBoth(Spieler one, Spieler two) {
        System.out.println();
        for (int y = 0; y < one.mapSize; y++) {
            one.printRow(y);
            System.out.print("     ");
            one.printCollisionRow(y);
            System.out.print("     ");
            two.printRow(y);
            System.out.println("\n");
        }
    }

    /**
     * printet nur 1 Reihe, wird in printBoth() verwendet
     *
     * @param row       Zeilenangabe
     */
    public void printRow(int row) {
        for (int x = 0; x < mapSize; x++) {
            if(board[x][row] instanceof com.company.Ship) {
                System.out.print("@" + "    ");
            }
            if(board[x][row] instanceof TrefferObject) {
                System.out.print("X" + "    ");
            }
            if(board[x][row] == null) {
                System.out.print("~" + "    ");
            }
            if(board[x][row] instanceof MisfireObject) {
                System.out.print("Z" + "    ");
            }
        }
    }

    public void printCollisionRow(int row){
        for (int x = 0; x < mapSize; x++) {
            System.out.print(collisionMap[x][row] + "    ");
        }
    }

    /**
     * Manuelle Positionierung von Schiffen
     *
     * @param x         x wert
     * @param y         y wert
     * @param direction richtung
     * @param length    laenge
     */
    public void manualShipPlacement(int x, int y, int direction, int length) {
        this.x = x - 1;
        this.y = y - 1;
        this.direction = direction;
        this.length = length;
        if (spaceCheck()) {
            placeShip(true);
        } else {
            System.out.println("Manual ship placement failed!");
        }
    }

    /**
     * Fraegt den Spieler wo er Schiffe haben will. Ist in einem loop, bis passende Position gefunden wurde.
     */
    public void placeShipRequest() {
        System.out.println("X-Koordinate bei der das Schiff anfangen soll: ...");
        x = userinput.nextInt() - 1;                  //da User nicht davon ausgeht das Matrix mit [0] startet
        System.out.println("Y-Koordinate bei der das Schiff anfangen soll: ...");
        y = userinput.nextInt() - 1;
        System.out.println("Ship direction (numpad notation): ...");
        direction = userinput.nextInt();
        System.out.println("Schiffsgroesse eingeben: ...");
        length = userinput.nextInt();
        if (spaceCheck()) {
            placeShip(true);
        } else {
            System.out.println("Ship could not be placed, try again!");
            placeShipRequest();
        }
    }

    /**
     * Ueberprueft  mittels der in placeShip aktualisierten collision map ob an der gewuenschten Stelle Platz fuer das Schiff ist
     *
     * @return false = kein Platz fuer das Schiff
     */
    public boolean spaceCheck() {
        xd = 0;      // xd/yd variable wird angeben in welche Richtung Schiff beim platzieren waechst
        yd = 0;
        switch (direction) {             //sucht nach passendem xd oder yd wert
            case 4 -> xd = -1;
            case 6 -> xd = 1;
            case 8 -> yd = -1;
            case 2 -> yd = 1;
            default -> {        //wenn direction invalid ist
                System.out.println("Not a valid direction!");
                return false;
            }
        }

        int xCheck = xd * length + x;         //wenn xCheck<0 clipped das Schiff mit der linken Wand, bei xCheck>mapSize clipped es rechts
        int yCheck = yd * length + y;         //same wie xCheck nur vertikal
        if (xCheck > mapSize || xCheck+1 < 0 || yCheck > mapSize || yCheck + 1 < 0) {      //wallclipchecker
            System.out.println("not enough space");
            return false;
        }

        int tempX = x;      //damit x und y nicht ueberschrieben werden
        int tempY = y;
        for (int i = 0; i < length; i++) {                            //prueft ob Felder (+deren angrenzende Felder) bereits belegt sind
            if (collisionMap[tempX][tempY]>0) {
                System.out.println("Not enough space!");
                return false;
            }
            tempX = tempX + xd;
            tempY = tempY + yd;
        }
        return true;
    }

    /**
     * Platziert Schiff und aktualisiert collision map. Aufruf nur ueber interne Funktionen damit Platzierung auch valid ist
     */
    protected void printShipList(){
        int c = 1;
        for(com.company.Ship i: shipList){
            System.out.print(i.length+"-er Schiff auf Koordinaten:");
            for(int[] z: i.coordinates){
                System.out.print(Arrays.toString(z)+"; ");
                c++;
            }
            System.out.println("");
        }
    }
    protected void placeShip(boolean placeRemoveToggle) {
        System.out.println("placeRemoveToggle at the beginning of placeShip: "+placeRemoveToggle);
        int[] startingPoint = {x, y};
        System.out.println("Starting point: ["+x+"]["+y+"]");
        System.out.println("x="+x+" y="+y+" d="+direction);
        int[][] hitMap = new int[length][2];             //TODO check if hitmap still needed
        for (int i = 0; i < length; i++) {
            hitMap[i][0] = x;
            hitMap[i][1] = y;
            System.out.println("Making x=" + x + " y=" + y + " into zero");
            board[x][y] = null;
            x = x + xd;
            y = y + yd;
        }
        int[] endPoint = {x - xd, y - yd};
        System.out.println("placeRemoveToggle before ship add check: "+placeRemoveToggle);
        if (placeRemoveToggle) {
            com.company.Ship schiff = new com.company.Ship(startingPoint[0], startingPoint[1], direction, length);
            schiff.addCoordinates(hitMap);
            shipList.add(schiff);
            for (int i = 0; i < length; i++) {
                board[hitMap[i][0]][hitMap[i][1]] = schiff;
            }
        }

        if (xd != 0) {        //bei horizontalem Wachstum soll die Koordinate mit dem kleineren x Startpunkt sein
            if (startingPoint[0] > endPoint[0]) {
                int[] temp = startingPoint;
                startingPoint = endPoint;
                endPoint = temp;
            }
        } else {            //same bei vertikal, nur mit y
            if (startingPoint[1] > endPoint[1]) {
                int[] temp = startingPoint;
                startingPoint = endPoint;
                endPoint = temp;
            }
        }

        startingPoint[0] -= 1;  //Erweiterung um die angrenzenden Felder
        startingPoint[1] -= 1;
        endPoint[0] += 1;
        endPoint[1] += 1;

        System.out.println("placeRemoveToggle before collision update: "+placeRemoveToggle);
        int startxBU = startingPoint[0];
        for (int c = startingPoint[1]; c <= endPoint[1]; c++) {       //zeichenet das Quadratmit den Eckpunkten (sp0, sp1, ep0, ep1) in die collision map ein
            for (int cc = startingPoint[0]; cc <= endPoint[0]; cc++) {
                try {
                    if (placeRemoveToggle) {
                        collisionMap[startingPoint[0]][startingPoint[1]]++;
                    } else {
                        collisionMap[startingPoint[0]][startingPoint[1]]--;
                    }
                } catch (ArrayIndexOutOfBoundsException E) {
                }
                startingPoint[0]++;
            }
            startingPoint[1]++;
            startingPoint[0] = startxBU;
        }
    }


    public void removeShip(int x, int y) {
        if (board[x][y] instanceof com.company.Ship) {
            Object dummy = board[x][y];
            com.company.Ship schiff = (com.company.Ship) dummy;
            this.x = schiff.initialX;
            this.y = schiff.initialY;
            direction = schiff.initialD;
            length = schiff.length;
            xd = 0;      // xd/yd variable wird angeben in welche Richtung Schiff beim platzieren waechst
            yd = 0;
            switch (direction) {             //sucht nach passendem xd oder yd wert
                case 4 -> xd = -1;
                case 6 -> xd = 1;
                case 8 -> yd = -1;
                case 2 -> yd = 1;
            }
            System.out.println("Removing from x="+this.x+" y="+this.y+" d="+direction+" length="+length);
            placeShip(false);
            shipList.remove(dummy);
            System.out.println("Schiff erflogreich entfernt: x="+x+" y="+y+" d="+direction+" length="+length);
        }
    }



    public boolean attackToken;

    /**
     * Schiesst auf angegebene Koordinaten, soll nur von shootrequest() aufgerufen werden da dieses prüft ob bereits auf das Feld geschossen wurde
     * @param x x-Achse
     * @param y y-Achse
     */
    public void shoot(int x, int y) {
        if (board[x][y] instanceof com.company.Ship) {
            board[x][y] = trefferObject;
            System.out.println("TREFFER!!!");
            shootrequest();
        } else {
            board[x][y] = misfireObject;
            System.out.println("NICHTS GETROFFEN");
            attackToken = false;
        }
    }


    /**
     * Fraegt den Spieler nach Zielkoordinaten.
     */
    public void shootrequest() {
        System.out.println("new shootrequest!!!");
        try{
            System.out.println("X-Koordinate eingeben: ...");
            int xAxis = userinput.nextInt() - 1;  // Read user input
            System.out.println("Y-Koordinate eingeben: ...");
            int yAxis = userinput.nextInt() - 1;
            if (board[xAxis][yAxis] instanceof TrefferObject || board[xAxis][yAxis] instanceof MisfireObject) {
                System.out.println("Bereits auf Feld geschossen!");
                shootrequest();
            } else {
                shoot(xAxis, yAxis);
            }
        }catch (ArrayIndexOutOfBoundsException E){
            System.out.println("Out of bounds!");
            shootrequest();
        }
    }

    /**
     * Soll spaeter das Game starten und beenden, ist aber noch nicht fertig
     * @param spieler1
     * @param spieler2
     */
//    public void startgame(Spieler spieler1, Spieler spieler2, ) {
//        int p1health = spieler1.capacity;
//        int p2health = spieler2.capacity;
//        while (spieler1.capacity > 0 || spieler2.capacity > 0) {        //capacity wird 0 wenn alle Schiffe zerstoert wurden
//            //pingpong schleife
//        }
//        System.out.println("Spieler X hat gewonnen.");
//    }
}

