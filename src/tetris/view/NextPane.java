package tetris.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class NextPane extends VBox {

    private final Canvas nextCanvas;
    private final int nextCellSize;

    public NextPane() {
        setPrefSize(300, 900);
        setMinSize(300, 900);
        setMaxSize(300, 900);
        setAlignment(Pos.TOP_CENTER);
        setSpacing(24);
        setPadding(new Insets(40, 20, 20, 20));
        setStyle("-fx-background-color: #111;");

        Label label = new Label("NEXT");
        label.setStyle("-fx-font-size: 24px; -fx-text-fill: white;");

        nextCanvas = new Canvas(240, 240);
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
