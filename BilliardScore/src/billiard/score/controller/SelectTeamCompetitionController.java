/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.score.controller;

import billiard.model.TeamCompetition;
import billiard.common.ControllerInterface;
import billiard.common.PermittedValues;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jean
 */
public class SelectTeamCompetitionController implements Initializable, ControllerInterface {
    private Stage primaryStage;
    private ArrayList<TeamCompetition> competitions;
    private TeamCompetition selectedCompetition;
    private PermittedValues.Action action;

    @FXML
    private ListView<?> list;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        list.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }    

    @FXML
    private void onActionButtonSelect(ActionEvent event) {
        action = PermittedValues.Action.SELECT;
        selectedCompetition=competitions.get(list.getSelectionModel().getSelectedIndex());
        this.primaryStage.hide();
   }

    @FXML
    private void onActionButtonCancel(ActionEvent event) {
        action = PermittedValues.Action.CANCEL;
        this.primaryStage.hide();
    }

    @FXML
    private void OnActionButtonNew(ActionEvent event) {
        action = PermittedValues.Action.NEW;
        this.primaryStage.hide();
    }

    @Override
    public void initController(Stage stage) {
        primaryStage = stage;
    }

    public void setCompetitions(ArrayList<TeamCompetition> competitions) {
        this.competitions = competitions;
        ArrayList competitionList = new ArrayList();
        for (TeamCompetition competition: competitions){
            competitionList.add(competition.getName()+": " + competition.getTeam1().getName()+ " - " + competition.getTeam2().getName());
        }
        list.setItems(FXCollections.observableArrayList(competitionList));
        list.getSelectionModel().selectFirst();
    }

    public TeamCompetition getSelectedCompetition() {
        return selectedCompetition;
    }  

    public PermittedValues.Action getAction() {
        return action;
    }

    @FXML
    private void onActionButtonDelete(ActionEvent event) {
        action = PermittedValues.Action.DELETE;
        selectedCompetition=competitions.get(list.getSelectionModel().getSelectedIndex());
        this.primaryStage.hide();
    }
}
