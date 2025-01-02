import java.util.Random;

/***
 * Representation of the dungeon map, should be created with DungeonMap.fromString();
 */
public class DungeonMap {
    //Indexed in positive x then positive y.
    public Tile[][] tiles;

    public int winCount;

    //Private constructor is used to create an empty DungeonMap of a specific size.
    protected DungeonMap(int width, int height) {
        tiles = new Tile[width][height];
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[0].length; y++) {
                tiles[x][y] = new Tile();
            }
        }
    }

    /***
     * Finds a random feasible spawn location on this map.
     * @return The location.
     */
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

    /***
     * Creates a DungeonMap from the map string.
     * The reason a static function is used instead of a constructor is so future impl of DungeonMap initializers
     * can use just a string for its params as well.
     * @param map The map in string form.
     * @return Initialized DungeonMap.
     */
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
        for (int i = 0; i < mapSubstring.length(); i++) {
            char character = mapSubstring.charAt(i);
            if (character == '\n') continue;
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
            //Iterates with inverted y as (0, 0) is the bottom left in console.
            x++;
            if (x >= xCount) {
                x = 0;
                if (y > 0) {
                    y--;
                }
                else {
                    break;
                }
            }
        }
        return parsedMap;
    }
}
