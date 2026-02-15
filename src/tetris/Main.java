package tetris;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import tetris.controller.GameController;
import tetris.model.Board;
import tetris.view.EndCreditPane;
import tetris.view.GameView;
import tetris.view.HudPane;
import tetris.view.NextPane;
import tetris.view.Render;

public class Main extends Application {

    private Stage primaryStage;
    private static final int WINDOW_WIDTH = 1920;
    private static final int WINDOW_HEIGHT = 1080;
    private static final Path BGM_PATH = Path.of("audio", "bgm.mp3");
    private MediaPlayer bgmPlayer;

    private static final Path START_BACKGROUND_IMAGE = Paths.get("images", "start-bg.png");
    private static final Path GAME_OVER_BACKGROUND_IMAGE = Paths.get("images", "game-over-bg.png");
    private static final Path END_CREDIT_BACKGROUND_IMAGE = Paths.get("images", "end-credit-bg.png");

    private static final String DEFAULT_END_CREDIT_JSON = """
            {
              "title": "THANK YOU FOR PLAYING",
              "sections": [
                {"heading": "Development", "lines": ["Game Design", "Programming", "Balancing"]},
                {"heading": "Art", "lines": ["UI Layout", "Character Illustration"]},
                {"heading": "Special Thanks", "lines": ["All Players", "Open Source Community"]}
              ]
            }
            """;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("TETRIS");
        stage.setResizable(false);
        initBgmPlayer();
        showStartScene();
        stage.show();
    }

    private void initBgmPlayer() {
        if (!Files.exists(BGM_PATH)) {
            System.out.println("[BGM] Not found: " + BGM_PATH.toAbsolutePath());
            return;
        }

        try {
            Media media = new Media(BGM_PATH.toUri().toString());
            bgmPlayer = new MediaPlayer(media);
            bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            bgmPlayer.setVolume(0.35);
        } catch (Exception e) {
            System.out.println("[BGM] Failed to load: " + e.getMessage());
            bgmPlayer = null;
        }
    }

    private void playBgm() {
        if (bgmPlayer == null) {
            return;
        }
        if (bgmPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            bgmPlayer.play();
        }
    }

    private void stopBgm() {
        if (bgmPlayer == null) {
            return;
        }
        bgmPlayer.stop();
        bgmPlayer.seek(Duration.ZERO);
    }

    // =====================================================
    //  スタート画面
    // =====================================================
    private Scene makeStartScene() {

        Label title = new Label("TETRIS");
        title.setStyle("-fx-font-size: 48px; -fx-text-fill: white;");

        Label sub = new Label("Press SPACE to Start");
        sub.setStyle("-fx-font-size: 22px; -fx-text-fill: gray;");

        VBox root = new VBox(20, title, sub);
        root.setAlignment(Pos.CENTER);
        applyBackgroundImage(root, START_BACKGROUND_IMAGE);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                showGameScene();
            }
        });

        return scene;
    }

    private void showStartScene() {
        stopBgm();
        primaryStage.setScene(makeStartScene());
    }

    // =====================================================
    //  ゲーム画面
    // =====================================================
    private Scene makeGameScene() {

        GameView view = new GameView();
        GameController controller = new GameController();
        int cellSize = Math.min(
                (int) (view.getPlayFieldPane().getPlayfieldCanvas().getWidth() / Board.COLS),
                (int) (view.getPlayFieldPane().getPlayfieldCanvas().getHeight() / Board.ROWS));
        Render renderer = new Render(cellSize);
        NextPane nextPane = view.getNextPane();
        HudPane hudPane = view.getHudPane();

        Scene scene = new Scene(view.getRoot(), WINDOW_WIDTH, WINDOW_HEIGHT);

        // キー入力管理
        Set<KeyCode> keys = new HashSet<>();
        scene.setOnKeyPressed(e -> {
            keys.add(e.getCode());
        });
        scene.setOnKeyReleased(e -> keys.remove(e.getCode()));

        // 初回描画
        renderer.drawAll(
                view.getPlayFieldPane().getPlayfieldCanvas().getGraphicsContext2D(),
                controller.getBoard(),
                controller.getCurrent(),
                controller.getGhost());
        renderer.drawNext(
                nextPane.getNextCanvas().getGraphicsContext2D(),
                controller.getNext(),
                0,
                0);

        AnimationTimer timer = new AnimationTimer() {

            private long lastFall = 0;
            private final long FALL_SPEED = 300_000_000L;
            private int lastWorldRotateStep = -1;

            @Override
            public void handle(long now) {

                // ====== TRUE GAME OVER ======
                if (controller.isTrueGameOver()) {
                    stop();
                    int finalScore = controller.getScore();
                    int finalLines = controller.getLineCount();
                    showEndCreditScene(DEFAULT_END_CREDIT_JSON, () -> showGameOverScene(finalScore, finalLines));
                    return;
                }

                controller.updateInput(keys, now);

                if (now - lastFall > FALL_SPEED) {
                    controller.softDrop();
                    lastFall = now;
                }

                renderer.drawAll(
                        view.getPlayFieldPane().getPlayfieldCanvas().getGraphicsContext2D(),
                        controller.getBoard(),
                        controller.getCurrent(),
                        controller.getGhost());

                int score = controller.getScore();
                int lines = controller.getLineCount();
                hudPane.updateScore(score);
                hudPane.updateLines(lines);

                renderer.drawNext(
                        nextPane.getNextCanvas().getGraphicsContext2D(),
                        controller.getNext(),
                        0,
                        0);

                hudPane.updateDialogue(score, lines);

                int worldRotateStep = controller.getWorldRotateStep();
                if (worldRotateStep != lastWorldRotateStep) {
                    view.getCharacterPane().updateCharacterForWorldRotateStep(worldRotateStep);
                    lastWorldRotateStep = worldRotateStep;
                }
            }
        };

        timer.start();
        return scene;
    }

    private void showGameScene() {
        playBgm();
        primaryStage.setScene(makeGameScene());
    }

    // =====================================================
    //  ゲームオーバー画面
    // =====================================================
    private Scene makeGameOverScene(int score, int lines) {

        Label title = new Label("GAME OVER");
        title.setStyle("-fx-font-size: 48px; -fx-text-fill: red;");

        Label detail = new Label("Score: " + score + "\nLines: " + lines);
        detail.setStyle("-fx-font-size: 24px; -fx-text-fill: white;");

        Label retry = new Label("Press SPACE to Retry");
        retry.setStyle("-fx-font-size: 18px; -fx-text-fill: gray;");

        VBox root = new VBox(20, title, detail, retry);
        root.setAlignment(Pos.CENTER);
        applyBackgroundImage(root, GAME_OVER_BACKGROUND_IMAGE);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                showGameScene();
            }
        });

        return scene;
    }

    private void showGameOverScene(int score, int lines) {
        stopBgm();
        primaryStage.setScene(makeGameOverScene(score, lines));
    }


    // =====================================================
    //  エンドクレジット画面
    // =====================================================
    private Scene makeEndCreditScene(String creditJson, Runnable onComplete) {

        EndCreditPane creditPane = new EndCreditPane(creditJson, END_CREDIT_BACKGROUND_IMAGE);
        Scene scene = new Scene(creditPane.getRoot(), WINDOW_WIDTH, WINDOW_HEIGHT);

        Timeline timeline = creditPane.buildScrollAnimation();
        Runnable completeAction = onComplete != null ? onComplete : this::showStartScene;

        timeline.setOnFinished(e -> completeAction.run());
        timeline.play();

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE || e.getCode() == KeyCode.SPACE) {
                timeline.stop();
                completeAction.run();
            }
        });

        return scene;
    }

    private void showEndCreditScene(String creditJson, Runnable onComplete) {
        primaryStage.setScene(makeEndCreditScene(creditJson, onComplete));
    }


    private void applyBackgroundImage(VBox target, Path imagePath) {
        if (imagePath == null || !Files.exists(imagePath)) {
            target.setStyle("-fx-background-color: black;");
            return;
        }

        Image image = new Image(imagePath.toUri().toString());
        BackgroundSize size = new BackgroundSize(
                WINDOW_WIDTH,
                WINDOW_HEIGHT,
                false,
                false,
                false,
                false);
        BackgroundImage backgroundImage = new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                size);
        target.setBackground(new Background(backgroundImage));
    }

    public static void main(String[] args) {
        launch();
    }
}
