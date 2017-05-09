package controller;

import com.sun.glass.ui.Window;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.utils.SaveConstants;
import model.utils.SettingsUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Controller, Initializable {

    private MainController mainController;
    @FXML
    private TextField localPortField;
    @FXML
    private TextField remotePortField;
    @FXML
    private Button saveBtn;
    @FXML
    private Label warningLbl;
    @FXML
    private Button saveAndConnectBtn;

    public SettingsController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    void saveBtnAction(ActionEvent event) {
        try {
            if (settingsChanged()) {
                int localPort = Integer.parseInt(localPortField.getText());
                int remotePort = Integer.parseInt(remotePortField.getText());
                SaveConstants result = SettingsUtil.getInstance().saveSettings(localPort, remotePort);
                switch (result) {
                    case Failure:
                        warningLbl.setText("Save error");
                        break;
                    case Success:
                        Window.getFocusedWindow().close();
                        break;
                }
            } else {
                Window.getFocusedWindow().close();
            }
        } catch (NumberFormatException e) {
            warningLbl.setText("Incorrect data!");
        }
    }

    @FXML
    void saveAndConnectBtnAction(ActionEvent event) {
        try {
            if (settingsChanged()) {
                int localPort = Integer.parseInt(localPortField.getText());
                int remotePort = Integer.parseInt(remotePortField.getText());
                SaveConstants result = SettingsUtil.getInstance().saveSettings(localPort, remotePort);
                switch (result) {
                    case Success:
                        closeAndStart();
                        break;
                    case Failure:
                        warningLbl.setText("Save error");
                        break;
                }
            } else {
                closeAndStart();
            }

        } catch (NumberFormatException e) {
            warningLbl.setText("Incorrect data!");
        }
    }

    private void closeAndStart() {
        if (mainController.isInitialized()) {
            mainController.restartModel();
        } else {
            mainController.checkSettingsAndGo();
        }
        Window.getFocusedWindow().close();
    }

    private boolean settingsChanged() {
        int localPort = Integer.parseInt(localPortField.getText());
        int remotePort = Integer.parseInt(remotePortField.getText());
        return (localPort != SettingsUtil.getInstance().getLocalPort() || remotePort != SettingsUtil.getInstance().getRemotePort());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        warningLbl.setText("");
        if (SettingsUtil.getInstance().getLocalPort() != 0) {
            localPortField.setText(String.valueOf(SettingsUtil.getInstance().getLocalPort()));
        }
        if (SettingsUtil.getInstance().getRemotePort() != 0) {
            remotePortField.setText(String.valueOf(SettingsUtil.getInstance().getRemotePort()));
        }
    }

}
