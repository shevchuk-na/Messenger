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

public class ProfileController implements Controller, Initializable {

    private MainController mainController;

    public ProfileController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private TextField nameField;

    @FXML
    private Label warningLbl;

    @FXML
    private Button saveBtn;

    @FXML
    void saveProfileSettings(ActionEvent event) {
        String name = nameField.getText();
        if (!name.equals("")) {
            if (!name.equals(SettingsUtil.getInstance().getName())) {
                SaveConstants result = SettingsUtil.getInstance().saveProfile(name);
                switch (result) {
                    case Success:
                        if(mainController.isInitialized()) {
                            mainController.updateName();
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
        } else {
            warningLbl.setText("Enter name!");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (SettingsUtil.getInstance().getName() != null) {
            nameField.setText(SettingsUtil.getInstance().getName());
        }
    }
}
