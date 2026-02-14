package tetris;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

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
import javafx.stage.Stage;
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
        showStartScene();
        stage.show();
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
            if (e.getCode() == KeyCode.C) {
                showEndCreditScene(DEFAULT_END_CREDIT_JSON);
            }
        });

        return scene;
    }

    private void showStartScene() {
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
            @Override
            public void handle(long now) {

                // ====== TRUE GAME OVER ======
                if (controller.isTrueGameOver()) {
                    stop();
                    showGameOverScene(controller.getScore(), controller.getLineCount());
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
            }
        };

        timer.start();
        return scene;
    }

    private void showGameScene() {
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

        Label retry = new Label("Press SPACE to Retry\nPress C to View End Credits");
        retry.setStyle("-fx-font-size: 18px; -fx-text-fill: gray;");

        VBox root = new VBox(20, title, detail, retry);
        root.setAlignment(Pos.CENTER);
        applyBackgroundImage(root, GAME_OVER_BACKGROUND_IMAGE);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                showGameScene();
            }
            if (e.getCode() == KeyCode.C) {
                showEndCreditScene(DEFAULT_END_CREDIT_JSON);
            }
        });

        return scene;
    }

    private void showGameOverScene(int score, int lines) {
        primaryStage.setScene(makeGameOverScene(score, lines));
    }


    // =====================================================
    //  エンドクレジット画面
    // =====================================================
    private Scene makeEndCreditScene(String creditJson) {

        EndCreditPane creditPane = new EndCreditPane(creditJson, END_CREDIT_BACKGROUND_IMAGE);
        Scene scene = new Scene(creditPane.getRoot(), WINDOW_WIDTH, WINDOW_HEIGHT);

        Timeline timeline = creditPane.buildScrollAnimation();
        timeline.setOnFinished(e -> showStartScene());
        timeline.play();

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                timeline.stop();
                showStartScene();
            }
            if (e.getCode() == KeyCode.SPACE) {
                timeline.stop();
                showGameScene();
            }
        });

        return scene;
    }

    private void showEndCreditScene(String creditJson) {
        primaryStage.setScene(makeEndCreditScene(creditJson));
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
