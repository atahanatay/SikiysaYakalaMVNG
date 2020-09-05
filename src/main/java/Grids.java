public class Grids {
    int x, y;

    public Grids(int x, int y) {
        this.x = x;
        this.y = y;
    }

    static boolean isEqual(Grids x, Grids y) {
        return x.x == y.x && x.y == y.y;
    }
}
