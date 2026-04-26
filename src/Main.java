import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Main extends Application {

    private Game game;
    private GridPane boardGrid;

    @Override
    public void start(Stage stage) {
        game = new Game();

        BorderPane root = new BorderPane();

        boardGrid = new GridPane();
        boardGrid.setStyle("-fx-background-color: #2b2b2b; -fx-padding: 20px;");
        updateBoard();
        root.setCenter(boardGrid);

        VBox controlPanel = createControlPanel();
        root.setRight(controlPanel);

        Scene scene = new Scene(root, 1024, 768);
        stage.setTitle("CLUEDO - Watson Games Simulation");
        stage.setScene(scene);
        stage.show();
    }

    private void updateBoard() {
        boardGrid.getChildren().clear();

        int width = game.getWidth();
        int height = game.getHeight();
        Tile[][] tiles = game.getTiles();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Rectangle tileView = new Rectangle(25, 25);
                Tile currentTile = tiles[x][y];

                if (!currentTile.isEnterable) {
                    tileView.setFill(Color.DARKSLATEGRAY);
                } else if (currentTile.isOccupied) {
                    tileView.setFill(Color.CRIMSON);
                } else if (currentTile.doorTo != -1) {
                    tileView.setFill(Color.DARKORANGE);
                } else {
                    tileView.setFill(Color.LIGHTGOLDENRODYELLOW);
                }

                tileView.setStroke(Color.GRAY);
                tileView.setStrokeWidth(0.5);

                boardGrid.add(tileView, x, y);
            }
        }
    }

    private VBox createControlPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(30));
        panel.setStyle("-fx-background-color: #e0e0e0;");
        panel.setPrefWidth(250);

        Label statusLabel = new Label("Current Status: Game in progress");
        statusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Button rollBtn = new Button("Roll Dice & Move");
        Button suggestBtn = new Button("Make Suggestion");
        Button accuseBtn = new Button("Make Accusation");

        rollBtn.setMaxWidth(Double.MAX_VALUE);
        suggestBtn.setMaxWidth(Double.MAX_VALUE);
        accuseBtn.setMaxWidth(Double.MAX_VALUE);

        rollBtn.setOnAction(e -> {
            updateBoard();
        });

        suggestBtn.setOnAction(e -> {
        });

        accuseBtn.setOnAction(e -> {
        });

        panel.getChildren().addAll(statusLabel, rollBtn, suggestBtn, accuseBtn);
        return panel;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
