import javafx.scene.Scene;
import javafx.stage.Stage;
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

    public enum SceneType {
        LOGIN,
        MAIN_MENU,
        ACCOUNTS,
        COURSES,
        ENROLLMENT,
        GRADES,
        STATISTICS,
        ASSIGNMENTS
    }

    public SceneFactory (Stage stage) {
        this.stage = stage;
    }

    public Scene createScene(SceneType type){

        return switch (type) {
            case LOGIN -> new Scene(new LoginView(), SCENE_WIDTH,SCENE_HEIGHT);
            case MAIN_MENU -> new Scene(new MainMenuView(), SCENE_WIDTH,SCENE_HEIGHT);
            case ACCOUNTS -> new Scene(new AccountsView(), SCENE_WIDTH,SCENE_HEIGHT);
            case COURSES -> new Scene(new CoursesView(), SCENE_WIDTH,SCENE_HEIGHT);
            case ENROLLMENT -> new Scene(new EnrollmentView(), SCENE_WIDTH,SCENE_HEIGHT);
            case GRADES -> new Scene(new GradesView(), SCENE_WIDTH,SCENE_HEIGHT);
            case STATISTICS -> new Scene(new StatisticsView(), SCENE_WIDTH,SCENE_HEIGHT);
            case ASSIGNMENTS -> new Scene(new AssignmentsView(), SCENE_WIDTH,SCENE_HEIGHT);
        };
    }

    public void showScene (SceneType type) {
        stage.setScene(createScene(type));
    }


}
