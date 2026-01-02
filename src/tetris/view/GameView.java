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
        double gameAreaWidth = 1440;
        double gameAreaHeight = 1080;

        gameArea.setPrefSize(gameAreaWidth, gameAreaHeight);
        gameArea.setMinSize(gameAreaWidth, gameAreaHeight);
        gameArea.setMaxSize(gameAreaWidth, gameAreaHeight);

        double playFieldSize = 900; // Wireframe square playfield size.
        double nextHeight = 180; // Wireframe 7:1 vertical split (900:180).

        this.playFieldPane = new DeviceFramePane(playFieldSize);
        this.nextPane = new NextPane(playFieldSize, nextHeight);
        this.characterPane = new CharacterPane();
        this.hudPane = new HudPane();

        VBox leftColumn = new VBox(playFieldPane, nextPane);
        leftColumn.setAlignment(Pos.TOP_CENTER);
        leftColumn.setPrefSize(playFieldSize, playFieldSize + nextHeight);

        StackPane.setAlignment(leftColumn, Pos.CENTER);
        gameArea.getChildren().add(leftColumn);

        VBox sideArea = new VBox(characterPane, hudPane);
        sideArea.setPrefSize(480, 1080);
        sideArea.setMinSize(480, 1080);
        sideArea.setMaxSize(480, 1080);

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
