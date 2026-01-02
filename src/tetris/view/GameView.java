package tetris.view;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class GameView {

    private final BorderPane root;
    private final StackPane gameArea;
    private final NextPane nextPane;
    private final DeviceFramePane playFieldPane;
    private final CharacterPane characterPane;
    private final HudPane hudPane;

    public GameView() {
        this.root = new BorderPane();
        this.gameArea = new StackPane();
        double gameAreaWidth = 1200;
        double gameAreaHeight = 1080;

        gameArea.setPrefSize(gameAreaWidth, gameAreaHeight);
        gameArea.setMinSize(gameAreaWidth, gameAreaHeight);
        gameArea.setMaxSize(gameAreaWidth, gameAreaHeight);

        double rowHeight = Math.floor(gameAreaHeight * 0.85); // Keep 15% vertical margin for breathing room.
        double rowWidth = Math.floor(rowHeight * 8 / 7);
        rowWidth = Math.min(rowWidth, Math.floor(gameAreaWidth * 0.95)); // Cap width to preserve side padding.
        if (rowWidth < Math.floor(rowHeight * 8 / 7)) {
            rowHeight = Math.floor(rowWidth * 7 / 8);
        }

        double playFieldSize = rowHeight;
        double nextWidth = Math.floor(rowWidth / 8);

        this.playFieldPane = new DeviceFramePane(playFieldSize);
        this.nextPane = new NextPane(nextWidth, rowHeight);
        this.characterPane = new CharacterPane();
        this.hudPane = new HudPane();

        HBox row = new HBox(playFieldPane, nextPane);
        row.setAlignment(Pos.TOP_LEFT);
        row.setPrefSize(playFieldSize + nextWidth, rowHeight);

        StackPane.setAlignment(row, Pos.CENTER);
        gameArea.getChildren().add(row);

        VBox sideArea = new VBox(characterPane, hudPane);
        sideArea.setPrefSize(720, 1080);
        sideArea.setMinSize(720, 1080);
        sideArea.setMaxSize(720, 1080);

        root.setCenter(gameArea);
        root.setRight(sideArea);
        root.setStyle("-fx-background-color: black;");
    }

    public BorderPane getRoot() {
        return root;
    }

    public StackPane getGameArea() {
        return gameArea;
    }

    public NextPane getNextPane() {
        return nextPane;
    }

    public DeviceFramePane getPlayFieldPane() {
        return playFieldPane;
    }

    public CharacterPane getCharacterPane() {
        return characterPane;
    }

    public HudPane getHudPane() {
        return hudPane;
    }
}
