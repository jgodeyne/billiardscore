/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.score.controller;

import billiard.common.ControllerInterface;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jean
 */
public class ScoreboardHelpController implements Initializable, ControllerInterface {
    private Stage primaryStage;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void pbSluitenOnAction(ActionEvent event) {
        primaryStage.hide();
    }

    @Override
    public void initController(Stage stage) {
        this.primaryStage = stage;
    }
    
}
