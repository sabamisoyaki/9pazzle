package tetris.view;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class WorldView {

    private final StackPane root;
    private final Group worldFrame;
    private final DeviceFramePane deviceFramePane;
    private final CharacterPane characterPane;

    public WorldView() {
        this.root = new StackPane();
        this.worldFrame = new Group();
        this.deviceFramePane = new DeviceFramePane();
        this.characterPane = new CharacterPane();

        HBox row = new HBox(deviceFramePane, characterPane);
        row.setPrefSize(1200, 900);
        row.setAlignment(Pos.CENTER_LEFT);

        worldFrame.getChildren().add(row);

        root.getChildren().add(worldFrame);
        root.setPrefSize(1200, 1080);
        root.setMinSize(1200, 1080);
        root.setMaxSize(1200, 1080);
        root.setAlignment(Pos.CENTER);
    }

    public StackPane getRoot() {
        return root;
    }

    public Group getWorldFrame() {
        return worldFrame;
    }

    public DeviceFramePane getDeviceFramePane() {
        return deviceFramePane;
    }

    public CharacterPane getCharacterPane() {
        return characterPane;
    }
}
