package com.mmorano.crolympics;

import com.mmorano.crolympics.Save.CrolympicsSave;
import com.mmorano.crolympics.Save.SportEvent;
import com.mmorano.crolympics.Save.EventPlace;
import com.mmorano.crolympics.Save.SaveData;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.controlsfx.control.SearchableComboBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CrolympicsController{
    @FXML private Label lblPlayer1, lblPlayer2, lblPlayer3, lblPlayer4, lblPlayer5, lblPlayer6, lblPlayer7, lblPlayer8, lblPlayer9, lblPlayer10, lblPlayer11, lblPlayer12, lblPlayer13, lblPlayer14, lblPlayer15;
    @FXML private Label lblScore1, lblScore2, lblScore3, lblScore4, lblScore5, lblScore6, lblScore7, lblScore8, lblScore9, lblScore10, lblScore11, lblScore12, lblScore13, lblScore14, lblScore15;
    @FXML private ImageView imgGold, imgSilver, imgBronze, imgEvent;
    @FXML private Label lblPlace4, lblPlace5, lblPlace6, lblPlace7, lblPlace8, lblPlace9, lblPlace10, lblPlace11, lblPlace12, lblPlace13, lblPlace14, lblPlace15;
    @FXML private VBox vboxSchedule, vboxFunc;
    @FXML private Tab tabScoreboard, tabSchedule, tabCurrentEvent;
    @FXML private TabPane tabPane;
    @FXML private BorderPane borderPane;
    private Scanner sc;
    private ObservableList<Player> players;
    private ObservableList<Label> playerLabels, scoreLabels, placeLabels;
    private ObservableList<ImageView> placeImages;
    private final int fontSize = 32, fontSize2 = 24;
    private CrolympicsSave save = new CrolympicsSave();
    private SaveData saveData = new SaveData();
    private ArrayList<String> bonusPoints = new ArrayList<>();

    public void initialize(){
        players = FXCollections.observableArrayList();
        playerLabels = FXCollections.observableArrayList(List.of(lblPlayer1, lblPlayer2, lblPlayer3, lblPlayer4, lblPlayer5, lblPlayer6, lblPlayer7, lblPlayer8, lblPlayer9, lblPlayer10, lblPlayer11, lblPlayer12, lblPlayer13, lblPlayer14, lblPlayer15));
        scoreLabels = FXCollections.observableArrayList(List.of(lblScore1, lblScore2, lblScore3, lblScore4, lblScore5, lblScore6, lblScore7, lblScore8, lblScore9, lblScore10, lblScore11, lblScore12, lblScore13, lblScore14, lblScore15));
        placeImages = FXCollections.observableArrayList(List.of(imgGold, imgSilver, imgBronze));
        placeLabels = FXCollections.observableArrayList(List.of(lblPlace4, lblPlace5, lblPlace6, lblPlace7, lblPlace8, lblPlace9, lblPlace10, lblPlace11, lblPlace12, lblPlace13, lblPlace14, lblPlace15));

        saveData = save.TryLoad();

        try{
            sc = new Scanner(getFile("players.txt"));
            while(sc.hasNextLine()){
                players.add(new Player(sc.nextLine()));
            }
        }catch (Exception ignored){}
        tabScoreboard.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                setLeaderboard();
        });
        tabCurrentEvent.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                setTicker();
        });
        tabSchedule.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue)
                save.TrySave(getSaveData());
        });
        players.sort(Comparator.comparing(Player::getPoints).thenComparing(Player::getName));
        setEvents();
        addBonusPoints();
        setLeaderboard();
        setFunctions();
        setTicker();
    }

    public void setLeaderboard(){
        players.sort(Comparator.comparing(Player::getPoints).reversed());
        for(int i = 0; i < playerLabels.size() && i < players.size(); i++){
            playerLabels.get(i).setText(players.get(i).getName());
            scoreLabels.get(i).setText(players.get(i).getPoints() + " pts");
            //If 2 players are tied, do not show place number/image for second+ player to indicate they are tied for above place
            if(i > 0 && players.get(i).getPoints() == players.get(i - 1).getPoints()){
                if(i < 3){
                    placeImages.get(i).setVisible(false);
                } else {
                    placeLabels.get(i - 3).setVisible(false);
                }
            }
            else{
                if(i < 3){
                    placeImages.get(i).setVisible(true);
                } else {
                    placeLabels.get(i - 3).setVisible(true);
                }
            }
        }
        players.sort(Comparator.comparing(Player::getName));
    }

    private void setEvents(){
        HashMap<String, SportEvent> existingEventsMap = (HashMap<String, SportEvent>) saveData.getEvents().stream()
                .collect(Collectors.toMap(SportEvent::getName, Function.identity()));
        try{
            sc = new Scanner(getFile("events.txt"));
            vboxSchedule.setSpacing(10);
            while(sc.hasNextLine()){
                String[] elements = sc.nextLine().split(";");
                if(elements[0].startsWith("{"))
                    continue;
                String eventName = elements[0];
                SportEvent existingEvent = existingEventsMap.get(eventName);
                int round = Integer.parseInt(elements[1]);
                AnchorPane anchorPane = new AnchorPane();
                anchorPane.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-border-style: solid;");
                anchorPane.setPrefHeight(Region.USE_COMPUTED_SIZE);
                anchorPane.setMaxWidth(1900);

                Label eventLabel = new Label(eventName);
                eventLabel.setLayoutX(5);
                eventLabel.setLayoutY(5);
                eventLabel.setPrefWidth(Region.USE_COMPUTED_SIZE);
                eventLabel.setFont(new Font(fontSize));
                eventLabel.setOnMouseClicked(event -> {
                    if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2){
                        onEventDoubleClicked(round);
                    }
                });
                anchorPane.getChildren().add(eventLabel);

                int count = 2;
                int xCord = 250;
                int yCord = 5;
                while(elements.length >= count + 3){
                    String placeName = elements[count];
                    Label placeLabel = new Label(placeName + " (" + elements[count + 2] + "pts)");
                    placeLabel.setLayoutX(xCord);
                    placeLabel.setLayoutY(yCord);
                    yCord += 55;//label height = 45 + 10 for spacing
                    placeLabel.setPrefWidth(Region.USE_COMPUTED_SIZE);
                    placeLabel.setFont(new Font(fontSize));
                    anchorPane.getChildren().add(placeLabel);

                    int points = Integer.parseInt(elements[count + 2]);
                    if(elements[count + 1].equalsIgnoreCase("many")) {
                        //checkboxes of all players
                        GridPane gridPane = new GridPane();
                        gridPane.setPadding(new Insets(20));
                        gridPane.setHgap(15);
                        gridPane.setVgap(15);
                        gridPane.setLayoutX(xCord);
                        gridPane.setLayoutY(yCord);
                        double playerSize = players.size();
                        int rows = (int) Math.ceil(playerSize / 4);
                        int columns = 4;
                        for(int r = 0; r < rows; r++){
                            for(int c = 0; c < columns && r * columns + c < players.size(); c++){
                                Player player = players.get(r * columns + c);
                                CheckBox ckBox = new CheckBox(player.getName());
                                ckBox.setUserData(player);
                                ckBox.setOnAction(event -> {
                                    CheckBox source = (CheckBox) event.getSource();
                                    Player associatedPlayer = (Player) source.getUserData();
                                    if (source.isSelected()) {
                                        associatedPlayer.setPoints(associatedPlayer.getPoints() + points);
                                        System.out.println(associatedPlayer.getName() + " " + associatedPlayer.getPoints());
                                    } else {
                                        associatedPlayer.setPoints(associatedPlayer.getPoints() - points);
                                        System.out.println(associatedPlayer.getName() + " " + associatedPlayer.getPoints());
                                    }
                                });
                                if(existingEvent != null && existingEvent.getPlaces().size() > 0){
                                    Player existingPlayer = existingEvent.getPlaces().get(0).getWinners().stream().filter(x -> x.getName().equals(player.getName())).findFirst().orElse(null);
                                    if(existingPlayer != null){
                                        ckBox.setSelected(true);
                                        player.setPoints(player.getPoints() + existingEvent.getPlaces().get(0).getPoints());
                                    }
                                }
                                ckBox.setFont(new Font(fontSize2));
                                gridPane.add(ckBox, c, r);
                            }
                        }
                        anchorPane.getChildren().add(gridPane);
                    } else {
                        //dropdowns per place
                        for (int i = 0; i < Integer.parseInt(elements[count + 1]); i++) {
                            SearchableComboBox<Player> comboBox = getPlayerComboBox(points);
                            if(existingEvent != null && existingEvent.getPlaces().size() > 0){
                                EventPlace existingPlace = existingEvent.getPlaces().stream().filter(x -> x.getPlaceName().startsWith(placeName)).findFirst().orElse(null);
                                if(existingPlace != null && existingPlace.getWinners().size() > i){
                                    int index = i;
                                    Player player = players.stream().filter(x -> x.getName().equals(existingPlace.getWinners().get(index).getName())).findFirst().orElse(null);
                                    if(player != null){
                                        //player.setPoints(player.getPoints() + existingPlace.getPoints());
                                        comboBox.setValue(player);
                                    }
                                }
                            }
                            comboBox.setLayoutX(xCord);
                            comboBox.setLayoutY(yCord);
                            yCord += 62;//combobox height = 52 + 10 for spacing
                            anchorPane.getChildren().add(comboBox);
                        }
                    }
                    xCord += 335;
                    yCord = 5;
                    count+=3;
                }
                vboxSchedule.getChildren().add(anchorPane);
            }
        }catch (Exception ignored){}
    }

    private SearchableComboBox<Player> getPlayerComboBox(int points) {
        SearchableComboBox<Player> comboBox = new SearchableComboBox<>(players);
        // Define the StringConverter to show the Name field
        comboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Player player) {
                return (player == null) ? "" : player.getName();
            }

            @Override
            public Player fromString(String string) {
                // Not needed unless the ComboBox is editable
                return null;
            }
        });
        if(points > 0) {
            // Handle Selection Changes (Retrieves the full object)
            comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    newValue.setPoints(newValue.getPoints() + points);
                }
                if (oldValue != null) {
                    oldValue.setPoints(oldValue.getPoints() - points);
                }
            });
        }
        comboBox.setStyle("-fx-font-size: " + fontSize2 + "px;");

        return comboBox;
    }

    private void setTicker(){
        players.sort(Comparator.comparing(Player::getPoints).reversed().thenComparing(Player::getName));
        HBox footer = new HBox();
        footer.setStyle("-fx-background-color: #2b2b2b; -fx-padding: 10;");

        StringBuilder sb = new StringBuilder();
        for(Player player : players){
            sb.append(player.getName());
            sb.append(": ");
            sb.append(player.getPoints());
            sb.append(" pts\t\t");
        }

        Text textNode = new Text(sb.toString());
        textNode.setFill(Color.WHITE);
        textNode.setFont(new Font(18));

        // Apply clip to constrain text visibility
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(footer.widthProperty());
        clip.heightProperty().bind(footer.heightProperty());
        footer.setClip(clip);
        footer.getChildren().add(textNode);
        borderPane.setBottom(footer);

        // Animate text movement
        //TODO - adjust timing and end point to be based on
        TranslateTransition transition = new TranslateTransition(Duration.seconds(players.size() * 1.5), textNode);
        transition.setFromX(2000); // Start off-screen
        transition.setToX(-players.size() * 200); // End off-screen
        transition.setInterpolator(Interpolator.LINEAR);
        transition.setCycleCount(Animation.INDEFINITE);
        transition.play();
    }

    private void setFunctions(){
        AnchorPane addPlayerPane = new AnchorPane();
        addPlayerPane.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-border-style: solid;");
        addPlayerPane.setPrefHeight(Region.USE_COMPUTED_SIZE);

        Label addPlayerLabel = new Label("Add Player");
        addPlayerLabel.setLayoutX(5);
        addPlayerLabel.setLayoutY(5);
        addPlayerLabel.setPrefWidth(Region.USE_COMPUTED_SIZE);
        addPlayerLabel.setFont(new Font(fontSize));

        TextField addPlayerInput = new TextField();
        addPlayerInput.setPromptText("Player Name");
        addPlayerInput.setLayoutX(177);
        addPlayerInput.setLayoutY(5);
        addPlayerInput.setFont(new Font(fontSize2));

        Button addPlayerButton = new Button("Add");
        addPlayerButton.setLayoutX(483);
        addPlayerButton.setLayoutY(5);
        addPlayerButton.setFont(new Font(fontSize2));
        addPlayerButton.setOnAction(Event -> {
            players.add(new Player(addPlayerInput.getText()));
            try {
                // StandardOpenOption.APPEND ensures it adds to the end without overwriting
                Files.writeString(getFile("players.txt").toPath(), addPlayerInput.getText(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            addPlayerInput.setText("");
        });

        addPlayerPane.getChildren().add(addPlayerLabel);
        addPlayerPane.getChildren().add(addPlayerInput);
        addPlayerPane.getChildren().add(addPlayerButton);

        AnchorPane addPointsPane = new AnchorPane();
        addPointsPane.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-border-style: solid;");
        addPointsPane.setPrefHeight(Region.USE_COMPUTED_SIZE);

        Label addPointsLabel = new Label("Add Points");
        addPointsLabel.setLayoutX(5);
        addPointsLabel.setLayoutY(5);
        addPointsLabel.setPrefWidth(Region.USE_COMPUTED_SIZE);
        addPointsLabel.setFont(new Font(fontSize));

        SearchableComboBox<Player> addPointsComboBox = getPlayerComboBox(0);
        addPointsComboBox.setLayoutX(177);
        addPointsComboBox.setLayoutY(5);

        TextField addPointsInput = new TextField();
        addPointsInput.setPromptText("Points");
        addPointsInput.setLayoutX(494);
        addPointsInput.setLayoutY(5);
        addPointsInput.setPrefWidth(103);
        addPointsInput.setFont(new Font(fontSize2));

        Button addPointsButton = new Button("Add");
        addPointsButton.setLayoutX(607);
        addPointsButton.setLayoutY(5);
        addPointsButton.setFont(new Font(fontSize2));
        addPointsButton.setOnAction(Event -> {
            if(!addPointsInput.getText().equals("")) {
                Player player = addPointsComboBox.getValue();
                player.setPoints(player.getPoints() + Integer.parseInt(addPointsInput.getText()));
                bonusPoints.add(player.getName() + ";" + addPointsInput.getText());
                addPointsInput.setText("");
                addPointsComboBox.getSelectionModel().clearSelection();
                addPointsComboBox.setValue(null);
                save.TrySave(getSaveData());
            }
        });

        addPointsPane.getChildren().add(addPointsLabel);
        addPointsPane.getChildren().add(addPointsComboBox);
        addPointsPane.getChildren().add(addPointsInput);
        addPointsPane.getChildren().add(addPointsButton);


        AnchorPane subtractPointsPane = new AnchorPane();
        subtractPointsPane.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-border-style: solid;");
        subtractPointsPane.setPrefHeight(Region.USE_COMPUTED_SIZE);

        Label subtractPointsLabel = new Label("Subtract Points");
        subtractPointsLabel.setLayoutX(5);
        subtractPointsLabel.setLayoutY(5);
        subtractPointsLabel.setPrefWidth(Region.USE_COMPUTED_SIZE);
        subtractPointsLabel.setFont(new Font(fontSize));

        SearchableComboBox<Player> subtractPointsComboBox = getPlayerComboBox(0);
        subtractPointsComboBox.setLayoutX(244);
        subtractPointsComboBox.setLayoutY(5);

        TextField subtractPointsInput = new TextField();
        subtractPointsInput.setPromptText("Points");
        subtractPointsInput.setLayoutX(561);
        subtractPointsInput.setLayoutY(5);
        subtractPointsInput.setPrefWidth(103);
        subtractPointsInput.setFont(new Font(fontSize2));

        Button subtractPointsButton = new Button("subtract");
        subtractPointsButton.setLayoutX(674);
        subtractPointsButton.setLayoutY(5);
        subtractPointsButton.setFont(new Font(fontSize2));
        subtractPointsButton.setOnAction(Event -> {
            if(!subtractPointsInput.getText().equals("")) {
                Player player = subtractPointsComboBox.getValue();
                player.setPoints(player.getPoints() - Integer.parseInt(subtractPointsInput.getText()));
                bonusPoints.add(player.getName() + ";-" + subtractPointsInput.getText());
                subtractPointsInput.setText("");
                subtractPointsComboBox.getSelectionModel().clearSelection();
                subtractPointsComboBox.setValue(null);
                save.TrySave(getSaveData());
            }
        });

        subtractPointsPane.getChildren().add(subtractPointsLabel);
        subtractPointsPane.getChildren().add(subtractPointsComboBox);
        subtractPointsPane.getChildren().add(subtractPointsInput);
        subtractPointsPane.getChildren().add(subtractPointsButton);

        AnchorPane pane = new AnchorPane();
        pane.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-border-style: solid;");
        pane.setPrefHeight(Region.USE_COMPUTED_SIZE);
        Label lbl = new Label();
        lbl.setLayoutY(0);
        lbl.setLayoutX(150);
        lbl.setFont(new Font(24));
        Button temp = new Button("Get Screen Size");
        temp.setLayoutX(0);
        temp.setLayoutY(5);
        temp.setOnAction(Event -> {
            Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();

            double usableWidth = visualBounds.getWidth();
            double usableHeight = visualBounds.getHeight();

            lbl.setText("Usable Screen Size: " + usableWidth + " x " + usableHeight);

        });
        pane.getChildren().add(temp);
        pane.getChildren().add(lbl);
        vboxFunc.getChildren().add(pane);

        vboxFunc.setSpacing(5);
        vboxFunc.getChildren().add(addPlayerPane);
        vboxFunc.getChildren().add(addPointsPane);
        vboxFunc.getChildren().add(subtractPointsPane);
    }

    private SaveData getSaveData(){
        SaveData data = new SaveData();
        AnchorPane aPane = (AnchorPane) tabSchedule.getContent();
        ObservableList<Node> nodes = aPane.getChildren();
        ScrollPane sPane = (ScrollPane) nodes.get(0);
        VBox vbox = (VBox) sPane.getContent();
        nodes = vbox.getChildren();
        int placePoints = 0;
        for(Node node : nodes){
            aPane = (AnchorPane) node;
            ObservableList<Node> aNodes = aPane.getChildren();
            SportEvent event = new SportEvent();
            EventPlace eventPlace = null;
            for(int i = 0; i < aNodes.size(); i++){
                Node aNode = aNodes.get(i);
                if(i == 0) {
                    event.setName(((Label)aNode).getText());
                    data.getEvents().add(event);
                } else if(aNode.getClass() == Label.class) {
                    eventPlace = new EventPlace();
                    String placeName = ((Label)aNode).getText();
                    eventPlace.setPlaceName(placeName);
                    eventPlace.setPoints(parsePoints(placeName));
                    event.getPlaces().add(eventPlace);
                } else {
                    while(i < aNodes.size() && aNode.getClass() != Label.class){
                        if(aNode.getClass() == SearchableComboBox.class){
                            Player player = (Player)((SearchableComboBox)aNode).getValue();
                            if(player != null) {
                                eventPlace.getWinners().add(player);
                            }
                        } else if(aNode.getClass() == GridPane.class){
                            ObservableList<Node> gridNodes = ((GridPane)aNode).getChildren();
                            for(Node gridNode : gridNodes){
                                CheckBox ckBox = (CheckBox)gridNode;
                                if(ckBox.isSelected())
                                    eventPlace.getWinners().add((Player) ckBox.getUserData());
                            }
                        }
                        i++;
                        if(i < aNodes.size())
                            aNode = aNodes.get(i);
                    }
                    i--;
                }
            }
        }
        //Add bonus points
        if(bonusPoints.size() > 0) {
            SportEvent bonusEvent = new SportEvent();
            bonusEvent.setName("bonus");
            for (String bonus : bonusPoints) {
                EventPlace place = new EventPlace();
                String[] elements = bonus.split(";");
                place.setPlaceName(elements[0]);
                place.setPoints(Integer.parseInt(elements[1]));
                bonusEvent.getPlaces().add(place);
            }
            data.getEvents().add(bonusEvent);
        }
        return data;
    }

    private void addBonusPoints(){
        SportEvent bonusSport = saveData.getEvents().stream().filter(x -> x.getName().equals("bonus")).findFirst().orElse(null);
        if(bonusSport != null){
            for(EventPlace place : bonusSport.getPlaces()){
                Player player = players.stream().filter(x -> x.getName().equals(place.getPlaceName())).findFirst().orElse(null);
                if(player != null){
                    player.setPoints(player.getPoints() + place.getPoints());
                    bonusPoints.add(player.getName() + ";" + place.getPoints());
                }
            }
        }
    }

    private File getFile(String fileName){
        String userHome = System.getProperty("user.home");
        Path filePath = Path.of(userHome, "Desktop", fileName);
        return filePath.toFile();
    }

    private void onEventDoubleClicked(int round){
        FileInputStream stream = null;
        try {
            stream = switch (round) {
                case 1 -> new FileInputStream(getFile("EventImages/round1.png"));
                case 2 -> new FileInputStream(getFile("EventImages/round2.png"));
                case 3 -> new FileInputStream(getFile("EventImages/round3.png"));
                case 4 -> new FileInputStream(getFile("EventImages/round4.png"));
                case 5 -> new FileInputStream(getFile("EventImages/round5.png"));
                case 6 -> new FileInputStream(getFile("EventImages/round6.png"));
                case 7 -> new FileInputStream(getFile("EventImages/round7.png"));
                case 8 -> new FileInputStream(getFile("EventImages/round8.png"));
                case 9 -> new FileInputStream(getFile("EventImages/round9.png"));
                case 10 -> new FileInputStream(getFile("EventImages/round10.png"));
                case 11 -> new FileInputStream(getFile("EventImages/round11.png"));
                case 12 -> new FileInputStream(getFile("EventImages/round12.png"));
                case 13 -> new FileInputStream(getFile("EventImages/round13.png"));
                case 14 -> new FileInputStream(getFile("EventImages/round14.png"));
                case 15 -> new FileInputStream(getFile("EventImages/round15.png"));
                case 16 -> new FileInputStream(getFile("EventImages/round16.png"));
                case 17 -> new FileInputStream(getFile("EventImages/round17.png"));
                case 18 -> new FileInputStream(getFile("EventImages/round18.png"));
                case 19 -> new FileInputStream(getFile("EventImages/round19.png"));
                case 20 -> new FileInputStream(getFile("EventImages/round20.png"));
                default -> new FileInputStream("@../images/broken.jpg");
            };
        }catch (Exception ignored){}
        imgEvent.setImage(new Image(stream));
        tabPane.getSelectionModel().select(tabCurrentEvent);
    }

    private int parsePoints(String st){
        int start = st.indexOf("(");
        int end = st.lastIndexOf("p");
        int result = 0;
        if(start > -1 && end > start)
            result = Integer.parseInt(st.substring(start + 1, end));
        return result;
    }

    public void printNodeDetails(Node node, String indent) {
        // Print node's class name and ID
        String id = (node.getId() != null) ? node.getId() : "No ID";
        System.out.println(indent + "Node Class: " + node.getClass().getSimpleName() +
                " | fx:id / ID: " + id);

        // Print common properties
        System.out.println(indent + "  Layout X/Y: (" + node.getLayoutX() + ", " + node.getLayoutY() + ")");
        System.out.println(indent + "  Translate X/Y/Z: (" + node.getTranslateX() + ", " +
                node.getTranslateY() + ", " + node.getTranslateZ() + ")");
        System.out.println(indent + "  Height/Width: (" + node.getBoundsInLocal().getHeight() + ", " +
                node.getBoundsInLocal().getWidth() + ")");
        System.out.println(indent + "  Visible: " + node.isVisible() + " | Opacity: " + node.getOpacity());
        System.out.println(indent + "  Style Classes: " + node.getStyleClass());

        // Print text-specific properties if the node is a Label, Button, etc.
        if (node instanceof Labeled) {
            System.out.println(indent + "  Text Content: \"" + ((Labeled) node).getText() + "\"");
        }

        // Recursively traverse if it's a Parent node
        if (node instanceof Parent) {
            Parent parent = (Parent) node;
            for (Node child : parent.getChildrenUnmodifiable()) {
                printNodeDetails(child, indent + "  ");
            }
        }
    }
}
