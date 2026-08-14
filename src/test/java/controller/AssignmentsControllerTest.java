package controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssignmentsControllerTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/views/assignments-view.fxml")
        );

        Scene scene = new Scene(loader.load());

        stage.setScene(scene);
        stage.show();
    }

    @Test
    void emptyFieldsShowValidationMessage() {
        Button addButton = lookup("#addButton").query();
        Label statusLabel = lookup("#statusLabel").query();

        Platform.runLater(addButton::fire);
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(
                "Required fields cannot be empty.",
                statusLabel.getText()
        );
    }
}
