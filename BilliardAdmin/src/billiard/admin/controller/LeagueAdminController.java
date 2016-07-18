/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.admin.controller;

import billiard.common.ControllerInterface;
import billiard.common.PermittedValues;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jean
 */
public class LeagueAdminController implements Initializable, ControllerInterface {
    private Stage primaryStage;
    private String leagueName = "";
    private PermittedValues.Action action;

    @FXML
    private ListView<String> list;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnNew;
    @FXML
    private Button btnClose;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnExport;
    @FXML
    private Button btnSendData;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        list.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        list.getSelectionModel().selectedItemProperty()
            .addListener(new ChangeListener<String>() {
              @Override
              public void changed(ObservableValue<? extends String> observable,
                  String oldValue, String newValue) {
                  if(null==newValue) {
                      btnUpdate.setDisable(true);
                      btnDelete.setDisable(true);
                      btnExport.setDisable(true);
                      btnSendData.setDisable(true);
                  } else {
                      btnUpdate.setDisable(false);
                      btnDelete.setDisable(false);
                      btnExport.setDisable(false);
                      btnSendData.setDisable(false);
                  }
              }
            });
    }    

    @Override
    public void initController(Stage stage) {
        primaryStage = stage;
    }

    @FXML
    private void onActionButtonUpdate(ActionEvent event) {
        leagueName = list.getSelectionModel().getSelectedItem();
        action = PermittedValues.Action.EDIT;
        primaryStage.hide();
    }

    @FXML
    private void OnActionButtonDelete(ActionEvent event) {
        leagueName = list.getSelectionModel().getSelectedItem();
        action = PermittedValues.Action.DELETE;
        primaryStage.hide();
    }

    @FXML
    private void onActionButtonClose(ActionEvent event) {
        leagueName= "";
        action = PermittedValues.Action.CLOSE;
        primaryStage.hide();
    }

    @FXML
    private void onActionButtonNew(ActionEvent event) {
        leagueName="";
        action = PermittedValues.Action.NEW;
        primaryStage.hide();
    }

    public void setLeagues(ArrayList<String> leagues) {
        list.setItems(FXCollections.observableArrayList(leagues));
        list.getSelectionModel().clearSelection();
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        btnExport.setDisable(true);
    }
    
    public PermittedValues.Action getAction() {
        return action;
    }
    
    public String getSelectedLeagueName() {
        return leagueName;
    }

    @FXML
    private void OnActionButtonExport(ActionEvent event) {
        leagueName = list.getSelectionModel().getSelectedItem();
        action = PermittedValues.Action.EXPORT;
        primaryStage.hide();
    }

    @FXML
    private void onActionButtonImport(ActionEvent event) {
        action = PermittedValues.Action.IMPORT;
        primaryStage.hide();
    }

    @FXML
    private void onActionBtnSendData(ActionEvent event) {
        leagueName = list.getSelectionModel().getSelectedItem();
        action = PermittedValues.Action.SEND;
        primaryStage.hide();
   }
}
