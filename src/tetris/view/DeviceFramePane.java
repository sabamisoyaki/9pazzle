package tetris.view;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class DeviceFramePane extends Pane {

    private static final Path DEFAULT_PHOTO_BACKGROUND = Paths.get("images", "playfield-bg.png");

    private final StackPane playfieldStack;
    private final ImageView realPhotoBackgroundView;
    private final Canvas mouthBackgroundCanvas;
    private final Canvas gridFrameCanvas;
    private final Canvas blocksCanvas;

    private final double frameSize;
    private final DoubleProperty mouthOpen = new SimpleDoubleProperty(0.30);
    private final DoubleProperty saliva = new SimpleDoubleProperty(0.10);
    private final DoubleProperty tremor = new SimpleDoubleProperty(0.0);

    private Timeline mouthOpenTimeline;

    public DeviceFramePane(double frameSize) {
        this.frameSize = frameSize;

        setPrefSize(frameSize, frameSize);
        setMinSize(frameSize, frameSize);
        setMaxSize(frameSize, frameSize);

        realPhotoBackgroundView = new ImageView();
        realPhotoBackgroundView.setFitWidth(frameSize);
        realPhotoBackgroundView.setFitHeight(frameSize);
        realPhotoBackgroundView.setPreserveRatio(false);
        realPhotoBackgroundView.setOpacity(0.75);

        mouthBackgroundCanvas = new Canvas(frameSize, frameSize);
        gridFrameCanvas = new Canvas(frameSize, frameSize);
        blocksCanvas = new Canvas(frameSize, frameSize);

        playfieldStack = new StackPane(
                realPhotoBackgroundView,
                mouthBackgroundCanvas,
                gridFrameCanvas,
                blocksCanvas);
        playfieldStack.setPrefSize(frameSize, frameSize);
        playfieldStack.setMinSize(frameSize, frameSize);
        playfieldStack.setMaxSize(frameSize, frameSize);

        Rectangle clip = new Rectangle(frameSize, frameSize);
        playfieldStack.setClip(clip);

        getChildren().add(playfieldStack);

        mouthOpen.addListener((obs, oldValue, newValue) -> drawMouthBackground());

        loadRealPhotoBackground(DEFAULT_PHOTO_BACKGROUND);
        drawMouthBackground();
    }

    /**
     * Layer order (top -> bottom)
     * 1) blocksCanvas
     * 2) gridFrameCanvas
     * 3) mouthBackgroundCanvas
     * 4) realPhotoBackgroundView
     */
    public StackPane getPlayfieldStack() {
        return playfieldStack;
    }

    public Canvas getPlayfieldCanvas() {
        return blocksCanvas;
    }

    public Canvas getBlocksCanvas() {
        return blocksCanvas;
    }

    public Canvas getGridFrameCanvas() {
        return gridFrameCanvas;
    }

    public Canvas getMouthBackgroundCanvas() {
        return mouthBackgroundCanvas;
    }

    public void loadRealPhotoBackground(Path imagePath) {
        if (imagePath == null || !Files.exists(imagePath)) {
            realPhotoBackgroundView.setImage(null);
            return;
        }
        Image image = new Image(imagePath.toUri().toString());
        realPhotoBackgroundView.setImage(image);
    }

    /**
     * Main game-logic API for layered mouth effects.
     */
    public void updateMouthEffects(double mouthOpen, double saliva, double tremor) {
        this.mouthOpen.set(clamp01(mouthOpen));
        this.saliva.set(clamp01(saliva));
        this.tremor.set(clamp01(tremor));
        drawMouthBackground();
    }

    public double getMouthOpen() {
        return mouthOpen.get();
    }

    public double getSaliva() {
        return saliva.get();
    }

    public double getTremor() {
        return tremor.get();
    }

    /**
     * Optional helper for future state transitions.
     */
    public void animateMouthOpen(double targetMouthOpen, Duration duration) {
        if (mouthOpenTimeline != null) {
            mouthOpenTimeline.stop();
        }
        mouthOpenTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(mouthOpen, mouthOpen.get())),
                new KeyFrame(duration, new KeyValue(mouthOpen, clamp01(targetMouthOpen))));
        mouthOpenTimeline.play();
    }

    public void playMouthBreathLoop(double min, double max, Duration cycleDuration) {
        if (mouthOpenTimeline != null) {
            mouthOpenTimeline.stop();
        }
        mouthOpenTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(mouthOpen, clamp01(min))),
                new KeyFrame(cycleDuration.divide(2.0), new KeyValue(mouthOpen, clamp01(max))),
                new KeyFrame(cycleDuration, new KeyValue(mouthOpen, clamp01(min))));
        mouthOpenTimeline.setCycleCount(Animation.INDEFINITE);
        mouthOpenTimeline.play();
    }

    private void drawMouthBackground() {
        GraphicsContext gc = mouthBackgroundCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, frameSize, frameSize);

        double open = mouthOpen.get();
        double salivaAmount = saliva.get();
        double tremorAmount = tremor.get();

        // subtle horizontal tremor only, max ±2px
        double now = System.nanoTime() * 0.000000001;
        double tremorOffsetX = Math.sin(now * 15.0) * 2.0 * tremorAmount;

        // Abstract mouth-like background (dark wine -> deep red), tuned for readability.
        double stretch = 0.72 + (open * 0.22);
        double brightness = 0.20 + (open * 0.10);

        LinearGradient baseGradient = new LinearGradient(
                0,
                0.08,
                0,
                stretch,
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.rgb(30, 8, 16, 0.42 + brightness * 0.4)),
                new Stop(0.55, Color.rgb(70, 14, 30, 0.34 + brightness * 0.35)),
                new Stop(1.0, Color.rgb(110, 18, 28, 0.30 + brightness * 0.30)));

        gc.save();
        gc.translate(tremorOffsetX, 0);
        gc.setFill(baseGradient);
        gc.fillRect(-2, 0, frameSize + 4, frameSize);

        // Gentle center vignette to keep focus and avoid visual noise.
        LinearGradient centerDepth = new LinearGradient(
                0,
                0,
                1,
                0,
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.rgb(24, 6, 12, 0.12)),
                new Stop(0.5, Color.rgb(120, 24, 40, 0.04 + open * 0.06)),
                new Stop(1.0, Color.rgb(24, 6, 12, 0.12)));
        gc.setFill(centerDepth);
        gc.fillRect(-2, 0, frameSize + 4, frameSize);

        // Subtle gloss overlay controlled by saliva parameter.
        double glossOpacity = 0.03 + salivaAmount * 0.12;
        LinearGradient gloss = new LinearGradient(
                0,
                0,
                0,
                0.45,
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.rgb(255, 220, 220, glossOpacity)),
                new Stop(1.0, Color.rgb(255, 220, 220, 0.0)));
        gc.setFill(gloss);
        gc.fillRect(frameSize * 0.08, frameSize * 0.06, frameSize * 0.84, frameSize * 0.30);
        gc.restore();
    }

    private static double clamp01(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}
