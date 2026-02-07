package tetris.model;

public class Board {

    public static int ROWS = 25;
    public static int COLS = 25;

    private int[][] board = new int[ROWS][COLS];
    private int totalClearedLines = 0;
    public Board() {
        // 初期化済み配列なので特に何もしない
    }

    public int getCell(int r, int c) {
        return board[r][c];
    }

    public int getTotalClearedLines() {
        return totalClearedLines;
    }

    public int getRows() { return ROWS; }
    public int getCols() { return COLS; }


    // --- 衝突判定 ---
    public boolean canMove(Tetromino t, int dRow, int dCol) {
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (t.shape[r][c] == 1) {
                    int newR = t.row + r + dRow;
                    int newC = t.col + c + dCol;

                    if (newC < 0 || newC >= COLS) return false;
                    if (newR >= ROWS) return false;
                    if (newR >= 0 && board[newR][newC] != 0) return false;
                }
            }
        }
        return true;
    }

    public boolean canMoveDown(Tetromino t) {
        return canMove(t, 1, 0);
    }


    // --- 回転判定 ---
    public boolean canRotate(Tetromino t, int[][] rotated) {
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (rotated[r][c] == 1) {
                    int newR = t.row + r;
                    int newC = t.col + c;

                    if (newC < 0 || newC >= COLS) return false;
                    if (newR >= ROWS) return false;
                    if (newR >= 0 && board[newR][newC] != 0) return false;
                }
            }
        }
        return true;
    }

    // --- 固定 ---
    public void fixToBoard(Tetromino t) {
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (t.shape[r][c] == 1) {
                    int br = t.row + r;
                    int bc = t.col + c;
                    board[br][bc] = 1;
                }
            }
        }
    }
    /** 固定ブロック全体を90°回転させる（右回転） */
    public void rotateClockwise() {

        int[][] newBoard = new int[ROWS][COLS];

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {

                int value = board[r][c];

                if (value != 0) {
                    // 新しい座標へ
                    int newR = c;
                    int newC = (ROWS - 1) - r;
                    //正方形なので問題なし　
                    if (newR < ROWS && newC < COLS) {
                        newBoard[newR][newC] = value;
                    }
                }
            }
        }
        // 作り直した盤面を反映
        System.out.println("==== WORLD ROTATED ====");
        board = newBoard;
    }
    // --- ライン消去 ---
    private boolean isLineFull(int row) {
        for (int c = 0; c < COLS; c++) {
            if (board[row][c] == 0) return false;
        }
        return true;
    }

    private void clearLine(int row) {
        for (int r = row; r > 0; r--) {
            System.arraycopy(board[r - 1], 0, board[r], 0, COLS);
        }
        for (int c = 0; c < COLS; c++) {
            board[0][c] = 0;
        }
    }

    public int clearCompletedLines() {
        int count = 0;
        for (int r = ROWS - 1; r >= 0; r--) {
            if (isLineFull(r)) {
                clearLine(r);
                count++;
                r++;
            }
        }
        totalClearedLines += count;
        return count;
    }

    public boolean canPlace(int[][] shape, int row, int col) {
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (shape[r][c] == 1) {
                    int br = row + r;
                    int bc = col + c;

                    if (br >= ROWS || bc < 0 || bc >= COLS)
                        return false;

                    if (br >= 0 && board[br][bc] != 0)
                        return false;
                }
            }
        }
        return true;
    }
}
