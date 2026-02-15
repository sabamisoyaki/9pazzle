package tetris.view;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class EndCreditPane {

    private static final double WIDTH = 1920;
    private static final double HEIGHT = 1080;

    private final StackPane root;
    private final VBox creditsBox;

    public EndCreditPane(String creditJson, Path backgroundImagePath) {
        root = new StackPane();
        root.setPrefSize(WIDTH, HEIGHT);
        loadBackgroundImage(backgroundImagePath);

        creditsBox = new VBox(18);
        creditsBox.setAlignment(Pos.TOP_CENTER);

        List<String> lines = parseCreditLines(creditJson);
        for (String line : lines) {
            Label label = new Label(line);
            label.setStyle("-fx-font-size: 34px; -fx-text-fill: white;");
            if (line.startsWith("# ")) {
                label.setText(line.substring(2));
                label.setStyle("-fx-font-size: 42px; -fx-text-fill: #f3d673;");
            }
            creditsBox.getChildren().add(label);
        }

        Pane viewport = new Pane(creditsBox);
        viewport.setPrefSize(WIDTH, HEIGHT);

        Rectangle clip = new Rectangle(WIDTH, HEIGHT);
        viewport.setClip(clip);

        root.getChildren().add(viewport);
    }

    public StackPane getRoot() {
        return root;
    }

    public Timeline buildScrollAnimation() {
        double startY = HEIGHT;
        double endY = -(creditsBox.prefHeight(-1) + 250);
        double totalDistance = startY - endY;
        double speedPixelPerSecond = 85;
        Duration duration = Duration.seconds(totalDistance / speedPixelPerSecond);

        creditsBox.setTranslateY(startY);
        return new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(creditsBox.translateYProperty(), startY, Interpolator.LINEAR)),
                new KeyFrame(duration,
                        new KeyValue(creditsBox.translateYProperty(), endY, Interpolator.LINEAR)));
    }

    private void loadBackgroundImage(Path imagePath) {
        if (imagePath == null || !Files.exists(imagePath)) {
            root.setStyle("-fx-background-color: black;");
            return;
        }

        Image image = new Image(imagePath.toUri().toString());
        BackgroundSize size = new BackgroundSize(
                WIDTH,
                HEIGHT,
                false,
                false,
                false,
                false);
        BackgroundImage backgroundImage = new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                size);
        root.setBackground(new Background(backgroundImage));
    }

    private List<String> parseCreditLines(String json) {
        List<String> lines = new ArrayList<>();

        if (json == null || json.isBlank()) {
            lines.add("# END CREDITS");
            lines.add("No credit data.");
            return lines;
        }

        Matcher titleMatcher = Pattern.compile("\"title\"\\s*:\\s*\"(.*?)\"").matcher(json);
        if (titleMatcher.find()) {
            lines.add("# " + unescapeJson(titleMatcher.group(1)));
            lines.add("");
        }

        Matcher sectionMatcher = Pattern.compile("\\{\\s*\"heading\"\\s*:\\s*\"(.*?)\"\\s*,\\s*\"lines\"\\s*:\\s*\\[(.*?)\\]\\s*\\}",
                Pattern.DOTALL).matcher(json);

        while (sectionMatcher.find()) {
            lines.add("# " + unescapeJson(sectionMatcher.group(1)));
            Matcher lineMatcher = Pattern.compile("\"(.*?)\"").matcher(sectionMatcher.group(2));
            while (lineMatcher.find()) {
                lines.add(unescapeJson(lineMatcher.group(1)));
            }
            lines.add("");
        }

        if (lines.isEmpty()) {
            lines.add("# END CREDITS");
            for (String raw : json.split("\\r?\\n")) {
                if (!raw.isBlank()) {
                    lines.add(raw.trim());
                }
            }
        }

        return lines;
    }

    private String unescapeJson(String text) {
        return text
                .replace("\\\\n", "\n")
                .replace("\\\\\"", "\"")
                .replace("\\\\\\\\", "\\");
    }
}
