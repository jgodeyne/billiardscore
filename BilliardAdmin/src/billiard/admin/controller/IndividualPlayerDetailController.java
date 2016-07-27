/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.admin.controller;

import billiard.common.CommonDialogs;
import billiard.common.ControllerInterface;
import billiard.common.FormValidation;
import billiard.common.PermittedValues;
import billiard.data.LeagueItem;
import billiard.data.MemberItem;
import billiard.data.PlayerItem;
import billiard.data.TSPItem;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jean
 */
public class IndividualPlayerDetailController implements Initializable, ControllerInterface {
    private static final Logger LOGGER = Logger.getLogger(IndividualPlayerDetailController.class.getName());
    private Stage primaryStage;
    private PermittedValues.Action action;
    private PlayerItem player;
    private LeagueItem league;
    private MemberItem selectedMember;

    @FXML
    private TextField tfLicence;
    @FXML
    private TextField tfName;
    @FXML
    private ComboBox<String> cbDiscipline;
    @FXML
    private TextField tfTsp;
    @FXML
    private TextField tfClub;
    @FXML
    private Button btnSearchMember;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            btnSearchMember.setDisable(true);
            cbDiscipline.getItems().addAll(new ArrayList( Arrays.asList(PermittedValues.DISCIPLINES)));
            cbDiscipline.getSelectionModel().clearSelection();

            tfLicence.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    String lic = tfLicence.getText();
                    if(!lic.isEmpty()) {
                        try {
                            lookupPlayer(lic);
                        } catch (Exception ex) {
                            LOGGER.severe(Arrays.toString(ex.getStackTrace()));
                            throw new RuntimeException(ex);
                        }
                    } else {
                        selectedMember = null;
                    }
                }
            });

            this.cbDiscipline.valueProperty().addListener(new ChangeListener<String>() {
                 @Override public void changed(ObservableValue ov, String t, String t1) {
                     try {
                         disciplineChanged();
                     } catch (Exception ex) {
                        LOGGER.severe(Arrays.toString(ex.getStackTrace()));
                        throw new RuntimeException(ex);
                     }
                 }    
             });
        } catch (Exception ex) {
            LOGGER.severe(Arrays.toString(ex.getStackTrace()));
            CommonDialogs.showException(ex);
        }
    }    

    @Override
    public void initController(Stage stage) {
        primaryStage = stage;
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

    public void setPlayer(PlayerItem player) {
        this.player = player;
        tfName.textProperty().bindBidirectional(player.getNameProp());
        tfLicence.textProperty().bindBidirectional(player.getLicProp());
        tfTsp.textProperty().bindBidirectional(player.getTspProp());
        tfClub.textProperty().bindBidirectional(player.getClubProp());
        
        cbDiscipline.valueProperty().bindBidirectional(player.getDisciplineProp());
    }

    public void setLeague(LeagueItem league) {
        this.league = league;
        if(null!=this.league) {
            btnSearchMember.setDisable(false);
        }
    }

    public PermittedValues.Action getAction() {
        return action;
    }

    public PlayerItem getPlayer() {
        return this.player;
    }    
    
    private boolean validForm() {
        boolean result = FormValidation.validateTextField(tfLicence)
                & FormValidation.validateTextField(tfName);
        return result;
    }

    private void lookupPlayer(String lic) throws Exception {
        if(null!=league) {
            selectedMember = league.getMember(lic);
            populateMember();
        }
    }

    private void populateMember() {
        if(selectedMember!=null) {
            tfLicence.setText(selectedMember.getLic());
            tfName.setText(selectedMember.getName());
            tfClub.setText(league.getClub(selectedMember.getClubLic()).getName());
        }
        if(!cbDiscipline.getValue().isEmpty()) {
            disciplineChanged();
        }
    }
    private void disciplineChanged() {
        if(null!=selectedMember) {
            if(!selectedMember.getTsps().isEmpty()) {
                TSPItem tspItem = null;
                String discipline = cbDiscipline.getValue();
                if(!discipline.isEmpty()) {
                    tspItem = selectedMember.getTsp(discipline);
                }
                if(tspItem!=null) {
                    tfTsp.setText(tspItem.getTsp());
                }
            }
        }
    }

    @FXML
    private void onActionButtonSelectMember(ActionEvent event) throws Exception {
        selectedMember = CommonDialogs.searchMember(league);
        populateMember();
    }
}
