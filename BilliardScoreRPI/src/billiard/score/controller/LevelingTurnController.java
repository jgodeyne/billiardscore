/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.score.controller;

import billiard.model.Player;
import billiard.common.ControllerInterface;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jean
 */
public class LevelingTurnController implements Initializable, ControllerInterface {
    private Stage primaryStage;
    
    @FXML
    private Text text_player;
    @FXML
    private Text text_points;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    public void setData(Player player, int points) {
        text_player.setText(player.getName());
        text_points.setText("("+points+")");
    }

    @FXML
    private void btnOkOnAction(ActionEvent event) {
        primaryStage.hide();
    }

    @Override
    public void initController(Stage stage) {
        this.primaryStage = stage;
    }
}
