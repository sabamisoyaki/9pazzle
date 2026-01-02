package tetris.view;

import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CharacterPane extends StackPane {

    private final ImageView characterView;

    public CharacterPane() {
        setPrefSize(300, 900);
        setMinSize(300, 900);
        setMaxSize(300, 900);
        setTranslateX(-20);

        Rectangle placeholder = new Rectangle(240, 840);
        placeholder.setFill(Color.web("#222"));
        placeholder.setStroke(Color.web("#555"));
        placeholder.setStrokeWidth(2);

        characterView = new ImageView();
        characterView.setFitWidth(240);
        characterView.setFitHeight(840);
        characterView.setPreserveRatio(true);

        getChildren().addAll(placeholder, characterView);
    }

    public ImageView getCharacterView() {
        return characterView;
    }
}
