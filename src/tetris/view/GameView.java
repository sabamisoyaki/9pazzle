package tetris.view;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class GameView {

    private static final Path DEFAULT_BACKGROUND_IMAGE = Paths.get("images", "base-layer-1920x1080.png");
    private final BorderPane root;
    private final NextPane nextPane;
    private final DeviceFramePane playFieldPane;
    private final CharacterPane characterPane;
    private final HudPane hudPane;

    public GameView() {
        this.root = new BorderPane();
        double leftWidth = 1440;
        double leftHeight = 1080;

        double playFieldSize = 840; // Wireframe square playfield size.
        double nextHeight = 168; // Wireframe 7:1 vertical split (840:168).

        this.playFieldPane = new DeviceFramePane(playFieldSize);
        this.nextPane = new NextPane(playFieldSize, nextHeight);
        this.characterPane = new CharacterPane();
        this.hudPane = new HudPane();

        VBox leftColumn = new VBox(playFieldPane, nextPane);
        leftColumn.setAlignment(Pos.TOP_LEFT);
        leftColumn.setPrefSize(leftWidth, leftHeight);
        leftColumn.setMinSize(leftWidth, leftHeight);
        leftColumn.setMaxSize(leftWidth, leftHeight);

        StackPane sideArea = new StackPane(characterPane, hudPane);
        sideArea.setPrefSize(480, 1080);
        sideArea.setMinSize(480, 1080);
        sideArea.setMaxSize(480, 1080);
        StackPane.setAlignment(hudPane, Pos.TOP_LEFT);

        root.setLeft(leftColumn);
        root.setRight(sideArea);
        loadBaseLayerImage(DEFAULT_BACKGROUND_IMAGE);
    }

    private void loadBaseLayerImage(Path imagePath) {
        if (imagePath == null || !Files.exists(imagePath)) {
            root.setStyle("-fx-background-color: black;");
            return;
        }
        Image image = new Image(imagePath.toUri().toString());
        BackgroundSize backgroundSize = new BackgroundSize(
                1920,
                1080,
                false,
                false,
                false,
                false);
        BackgroundImage backgroundImage = new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize);
        root.setBackground(new Background(backgroundImage));
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
