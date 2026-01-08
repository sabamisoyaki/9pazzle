package tetris.view;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class GameView {

    private final BorderPane root;
    private final NextPane nextPane;
    private final DeviceFramePane playFieldPane;
    private final CharacterPane characterPane;
    private final HudPane hudPane;

    public GameView() {
        this.root = new BorderPane();
        double leftWidth = 1440;
        double leftHeight = 1080;

        double playFieldSize = 900; // Wireframe square playfield size.
        double nextHeight = 180; // Wireframe 7:1 vertical split (900:180).

        this.playFieldPane = new DeviceFramePane(playFieldSize);
        this.nextPane = new NextPane(playFieldSize, nextHeight);
        this.characterPane = new CharacterPane();
        this.hudPane = new HudPane();

        VBox leftColumn = new VBox(playFieldPane, nextPane);
        leftColumn.setAlignment(Pos.TOP_LEFT);
        leftColumn.setPrefSize(leftWidth, leftHeight);
        leftColumn.setMinSize(leftWidth, leftHeight);
        leftColumn.setMaxSize(leftWidth, leftHeight);

        VBox sideArea = new VBox(characterPane, hudPane);
        sideArea.setPrefSize(480, 1080);
        sideArea.setMinSize(480, 1080);
        sideArea.setMaxSize(480, 1080);

        root.setLeft(leftColumn);
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
