public class MapRenderer {
    public static String renderAsString(MapView mapView) {
        StringBuilder render = new StringBuilder();
        //Iterates with inverted y as (0, 0) is the bottom left in console.
        for (int y = mapView.tiles[0].length - 1; y >= 0; y--) {
            for (int x = 0; x < mapView.tiles.length; x++) {
                render.append(mapView.tiles[x][y].render());
            }
            render.append('\n');
        }
        return render.toString();
    }
}
