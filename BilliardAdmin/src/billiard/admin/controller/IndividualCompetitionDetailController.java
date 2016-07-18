/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.admin.controller;

import billiard.common.ControllerInterface;
import billiard.common.FormValidation;
import billiard.common.PermittedValues;
import billiard.data.IndividualCompetitionItem;
import billiard.data.LeagueDataManager;
import billiard.data.PlayerItem;
import java.net.URL;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jean
 */
public class IndividualCompetitionDetailController implements Initializable, ControllerInterface {
    private Stage primaryStage;
    private IndividualCompetitionItem competition;
    private PermittedValues.Action action;
    private PlayerItem selectedPlayer;

    @FXML
    private TableView<PlayerItem> tblPlayers;
    @FXML
    private TableColumn<PlayerItem, String> tcolName;
    @FXML
    private TableColumn<PlayerItem, String> tcolLic;
    @FXML
    private TableColumn<PlayerItem, String> tcolDiscipline;
    @FXML
    private TableColumn<PlayerItem, String> tcolTsp;
    @FXML
    private TableColumn<PlayerItem, String> tcolClub;
    @FXML
    private TextField tfName;
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
    private ChoiceBox<String> cbDiscipline;
    @FXML
    private ChoiceBox<String> cbLeague;
    @FXML
    private ChoiceBox<String> cbBilliardSize;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbDiscipline.getItems().addAll(PermittedValues.DISCIPLINES);
        cbBilliardSize.getItems().addAll(PermittedValues.TABLE_FORMAT);
        try {
            cbLeague.getItems().addAll(LeagueDataManager.getInstance().getLeagueNames());
            cbLeague.getSelectionModel().clearSelection();
        } catch (Exception ex) {
            Logger.getLogger(TeamCompetitionDetailController.class.getName()).log(Level.SEVERE, null, ex);
        }

        tblPlayers.getSelectionModel().selectedItemProperty()
            .addListener(new ChangeListener<PlayerItem>() {
              @Override
              public void changed(ObservableValue<? extends PlayerItem> observable,
                  PlayerItem oldValue, PlayerItem newValue) {
                  selectedPlayer = newValue;
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
    private void onActionButtonNew(ActionEvent event) {
        action = PermittedValues.Action.NEW;
        primaryStage.hide();
    }

    @FXML
    private void onActionButtonUpdate(ActionEvent event) {
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
        if(validForm()) {
            action = PermittedValues.Action.SAVE;
            primaryStage.hide();
        }
    }

    @FXML
    private void OnActionButtonCancel(ActionEvent event) {
        action = PermittedValues.Action.CANCEL;
        primaryStage.hide();
    }

    @Override
    public void initController(Stage stage) {
        primaryStage = stage;
    }

    public void setCompetition(IndividualCompetitionItem competition) {
        this.competition = competition;
        cbLeague.valueProperty().bindBidirectional(this.competition.getLeagueProp());
        tfName.textProperty().bindBidirectional(this.competition.getNameProp());
        tfContactName.textProperty().bindBidirectional(this.competition.getContactNameProp());
        tfContactEmail.textProperty().bindBidirectional(this.competition.getContactEmailProp());
        cbDiscipline.valueProperty().bindBidirectional(this.competition.getDisciplineProp());
        cbBilliardSize.valueProperty().bindBidirectional(this.competition.getTableFormatProp());
        tcolName.setCellValueFactory(cellData -> cellData.getValue().getNameProp());
        tcolLic.setCellValueFactory(cellData -> cellData.getValue().getLicProp());
        tcolDiscipline.setCellValueFactory(cellData -> cellData.getValue().getDisciplineProp());
        tcolTsp.setCellValueFactory(cellData -> cellData.getValue().getTspProp());
        tcolClub.setCellValueFactory(cellData -> cellData.getValue().getClubProp());
        tblPlayers.setItems(FXCollections.observableArrayList(this.competition.getPlayers().values()));
        if(null!=selectedPlayer) {
            tblPlayers.getSelectionModel().select(selectedPlayer);
        } else {
            tblPlayers.getSelectionModel().clearSelection();
        }
        
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
    }
        
    public PermittedValues.Action getAction() {
        return action;
    }
    
    public PlayerItem getSelectedPlayer() {
        return selectedPlayer;
    }

    private boolean validForm() {
        tfName.setText(tfName.getText().replace("/", "-"));
        boolean result = FormValidation.validateTextField(tfName);
        return result;
    }
}
