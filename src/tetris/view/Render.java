package tetris.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import tetris.model.Board;
import tetris.model.Tetromino;

public class Render {

    private final int cellSize;

    public Render(int cellSize) {
        this.cellSize = cellSize;
    }

    // ===========================
    //     全描画まとめ
    // ===========================
    public void drawAll(GraphicsContext gc, Board board, Tetromino current, Tetromino ghost) {
        drawBoard(gc, board);
        drawGhost(gc, ghost);
        drawTetromino(gc, current);
    }

    // ===========================
    //        Nextミノ描画
    // ===========================
    public void drawNext(GraphicsContext gc, Tetromino next, int offsetX, int offsetY) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        if (next == null) return;

        gc.setFill(next.color);

        // NEXT は 4×4 の中で中央寄せしたい
        int minR = 4, maxR = 0, minC = 4, maxC = 0;

        // 形の最小/最大座標を計算（中央に置くため）
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (next.shape[r][c] == 1) {
                    minR = Math.min(minR, r);
                    maxR = Math.max(maxR, r);
                    minC = Math.min(minC, c);
                    maxC = Math.max(maxC, c);
                }
            }
        }

        int shapeWidth  = (maxC - minC + 1);
        int shapeHeight = (maxR - minR + 1);

        // NEXT表示枠の中央に来るように計算
        int baseX = offsetX + (int)( (4 - shapeWidth)  * cellSize / 2 );
        int baseY = offsetY + (int)( (4 - shapeHeight) * cellSize / 2 );

        // 描画
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (next.shape[r][c] == 1) {

                    gc.fillRect(
                        baseX + (c - minC) * cellSize,
                        baseY + (r - minR) * cellSize,
                        cellSize - 1,
                        cellSize - 1
                    );
                }
            }
        }
    }

    // ===========================
    //         盤面描画
    // ===========================
    public void drawBoard(GraphicsContext gc, Board board) {
        for (int r = 0; r < Board.ROWS; r++) {
            for (int c = 0; c < Board.COLS; c++) {

                if (board.getCell(r, c) == 0)
                    gc.setFill(Color.BLACK);
                else
                    gc.setFill(Color.RED);

                gc.fillRect(
                    c * cellSize,
                    r * cellSize,
                    cellSize - 1,
                    cellSize - 1
                );
            }
        }
    }

    // ===========================
    //       現在ミノ描画
    // ===========================
    public void drawTetromino(GraphicsContext gc, Tetromino t) {
        if (t == null) return;

        gc.setFill(t.color);

        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (t.shape[r][c] == 1) {
                    gc.fillRect(
                        (t.col + c) * cellSize,
                        (t.row + r) * cellSize,
                        cellSize - 1,
                        cellSize - 1
                    );
                }
            }
        }
    }


    // ===========================
    //       ゴースト描画
    // ===========================
    public void drawGhost(GraphicsContext gc, Tetromino g) {
        if (g == null) return;

        gc.setGlobalAlpha(0.25);
        gc.setFill(Color.GRAY);

        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (g.shape[r][c] == 1) {
                    drawCell(gc, g.row + r, g.col + c);
                }
            }
        }

        gc.setGlobalAlpha(1.0);
    }

    // 汎用セル描画
    private void drawCell(GraphicsContext gc, int r, int c) {
        gc.fillRect(
            c * cellSize,
            r * cellSize,
            cellSize - 1,
            cellSize - 1
        );
    }
}
