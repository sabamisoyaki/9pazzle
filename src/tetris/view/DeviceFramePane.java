package tetris.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class DeviceFramePane extends Pane {

    private static final double FRAME_SIZE = 900;

    private final Canvas playfieldCanvas;

    public DeviceFramePane() {
        setPrefSize(FRAME_SIZE, FRAME_SIZE);
        setMinSize(FRAME_SIZE, FRAME_SIZE);
        setMaxSize(FRAME_SIZE, FRAME_SIZE);

        Rectangle frame = new Rectangle(FRAME_SIZE, FRAME_SIZE);
        frame.setFill(Color.web("#111"));
        frame.setStroke(Color.web("#444"));
        frame.setStrokeWidth(4);

        playfieldCanvas = new Canvas(FRAME_SIZE, FRAME_SIZE);
        playfieldCanvas.setLayoutX(0);
        playfieldCanvas.setLayoutY(0);

        getChildren().addAll(frame, playfieldCanvas);
    }

    public Canvas getPlayfieldCanvas() {
        return playfieldCanvas;
    }
}
