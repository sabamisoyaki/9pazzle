package tetris.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class NextPane extends VBox {

    private final Canvas nextCanvas;
    private final int nextCellSize;

    public NextPane(double width, double height) {
        setPrefSize(width, height);
        setMinSize(width, height);
        setMaxSize(width, height);
        setAlignment(Pos.TOP_CENTER);
        setSpacing(24);
        setPadding(new Insets(40, 20, 20, 20));
        setStyle("-fx-background-color: #111;");

        Label label = new Label("NEXT");
        label.setStyle("-fx-font-size: 24px; -fx-text-fill: white;");

        double canvasSize = Math.min(width * 0.8, height * 0.5);
        nextCanvas = new Canvas(canvasSize, canvasSize);
        nextCellSize = (int) Math.min(nextCanvas.getWidth() / 4, nextCanvas.getHeight() / 4);

        getChildren().addAll(label, nextCanvas);
    }

    public Canvas getNextCanvas() {
        return nextCanvas;
    }

    public int getNextCellSize() {
        return nextCellSize;
    }
}
