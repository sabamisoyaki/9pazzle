package tetris.view;

import javafx.scene.layout.BorderPane;

public class GameView {

    private final BorderPane root;
    private final NextPane nextPane;
    private final WorldView worldView;
    private final HudPane hudPane;

    public GameView() {
        this.root = new BorderPane();
        this.nextPane = new NextPane();
        this.worldView = new WorldView();
        this.hudPane = new HudPane();

        root.setLeft(nextPane);
        root.setCenter(worldView.getRoot());
        root.setRight(hudPane);
        root.setStyle("-fx-background-color: black;");
    }

    public BorderPane getRoot() {
        return root;
    }

    public NextPane getNextPane() {
        return nextPane;
    }

    public WorldView getWorldView() {
        return worldView;
    }

    public HudPane getHudPane() {
        return hudPane;
    }
}
