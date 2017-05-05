package view;

import controller.Controller;
import controller.MainController;
import controller.ProfileController;
import controller.SettingsController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javax.naming.OperationNotSupportedException;
import java.io.IOException;

public class ViewFactory {

    public static ViewFactory defaultFactory = new ViewFactory();
    private static boolean mainViewInitialized = false;

    private final String DEFAULT_CSS = "css/style.css";
    private final String MAIN_FXML = "fxml/MainLayout.fxml";
    private final String SETTINGS_FXML = "fxml/SettingsLayout.fxml";
    private final String PROFILE_FXML = "fxml/ProfileLayout.fxml";
    private MainController mainController;

    public Scene getMainScene() throws OperationNotSupportedException {
        if(!mainViewInitialized){
            mainController = new MainController();
            mainViewInitialized = true;
            return initializeScene(MAIN_FXML, mainController);
        } else {
            throw new OperationNotSupportedException("Main scene already initialized");
        }
    }

    private Scene initializeScene(String fxmlPath, Controller controller) {
        FXMLLoader loader;
        Parent parent;
        Scene scene;
        try {
            loader = new FXMLLoader(getClass().getClassLoader().getResource(fxmlPath));
            loader.setController(controller);
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        scene = new Scene(parent);
        scene.getStylesheets().add(getClass().getClassLoader().getResource(DEFAULT_CSS).toExternalForm());
        return scene;
    }


    public Scene getSettingsScene() {
        SettingsController settingsController = new SettingsController(mainController);
        return initializeScene(SETTINGS_FXML, settingsController);
    }

    public Scene getProfileScene(){
        ProfileController profileController = new ProfileController(mainController);
        return initializeScene(PROFILE_FXML, profileController);
    }
}
