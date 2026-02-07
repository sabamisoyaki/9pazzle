package tetris.view;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class DeviceFramePane extends Pane {

    private static final Path DEFAULT_BACKGROUND_IMAGE = Paths.get("images", "playfield-bg.png");

    private final Canvas playfieldCanvas;
    private final ImageView backgroundView;

    public DeviceFramePane(double frameSize) {
        setPrefSize(frameSize, frameSize);
        setMinSize(frameSize, frameSize);
        setMaxSize(frameSize, frameSize);

        backgroundView = new ImageView();
        backgroundView.setFitWidth(frameSize);
        backgroundView.setFitHeight(frameSize);

        Rectangle frame = new Rectangle(frameSize, frameSize);
        frame.setFill(Color.web("#111"));
        frame.setStroke(Color.web("#444"));
        frame.setStrokeWidth(4);

        playfieldCanvas = new Canvas();
        playfieldCanvas.setWidth(frameSize);
        playfieldCanvas.setHeight(frameSize);
        playfieldCanvas.setLayoutX(0);
        playfieldCanvas.setLayoutY(0);

        getChildren().addAll(backgroundView, frame, playfieldCanvas);

        loadBackgroundImage(DEFAULT_BACKGROUND_IMAGE);
    }

    public Canvas getPlayfieldCanvas() {
        return playfieldCanvas;
    }

    public void loadBackgroundImage(Path imagePath) {
        if (imagePath == null || !Files.exists(imagePath)) {
            backgroundView.setImage(null);
            return;
        }

        Image image = new Image(imagePath.toUri().toString());
        backgroundView.setImage(image);
    }
}
