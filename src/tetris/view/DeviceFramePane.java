package tetris.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class DeviceFramePane extends Pane {

    private static final double FRAME_SIZE = 900;
    private static final double PLAYFIELD_SIZE = 720;
    private static final double PLAYFIELD_OFFSET = (FRAME_SIZE - PLAYFIELD_SIZE) / 2;

    private final Canvas playfieldCanvas;

    public DeviceFramePane() {
        setPrefSize(FRAME_SIZE, FRAME_SIZE);
        setMinSize(FRAME_SIZE, FRAME_SIZE);
        setMaxSize(FRAME_SIZE, FRAME_SIZE);

        Rectangle frame = new Rectangle(FRAME_SIZE, FRAME_SIZE);
        frame.setFill(Color.web("#111"));
        frame.setStroke(Color.web("#444"));
        frame.setStrokeWidth(4);

        playfieldCanvas = new Canvas(PLAYFIELD_SIZE, PLAYFIELD_SIZE);
        playfieldCanvas.setLayoutX(PLAYFIELD_OFFSET);
        playfieldCanvas.setLayoutY(PLAYFIELD_OFFSET);

        getChildren().addAll(frame, playfieldCanvas);
    }

    public Canvas getPlayfieldCanvas() {
        return playfieldCanvas;
    }
}
