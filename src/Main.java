import javafx.animation.PauseTransition;
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
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.control.Spinner;
import javafx.util.Duration;
import javafx.scene.control.ComboBox;


public class Main extends Application {

    private Game game;
    private TextArea log;
    private GridPane boardGrid;

    //Declare private in the class and connect and use it!
    private Button rollBtn;
    private Button suggestBtn;
    private Button accuseBtn;
    private Button statusBtn;
    private Button notesBtn;



    // Player character colors for better readability
    private Color[] playerColors = {
            Color.RED,       // Player 0
            Color.BLUE,      // Player 1
            Color.GREEN,     // Player 2
            Color.YELLOW,    // Player 3
            Color.PURPLE,    // Player 4
            Color.ORANGE     // Player 5
    };



    @Override
    public void start(Stage stage) {
        stage.setTitle("CLUEDO - Watson Games Simulation");
        stage.setScene(createStartScreen(stage));
        stage.show();
    }

    // Start screen
    public Scene createStartScreen(Stage stage) {
        VBox root = new VBox(30);
        root.setPadding(new Insets(80));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #f5f5f5;");

        Label title = new Label("CLUEDO");
        title.setStyle("-fx-font-size: 48px; -fx-font-weight: bold;");

        Button startBtn = new Button("Start Game");
        Button settingsBtn = new Button("Settings");
        Button exitBtn = new Button("Exit");

        startBtn.setPrefWidth(200);
        settingsBtn.setPrefWidth(200);
        exitBtn.setPrefWidth(200);


        startBtn.setOnAction(e -> stage.setScene(initialGameSettingScreen(stage)));

        settingsBtn.setOnAction(e -> showSettingsPopup());
        exitBtn.setOnAction(e -> stage.close());

        root.getChildren().addAll(title, startBtn, settingsBtn, exitBtn);
        return new Scene(root, 800, 600);
    }
    public Scene initialGameSettingScreen(Stage stage) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #fafafa;");

        Label title = new Label("Game Setup");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        // Player Count
        Label playerLabel = new Label("Number of Players:");
        Spinner<Integer> playerCountSpinner = new Spinner<>(2,6,4);
        playerCountSpinner.setPrefWidth(150);

        Button startBtn = new Button("Start Game");
        startBtn.setPrefWidth(200);

        startBtn.setOnAction(e -> {
            stage.setScene(createMainGameScreen(stage, playerCountSpinner.getValue()));
        });

        root.getChildren().addAll(
                title,
                playerLabel,
                playerCountSpinner,
                startBtn
        );

        return new Scene(root, 600, 500);
    }



    // Main game screen
    public Scene createMainGameScreen(Stage stage, int playerCount) {
        game = new Game(playerCount);

        BorderPane root = new BorderPane();

        // Board
        boardGrid = new GridPane();
        boardGrid.setStyle("-fx-background-color: #2b2b2b; -fx-padding: 20px;");
        updateBoard();
        root.setCenter(boardGrid);

        // Right Panel
        VBox controlPanel = createControlPanel();
        root.setRight(controlPanel);

        // Bottom Log
        log = new TextArea();
        log.setEditable(false);
        log.setPrefHeight(120);
        log.setText("Game Started...\n");
        root.setBottom(log);

        updateButtonState();
        if (game.brains[game.getTurn()] != null) {
            runAITurn();
        }
        return new Scene(root, 1024, 768);
    }

    // AI logic for AI turn
    private void runAITurn() {
        int current = game.getTurn();

        if (game.brains[current] == null) {
            return;
        }

        String aiName = game.getDisplayName(current);


        log.appendText(aiName + " is thinking...\n");

        int delay = 2000 + new java.util.Random().nextInt(2000);

        PauseTransition pause = new PauseTransition(Duration.millis(delay));
        pause.setOnFinished(e -> {

            if (game.getTurn() != current) {
                return;
            }

            int roll = game.rollAndMove();
            log.appendText(aiName + " rolled " + roll + "\n");
            updateBoard();

            if (game.getPlayers()[current].inRoom != -1 &&
                    game.brains[current].makeSuggestion(game.getPlayers(), game.getTiles(), current)) {

                String suggestion = game.doSuggestion();
                log.appendText(aiName + ": " + suggestion + "\n");
            }

            if (game.brains[current].makeAccusation(game.getPlayers(), game.getTiles(), current)) {
                String accusation = game.doAccusation();
                log.appendText(aiName + ": " + accusation + "\n");

                if (accusation.contains("WRONG!")) {
                    log.appendText(aiName + " has been eliminated.\n");
                }
                if (accusation.contains("CORRECT!")) {
                    Stage stage = (Stage) boardGrid.getScene().getWindow();
                    stage.setScene(createGameOverScreen(stage, aiName));
                    return;
                }
                updateBoard();
            }

            updateButtonState();


            if (game.brains[game.getTurn()] != null) {
                runAITurn();
            }
        });

        pause.play();
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
                    tileView.setFill(Color.LIGHTGRAY);
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
        Player[] players = game.getPlayers();
        for (int i = 0; i < players.length; i++) {
            Player p = players[i];

            Rectangle piece = new Rectangle(24, 24);
            piece.setFill(playerColors[i]);
            piece.setStroke(Color.BLACK);
            piece.setStrokeWidth(1);

            boardGrid.add(piece, p.posX, p.posY);
        }
    }

    // control panel for main game screen
    private VBox createControlPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(30));
        panel.setStyle("-fx-background-color: #e0e0e0;");
        panel.setPrefWidth(250);

        Label statusLabel = new Label("Current Status: Game in progress");
        statusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        //Declare private in the class and connect and use it!
        rollBtn = new Button("Roll Dice & Move");
        suggestBtn = new Button("Make Suggestion");
        accuseBtn = new Button("Make Accusation");
        statusBtn = new Button("My status");
        notesBtn = new Button("My Notes");





        rollBtn.setMaxWidth(Double.MAX_VALUE);
        suggestBtn.setMaxWidth(Double.MAX_VALUE);
        accuseBtn.setMaxWidth(Double.MAX_VALUE);
        statusBtn.setMaxWidth(Double.MAX_VALUE);
        notesBtn.setMaxWidth(Double.MAX_VALUE);



        rollBtn.setOnAction(e -> {
            int currentPlayer = game.getTurn();
            String name = game.getDisplayName(currentPlayer);
            int roll = game.rollAndMove();
            log.appendText(name + " rolled " + roll + "\n");
            updateBoard();
            updateButtonState();
            //call AI turn logic if next turn is AI
            if (game.brains[game.getTurn()] != null) {
                runAITurn();
            }
        });

        suggestBtn.setOnAction(e -> {showSuggestionPopup();

            updateButtonState();
        });

        accuseBtn.setOnAction(e -> {
                showAccusationPopup();

        });
        statusBtn.setOnAction(e -> showStatusPopup());
        notesBtn.setOnAction(e -> showNotepadPopup());
        panel.getChildren().addAll(statusLabel, rollBtn, suggestBtn, accuseBtn, statusBtn, notesBtn);


        return panel;

    }

    // Button State for inactivate button on AI's turn
    private void updateButtonState() {
        int current = game.getTurn();
        boolean isAI = (game.brains[current] != null);
        boolean iAmEliminated = game.getPlayers()[game.humanIndex].hasGuessed;

        boolean disable = isAI || iAmEliminated;

        rollBtn.setDisable(disable);
        suggestBtn.setDisable(disable);
        accuseBtn.setDisable(disable);
    }
    // popup box for checking status
    private void showStatusPopup() {
        Stage popup = new Stage();
        popup.setTitle("Your Status");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_LEFT);

        int me = game.humanIndex;
        Player player = game.getPlayers()[me];

        Label title = new Label("Your Status");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Label name = new Label("Character: " + player.name);
        name.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label cardsTitle = new Label("Your Cards:");
        cardsTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox cardList = new VBox(5);

        for (Card c : player.hand.getCards()) {
            Label cardLabel = new Label("• " + c.getName());
            cardList.getChildren().add(cardLabel);
        }

        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> popup.close());

        root.getChildren().addAll(title, name, cardsTitle, cardList, closeBtn);

        popup.setScene(new Scene(root, 300, 350));
        popup.show();
    }

    private void showNotepadPopup() {
        Stage popup = new Stage();
        popup.setTitle("My Notepad");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_LEFT);

        int me = game.humanIndex;
        Player player = game.getPlayers()[me];

        Label title = new Label("Your Notes");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // call the recent Note
        TextArea generalNotesArea = new TextArea(player.notes.getGeneralNotes());
        generalNotesArea.setPrefHeight(200);

        Button saveBtn = new Button("Save");
        Button closeBtn = new Button("Close");

        saveBtn.setOnAction(e -> {
            player.notes.setGeneralNotes(generalNotesArea.getText());
            popup.close();
        });

        closeBtn.setOnAction(e -> popup.close());

        root.getChildren().addAll(title, generalNotesArea, saveBtn, closeBtn);

        popup.setScene(new Scene(root, 350, 350));
        popup.show();
    }

    private void showSuggestionPopup() {
        Stage popup = new Stage();
        popup.setTitle("Make a Suggestion");

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Make a Suggestion");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // Suspect ComboBox
        ComboBox<String> suspectBox = new ComboBox<>();
        for (Card c : game.getDecks()[0].getContents()) {
            suspectBox.getItems().add(c.getName());
        }
        suspectBox.setPromptText("Select Suspect");

        // Weapon ComboBox
        ComboBox<String> weaponBox = new ComboBox<>();
        for (Card c : game.getDecks()[1].getContents()) {
            weaponBox.getItems().add(c.getName());
        }
        weaponBox.setPromptText("Select Weapon");
        
        int me = game.getTurn();
        int roomId = game.getPlayers()[me].inRoom;

        if (roomId == -1) {
            log.appendText("You must be inside a room to make a suggestion.\n");
            popup.close();
            return;
        }

        String roomName = game.getDecks()[2].getContents()[roomId].getName();
        Label roomLabel = new Label("Room: " + roomName);
        roomLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Buttons
        Button confirmBtn = new Button("Confirm");
        Button cancelBtn = new Button("Cancel");

        confirmBtn.setOnAction(e -> {
            String suspect = suspectBox.getValue();
            String weapon = weaponBox.getValue();

            if (suspect == null || weapon == null) {
                log.appendText("You must select both suspect and weapon.\n");
                return;
            }

            String result = game.doSuggestionForHuman(suspect, weapon, roomName);
            log.appendText(result + "\n");

            popup.close();
            updateButtonState();
        });

        cancelBtn.setOnAction(e -> popup.close());

        root.getChildren().addAll(
                title,
                new Label("Suspect:"), suspectBox,
                new Label("Weapon:"), weaponBox,
                roomLabel,
                confirmBtn, cancelBtn
        );

        popup.setScene(new Scene(root, 350, 400));
        popup.show();
    }


    // Accusation popup
    private void showAccusationPopup() {
        Stage popup = new Stage();
        popup.setTitle("Make an Accusation");

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Make an Accusation");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // Suspect ComboBox
        ComboBox<String> suspectBox = new ComboBox<>();
        for (Card c : game.getDecks()[0].getContents()) {
            suspectBox.getItems().add(c.getName());
        }
        suspectBox.setPromptText("Select Suspect");

        // Weapon ComboBox
        ComboBox<String> weaponBox = new ComboBox<>();
        for (Card c : game.getDecks()[1].getContents()) {
            weaponBox.getItems().add(c.getName());
        }
        weaponBox.setPromptText("Select Weapon");

        // Room ComboBox
        ComboBox<String> roomBox = new ComboBox<>();
        for (Card c : game.getDecks()[2].getContents()) {
            roomBox.getItems().add(c.getName());
        }
        roomBox.setPromptText("Select Room");

        // Buttons
        Button confirmBtn = new Button("Confirm");
        Button cancelBtn = new Button("Cancel");

        confirmBtn.setOnAction(e -> {
            String suspect = suspectBox.getValue();
            String weapon = weaponBox.getValue();
            String room = roomBox.getValue();

            if (suspect == null || weapon == null || room == null) {
                log.appendText("You must select suspect, weapon, and room.\n");
                return;
            }

            String result = game.doAccusationForHuman(suspect, weapon, room);
            log.appendText(result + "\n");

            if (result.contains("CORRECT!")) {
                Stage stage = (Stage) boardGrid.getScene().getWindow();
                stage.setScene(createGameOverScreen(stage, game.getDisplayName(game.getTurn())));
                return;
            }
            if (result.contains("WRONG!")) {

                game.rollAndMove();

                updateBoard();
                updateButtonState();

                if (game.brains[game.getTurn()] != null) {
                    runAITurn();
                }

                popup.close();
                return;
            }


            popup.close();
            updateBoard();
            updateButtonState();

            if (game.brains[game.getTurn()] != null) {
                runAITurn();
            }
        });

        cancelBtn.setOnAction(e -> popup.close());

        root.getChildren().addAll(
                title,
                new Label("Suspect:"), suspectBox,
                new Label("Weapon:"), weaponBox,
                new Label("Room:"), roomBox,
                confirmBtn, cancelBtn
        );

        popup.setScene(new Scene(root, 350, 450));
        popup.show();
    }


    // popup box for setting option
    private void showSettingsPopup() {
        Stage popup = new Stage();
        popup.setTitle("Settings");

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Settings");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> popup.close());

        root.getChildren().addAll(title, closeBtn);



        Scene scene = new Scene(root, 300, 200);
        popup.setScene(scene);
        popup.show();
    }

    // Game over screen
    public Scene createGameOverScreen(Stage stage, String winner) {
        VBox root = new VBox(30);
        root.setPadding(new Insets(80));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #fafafa;");

        Label result = new Label("Winner: " + winner);
        result.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        Button viewSolution = new Button("View Solution");
        Button mainMenu = new Button("Main Menu");
        Button playAgain = new Button("Play Again");

        viewSolution.setPrefWidth(200);
        viewSolution.setOnAction(e -> {
            Stage popup = new Stage();
            popup.setTitle("Solution");

            VBox box = new VBox(10);
            box.setPadding(new Insets(20));
            box.setAlignment(Pos.CENTER);

            box.getChildren().add(new Label("The Answer:"));
            for (Card c : game.getAnswer().getCards()) {
                box.getChildren().add(new Label("• " + c.getName()));
            }

            Button close = new Button("Close");
            close.setOnAction(ev -> popup.close());
            box.getChildren().add(close);

            popup.setScene(new Scene(box, 280, 240));
            popup.show();
        });

        mainMenu.setPrefWidth(200);
        playAgain.setPrefWidth(200);

        mainMenu.setOnAction(e -> stage.setScene(createStartScreen(stage)));
        playAgain.setOnAction(e -> stage.setScene(createMainGameScreen(stage, game.getPlayerCount())));

        root.getChildren().addAll(result, viewSolution, mainMenu, playAgain);

        return new Scene(root, 800, 600);
    }
    public static void main(String[] args) {
        launch(args);
    }
}
