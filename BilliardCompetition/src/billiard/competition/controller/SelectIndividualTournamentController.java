/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.competition.controller;

import billiard.common.ControllerInterface;
import billiard.model.IndividualTournament;
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
public class SelectIndividualTournamentController implements Initializable, ControllerInterface {

    private Stage primaryStage;
    private IndividualTournament selectedTournament;
    @FXML
    private ListView<IndividualTournament> lvTournaments;
    @FXML
    private Button btnSelect;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        selectedTournament = null;
        lvTournaments.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        btnSelect.setDisable(true);
    }

    public void setTournaments(ArrayList<IndividualTournament> tournament) {
        lvTournaments.setItems(FXCollections.observableArrayList(tournament));
        lvTournaments.getSelectionModel().selectFirst();
        btnSelect.setDisable(false);
    }

    public IndividualTournament getSelectedTournament() {
        return selectedTournament;
    }

    @Override
    public void initController(Stage stage) {
        this.primaryStage = stage;
    }

    @FXML
    private void btnSelect_OnAction(ActionEvent event) {
        selectedTournament=lvTournaments.getSelectionModel().getSelectedItem();
        this.primaryStage.hide();
    }

    @FXML
    private void btnCancel_OnAction(ActionEvent event) {
        primaryStage.hide();
    }

    @FXML
    private void lvTournaments_OnMouseClicked(MouseEvent event) {
        btnSelect.setDisable(false);
    }
    
}
