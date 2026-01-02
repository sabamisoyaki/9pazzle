package tetris.view;

import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CharacterPane extends StackPane {

    private final ImageView characterView;

    public CharacterPane() {
        setPrefSize(480, 800);
        setMinSize(480, 800);
        setMaxSize(480, 800);

        Rectangle placeholder = new Rectangle(420, 760);
        placeholder.setFill(Color.web("#222"));
        placeholder.setStroke(Color.web("#555"));
        placeholder.setStrokeWidth(2);

        characterView = new ImageView();
        characterView.setFitWidth(420);
        characterView.setFitHeight(760);
        characterView.setPreserveRatio(true);

        getChildren().addAll(placeholder, characterView);
    }

    public ImageView getCharacterView() {
        return characterView;
    }
}
