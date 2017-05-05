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

    public SettingsController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private TextField incomingPortField;

    @FXML
    private Button saveBtn;

    @FXML
    private Label warningLbl;

    @FXML
    void saveBtnAction(ActionEvent event) {
        try {
            int inPort = Integer.parseInt(incomingPortField.getText());
            if (inPort != SettingsUtil.getInstance().getIncomingPort()) {
                SaveConstants result = SettingsUtil.getInstance().saveSettings(inPort);
                switch (result) {
                    case Success:
                        if(mainController.isInitialized()) {
                            mainController.restartModel();
                        } else {
                            mainController.checkSettingsAndGo();
                        }
                        break;
                    case Failure:
                        warningLbl.setText("Save error");
                        break;
                }
            }
            Window.getFocusedWindow().close();
        } catch (NumberFormatException e) {
            warningLbl.setText("Incorrect data!");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        warningLbl.setText("");
        if (SettingsUtil.getInstance().getIncomingPort() != 0) {
            incomingPortField.setText(String.valueOf(SettingsUtil.getInstance().getIncomingPort()));
        }
    }

}
