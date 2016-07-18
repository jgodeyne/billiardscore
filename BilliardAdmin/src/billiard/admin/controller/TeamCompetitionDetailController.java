/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.admin.controller;

import billiard.common.ControllerInterface;
import billiard.common.FormValidation;
import billiard.common.PermittedValues;
import billiard.data.LeagueDataManager;
import billiard.data.TeamCompetitionItem;
import billiard.model.pointssystem.PointSystemFactory;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jean
 */
public class TeamCompetitionDetailController implements Initializable, ControllerInterface {
    private Stage primaryStage;
    private TeamCompetitionItem competition;
    private PermittedValues.Action action;
    private String selectedTeamName;

    @FXML
    private ChoiceBox<String> cbDiscipline;
    @FXML
    private TextField tfGroup;
    @FXML
    private ChoiceBox<String> cbTableFormat;
    @FXML
    private ChoiceBox<String> cbPointSystem;
    @FXML
    private TextField tfName;
    @FXML
    private ListView<String> lvTeams;
    @FXML
    private TextField tfNbrOfPlayers;
    @FXML
    private TextField tfDisciplinePlayers;
    @FXML
    private TextField tfTspPlayers;
    @FXML
    private Button btnNew;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnDelete;   
    @FXML
    private TextField tfContactName;
    @FXML
    private TextField tfContactEmail;
    @FXML
    private ChoiceBox<String> cbLeague;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbDiscipline.getItems().addAll(Arrays.asList(PermittedValues.DISCIPLINES));
        cbDiscipline.getSelectionModel().clearSelection();
        cbTableFormat.getItems().addAll(Arrays.asList(PermittedValues.TABLE_FORMAT));
        cbTableFormat.getSelectionModel().clearSelection();
        cbPointSystem.getItems().addAll(PointSystemFactory.PointSystem.stringValues());
        cbPointSystem.getSelectionModel().selectFirst();
        try {
            cbLeague.getItems().addAll(LeagueDataManager.getInstance().getLeagueNames());
            cbLeague.getSelectionModel().clearSelection();
        } catch (Exception ex) {
            Logger.getLogger(TeamCompetitionDetailController.class.getName()).log(Level.SEVERE, null, ex);
        }        

        lvTeams.getSelectionModel().selectedItemProperty()
            .addListener(new ChangeListener<String>() {
              @Override
              public void changed(ObservableValue<? extends String> observable,
                  String oldValue, String newValue) {
                  selectedTeamName = newValue;
                  if(null==newValue) {
                      btnUpdate.setDisable(true);
                      btnDelete.setDisable(true);
                  } else {
                      btnUpdate.setDisable(false);
                      btnDelete.setDisable(false);
                  }
              }
            });
    }    

    @FXML
    private void OnActionButtonNew(ActionEvent event) {
        action = PermittedValues.Action.NEW;
        primaryStage.hide();
    }

    @FXML
    private void onActionButtonEdit(ActionEvent event) {
        action = PermittedValues.Action.EDIT;
        primaryStage.hide();
    }

    @FXML
    private void onActionButtonDelete(ActionEvent event) {
        action = PermittedValues.Action.DELETE;
        primaryStage.hide();
    }

    @FXML
    private void onActionButtonSave(ActionEvent event) {
        if (validForm()) {
            action = PermittedValues.Action.SAVE;
            primaryStage.hide();
        }
    }

    @FXML
    private void onActionButtonCancel(ActionEvent event) {
        action = PermittedValues.Action.CANCEL;
        primaryStage.hide();
    }

    @Override
    public void initController(Stage stage) {
        primaryStage = stage;
    }
 
    public void setCompetition(TeamCompetitionItem competition) {
        this.competition = competition;
        cbLeague.valueProperty().bindBidirectional(this.competition.getLeagueProp());
        tfName.textProperty().bindBidirectional(this.competition.getNameProp());
        cbDiscipline.valueProperty().bindBidirectional(this.competition.getDisciplineProp());
        cbPointSystem.valueProperty().bindBidirectional(this.competition.getPointSystemProp());
        cbTableFormat.valueProperty().bindBidirectional(this.competition.getTableFormatProp());
        tfGroup.textProperty().bindBidirectional(this.competition.getGroupProp());
        tfNbrOfPlayers.textProperty().bindBidirectional(this.competition.getNbrOfPlayersProp());
        tfTspPlayers.textProperty().bindBidirectional(this.competition.getTspPlayersProp());
        tfDisciplinePlayers.textProperty().bindBidirectional(this.competition.getDisciplinePlayersProp());
        tfContactName.textProperty().bindBidirectional(this.competition.getContactNameProp());
        tfContactEmail.textProperty().bindBidirectional(this.competition.getContactEmailProp());
        lvTeams.setItems(FXCollections.observableArrayList(this.competition.getTeamNames()));
        if(null!=selectedTeamName) {
            lvTeams.getSelectionModel().select(selectedTeamName);
        } else {
            lvTeams.getSelectionModel().clearSelection();
        }
        
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
    }
    
    public TeamCompetitionItem getCompetition() {
        return competition;
    }
    
    public PermittedValues.Action getAction() {
        return action;
    }
    
    public String getSelectedTeam() {
        return lvTeams.getSelectionModel().getSelectedItem();
    }
    
    private boolean validForm() {
        tfName.setText(tfName.getText().replace("/", "-"));
        boolean result = FormValidation.validateTextField(tfName);
        return result;
    }
}
