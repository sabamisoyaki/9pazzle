package tetris.view;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class GameView {

    private final BorderPane root;
    private final NextPane nextPane;
    private final DeviceFramePane playFieldPane;
    private final CharacterPane characterPane;
    private final HudPane hudPane;

    public GameView() {
        this.root = new BorderPane();
        this.nextPane = new NextPane();
        this.playFieldPane = new DeviceFramePane();
        this.characterPane = new CharacterPane();
        this.hudPane = new HudPane();

        HBox gameArea = new HBox(playFieldPane, nextPane);
        gameArea.setAlignment(Pos.CENTER_LEFT);
        gameArea.setPrefSize(1200, 900);

        StackPane gameAreaContainer = new StackPane(gameArea);
        gameAreaContainer.setPrefSize(1200, 1080);
        gameAreaContainer.setMinSize(1200, 1080);
        gameAreaContainer.setMaxSize(1200, 1080);
        gameAreaContainer.setAlignment(Pos.CENTER);

        VBox sideArea = new VBox(characterPane, hudPane);
        sideArea.setPrefSize(720, 1080);
        sideArea.setMinSize(720, 1080);
        sideArea.setMaxSize(720, 1080);

        root.setCenter(gameAreaContainer);
        root.setRight(sideArea);
        root.setStyle("-fx-background-color: black;");
    }

    public BorderPane getRoot() {
        return root;
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
