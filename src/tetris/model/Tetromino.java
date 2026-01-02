package tetris.model;

import javafx.scene.paint.Color;

public class Tetromino {

    public ShapeType type;
    public int[][] shape;
    public int row;
    public int col;
    public int rotation;  // 0〜3
    public Color color;

    public Tetromino(ShapeType type) {
        this.type = type;
        this.shape = deepCopy(type.shape);
        this.color = type.color;
        this.row = 0;
        this.col = 3;
        this.rotation = 0;
    }

    public Tetromino copy() {
        Tetromino t = new Tetromino(this.type);
        t.row = this.row;
        t.col = this.col;
        t.rotation = this.rotation;

        t.shape = new int[4][4];
        for (int r = 0; r < 4; r++)
            for (int c = 0; c < 4; c++)
                t.shape[r][c] = this.shape[r][c];

        return t;
    }

    public int countBlocks() {
        int count = 0;
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (this.shape[r][c] == 1) {
                    count++;
                }
            }
        }
        return count;
    }

    private static int[][] deepCopy(int[][] src) {
        int[][] dst = new int[src.length][];
        for (int i = 0; i < src.length; i++) {
            dst[i] = src[i].clone();
        }
        return dst;
    }
}
