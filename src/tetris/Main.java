package tetris;

import java.util.HashSet;
import java.util.Set;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tetris.controller.GameController;
import tetris.model.Board;
import tetris.view.Render;

public class Main extends Application {

    private Stage primaryStage;
    private static final int CELL_SIZE = 25;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("TETRIS");
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

        Scene scene = new Scene(root, 450, 600);

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

        GameController controller = new GameController();
        Render renderer = new Render(CELL_SIZE);

        Canvas gameCanvas = new Canvas(
                Board.COLS * CELL_SIZE,
                Board.ROWS * CELL_SIZE);
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();

        // UI（右側）
        Label scoreLabel = new Label("Score: 0");
        Label lineLabel = new Label("Lines: 0");
        Label nextLabel = new Label("Next:");

        scoreLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        lineLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        nextLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        Canvas nextCanvas = new Canvas(6 * CELL_SIZE, 6 * CELL_SIZE);
        GraphicsContext ngc = nextCanvas.getGraphicsContext2D();

        VBox uiBox = new VBox(16, scoreLabel, lineLabel, nextLabel, nextCanvas);
        uiBox.setPadding(new Insets(10));
        uiBox.setStyle("""
            -fx-background-color: #222;
            -fx-padding: 15;
            -fx-spacing: 10;

            -fx-font-family: 'Consolas', 'Meiryo', sans-serif;
            -fx-text-fill: white;
        """);
        uiBox.setMinWidth(140);

        HBox root = new HBox(gameCanvas, uiBox);
        root.setStyle("-fx-background-color: black;");
        Scene scene = new Scene(root);

        // キー入力管理
        Set<KeyCode> keys = new HashSet<>();
        scene.setOnKeyPressed(e -> keys.add(e.getCode()));
        scene.setOnKeyReleased(e -> keys.remove(e.getCode()));

        // 初回描画
        renderer.drawAll(gc, controller.getBoard(), controller.getCurrent(), controller.getGhost());
        renderer.drawNext(ngc, controller.getNext(), CELL_SIZE, CELL_SIZE);

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

                renderer.drawAll(gc,
                        controller.getBoard(),
                        controller.getCurrent(),
                        controller.getGhost());

                scoreLabel.setText("Score: " + controller.getScore());
                lineLabel.setText("Lines: " + controller.getLineCount());

                renderer.drawNext(ngc, controller.getNext(), CELL_SIZE, CELL_SIZE);
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

        Label retry = new Label("Press SPACE to Retry");
        retry.setStyle("-fx-font-size: 18px; -fx-text-fill: gray;");

        VBox root = new VBox(20, title, detail, retry);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(root, 450, 600);

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
