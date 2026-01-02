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
        setPrefSize(720, 280);
        setMinSize(720, 280);
        setMaxSize(720, 280);
        setAlignment(Pos.TOP_LEFT);
        setSpacing(20);
        setPadding(new Insets(20, 30, 20, 30));
        setStyle("-fx-background-color: #181818;");

        scoreLabel = new Label("Score: 0");
        linesLabel = new Label("Lines: 0");
        dialogueLabel = new Label();

        scoreLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: white;");
        linesLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: white;");
        dialogueLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #f0f0f0;");
        dialogueLabel.setWrapText(true);
        dialogueLabel.setMaxWidth(660);

        getChildren().addAll(scoreLabel, linesLabel, dialogueLabel);
    }

    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void updateLines(int lines) {
        linesLabel.setText("Lines: " + lines);
    }

    public void updateDialogue(int score, int lines) {
        String text = String.format(
                "現在の点数は %d 点。まだ足りないわね。\n"
                        + "累計ラインは %d。次はどうする？\n"
                        + "次のミノはこれよ。下にも表示してるけど。\n"
                        + "焦らず積んでいきましょ。",
                score,
                lines);
        dialogueLabel.setText(text);
    }
}
