package gui;

import database.DBConnection;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {

    private Controller theController;
    private BorderPane root;  //the root element of this GUI
    private Stage editPop;
    private Stage primaryStage;  //The stage that is passed in on initialization
    private MenuBar menuBar;
    private TextArea textArea;
    private ComboBox Doors;
    private ComboBox removeMonster;
    private ComboBox monsterList;
    private ListView listView;
    private FileChooser fileChooser;
    private DBConnection connection;

    /**
     * @param assignedStage the main stage
     * @throws Exception any exceptions
     */
    @Override
    public void start(Stage assignedStage) throws Exception {
        /*Initializing instance variables */
        if (theController == null) {
            theController = new Controller(this);
        }
        fileChooser = new FileChooser();
        connection = new DBConnection();
        menuBar = new MenuBar();
        newText();
        createPopUp();
        Doors = newComboBox();
        primaryStage = assignedStage;
        /*Border Panes have  top, left, right, center and bottom sections */
        root = setUpRoot();
        Scene scene = new Scene(root, 1240, 720);
        primaryStage.setTitle("DnD Level Generator");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private BorderPane setUpRoot() {
        BorderPane temp = new BorderPane();
        BorderPane rightSection = new BorderPane();
        Menu menu = setUpMenu("File", "Save File", "Load File");
        menuBar.getMenus().addAll(menu);
        temp.setTop(menuBar);
        temp.setCenter(textArea);
        temp.setMargin(textArea, new Insets(30, 20, 30, 20));

        temp.setRight(rightSection);
        rightSection.setTop(Doors);
        temp.setMargin(Doors, new Insets(30, 30, 0, 0));
        Node buttons = setButtonPanel();  //separate method for the bottom section
        rightSection.setBottom(buttons);

        ObservableList<String> list = FXCollections.observableArrayList(theController.getNameList());
        listView = createListView(list);

        temp.setLeft(listView);
        temp.setMargin(listView, new Insets(30, 0, 30, 30));

        return temp;
    }

    private Menu setUpMenu(String text, String... items) {
        Menu menu = new Menu(text);

        for (int i = 0; i < items.length; i++) {
            MenuItem item = new MenuItem(items[i]);
            item.setOnAction(e -> {
                System.out.println(item.getText() + " selected");
                if (item.getText().equals("Save File")) {
                    theController.saveFile(fileChooser, primaryStage);
                } else if (item.getText().equals("Load File")) {
                    theController.openFile(fileChooser, primaryStage);
                }
            });
            menu.getItems().add(item);
        }
        return menu;
    }

    /**
     * @param text optional text
     */
    public void newText(String... text) {
        if (text.length > 0 && text != null) {
            textArea = new TextArea(text[0]);
        } else {
            textArea = new TextArea();
        }

        textArea.setPrefColumnCount(600);
        textArea.setPrefRowCount(600);
        textArea.setEditable(false);
        textArea.setWrapText(true);
    }

    /**
     * @param text text to set
     */
    public void setTextArea(String text) {
        textArea.setText(text);
    }

    private Node setButtonPanel() {
        /*this method should be broken down into even smaller methods, maybe one per button*/
        GridPane temp = new GridPane();
        temp.setStyle("-fx-padding: 30");
        temp.setAlignment(Pos.BOTTOM_CENTER);

        Button showButton = createButton("Edit", "-fx-background-color: #FFFFFF; ");
        showButton.setOnAction((ActionEvent event) -> {
            editPop.show();
            if (listView.getSelectionModel().getSelectedItem() != null) {
                removeMonster.setItems(FXCollections.observableList(theController.getMonsterName(listView.getSelectionModel().getSelectedItem().toString())));
            }
        });
        temp.add(showButton, 1, 0);
        temp.setMargin(showButton, new Insets(0, 10, 0, 0));
        return temp;

    }

    private ListView createListView(ObservableList<String> spaces) {
        listView = new ListView<String>(spaces);
        listView.setMaxWidth(200);
        listView.setMaxHeight(700);
        listView.setOnMouseClicked((MouseEvent event) -> {
            theController.listClick(listView);
        });

        return listView;
    }

    private ComboBox newComboBox() {
        ArrayList<String> start = new ArrayList<>();
        start.add("Please select a chamber or Passage first");
        ComboBox temp = new ComboBox(FXCollections.observableList(start));
        temp.setVisibleRowCount(5);
        temp.setMinWidth(100);
        temp.setOnHidden(event -> {
            theController.doorClick(temp, listView);
        });
        return temp;
    }

    /**
     * @param items sets the combo box with items
     */
    public void setComboBox(ArrayList<String>... items) {
        if (items != null) {
            Doors.setItems(FXCollections.observableList(items[0]));
            Doors.getSelectionModel().selectFirst();
        }
    }


    private Button createButton(String text, String format) {
        Button btn = new Button();
        btn.setText(text);
        return btn;
    }

    private void createPopUp() {
        editPop = new Stage();
        BorderPane rootPane = new BorderPane();
        rootPane.setMinHeight(200);
        rootPane.setMinWidth(300);
        rootPane.setTop(setTopPane());
        rootPane.setCenter(setMiddlePane());
        rootPane.setBottom(setBottomPane());
        Scene popup = new Scene(rootPane);
        editPop.setScene(popup);
        editPop.setResizable(false);
    }

    private BorderPane setTopPane() {
        BorderPane topPane = new BorderPane();
        Label addMonsterLabel = new Label("Add monster");
        addMonsterLabel.setAlignment(Pos.CENTER_LEFT);
        addMonsterLabel.setPadding(new Insets(0, 0, 10, 0));
        topPane.setTop(addMonsterLabel);
        ArrayList<String> temp = connection.getAllMonsters();
//        if (temp.size() < 1) {
//            connection.addMonster("kobold", "5", "50", "Tiny little humanoids with sharp spears");
//            connection.addMonster("orc", "3", "6", "Smelly creatures with blunt teeth");
//            connection.addMonster("snake", "6", "36", "Why did it have to be snakes");
//            connection.addMonster("golem", "1", "1", "Stone Golem");
//        }
        monsterList = new ComboBox(FXCollections.observableList(getMonsterNames(connection.getAllMonsters())));
        monsterList.setMinWidth(200);
        topPane.setLeft(monsterList);
        Button add = new Button("add");
        add.setOnAction((ActionEvent e) -> {
            theController.addMonsterDescription(connection, monsterList.getSelectionModel().getSelectedItem().toString(), listView.getSelectionModel().getSelectedItem().toString());
        });
        topPane.setRight(add);
        topPane.setPadding(new Insets(20));
        return topPane;
    }

    private BorderPane setMiddlePane() {
        BorderPane middlePane = new BorderPane();
        middlePane.setPadding(new Insets(20));
        Label removeMonsterLabel = new Label("Remove monster");
        removeMonsterLabel.setAlignment(Pos.CENTER_LEFT);
        removeMonsterLabel.setPadding(new Insets(0, 0, 10, 0));
        middlePane.setTop(removeMonsterLabel);
        ArrayList<String> temp = new ArrayList<>();
        temp.add("Please select a chamber or passage");
        removeMonster = new ComboBox(FXCollections.observableList(temp));
        removeMonster.getSelectionModel().selectFirst();
        middlePane.setLeft(removeMonster);
        Button remove = new Button("remove");
        remove.setOnAction((ActionEvent e) -> {
            theController.removeMonster(removeMonster.getSelectionModel().getSelectedItem().toString(), listView.getSelectionModel().getSelectedItem().toString());
            monsterList.setItems(FXCollections.observableList(connection.getAllMonsters()));
        });
        middlePane.setRight(remove);
        return middlePane;
    }

    private BorderPane setBottomPane() {
        BorderPane bottomPane = new BorderPane();
        Label newMonsterLabel = new Label("Create a monster");
        newMonsterLabel.setAlignment(Pos.CENTER_LEFT);
        newMonsterLabel.setPadding(new Insets(0, 0, 0, 20));
        bottomPane.setTop(newMonsterLabel);
        FlowPane texts = new FlowPane(Orientation.HORIZONTAL, 5, 1);
        texts.setPadding(new Insets(20));
        TextField name = new TextField();
        name.setPromptText("name");
        TextField upper = new TextField();
        upper.setPromptText("upper bounds");
        TextField lower = new TextField();
        lower.setPromptText("lower bounds");
        TextField description = new TextField();
        description.setPromptText("description");
        Button add = new Button("add");
        add.setOnAction((ActionEvent e) -> {
            theController.addNewMonster(connection, name.getText(), upper.getText(), lower.getText(), description.getText());
        });
        texts.getChildren().addAll(name, upper, lower, description, add);
        bottomPane.setCenter(texts);
        return bottomPane;
    }

    private ArrayList<String> getMonsterNames(ArrayList<String> allMonsters) {
        ArrayList<String> names = new ArrayList<>();

        for (String s : allMonsters) {
            names.add(s.split(",")[0]);
        }
        return names;
    }

    /**
     * @param args main arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
