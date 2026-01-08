package tetris.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class DeviceFramePane extends Pane {

    private final Canvas playfieldCanvas;

    public DeviceFramePane(double frameSize) {
        setPrefSize(frameSize, frameSize);
        setMinSize(frameSize, frameSize);
        setMaxSize(frameSize, frameSize);

        Rectangle frame = new Rectangle(frameSize, frameSize);
        frame.setFill(Color.web("#111"));
        frame.setStroke(Color.web("#444"));
        frame.setStrokeWidth(4);

        playfieldCanvas = new Canvas();
        playfieldCanvas.setWidth(frameSize);
        playfieldCanvas.setHeight(frameSize);
        playfieldCanvas.setLayoutX(0);
        playfieldCanvas.setLayoutY(0);

        getChildren().addAll(frame, playfieldCanvas);
    }

    public Canvas getPlayfieldCanvas() {
        return playfieldCanvas;
    }
}
