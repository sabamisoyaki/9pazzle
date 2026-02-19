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
import javafx.scene.paint.RadialGradient;
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
    private final DoubleProperty mouthOpen = new SimpleDoubleProperty(0.35);
    private final DoubleProperty saliva = new SimpleDoubleProperty(0.15);
    private final DoubleProperty tremor = new SimpleDoubleProperty(0.0);

    private final Timeline mouthRedrawLoop;
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
        saliva.addListener((obs, oldValue, newValue) -> drawMouthBackground());
        tremor.addListener((obs, oldValue, newValue) -> drawMouthBackground());

        loadRealPhotoBackground(DEFAULT_PHOTO_BACKGROUND);
        drawMouthBackground();

        mouthRedrawLoop = new Timeline(new KeyFrame(Duration.millis(33), e -> drawMouthBackground()));
        mouthRedrawLoop.setCycleCount(Animation.INDEFINITE);
        mouthRedrawLoop.play();
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

    public double getMouthOpen() {
        return mouthOpen.get();
    }

    public void setMouthOpen(double value) {
        mouthOpen.set(clamp01(value));
    }

    public double getSaliva() {
        return saliva.get();
    }

    public void setSaliva(double value) {
        saliva.set(clamp01(value));
    }

    public double getTremor() {
        return tremor.get();
    }

    public void setTremor(double value) {
        tremor.set(clamp01(value));
    }

    /**
     * Example API for game-logic-side control.
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

        double now = System.nanoTime() * 0.000000001;
        double tremorOffsetX = Math.sin(now * 19.0) * 8.0 * tremorAmount;
        double tremorOffsetY = Math.cos(now * 16.5) * 6.0 * tremorAmount;

        LinearGradient baseTint = new LinearGradient(
                0,
                0,
                0,
                1,
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.rgb(76, 18, 20, 0.45)),
                new Stop(0.4, Color.rgb(120, 30, 34, 0.30 + open * 0.15)),
                new Stop(1.0, Color.rgb(32, 8, 12, 0.55)));
        gc.setFill(baseTint);
        gc.fillRect(0, 0, frameSize, frameSize);

        double cavityHeight = frameSize * (0.24 + 0.42 * open);
        double cavityY = frameSize * 0.5 - cavityHeight * 0.5 + tremorOffsetY;

        RadialGradient innerMouth = new RadialGradient(
                0,
                0,
                frameSize * 0.5 + tremorOffsetX,
                frameSize * 0.5 + tremorOffsetY,
                frameSize * 0.46,
                false,
                CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.rgb(24, 6, 9, 0.88)),
                new Stop(0.65, Color.rgb(94, 22, 26, 0.55)),
                new Stop(1.0, Color.rgb(160, 58, 68, 0.25)));
        gc.setFill(innerMouth);
        gc.fillOval(frameSize * 0.12 + tremorOffsetX, cavityY, frameSize * 0.76, cavityHeight);

        gc.setStroke(Color.rgb(245, 220, 220, 0.18 + 0.22 * salivaAmount));
        gc.setLineWidth(frameSize * 0.008);
        gc.strokeArc(
                frameSize * 0.18 + tremorOffsetX,
                frameSize * (0.22 + 0.06 * (1.0 - open)) + tremorOffsetY,
                frameSize * 0.64,
                frameSize * 0.18,
                180,
                180,
                javafx.scene.shape.ArcType.OPEN);

        gc.setStroke(Color.rgb(220, 240, 255, 0.08 + 0.18 * salivaAmount));
        gc.setLineWidth(frameSize * 0.004);
        double salivaPhase = Math.sin(now * 2.7) * 0.5 + 0.5;
        gc.strokeLine(
                frameSize * 0.28 + tremorOffsetX,
                frameSize * (0.34 + salivaPhase * 0.03),
                frameSize * 0.72 + tremorOffsetX,
                frameSize * (0.36 + salivaPhase * 0.02));
    }

    private static double clamp01(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}
