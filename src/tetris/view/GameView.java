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

        double rowHeight = Math.floor(gameAreaHeight * 0.85); // 85% height keeps vertical margins as specified.
        double nextWidth = Math.floor(rowHeight / 7);
        double rowWidth = rowHeight + nextWidth;

        this.playFieldPane = new DeviceFramePane(rowHeight);
        this.nextPane = new NextPane(nextWidth, rowHeight);
        this.characterPane = new CharacterPane();
        this.hudPane = new HudPane();

        HBox row = new HBox(playFieldPane, nextPane);
        row.setAlignment(Pos.TOP_LEFT);
        row.setPrefSize(rowWidth, rowHeight);

        StackPane.setAlignment(row, Pos.CENTER);
        gameArea.getChildren().add(row);

        VBox sideArea = new VBox(characterPane, hudPane);
        sideArea.setPrefSize(480, 1080);
        sideArea.setMinSize(480, 1080);
        sideArea.setMaxSize(480, 1080);

        StackPane leftSpacer = new StackPane();
        leftSpacer.setPrefSize(240, 1080);
        leftSpacer.setMinSize(240, 1080);
        leftSpacer.setMaxSize(240, 1080);

        root.setLeft(leftSpacer);
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
