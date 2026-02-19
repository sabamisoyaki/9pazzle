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

    public void drawAll(GraphicsContext gc, Board board, Tetromino current, Tetromino ghost) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        drawBoard(gc, board);
        drawGhost(gc, ghost);
        drawTetromino(gc, current);
    }

    public void drawGridFrame(GraphicsContext gc, int rows, int cols) {
        double width = gc.getCanvas().getWidth();
        double height = gc.getCanvas().getHeight();
        gc.clearRect(0, 0, width, height);

        gc.setStroke(Color.rgb(240, 220, 220, 0.12));
        gc.setLineWidth(1.0);
        for (int c = 0; c <= cols; c++) {
            double x = c * cellSize;
            gc.strokeLine(x, 0, x, height);
        }
        for (int r = 0; r <= rows; r++) {
            double y = r * cellSize;
            gc.strokeLine(0, y, width, y);
        }

        gc.setStroke(Color.rgb(240, 228, 210, 0.42));
        gc.setLineWidth(4.0);
        gc.strokeRect(2, 2, width - 4, height - 4);

        drawDentalCornerAccents(gc, width, height);
        drawTopFrameReflection(gc, width);
    }

    private void drawDentalCornerAccents(GraphicsContext gc, double width, double height) {
        gc.setStroke(Color.rgb(220, 230, 240, 0.55));
        gc.setLineWidth(3.0);
        double s = Math.min(width, height) * 0.06;

        // Top-left
        gc.strokeLine(8, 8, 8 + s, 8);
        gc.strokeLine(8, 8, 8, 8 + s);
        // Top-right
        gc.strokeLine(width - 8, 8, width - 8 - s, 8);
        gc.strokeLine(width - 8, 8, width - 8, 8 + s);
        // Bottom-left
        gc.strokeLine(8, height - 8, 8 + s, height - 8);
        gc.strokeLine(8, height - 8, 8, height - 8 - s);
        // Bottom-right
        gc.strokeLine(width - 8, height - 8, width - 8 - s, height - 8);
        gc.strokeLine(width - 8, height - 8, width - 8, height - 8 - s);
    }

    private void drawTopFrameReflection(GraphicsContext gc, double width) {
        gc.setStroke(Color.rgb(255, 255, 255, 0.18));
        gc.setLineWidth(2.0);
        gc.strokeLine(width * 0.1, 12, width * 0.9, 12);
        gc.setStroke(Color.rgb(255, 255, 255, 0.10));
        gc.setLineWidth(1.0);
        gc.strokeLine(width * 0.16, 16, width * 0.84, 16);
    }

    public void drawNext(GraphicsContext gc, Tetromino next, int offsetX, int offsetY) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        if (next == null) return;

        gc.setFill(next.color);

        int minR = 4, maxR = 0, minC = 4, maxC = 0;

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

        int shapeWidth = (maxC - minC + 1);
        int shapeHeight = (maxR - minR + 1);

        int baseX = offsetX + (int) ((4 - shapeWidth) * cellSize / 2);
        int baseY = offsetY + (int) ((4 - shapeHeight) * cellSize / 2);

        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (next.shape[r][c] == 1) {

                    gc.fillRect(
                            baseX + (c - minC) * cellSize,
                            baseY + (r - minR) * cellSize,
                            cellSize - 1,
                            cellSize - 1);
                }
            }
        }
    }

    public void drawBoard(GraphicsContext gc, Board board) {
        for (int r = 0; r < Board.ROWS; r++) {
            for (int c = 0; c < Board.COLS; c++) {
                if (board.getCell(r, c) != 0) {
                    gc.setFill(Color.RED);
                    gc.fillRect(
                            c * cellSize,
                            r * cellSize,
                            cellSize - 1,
                            cellSize - 1);
                }
            }
        }
    }

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
                            cellSize - 1);
                }
            }
        }
    }

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

    private void drawCell(GraphicsContext gc, int r, int c) {
        gc.fillRect(
                c * cellSize,
                r * cellSize,
                cellSize - 1,
                cellSize - 1);
    }
}
