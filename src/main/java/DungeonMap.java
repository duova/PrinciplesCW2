import java.util.Random;

public class DungeonMap {
    //Indexed in positive x then positive y.
    public Tile[][] tiles;

    public int winCount;

    private DungeonMap(int width, int height) {
        tiles = new Tile[width][height];
        for (Tile[] column : tiles) {
            for (Tile tile : column) {
                tile = new Tile();
            }
        }
    }

    public Vec getSpawnLocation() {
        int numPossibleTiles = 0;
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[0].length; y++) {
                if (tiles[x][y].pieces.isEmpty() && !tiles[x][y].isWall) {
                    numPossibleTiles++;
                }
            }
        }
        Random rand = new Random();
        int randomNumber = rand.nextInt(numPossibleTiles);
        int i = 0;
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[0].length; y++) {
                if (tiles[x][y].pieces.isEmpty() && !tiles[x][y].isWall) {
                    if (i == randomNumber) {
                        return new Vec(x, y);
                    }
                    i++;
                }
            }
        }
        return new Vec(0, 0);
    }

    public static DungeonMap fromString(String map) {
        int winTokenIndex = map.indexOf("win");
        String winCountSubstring = map.substring(winTokenIndex, map.indexOf("\n", winTokenIndex));
        int winCount = Integer.parseInt(winCountSubstring.replaceAll("[^0-9]", ""));

        String mapSubstring = map.substring(map.indexOf('#'));
        int x = 0;
        int y = 0;
        int xCount = mapSubstring.indexOf('\n');
        int yCount = 0;
        for (char character : mapSubstring.toCharArray()) {
            if (character == '\n') {
                yCount++;
            }
        }
        //We need to set y to the max because we start at max X on the string version of the map.
        y = yCount - 1;
        DungeonMap parsedMap = new DungeonMap(xCount, yCount);
        parsedMap.winCount = winCount;
        for (char character : mapSubstring.toCharArray()) {
            Tile tile = parsedMap.tiles[x][y];
            if (character == '#') {
                tile.isWall = true;
            }
            else if (character == 'E') {
                tile.pieces.add(new Exit(x, y, parsedMap));
            }
            else if (character == 'G') {
                tile.pieces.add(new Gold(x, y, parsedMap));
            }

            //Move to next position.
            x++;
            if (x <= xCount) {
                x = 0;
                y--;
            }
        }
        return parsedMap;
    }
}
