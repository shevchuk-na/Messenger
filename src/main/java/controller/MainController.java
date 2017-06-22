package controller;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import model.MessengerModel;
import model.data.Message;
import model.net.Client;
import model.utils.DateUtil;
import model.utils.SettingsUtil;
import view.ViewFactory;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Controller, Initializable {

    private MessengerModel model;
    private boolean isInitialized = false;

    @FXML
    private TextArea inputArea;

    @FXML
    private TableView<Client> clientsTable;

    @FXML
    private TableColumn<Client, String> nameColumn;

    @FXML
    private ScrollPane messageScrollPane;

    @FXML
    private Button sendBtn;

    @FXML
    private TextFlow messageArea;

    @FXML
    private ImageView settingsImageBtn;

    @FXML
    private ImageView profileImageBtn;

    public MainController() {

    }

    @FXML
    void sendBtnAction(ActionEvent event) {
        model.sendMessage(inputArea.getText(), clientsTable.getSelectionModel().getSelectedItem());
        event.consume();
        inputArea.clear();
        inputArea.requestFocus();
    }

    @FXML
    void profileMenuMousePressed(MouseEvent event) {
        profileImageBtn.setImage(new Image("images/profile2.png"));
    }

    @FXML
    void profileMenuMouseReleased(MouseEvent event) {
        profileImageBtn.setImage(new Image("images/profile.png"));
        showProfileWindow();
    }

    @FXML
    void settingsMenuMousePressed(MouseEvent event) {
        settingsImageBtn.setImage(new Image("images/settings2.png"));
    }

    @FXML
    void settingsMenuMouseReleased(MouseEvent event) {
        settingsImageBtn.setImage(new Image("images/settings.png"));
        showSettingsWindow();
    }

    @FXML
    void settingsMenuAction(MouseEvent event) {

    }

    @FXML
    void processDragDrop(DragEvent event) {
        System.out.println("New Drag!");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        checkSettingsAndGo();
    }

    public void checkSettingsAndGo() {
        if (!SettingsUtil.getInstance().profileLoaded()) {
            showProfileWindow();
        } else if (!SettingsUtil.getInstance().settingsLoaded()) {
            showSettingsWindow();
        } else {
            go();
        }
    }

    private void showSettingsWindow() {
        Scene scene = ViewFactory.defaultFactory.getSettingsScene();
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.show();
    }

    private void showProfileWindow() {
        Scene scene = ViewFactory.defaultFactory.getProfileScene();
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.show();
    }

    void go() {
        isInitialized = true;
        model = new MessengerModel();
        model.getMessages().addListener((ListChangeListener<Message>) c -> {
            while (c.next()) {
                List<Message> addedMessages = new ArrayList<>(c.getAddedSubList());
                for (Message message : addedMessages) {
                    Node text = constructMessage(message);
                    Platform.runLater(() -> messageArea.getChildren().add(text));
                }
            }
        });
        model.getClients().addListener((ListChangeListener<Client>) c -> {
            while (c.next()) {
                clientsTable.setItems(model.getClients());
            }
        });
        inputArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (!inputArea.getText().equals("")) {
                    sendBtn.fire();
                }
                event.consume();
            }
        });
        inputArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (inputArea.getText().equals("")) {
                sendBtn.setDisable(true);
            } else {
                sendBtn.setDisable(false);
            }
        });
        messageArea.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            } else {
                event.consume();
            }
        });
        messageArea.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                success = true;
                for (File file : db.getFiles()) {
                    model.transferFile(file, clientsTable.getSelectionModel().getSelectedItem());
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
        messageScrollPane.vvalueProperty().bind(messageArea.heightProperty());
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        clientsTable.setItems(model.getClients());
        clientsTable.setRowFactory(param -> {
            final TableRow<Client> row = new TableRow<>();
            row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                final int index = row.getIndex();
                if (index >= 0 && index < clientsTable.getItems().size() && clientsTable.getSelectionModel().isSelected(index)) {
                    clientsTable.getSelectionModel().clearSelection();
                    sendBtn.setText("Send");
                    event.consume();
                }
            });
            return row;
        });
        clientsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                sendBtn.setText("Send private to " + clientsTable.getSelectionModel().getSelectedItem().getName());
            }
        });

        model.initialize();
        inputArea.requestFocus();
    }

    private Node constructMessage(Message message) {
        String timestamp = DateUtil.sdf.format(message.getTimestamp());
        if(message.getReceiver() != null){
            Text text = new Text(timestamp + " - " + message.getSender() + " (to " + message.getReceiver() + "): " + message.getContent() + "\n");
            text.setFill(Color.PURPLE);
            return text;
        }
        return new Text(timestamp + " - " + message.getSender() + ": " + message.getContent() + "\n");
    }

    boolean isInitialized() {
        return isInitialized;
    }

    private void showSystemMessage(String message) {
        Text text = new Text(message + "\n");
        text.setFill(Color.BLUE);
        Platform.runLater(() -> messageArea.getChildren().add(text));
    }

    public void updateName() {
        model.updateName();
    }

    public void restartModel() {
        model.shutdown();
        model = new MessengerModel();
        model.initialize();
    }
}


