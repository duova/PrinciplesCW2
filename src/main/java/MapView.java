public class MapView {
    public Tile[][] tiles;

    /**
     * Constructor will fill in blank space with new wall tiles and as such ranges outside of bounds can be used.
     */
    public MapView(DungeonMap map, int xMinInclusive, int xMaxInclusive, int yMinInclusive, int yMaxInclusive) {
        tiles = new Tile[xMaxInclusive - xMinInclusive + 1][yMaxInclusive - yMinInclusive + 1];
        int x = xMinInclusive;
        int y = yMinInclusive;
        for (x = xMinInclusive; x < xMaxInclusive + 1; x++) {
            for (y = yMinInclusive; y < yMaxInclusive + 1; x++) {
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
