/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package billiard.score.controller;

import billiard.model.IndividualCompetition;
import billiard.model.Player;
import billiard.common.PermittedValues;
import billiard.common.ControllerInterface;
import billiard.common.FormValidation;
import billiard.data.LeagueDataManager;
import billiard.data.LeagueItem;
import billiard.data.MemberItem;
import billiard.data.TSPItem;
import billiard.data.IndividualCompetitionDataManager;
import billiard.data.IndividualCompetitionItem;
import billiard.data.PlayerItem;
import billiard.common.AppProperties;
import billiard.common.CommonDialogs;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jean
 */
public class NewIndividualMatchController implements Initializable, ControllerInterface {
    private static final Logger LOGGER = Logger.getLogger(NewIndividualMatchController.class.getName());
    private IndividualCompetition individualCompetition;
    private Stage primaryStage;
    private MemberItem selectedMember1;
    private MemberItem selectedMember2;
    private IndividualCompetitionDataManager competitionManager;
    private IndividualCompetitionItem selectedCompetition;
    private LeagueItem league;
    private LeagueItem defaultLeague;
    
    @FXML
    private TextField player_1_name;
    @FXML
    private TextField player_1_club;
    @FXML
    private TextField player_1_tsp;
    @FXML
    private TextField player_2_name;
    @FXML
    private TextField player_2_club;
    @FXML
    private TextField player_2_tsp;
    @FXML
    private Button ok;
    @FXML
    private Button cancel;
    @FXML
    private TextField player_1_licentie;
    @FXML
    private TextField player_2_licentie;
    @FXML
    private TextField matchNumber;
    @FXML
    private TextField group;
    @FXML
    private TextField tableNumber;
    @FXML
    private ChoiceBox<String> tableFormat;
    @FXML
    private ChoiceBox<String> discipline;
    @FXML
    private ComboBox<String> cbCompetition;
    @FXML
    private Button btnSearchMember1;
    @FXML
    private Button btnSearchMember2;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            competitionManager = IndividualCompetitionDataManager.getInstance(AppProperties.getInstance().getDataPath());
            cbCompetition.getItems().addAll(competitionManager.getCompetitionNames());
            cbCompetition.getSelectionModel().clearSelection();
        } catch (Exception ex) {
            Logger.getLogger(NewIndividualMatchController.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.discipline.getItems().addAll(new ArrayList( Arrays.asList(PermittedValues.DISCIPLINES)));
        this.discipline.getSelectionModel().selectFirst();
        this.tableFormat.getItems().addAll(new ArrayList(Arrays.asList(PermittedValues.TABLE_FORMAT)));
        this.tableFormat.getSelectionModel().select(1);

        try {
            String leagueName = AppProperties.getInstance().getDefaultLeague();
            LeagueDataManager ldmgr;
            ldmgr = LeagueDataManager.getInstance(AppProperties.getInstance().getDataPath());
            league = ldmgr.getLeague(leagueName);
            defaultLeague = league;
        } catch (Exception ex) {
            Logger.getLogger(NewIndividualMatchController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(null==league) {
            btnSearchMember1.setDisable(true);
            btnSearchMember2.setDisable(true);
        }

        player_1_licentie.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue && !player_1_licentie.getText().isEmpty()) {
                    try {
                        lookupPlayer1();
                    } catch (Exception ex) {
                        LOGGER.log(Level.SEVERE, "Player1LicLooseFocus => Exception: {0}", new Object[] {ex});
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        player_2_licentie.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue && !player_2_licentie.getText().isEmpty()) {
                    try {
                        lookupPlayer2();
                    } catch (Exception ex) {
                        LOGGER.log(Level.SEVERE, "Player2LicLooseFocus => Exception: {0}", new Object[] {ex});
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        this.discipline.valueProperty().addListener(new ChangeListener<String>() {
             @Override public void changed(ObservableValue ov, String t, String t1) {
                 try {
                     if(null!=selectedMember1) {
                         disciplinePlayer1Changed();
                     }
                     if(null!=selectedMember2) {
                         disciplinePlayer2Changed();
                     }
                 } catch (Exception ex) {
                     LOGGER.log(Level.SEVERE, "DisciplineValueChanged => Exception: {0}", ex);
                     throw new RuntimeException(ex);
                 }
             }    
         });

        this.cbCompetition.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue ov, String t, String t1) {
                try {
                    if(!t1.isEmpty()) {
                        String competitionName = t1;
                        selectedCompetition = competitionManager.getCompetition(competitionName);
                        if(null!=selectedCompetition) {
                            discipline.getSelectionModel().select(selectedCompetition.getDiscipline());
                            tableFormat.getSelectionModel().select(selectedCompetition.getTableFormat());
                            //
                            String leagueName;
                            leagueName = selectedCompetition.getLeague();
                            if(!leagueName.isEmpty()) {
                                LeagueDataManager ldmgr = LeagueDataManager.getInstance(AppProperties.getInstance().getDataPath());
                                league = ldmgr.getLeague(leagueName);
                                if(null==league) {
                                    league = defaultLeague;
                                }
                            }
                        }
                    } else {
                        selectedCompetition = null;
                    }
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "DisciplineValueChanged => Exception: {0}", ex);
                    throw new RuntimeException(ex);
                }
             }    
         });
    }    

    @FXML
    private void onActionOK(ActionEvent event) throws IOException {
        // Input validation
        boolean valid = false;
        
        if (validForm()) {


            Player player1 = new Player(this.player_1_name.getText()
                    ,this.player_1_club.getText()
                    ,new Integer(this.player_1_tsp.getText())
                    , discipline.getValue()
                    , this.player_1_licentie.getText());
            Player player2 = new Player(this.player_2_name.getText()
                    ,this.player_2_club.getText()
                    ,new Integer(this.player_2_tsp.getText())
                    , discipline.getValue()
                    , this.player_2_licentie.getText());
                        
            individualCompetition = new IndividualCompetition(this.cbCompetition.getValue()
                    , tableFormat.getValue(), discipline.getValue(),matchNumber.getText()
                    ,player1,player2);
            individualCompetition.setGroup(group.getText());
            individualCompetition.setTableNumber(tableNumber.getText());

            primaryStage.hide();
        }
    }

    @FXML
    private void onActionCancel(ActionEvent event) {
        primaryStage.hide();
    }

    public IndividualCompetition getIndividualCompetition() {
        return individualCompetition;
    }

    @Override
    public void initController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private boolean validForm() {
        boolean valid=FormValidation.validateComboBox(cbCompetition)
                && FormValidation.validateTextField(player_1_name)
                && FormValidation.validateTextField(player_1_licentie)
                && FormValidation.validateTextField(player_1_tsp, true)
                && FormValidation.validateTextField(player_1_club)
                && FormValidation.validateTextField(player_2_club)
                && FormValidation.validateTextField(player_2_licentie)
                && FormValidation.validateTextField(player_2_name)
                && FormValidation.validateTextField(player_2_tsp, true);
        
        return valid;
    }

    private void lookupPlayer1() throws Exception {
        String lic = player_1_licentie.getText();
        LOGGER.log(Level.FINEST, "lookupPlayer1 - lic={0}", lic);
        
        if(selectedCompetition!=null) {
            PlayerItem player = selectedCompetition.getPlayer(lic);
            if(null!= player) {
                player_1_name.setText(player.getName());
                player_1_club.setText(player.getClub());
                player_1_tsp.setText(player.getTsp());
                return;
            }
        }
        
        selectedMember1 = null;
        if(league!=null) {
            LOGGER.log(Level.FINEST, "lookupPlayer1 - (leagueItem!=null): {0}", league);
            selectedMember1 = league.getMember(lic);
            if(selectedMember1!=null) {
                LOGGER.log(Level.FINEST, "lookupPlayer1 - (member!=null): {0}", selectedMember1.getName());
                populatePlayer1FromMember();
            }
        }
    }
    
    private void populatePlayer1FromMember() {
        if(null!=selectedMember1) {
            player_1_licentie.setText(selectedMember1.getLic());
            player_1_name.setText(selectedMember1.getName());
            player_1_club.setText(league.getClub(selectedMember1.getClubLic()).getName());
            disciplinePlayer1Changed();
        }
        
    }

    private void disciplinePlayer1Changed() {
        TSPItem tspItem = null;
        if(!discipline.getSelectionModel().isEmpty()) {
            tspItem = selectedMember1.getTsp(discipline.getValue());
        }
        if(tspItem!=null) {
            player_1_tsp.setText(tspItem.getTsp());
        } else {
            player_1_tsp.setText("");
        }
    }
    
    private void lookupPlayer2() throws Exception {
        String lic = player_2_licentie.getText();
        LOGGER.log(Level.FINEST, "lookupPlayer2 - lic={0}", lic);
        
        if(selectedCompetition!=null) {
            PlayerItem player = selectedCompetition.getPlayer(lic);
            if(null!= player) {
                player_2_name.setText(player.getName());
                player_2_club.setText(player.getClub());
                player_2_tsp.setText(player.getTsp());
                return;
            }
        }

        selectedMember2 = null;
        
        if(league!=null) {
            LOGGER.log(Level.FINEST, "lookupPlayer2 - (leagueItem!=null): {0}", league);
            selectedMember2 = league.getMember(lic);
            if(selectedMember2!=null) {
                LOGGER.log(Level.FINEST, "lookupPlayer2 - (member!=null): {0}", selectedMember2.getName());
                populatePlayer2FromMember();
            }
        }
    }

    private void populatePlayer2FromMember() {
        if(null!=selectedMember2) {
            player_2_licentie.setText(selectedMember2.getLic());
            player_2_name.setText(selectedMember2.getName());
            player_2_club.setText(league.getClub(selectedMember2.getClubLic()).getName());
            disciplinePlayer2Changed();
        }
    }
    private void disciplinePlayer2Changed() {
        TSPItem tspItem = null;
        if(!discipline.getSelectionModel().isEmpty()) {
            tspItem = selectedMember2.getTsp(discipline.getValue());
        }
        if(tspItem!=null) {
            player_2_tsp.setText(tspItem.getTsp());
        } else {
            player_2_tsp.setText("");
        }
    }

    @FXML
    private void onActionSearchMember1(ActionEvent event) throws Exception {
        selectedMember1 = CommonDialogs.searchMember(league);
        populatePlayer1FromMember();
    }

    @FXML
    private void onActionSearchMember2(ActionEvent event) throws Exception {
        selectedMember2 = CommonDialogs.searchMember(league);
        populatePlayer2FromMember();
    }
}
