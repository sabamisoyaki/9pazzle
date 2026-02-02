package tetris.view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CharacterPane extends StackPane {

    private static final Path DEFAULT_CHARACTER_IMAGE = Paths.get("images", "character.png");

    private final ImageView characterView;

    public CharacterPane() {
        setPrefSize(480, 1080);
        setMinSize(480, 1080);
        setMaxSize(480, 1080);

        Rectangle placeholder = new Rectangle(460, 1040);
        placeholder.setFill(Color.web("#222"));
        placeholder.setStroke(Color.web("#555"));
        placeholder.setStrokeWidth(2);

        characterView = new ImageView();
        characterView.setFitWidth(460);
        characterView.setFitHeight(1040);
        characterView.setPreserveRatio(true);

        getChildren().addAll(placeholder, characterView);

        loadCharacterImage(DEFAULT_CHARACTER_IMAGE);
    }

    public ImageView getCharacterView() {
        return characterView;
    }

    public void loadCharacterImage(Path imagePath) {
        if (imagePath == null || !Files.exists(imagePath)) {
            characterView.setImage(null);
            return;
        }

        try {
            Image image = new Image(new FileInputStream(imagePath.toFile()));
            characterView.setImage(image);
        } catch (FileNotFoundException e) {
            characterView.setImage(null);
        }
    }
}
