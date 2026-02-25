package tetris.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import tetris.model.Board;
import tetris.model.Tetromino;

public class Render {

    private static final Color BOARD_BG_TOP = Color.web("#1A1C22", 0.96);
    private static final Color BOARD_BG_BOTTOM = Color.web("#111319", 0.96);
    private static final Color GRID_COLOR = Color.web("#D9E4FF", 0.10);
    private static final Color FRAME_BASE = Color.web("#727D92", 0.78);
    private static final Color FRAME_INNER = Color.web("#B9C4D9", 0.35);

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

        LinearGradient premiumBg = new LinearGradient(
                0,
                0,
                0,
                1,
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0.0, BOARD_BG_TOP),
                new Stop(1.0, BOARD_BG_BOTTOM));
        gc.setFill(premiumBg);
        gc.fillRect(0, 0, width, height);

        gc.setStroke(GRID_COLOR);
        gc.setLineWidth(1.0);
        for (int c = 0; c <= cols; c++) {
            double x = c * cellSize;
            gc.strokeLine(x, 0, x, height);
        }
        for (int r = 0; r <= rows; r++) {
            double y = r * cellSize;
            gc.strokeLine(0, y, width, y);
        }

        double frameThickness = Math.max(6.0, width * 0.012);
        gc.setStroke(FRAME_BASE);
        gc.setLineWidth(frameThickness);
        gc.strokeRect(frameThickness * 0.5, frameThickness * 0.5, width - frameThickness, height - frameThickness);

        gc.setStroke(FRAME_INNER);
        gc.setLineWidth(1.5);
        gc.strokeRect(frameThickness + 1.0, frameThickness + 1.0, width - frameThickness * 2.0 - 2.0, height - frameThickness * 2.0 - 2.0);

        drawMetallicCornerAccents(gc, width, height, frameThickness);
        drawTopFrameReflection(gc, width, frameThickness);
    }

    private void drawMetallicCornerAccents(GraphicsContext gc, double width, double height, double frameThickness) {
        gc.setStroke(Color.rgb(188, 198, 214, 0.58));
        gc.setLineWidth(2.0);
        double s = Math.min(width, height) * 0.040;
        double p = frameThickness * 0.7;

        gc.strokeLine(p, p, p + s, p);
        gc.strokeLine(p, p, p, p + s);

        gc.strokeLine(width - p, p, width - p - s, p);
        gc.strokeLine(width - p, p, width - p, p + s);

        gc.strokeLine(p, height - p, p + s, height - p);
        gc.strokeLine(p, height - p, p, height - p - s);

        gc.strokeLine(width - p, height - p, width - p - s, height - p);
        gc.strokeLine(width - p, height - p, width - p, height - p - s);
    }

    private void drawTopFrameReflection(GraphicsContext gc, double width, double frameThickness) {
        double y = frameThickness * 0.55;
        gc.setStroke(Color.rgb(255, 255, 255, 0.16));
        gc.setLineWidth(1.6);
        gc.strokeLine(width * 0.15, y, width * 0.85, y);
        gc.setStroke(Color.rgb(210, 226, 255, 0.10));
        gc.setLineWidth(1.0);
        gc.strokeLine(width * 0.22, y + 3.0, width * 0.78, y + 3.0);
    }

    public void drawLineClearEffect(GraphicsContext gc, int row, int cols, double progress) {
        double p = Math.max(0.0, Math.min(1.0, progress));
        double y = row * cellSize;
        Color softSweep = Color.rgb(255, 220, 180, 0.10 + 0.18 * (1.0 - p));
        gc.setFill(softSweep);
        gc.fillRect(0, y, cols * cellSize * p, cellSize);
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
                    drawShadedCell(gc, c, r, Color.RED, 1.0);
                }
            }
        }
    }

    public void drawTetromino(GraphicsContext gc, Tetromino t) {
        if (t == null) return;

        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (t.shape[r][c] == 1) {
                    drawShadedCell(gc, t.col + c, t.row + r, t.color, 1.0);
                }
            }
        }
    }

    public void drawGhost(GraphicsContext gc, Tetromino g) {
        if (g == null) return;

        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (g.shape[r][c] == 1) {
                    drawShadedCell(gc, g.col + c, g.row + r, Color.GRAY, 0.35);
                }
            }
        }
    }

    private void drawShadedCell(GraphicsContext gc, int col, int row, Color baseColor, double alpha) {
        double x = col * cellSize;
        double y = row * cellSize;
        double w = cellSize - 1;
        double h = cellSize - 1;

        Color bright = baseColor.interpolate(Color.WHITE, 0.26);
        Color dark = baseColor.interpolate(Color.BLACK, 0.34);

        LinearGradient blockFill = new LinearGradient(
                0,
                0,
                1,
                1,
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0.0, withAlpha(bright, alpha)),
                new Stop(0.45, withAlpha(baseColor, alpha)),
                new Stop(1.0, withAlpha(dark, alpha)));
        gc.setFill(blockFill);
        gc.fillRect(x, y, w, h);

        gc.setStroke(withAlpha(Color.WHITE, 0.32 * alpha));
        gc.setLineWidth(1.0);
        gc.strokeLine(x + 1, y + 1, x + w - 1, y + 1);
        gc.strokeLine(x + 1, y + 1, x + 1, y + h - 1);

        gc.setStroke(withAlpha(Color.BLACK, 0.30 * alpha));
        gc.strokeLine(x + 1, y + h - 1, x + w - 1, y + h - 1);
        gc.strokeLine(x + w - 1, y + 1, x + w - 1, y + h - 1);
    }

    private Color withAlpha(Color c, double alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.max(0.0, Math.min(1.0, c.getOpacity() * alpha)));
    }
}
