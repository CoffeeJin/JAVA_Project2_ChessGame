package au.edu.sydney.soft3202.task3.view;

import au.edu.sydney.soft3202.task3.model.GameBoard;
import au.edu.sydney.soft3202.task3.model.GameRecord;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This is the overall window scene for the application. It creates and contains the different elements in the
 * top, bottom, center, and right side of the window, along with linking them to the model.
 *
 * Identify the mutable couplings between the View and the Model: This class and the BoardPane are the only 2 View
 * classes that mutate the Model, and all mutations go first to the GameBoard. There is coupling in other ways
 * from the View to the Model, but they are accessor methods only.
 *
 * Also note that while this represents the game window, it *contains* the Scene that JavaFX needs, and does not
 * inherit from Scene. This is true for all JavaFX components in this application, they are contained, not extended.
 */
public class GameWindow {
    private final BoardPane boardPane;
    private final Scene scene;
    private MenuBar menuBar;
    private VBox sideButtonBar;
    private String username;

    private final GameBoard model;

    public GameWindow(GameBoard model) {
        this.model = model;

        BorderPane pane = new BorderPane();
        this.scene = new Scene(pane);

        this.boardPane = new BoardPane(model);
        StatusBarPane statusBar = new StatusBarPane(model);
        buildMenu();
        buildSideButtons();
        buildKeyListeners();

        pane.setCenter(boardPane.getPane());
        pane.setTop(menuBar);
        pane.setRight(sideButtonBar);
        pane.setBottom(statusBar.getStatusBar());

    }

    private void buildKeyListeners() {
        // This allows keyboard input. Note that the scene is used, so any time
        // the window is in focus the keyboard input will be registered.
        // More often, keyboard input is more closely linked to a specific
        // node that must have focus, i.e. the Enter key in a text input to submit
        // a form.

        scene.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (event.isControlDown() && event.getCode() == KeyCode.N) {
                newGameAction();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.S) {
                saveGame();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.L) {
                loadGame();
            }
        });
    }

    private void buildSideButtons() {
        Button newGameBtn = new Button("New Game");
        newGameBtn.setOnAction((event) -> newGameAction());

        Button serialiseBtn = new Button("Save Game");
        serialiseBtn.setOnAction((event) -> saveGame());

        Button deserialiseBtn = new Button("Load Game");
        deserialiseBtn.setOnAction((event) -> loadGame());

        this.sideButtonBar = new VBox(newGameBtn, serialiseBtn, deserialiseBtn);
        sideButtonBar.setSpacing(10);
    }

    private void buildMenu() {
        Menu actionMenu = new Menu("Actions");

        MenuItem newGameItm = new MenuItem("New Game");
        newGameItm.setOnAction((event)-> newGameAction());

        MenuItem serialiseItm = new MenuItem("Save Game");
        serialiseItm.setOnAction((event)-> saveGame());

        MenuItem deserialiseItm = new MenuItem("Load Game");
        deserialiseItm.setOnAction((event)-> loadGame());

        actionMenu.getItems().addAll(newGameItm, serialiseItm, deserialiseItm);

        this.menuBar = new MenuBar();
        menuBar.getMenus().add(actionMenu);
    }

    private void newGameAction() {
        // Note the separation here between newGameAction and doNewGame. This allows
        // for the validation aspects to be separated from the operation itself.

        if (null == model.getCurrentTurn()) { // no current game
            TextInputDialog textInput = new TextInputDialog("");
            textInput.setTitle("Start a game");
            textInput.setHeaderText("Please input your username:");
            Optional<String> input = textInput.showAndWait();
            if(input.isPresent()){
                if (!input.get().equals("")) {

                    doNewGame(input.get());

                    username = input.get();
                    Alert alert0 = new Alert(Alert.AlertType.INFORMATION);
                    alert0.setTitle("Game start");
                    alert0.setHeaderText(username+"'s new game start!");
                    alert0.showAndWait();
                }else{
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Game start failed!");
                    alert.setHeaderText("Please input a valid name and try again!");
                    alert.showAndWait();

                }
            }else{
                return;
            }


        }else{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("New Game Warning");
            alert.setHeaderText("Starting a new game now will lose all current progress.");
            alert.setContentText("Are you ok with this?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK){
                TextInputDialog textInput = new TextInputDialog("");
                textInput.setTitle("Start a game");
                textInput.setHeaderText("Please input your username:");
                Optional<String> input = textInput.showAndWait();
                if(input.isPresent()){
                    if (!input.get().equals("")) {

                        doNewGame(input.get());
                        username = input.get();
                        Alert alert0 = new Alert(Alert.AlertType.INFORMATION);
                        alert0.setTitle("Game start");
                        alert0.setHeaderText(username+"'s new game start!");
                        alert0.showAndWait();

                    }else{
                        Alert alert1 = new Alert(Alert.AlertType.ERROR);
                        alert1.setTitle("Game start failed!");
                        alert1.setHeaderText("Please input a valid name and try again!");
                        alert1.showAndWait();

                    }
                }

            }
        }
        if(!model.isExistedUser(username)){
            model.claimUser(username);
        }
    }

    private void serialiseAction() {
        // Serialisation is a way of turning some data into a communicable form.
        // In Java it has a library to support it, but here we are just manually converting the field
        // we know we need into a string (in the model). We can then use that string in reverse to get that state back

        if (null == model.getCurrentTurn()) { // no current game
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Serialisation Error");
            alert.setHeaderText("There is no game to serialise!");

            alert.showAndWait();
            return;
        }

        String serialisation = model.serialise();

        TextInputDialog textInput = new TextInputDialog(serialisation);
        textInput.setTitle("Serialisation");
        textInput.setHeaderText("Your serialisation string is:");
        textInput.showAndWait();
    }

    private void deserialiseAction() {
        // Here we take an existing serialisation string and feed it back into the model to retrieve that state.
        // We don't do any validation here, as that would leak model knowledge into the view.

        TextInputDialog textInput = new TextInputDialog("");
        textInput.setTitle("Serialisation");
        textInput.setHeaderText("Enter your serialisation string:");

        Optional<String> input = textInput.showAndWait();

        if (input.isPresent()) {
            String serialisation = input.get();

            try {
                model.deserialise(serialisation);
            } catch (IllegalArgumentException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Serialisation Error");
                alert.setHeaderText(e.getMessage());

                alert.showAndWait();
                return;
            }

            boardPane.updateBoard();
        }
    }

    private void saveGame() {
        // save a game
        // username should be not null

        if (null == model.getCurrentTurn()) { // no current game
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save Error");
            alert.setHeaderText("There is no game to save!");
            alert.showAndWait();
            return;
        }

        String serialisation = model.serialise();

        TextInputDialog textInput = new TextInputDialog("");
        textInput.setTitle("Save game");
        textInput.setHeaderText("Please input game name:");
        Optional<String> input = textInput.showAndWait();
        if(input.isPresent()){
            if (input.isPresent()&&!input.get().equals("")) {
                String gameName = input.get();
                if(model.isExistedGame(username,gameName)){
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Notification");
                    alert.setHeaderText("Save name exists, are you sure you want to overwrite?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if(result.isPresent() && result.get() == ButtonType.OK) {
                        model.saveGame(username,gameName,serialisation);
                        Alert alert0 = new Alert(Alert.AlertType.INFORMATION);
                        alert0.setTitle("Notification");
                        alert0.setHeaderText("Saved successfully!");
                        alert0.showAndWait();
                    }
                }else{
                    model.saveGame(username,gameName,serialisation);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Notification");
                    alert.setHeaderText("Saved successfully!");
                    alert.showAndWait();
                }
            }else{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Notification");
                alert.setHeaderText("Save failed!");
                alert.setContentText("Please input a valid game name!");
                alert.showAndWait();
            }
        }

    }

    private void loadGame() {
        //load games from database records
        //update board if load succeed

        if (null == model.getCurrentTurn()) { // no current game
            TextInputDialog textInput = new TextInputDialog("");
            textInput.setTitle("Load game");
            textInput.setHeaderText("Please input username to continue:");
            Optional<String> input = textInput.showAndWait();
            if(input.isPresent()){
                if(model.isExistedUser(input.get())){
                    username = input.get();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Login");
                    alert.setHeaderText("Login succeed!");
                    alert.showAndWait();
                }else{
                    model.claimUser(input.get());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Login");
                    alert.setHeaderText("New user has been created!");
                    alert.setContentText("No saved game found, new game start!");
                    alert.showAndWait();
                    doNewGame(input.get());
                    return;
                }
            }else{
                return;
            }
        }
        System.out.println(username);
        List<GameRecord> records = model.getRecords(username);
        if (records.size()>0){
            List<String> gameNames = new ArrayList<>();
            for(GameRecord record: records){
                gameNames.add(record.getGameName());
            }
            ChoiceDialog choices = new ChoiceDialog(gameNames.get(0), gameNames);
            choices.setTitle("Saved games");
            choices.setHeaderText("Please choose a saved game to load");
            Optional<String> result = choices.showAndWait();
            if ( result.isPresent() )
            {
                String game = result.get();
                String serialisation = "";
                for(GameRecord record:records){
                    if(record.getUsername().equals(username)){
                        if(record.getGameName().equals(game)){
                            serialisation = record.getSerialisation();
                        }
                    }
                }
                try{
                    model.deserialise(serialisation);
                    boardPane.updateBoard();
                } catch (IllegalArgumentException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Serialisation Error");
                    alert.setHeaderText(e.getMessage());
                    alert.showAndWait();
                    return;
                }



            }

        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Load game");
            alert.setHeaderText("Load game failed!");
            alert.setContentText("No game record of current user, please start new game and try again!");
            alert.showAndWait();
            return;
        }
    }

    private void doNewGame(String username) {
        // Here we have an action that we know would likely mutate the state of the model, and so the view should
        // update. Unlike the StatusBarPane that uses the observer pattern to do this, here we can just trigger it
        // because we know the model will mutate as a result of our call to it.
        // Generally speaking the observer pattern is superior - I would recommend using it instead of
        // doing it this way.
        this.username = username;
        model.newGame();
        boardPane.updateBoard();
    }


    public Scene getScene() {
        return this.scene;
    }
}
