package tetris.view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CharacterPane extends StackPane {

    private static final Path DEFAULT_BACKGROUND_IMAGE = Paths.get("images", "character-bg.png");
    private static final Path DEFAULT_CHARACTER_IMAGE = Paths.get("images", "character.png");
    private static final Path ROTATE_CHARACTER_IMAGE_1 = Paths.get("images", "character-rotate-1.png");
    private static final Path ROTATE_CHARACTER_IMAGE_2 = Paths.get("images", "character-rotate-2.png");
    private static final Path ROTATE_CHARACTER_IMAGE_3 = Paths.get("images", "character-rotate-3.png");
    private static final double DEFAULT_CHARACTER_WIDTH = 460;
    private static final double DEFAULT_CHARACTER_HEIGHT = 1040;

    private final ImageView backgroundView;
    private final ImageView characterView;
    private final Rectangle placeholder;

    public CharacterPane() {
        setPrefSize(480, 1080);
        setMinSize(480, 1080);
        setMaxSize(480, 1080);

        backgroundView = new ImageView();
        backgroundView.setFitWidth(DEFAULT_CHARACTER_WIDTH);
        backgroundView.setFitHeight(DEFAULT_CHARACTER_HEIGHT);

        placeholder = new Rectangle(DEFAULT_CHARACTER_WIDTH, DEFAULT_CHARACTER_HEIGHT);
        placeholder.setFill(Color.web("#222"));
        placeholder.setStroke(Color.web("#555"));
        placeholder.setStrokeWidth(2);

        characterView = new ImageView();
        characterView.setFitWidth(DEFAULT_CHARACTER_WIDTH);
        characterView.setFitHeight(DEFAULT_CHARACTER_HEIGHT);
        characterView.setPreserveRatio(true);

        getChildren().addAll(backgroundView, placeholder, characterView);
        StackPane.setAlignment(characterView, Pos.CENTER_LEFT);

        loadBackgroundImage(DEFAULT_BACKGROUND_IMAGE);
        loadCharacterImage(DEFAULT_CHARACTER_IMAGE);
    }

    public ImageView getCharacterView() {
        return characterView;
    }

    public void setCharacterSize(double width, double height) {
        backgroundView.setFitWidth(width);
        backgroundView.setFitHeight(height);
        placeholder.setWidth(width);
        placeholder.setHeight(height);
        characterView.setFitWidth(width);
        characterView.setFitHeight(height);
    }

    public void updateCharacterForWorldRotateCount(int rotateCount) {
        Path imagePath = DEFAULT_CHARACTER_IMAGE;

        if (rotateCount >= 3) {
            imagePath = ROTATE_CHARACTER_IMAGE_3;
        } else if (rotateCount >= 2) {
            imagePath = ROTATE_CHARACTER_IMAGE_2;
        } else if (rotateCount >= 1) {
            imagePath = ROTATE_CHARACTER_IMAGE_1;
        }

        if (!Files.exists(imagePath)) {
            imagePath = DEFAULT_CHARACTER_IMAGE;
        }

        loadCharacterImage(imagePath);
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

    public void loadBackgroundImage(Path imagePath) {
        if (imagePath == null || !Files.exists(imagePath)) {
            backgroundView.setImage(null);
            return;
        }

        Image image = new Image(imagePath.toUri().toString());
        backgroundView.setImage(image);
    }
}
