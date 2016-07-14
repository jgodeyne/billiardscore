/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.common.controller;

import billiard.common.ControllerInterface;
import billiard.common.PermittedValues;
import billiard.common.hazelcast.SyncManager;
import billiard.model.ScoreboardManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jean
 */
public class SelectScoreboardController implements Initializable, ControllerInterface {
    private Stage primaryStage;
    private PermittedValues.Action action;

    @FXML
    private ListView<String> list;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        list.setItems(FXCollections.observableArrayList(ScoreboardManager.getInstance().listScoreboards()));
    }    

    @FXML
    private void onActionButtonSelect(ActionEvent event) {
        action = PermittedValues.Action.SELECT;
        primaryStage.hide();
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

    public PermittedValues.Action getAction() {
        return action;
    }
    
    public ArrayList<String> getSelection() {
        String[] tmp = list.getSelectionModel().getSelectedItems().toArray(new String[0]);
        return new ArrayList<>(Arrays.asList(tmp));
    }
}
