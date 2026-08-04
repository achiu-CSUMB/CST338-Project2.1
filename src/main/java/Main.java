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
        sceneFactory.showScene(SceneFactory.SceneType.LOGIN);

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
