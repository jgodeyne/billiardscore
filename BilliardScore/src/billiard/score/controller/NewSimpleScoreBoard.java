/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package billiard.score.controller;

import billiard.model.Match;
import billiard.model.Player;
import billiard.common.ControllerInterface;
import billiard.common.FormValidation;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jean
 */
public class NewSimpleScoreBoard implements Initializable, ControllerInterface {
    @FXML
    private TextField player_1_name;
    @FXML
    private TextField player_1_tsp;
    @FXML
    private TextField player_2_name;
    @FXML
    private TextField player_2_tsp;
    @FXML
    private Button ok;
    @FXML
    private Button cancel;
    
    private Stage primaryStage;
    private Match match;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    @FXML
    private void onActionOK(ActionEvent event) throws IOException {
        // Input validation
        if (validForm()) {
            Player player1 = new Player(this.player_1_name.getText(),new Integer(this.player_1_tsp.getText()));
            Player player2 = new Player(this.player_2_name.getText(),new Integer(this.player_2_tsp.getText()));
            match = new Match("0", "", player1, player2);
            primaryStage.hide();
        }
    }

    @FXML
    private void onActionCancel(ActionEvent event) {
        primaryStage.hide();
    }
    
    private boolean validForm() {
        boolean result=FormValidation.validateTextField(player_1_name)
                && FormValidation.validateTextField(player_1_tsp, true)
                && FormValidation.validateTextField(player_2_name)
                && FormValidation.validateTextField(player_2_tsp, true);
        
        return result;
    }

    @Override
    public void initController(Stage stage) {
        this.primaryStage = stage;
    }

    public Match getMatch() {
        return match;
    }
}
