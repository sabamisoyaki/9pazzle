package tetris.panic;

public interface BoardProvider {
    int getWidth();
    int getHeight();
    boolean isOccupied(int x, int y);
}
