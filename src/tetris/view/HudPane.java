package tetris.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class HudPane extends VBox {

    private final Label scoreLabel;
    private final Label linesLabel;
    private final Label dialogueLabel;

    public HudPane() {
        setPrefSize(480, 1080);
        setMinSize(480, 1080);
        setMaxSize(480, 1080);
        setAlignment(Pos.TOP_LEFT);
        setSpacing(20);
        setPadding(new Insets(40, 30, 30, 30));
        setStyle("-fx-background-color: #181818;");

        scoreLabel = new Label("Score: 0");
        linesLabel = new Label("Lines: 0");
        dialogueLabel = new Label();

        scoreLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: white;");
        linesLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: white;");
        dialogueLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #f0f0f0;");
        dialogueLabel.setWrapText(true);
        dialogueLabel.setMaxWidth(420);

        getChildren().addAll(scoreLabel, linesLabel, dialogueLabel);
    }

    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void updateLines(int lines) {
        linesLabel.setText("Lines: " + lines);
    }

    public void updateDialogue(int score, int rotateRemain, int remainScore) {
        String text = String.format(
                "現在の点数は %d 点。まだ足りないわね。\n"
                        + "あと %d ライン消すと、ぐるっと回るわ。\n"
                        + "次のミノはこれよ。下にも表示してるけど。\n"
                        + "あと %d 点で脱ぐわ。頑張って。",
                score,
                rotateRemain,
                remainScore);
        dialogueLabel.setText(text);
    }
}
