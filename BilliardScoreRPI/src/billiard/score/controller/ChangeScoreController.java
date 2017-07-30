/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.score.controller;

import billiard.model.Match;
import billiard.model.Player;
import billiard.model.ScoreChange;
import billiard.common.ControllerInterface;
import billiard.common.FormValidation;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jean
 */
public class ChangeScoreController implements Initializable, ControllerInterface {
    @FXML
    private ChoiceBox<String> cbPlayer;
    @FXML
    private TextField tfInning;
    @FXML
    private TextField tfPoints;
    
    private Stage primaryStage;
    private Match match;
    private int currentInning;
    private ScoreChange scoreChange;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void btnOKOnAction(ActionEvent event) {
        if(validForm()) {
            Player player = cbPlayer.getSelectionModel().getSelectedIndex()==0?match.getPlayer1():match.getPlayer2();
            int inning = Integer.parseInt(tfInning.getText());
            int points = Integer.parseInt(tfPoints.getText());
            scoreChange = new ScoreChange(player, inning, points);
            primaryStage.hide();
        }
    }

    @FXML
    private void btnCancelOnAction(ActionEvent event) {
        primaryStage.hide();
    }

    @Override
    public void initController(Stage stage) {
        this.primaryStage = stage;
    }
    
    public void setData(Match match, int currentInning) {
        this.match = match;
        this.currentInning = currentInning;
        cbPlayer.getItems().add(match.getPlayer1().getName());
        cbPlayer.getItems().add(match.getPlayer2().getName());
    }
    
    public ScoreChange getScoreChange() {
        return scoreChange;
    }

    private boolean validForm() {
        boolean valid = true;
        valid &= FormValidation.validateChoiceBox(cbPlayer)
                && FormValidation.validateTextField(tfInning, true)
                && FormValidation.validateTextField(tfPoints, true);
        if(valid) {
            int inning = Integer.parseInt(tfInning.getText());
            if (inning > currentInning) {
                tfInning.setStyle("-fx-background-color:red");
                valid&= false;
            } else {
                tfInning.setStyle("-fx-background-color:white");
                valid&=true;
            }
        }
        return valid;
    }
}
