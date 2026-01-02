package tetris.view;

import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;

public class NextPane extends StackPane {

    private final Canvas nextCanvas;
    private final int nextCellSize;

    public NextPane(double width, double height) {
        setPrefSize(width, height);
        setMinSize(width, height);
        setMaxSize(width, height);
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: #111;");

        nextCanvas = new Canvas();
        nextCanvas.setWidth(width);
        nextCanvas.setHeight(height);
        nextCellSize = (int) Math.min(nextCanvas.getWidth() / 4, nextCanvas.getHeight() / 4);

        getChildren().add(nextCanvas);
    }

    public Canvas getNextCanvas() {
        return nextCanvas;
    }

    public int getNextCellSize() {
        return nextCellSize;
    }
}
