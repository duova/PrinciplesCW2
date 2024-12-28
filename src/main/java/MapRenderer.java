public class MapRenderer {
    public static String renderAsString(MapView mapView) {
        char[] render = new char[mapView.tiles.length * mapView.tiles[0].length];
        int charNum = 0;
        for (int y = mapView.tiles[0].length - 1; y >= 0; y--) {
            for (int x = mapView.tiles.length - 1; x >= 0; x--) {
                render[charNum] = mapView.tiles[x][y].render();
            }
        }
        return String.copyValueOf(render);
    }
}
