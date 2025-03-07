package controllers;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;
import javafx.event.ActionEvent;

public class MainController {

    @FXML
    private Button startButton;

    @FXML
    public void handleStartButton(ActionEvent event) throws IOException {
        Parent gameRoot = FXMLLoader.load(getClass().getResource("/views/game_view.fxml"));

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(gameRoot, 800, 800));
        stage.show();
    }

    @FXML
    public void initialize() {
    }
}


