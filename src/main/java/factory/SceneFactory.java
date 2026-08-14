package factory;

import controller.MenuController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.User;

/**
 * Author: Alvin Chiu
 * Created: 7/30/2026
 * Current version: V1.0 - 7/30/2026
 * Description: Generates base scenes to build on.
 * This will standardize our scenes a little.
 * It will also reduce workload by not having all of us create a new scene from scratch.
 */

public class SceneFactory {
    private static final int SCENE_WIDTH = 600;
    private static final int SCENE_HEIGHT = 500;
    private final Stage stage;
    private User currentUser;

    public enum SceneType {
        LOGIN,
        REGISTER,
        MAIN_MENU,
        ACCOUNTS,
        COURSES,
        ENROLLMENT,
        //GRADES,
        //STATISTICS,
        ASSIGNMENTS
    }

    public SceneFactory (Stage stage) {
        this.stage = stage;
    }

    public SceneFactory(Stage stage, User user) {
        this.stage = stage;
        this.currentUser = user;
    }

    public Scene createScene(SceneType type){

        return switch (type) {
            case LOGIN -> loadFxmlScene("/views/login-view.fxml");
            case REGISTER -> loadFxmlScene("/views/register-view.fxml");
            case MAIN_MENU -> createMenuScene();
            case ACCOUNTS -> loadFxmlScene("/views/accounts-view.fxml");
            case COURSES -> loadFxmlScene("/views/courses-view.fxml");
            case ENROLLMENT -> loadFxmlScene("/views/enrollment-view.fxml");
            //case GRADES -> new Scene(new GradesView(), SCENE_WIDTH,SCENE_HEIGHT);
            //case STATISTICS -> new Scene(new StatisticsView(), SCENE_WIDTH,SCENE_HEIGHT);
            case ASSIGNMENTS -> loadFxmlScene("/views/assignments-view.fxml");
        };
    }

    // Fixes the issue of buttons refusing to load you into a scene after you leave and try to go back via menu.
    private Scene createMenuScene() {
        try {
            FXMLLoader loader = new FXMLLoader(SceneFactory.class.getResource("/views/menu-view.fxml"));
            Parent root = loader.load();
            MenuController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            return new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        } catch(Exception e) {
            throw new RuntimeException("Cannot load menu",e);
        }
    }

    private Scene loadFxmlScene(String resourcePath){
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneFactory.class.getResource(resourcePath)
            );

            Parent root = loader.load();
            return new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Can't load FXML scene: " + resourcePath,
                    e
            );
        }
    }

    public void showScene (SceneType type) {
        stage.setScene(createScene(type));
    }


}
