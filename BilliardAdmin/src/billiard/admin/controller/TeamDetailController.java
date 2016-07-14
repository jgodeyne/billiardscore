/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.admin.controller;

import billiard.common.ControllerInterface;
import billiard.common.FormValidation;
import billiard.common.PermittedValues;
import billiard.data.PlayerItem;
import billiard.data.TeamItem;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jean
 */
public class TeamDetailController implements Initializable, ControllerInterface {
    private Stage primaryStage;
    private TeamItem team;
    private PermittedValues.Action action;
    private PlayerItem selectedPlayer;
    
    @FXML
    private TableView<PlayerItem> tblPlayers;
    @FXML
    private TableColumn<PlayerItem, String> tcolOrder;
    @FXML
    private TableColumn<PlayerItem, String> tcolName;
    @FXML
    private TableColumn<PlayerItem, String> tcolLic;
    @FXML
    private TableColumn<PlayerItem, String> tcolDiscipline;
    @FXML
    private TableColumn<PlayerItem, String> tcolTsp;
    @FXML
    private TextField tfName;
    @FXML
    private Button btnNew;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnDelete;
    @FXML
    private TextField tfFixedPlayers;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
    private void onActionButtonOK(ActionEvent event) {
        if (validForm()) {
            action = PermittedValues.Action.OK;
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

    public void setTeam(TeamItem team) {
        this.team = team;
        tfName.textProperty().bindBidirectional(this.team.getNameProp());
        tfFixedPlayers.textProperty().bindBidirectional(this.team.getFixedPlayersProp());
        tcolOrder.setCellValueFactory(cellData -> cellData.getValue().getOrderProp());
        tcolName.setCellValueFactory(cellData -> cellData.getValue().getNameProp());
        tcolLic.setCellValueFactory(cellData -> cellData.getValue().getLicProp());
        tcolDiscipline.setCellValueFactory(cellData -> cellData.getValue().getDisciplineProp());
        tcolTsp.setCellValueFactory(cellData -> cellData.getValue().getTspProp());
        tblPlayers.setItems(FXCollections.observableArrayList(this.team.getPlayers().values()));
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
        boolean result = FormValidation.validateTextField(tfName);
        return result;
    }

}
