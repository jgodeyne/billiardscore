/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.competition.controller;

import billiard.common.ControllerInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jean
 */
public class SelectScoreboardController implements Initializable, ControllerInterface {

    private Stage primaryStage;
    private String selectedScoreboard;
    @FXML
    private ListView<String> lvScoreboards;
    @FXML
    private Button btnSelect;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        selectedScoreboard = null;
        lvScoreboards.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        btnSelect.setDisable(true);
    }

    public void setScoreboards(ArrayList<String> scoreboards) {
        lvScoreboards.setItems(FXCollections.observableArrayList(scoreboards));
        lvScoreboards.getSelectionModel().selectFirst();
        btnSelect.setDisable(false);
    }

    public String getSelectedScoreboard() {
        return selectedScoreboard;
    }

    @Override
    public void initController(Stage stage) {
        this.primaryStage = stage;
    }

    @FXML
    private void btnSelect_OnAction(ActionEvent event) {
        selectedScoreboard=lvScoreboards.getSelectionModel().getSelectedItem();
        this.primaryStage.hide();
    }

    @FXML
    private void btnCancel_OnAction(ActionEvent event) {
        primaryStage.hide();
    }

    @FXML
    private void lvScoreboards_OnMouseClicked(MouseEvent event) {
        btnSelect.setDisable(false);
    }
    
}
