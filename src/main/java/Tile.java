import java.util.ArrayList;
import java.util.List;

public class Tile {
    public ArrayList<Piece> pieces = new ArrayList<Piece>(); //Last piece is rendered.

    public boolean isWall; //We make tiles optionally walls as making walls a piece seems excessive with type checking.

    public char render() {
        return 0;
    }
}
