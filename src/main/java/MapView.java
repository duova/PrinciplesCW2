/***
 * A MapView contains references to the tiles on a section of the map. It's used to represent a creature's view.
 */
public class MapView {
    public Tile[][] tiles;

    public int xTranslation;
    public int yTranslation;

    /**
     * Constructor will fill in blank space with new wall tiles and as such ranges outside of bounds can be used.
     */
    public MapView(DungeonMap map, int xMinInclusive, int xMaxInclusive, int yMinInclusive, int yMaxInclusive) {
        xTranslation = xMinInclusive;
        yTranslation = yMinInclusive;
        tiles = new Tile[xMaxInclusive - xMinInclusive + 1][yMaxInclusive - yMinInclusive + 1];
        for (int x = xMinInclusive; x < xMaxInclusive + 1; x++) {
            for (int y = yMinInclusive; y < yMaxInclusive + 1; y++) {
                if (x < 0 || x >= map.tiles.length || y < 0 || y >= map.tiles[x].length) {
                    Tile fillTile = new Tile();
                    fillTile.isWall = true;
                    tiles[x - xMinInclusive][y - yMinInclusive] = fillTile;
                    continue;
                }
                tiles[x - xMinInclusive][y - yMinInclusive] = map.tiles[x][y];
            }
        }
    }
}
