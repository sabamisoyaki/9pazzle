package tetris;

import java.util.HashSet;
import java.util.Set;

import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import tetris.controller.GameController;
import tetris.model.Board;
import tetris.view.GameView;
import tetris.view.HudPane;
import tetris.view.NextPane;
import tetris.view.Render;

public class Main extends Application {

    private Stage primaryStage;
    private static final int WINDOW_WIDTH = 1920;
    private static final int WINDOW_HEIGHT = 1080;

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
        root.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                showGameScene();
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
            KeyCode code = e.getCode();
            if (!keys.contains(code)) {
                if (code == KeyCode.Z) {
                    playRotateAnimation(view.getRoot(), false);
                } else if (code == KeyCode.X || code == KeyCode.UP) {
                    playRotateAnimation(view.getRoot(), true);
                }
            }
            keys.add(code);
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
                nextPane.getNextCellSize(),
                nextPane.getNextCellSize());

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
                        nextPane.getNextCellSize(),
                        nextPane.getNextCellSize());

                hudPane.updateDialogue(score, lines);
            }
        };

        timer.start();
        return scene;
    }

    private void playRotateAnimation(BorderPane root, boolean clockwise) {
        double angle = clockwise ? 6.0 : -6.0;
        root.setRotate(0);
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(root.rotateProperty(), 0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(250),
                        new KeyValue(root.rotateProperty(), angle, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(500),
                        new KeyValue(root.rotateProperty(), 0, Interpolator.EASE_BOTH))
        );
        timeline.play();
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

        Label retry = new Label("Press SPACE to Retry");
        retry.setStyle("-fx-font-size: 18px; -fx-text-fill: gray;");

        VBox root = new VBox(20, title, detail, retry);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                showGameScene();
            }
        });

        return scene;
    }

    private void showGameOverScene(int score, int lines) {
        primaryStage.setScene(makeGameOverScene(score, lines));
    }

    public static void main(String[] args) {
        launch();
    }
}
