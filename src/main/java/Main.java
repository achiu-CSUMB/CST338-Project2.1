import database.DatabaseManager;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Author: John Ly
 * Date: 8/3/2026
 * Description:
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Grade and Assignment Tracker");

        SceneFactory sceneFactory = new SceneFactory(primaryStage);

        // Change name of SceneType below to access a specific scene (COURSES, LOGIN, etc.)

        // Note: Make sure to run the file with Gradle.
        sceneFactory.showScene(SceneFactory.SceneType.ENROLLMENT);

        primaryStage.show();
    }

    @Override
    public void stop() {
        DatabaseManager.getInstance().close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
