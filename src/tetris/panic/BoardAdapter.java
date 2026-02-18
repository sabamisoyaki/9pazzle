package tetris.panic;

import tetris.model.Board;

public class BoardAdapter implements BoardProvider {

    private final Board board;

    public BoardAdapter(Board board) {
        this.board = board;
    }

    @Override
    public int getWidth() {
        return board.getCols();
    }

    @Override
    public int getHeight() {
        return board.getRows();
    }

    @Override
    public boolean isOccupied(int x, int y) {
        return board.getCell(y, x) != 0;
    }
}
