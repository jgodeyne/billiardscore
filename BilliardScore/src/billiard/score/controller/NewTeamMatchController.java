/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.score.controller;

import billiard.common.AppProperties;
import billiard.data.TeamCompetitionDataManager;
import billiard.data.PlayerItem;
import billiard.model.Player;
import billiard.model.Team;
import billiard.model.TeamCompetition;
import billiard.common.PermittedValues;
import billiard.common.ControllerInterface;
import billiard.common.FormValidation;
import billiard.model.pointssystem.PointSystemFactory;
import billiard.data.TeamCompetitionItem;
import billiard.data.LeagueDataManager;
import billiard.data.LeagueItem;
import billiard.data.MemberItem;
import billiard.data.TSPItem;
import billiard.data.TeamItem;
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
public class NewTeamMatchController implements Initializable, ControllerInterface {
    private static final Logger LOGGER = Logger.getLogger(NewTeamMatchController.class.getName());
    
    @FXML
    private ComboBox<String> competition;
    @FXML
    private ChoiceBox<String> discipline;
    @FXML
    private TextField matchNumber;
    @FXML
    private ChoiceBox<String> tableFormat;
    @FXML
    private ChoiceBox<String> cbPointSystem;
    @FXML
    private TextField group;
    @FXML
    public ComboBox<String> team_1;
    @FXML
    public TextField player_11_licentie;
    @FXML
    public ComboBox<String> player_11_name;
    @FXML
    public TextField player_11_tsp;
    @FXML
    public TextField player_12_licentie;
    @FXML
    public ComboBox<String> player_12_name;
    @FXML
    public TextField player_12_tsp;
    @FXML
    public ChoiceBox<String> player_11_discipline;
    @FXML
    public ChoiceBox<String> player_12_discipline;
    @FXML
    public TextField player_13_licentie;
    @FXML
    public ComboBox<String> player_13_name;
    @FXML
    public TextField player_13_tsp;
    @FXML
    public TextField player_14_licentie;
    @FXML
    public ComboBox<String> player_14_name;
    @FXML
    public TextField player_14_tsp;
    @FXML
    public ChoiceBox<String> player_13_discipline;
    @FXML
    public ChoiceBox<String> player_14_discipline;
    @FXML
    public ComboBox<String> team_2;
    @FXML
    public TextField player_21_licentie;
    @FXML
    public TextField player_22_licentie;
    @FXML
    public TextField player_23_licentie;
    @FXML
    public TextField player_24_licentie;
    @FXML
    public ComboBox<String> player_21_name;
    @FXML
    public ComboBox<String> player_22_name;
    @FXML
    public ComboBox<String> player_23_name;
    @FXML
    public ComboBox<String> player_24_name;
    @FXML
    public TextField player_21_tsp;
    @FXML
    public TextField player_22_tsp;
    @FXML
    public TextField player_23_tsp;
    @FXML
    public TextField player_24_tsp;
    @FXML
    public ChoiceBox<String> player_21_discipline;
    @FXML
    public ChoiceBox<String> player_22_discipline;
    @FXML
    public ChoiceBox<String> player_23_discipline;
    @FXML
    public ChoiceBox<String> player_24_discipline;
    @FXML
    private Button ok;
    @FXML
    private Button cancel;
    
    private Stage primaryStage;
    private TeamCompetition teamCompetition;
    private TeamCompetitionDataManager mgr;
    private TeamCompetitionItem compItem;
    //private final int[] idxTeam = new int[3];
    private String league = "";
    private PermittedValues.Mode mode;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            this.competition.getItems().add("");
            this.discipline.getItems().addAll(Arrays.asList(PermittedValues.DISCIPLINES));
            this.discipline.getSelectionModel().clearSelection();
            this.tableFormat.getItems().addAll(Arrays.asList(PermittedValues.TABLE_FORMAT));
            this.tableFormat.getSelectionModel().select(1);
            this.cbPointSystem.getItems().addAll(PointSystemFactory.PointSystem.stringValues());
            this.cbPointSystem.getSelectionModel().selectFirst();

            for (int i = 1; i < 3; i++) {
                for (int j = 1; j < 5; j++) {
                    ChoiceBox cb = getChoiceBoxPlayerDiscipline(i, j);
                    cb.getItems().addAll(new ArrayList( Arrays.asList(PermittedValues.DISCIPLINES)));
                    cb.getSelectionModel().clearSelection();
                }
            }

            mgr = TeamCompetitionDataManager.getInstance();
            if (!mgr.getCompetitionNames().isEmpty()) {
                this.competition.getItems().addAll(mgr.getCompetitionNames());
            }

            this.competition.valueProperty().addListener(new ChangeListener<String>() {
                 @Override public void changed(ObservableValue ov, String t, String t1) {
                     try {
                         competitionValueChanged();
                     } catch (Exception ex) {
                         LOGGER.log(Level.SEVERE, "CompetitionValueChanges => Exception: {0}", ex);
                         throw new RuntimeException(ex);
                     }
                 }    
             });

            this.discipline.valueProperty().addListener(new ChangeListener<String>() {
                 @Override public void changed(ObservableValue ov, String t, String t1) {
                     try {
                         disciplineChanged();
                     } catch (Exception ex) {
                         LOGGER.log(Level.SEVERE, "DisciplineValueChanged => Exception: {0}", ex);
                         throw new RuntimeException(ex);
                     }
                 }    
             });

            for (int i = 1; i < 3; i++) {
                int intTeam = i;
                ComboBox cbTeam = getComboBoxTeam(intTeam);
                cbTeam.valueProperty().addListener(new ChangeListener<String>() {
                    @Override public void changed(ObservableValue ov, String t, String t1) {
                        try {
                            teamValueChanged(intTeam);
                        } catch (Exception ex) {
                            LOGGER.log(Level.SEVERE, "TeamValueChanged => Team: {0} - Exception: {1}", new Object[] {intTeam, ex});
                            throw new RuntimeException(ex);
                        }
                    }
                });
                for (int j = 1; j < 5; j++) {
                    int intPlayer = j;
                    ComboBox cbPlayer = getComboboxPlayerName(intTeam, intPlayer);
                    cbPlayer.valueProperty().addListener(new ChangeListener<String>() {
                        @Override public void changed(ObservableValue ov, String t, String t1) {
                            try {
                                playerValueChanged(intTeam, intPlayer);
                            } catch (Exception ex) {
                                LOGGER.log(Level.SEVERE, "PlayerValueChanged => Team: {0} - Player: {1} - Exception: {2}", new Object[] {intTeam, intPlayer, ex});
                                throw new RuntimeException(ex);
                            }
                        }
                    });
                    TextField tfLic = getTextFieldPlayerLic(intTeam, intPlayer);
                    tfLic.focusedProperty().addListener(new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                            if(!newValue && !tfLic.getText().isEmpty()) {
                                try {
                                    lookupPlayer(intTeam, intPlayer);
                                } catch (Exception ex) {
                                    LOGGER.log(Level.SEVERE, "PlayerLicLooseFocus => Team: {0} - Player: {1} - Exception: {2}", new Object[] {intTeam, intPlayer, ex});
                                    throw new RuntimeException(ex);
                                }
                            }
                        }
                    });
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "initialize => Exception: {0}", ex);
            throw new RuntimeException(ex);
        }
    }    

    @FXML
    private void onActionOK(ActionEvent event) {
        // Input validation
        boolean valid = false;
        
        if (validForm()) {
            
            ArrayList<Player> playersT1 = new ArrayList<>();
            
            Player player11 = new Player(this.player_11_name.getValue()
                    ,this.team_1.getValue()
                    ,new Integer(this.player_11_tsp.getText())
                    ,(!discipline.getSelectionModel().isEmpty()?discipline.getValue():player_11_discipline.getValue())
                    ,this.player_11_licentie.getText());
            playersT1.add(player11);
            
            Player player12 = new Player(this.player_12_name.getValue()
                    ,this.team_1.getValue()
                    ,new Integer(this.player_12_tsp.getText())
                    ,(!discipline.getSelectionModel().isEmpty()?discipline.getValue():player_12_discipline.getValue())
                    ,this.player_12_licentie.getText());
            playersT1.add(player12);

            if (!player_13_licentie.getText().isEmpty()) {
                Player player13 = new Player(this.player_13_name.getValue()
                        ,this.team_1.getValue()
                        ,new Integer(this.player_13_tsp.getText())
                        ,(!discipline.getSelectionModel().isEmpty()?discipline.getValue():player_13_discipline.getValue())
                        ,this.player_13_licentie.getText());
                playersT1.add(player13);

                if (!player_14_licentie.getText().isEmpty()) {
                    Player player14 = new Player(this.player_14_name.getValue()
                            ,this.team_1.getValue()
                            ,new Integer(this.player_14_tsp.getText())
                            ,(!discipline.getSelectionModel().isEmpty()?discipline.getValue():player_14_discipline.getValue())
                            ,this.player_14_licentie.getText());
                    playersT1.add(player14);
                }
            }
            Team teamOne = new Team(team_1.getValue(), playersT1);

            ArrayList<Player> playersT2 = new ArrayList<>();
            
            Player player21 = new Player(this.player_21_name.getValue()
                    ,this.team_2.getValue()
                    ,new Integer(this.player_21_tsp.getText())
                    ,(!discipline.getSelectionModel().isEmpty()?discipline.getValue():player_21_discipline.getValue())
                    ,this.player_21_licentie.getText());
            playersT2.add(player21);
            
            Player player22 = new Player(this.player_22_name.getValue()
                    ,this.team_2.getValue()
                    ,new Integer(this.player_22_tsp.getText())
                    ,(!discipline.getSelectionModel().isEmpty()?discipline.getValue():player_22_discipline.getValue())
                    ,this.player_22_licentie.getText());
            playersT2.add(player22);

            if (!player_23_licentie.getText().isEmpty()) {
                Player player23 = new Player(this.player_23_name.getValue()
                        ,this.team_2.getValue()
                        ,new Integer(this.player_23_tsp.getText())
                        ,(!discipline.getSelectionModel().isEmpty()?discipline.getValue():player_23_discipline.getValue())
                        ,this.player_23_licentie.getText());
                playersT2.add(player23);

                if (!player_24_licentie.getText().isEmpty()) {
                    Player player24 = new Player(this.player_24_name.getValue()
                            ,this.team_2.getValue()
                            ,new Integer(this.player_24_tsp.getText())
                            ,(!discipline.getSelectionModel().isEmpty()?discipline.getValue():player_24_discipline.getValue())
                            ,this.player_24_licentie.getText());
                    playersT2.add(player24);
                }
            }
            Team teamTwo = new Team(team_2.getValue(), playersT2);
            
            String strDiscipline = discipline.getSelectionModel().isEmpty()?"":discipline.getValue();
            teamCompetition = new TeamCompetition(this.competition.getValue(), strDiscipline, tableFormat.getValue(), teamOne, teamTwo);
            teamCompetition.setPointsSystem(cbPointSystem.getValue());
            teamCompetition.setGroup(group.getText());
            teamCompetition.setNumber(matchNumber.getText());
            if (compItem!=null) {
                teamCompetition.setLeagueName(compItem.getLeague());
                teamCompetition.setCompetitionItemName(compItem.getName());
            }
            
            primaryStage.hide();
        }
    }

    @FXML
    private void onActionCancel(ActionEvent event) {
        this.primaryStage.hide();
    }
    
    private void competitionValueChanged() throws Exception {
        LOGGER.log(Level.FINEST, "competitionValueChanged => Start");
        // Index selected competition
        String competitionName = competition.getSelectionModel().getSelectedItem();
        int idxCompetition = competition.getSelectionModel().getSelectedIndex();
        // Label of the selected competition or "" if nothing selected
        String sval = (idxCompetition<0?"":competition.getItems().get(idxCompetition));
        // Value of the entry field
        String val = competition.getValue();
        LOGGER.log(Level.FINEST, "competitionValueChanged => {0}, {1}, {2}", new Object[] {idxCompetition, sval, val});
        if (idxCompetition > 0 && sval.equals(val)) {
            LOGGER.log(Level.FINEST, "competitionValueChanged => (idxCompetition!=-1 && sval.equals(val))");
            //idxCompetition--;
            discipline.getSelectionModel().clearSelection();
            cbPointSystem.getSelectionModel().clearSelection();
            compItem = mgr.getCompetition(competitionName);
            if(!compItem.getDiscipline().isEmpty()) {
                LOGGER.log(Level.FINEST, "competitionValueChanged => (!compItem.getDiscipline().isEmpty())");
                discipline.getSelectionModel().select(compItem.getDiscipline());
            } else {
                discipline.setValue("");
            }
            if(!compItem.getTableFormat().isEmpty()) {
                LOGGER.log(Level.FINEST, "competitionValueChanged => (!compItem.getTableFormat().isEmpty())");
                tableFormat.getSelectionModel().select(compItem.getTableFormat());
            } else {
                tableFormat.getSelectionModel().selectFirst();
            }
            if(!compItem.getPointsSystem().isEmpty()) {
                LOGGER.log(Level.FINEST, "competitionValueChanged => (!compItem.getPointsSystem().isEmpty())");
                cbPointSystem.getSelectionModel().select(compItem.getPointsSystem());
            } else {
                cbPointSystem.getSelectionModel().selectFirst();
            }
            if(!compItem.getGroup().isEmpty()) {
                LOGGER.log(Level.FINEST, "competitionValueChanged => (!compItem.getGroup().isEmpty())");
                group.setText(compItem.getGroup());
            } else {
                group.setText("");
            }
            if(compItem.getDiscipline().isEmpty()) {
                for (int i = 1; i < 5; i++) {
                    int intPlayer = i;
                    ChoiceBox<String> cbDiscipline1 = getChoiceBoxPlayerDiscipline(1, intPlayer);
                    cbDiscipline1.getSelectionModel().clearSelection();
                    cbDiscipline1.setValue("");
                    ChoiceBox<String> cbDiscipline2 = getChoiceBoxPlayerDiscipline(2, intPlayer);
                    cbDiscipline2.getSelectionModel().clearSelection();
                    cbDiscipline2.setValue("");
                }
                String[] discs = compItem.getDisciplinePlayers().split("[:]");
                if(discs.length>0) {
                    LOGGER.log(Level.FINEST, "competitionValueChanged => (compItem.getDisciplinePlayers().length>0)");
                    for (int i = 0; i < discs.length; i++) {
                        String disc = discs[i];
                        int intPlayer = i+1;
                        ChoiceBox<String> cbDiscipline1 = getChoiceBoxPlayerDiscipline(1, intPlayer);
                        cbDiscipline1.getSelectionModel().select(disc);
                        ChoiceBox<String> cbDiscipline2 = getChoiceBoxPlayerDiscipline(2, intPlayer);
                        cbDiscipline2.getSelectionModel().select(disc);
                    }
                }
            }
            for (int i = 1; i < 5; i++) {
                int intPlayer = i;
                TextField tfTsp1 = getTextFieldPlayerTsp(1, intPlayer);
                tfTsp1.setText("");
                TextField tfTsp2 = getTextFieldPlayerTsp(2, intPlayer);
                tfTsp2.setText("");
            }
            String[] tsps = compItem.getTspPlayers().split("[:]");
            if(tsps.length>0) {
                LOGGER.log(Level.FINEST, "competitionValueChanged => (compItem.getTspPlayers().length>0)");
                for (int i = 0; i < tsps.length; i++) {
                    String tsp = tsps[i];
                    int intPlayer = i+1;
                    TextField tfTsp1 = getTextFieldPlayerTsp(1, intPlayer);
                    tfTsp1.setText(tsp);
                    TextField tfTsp2 = getTextFieldPlayerTsp(2, intPlayer);
                    tfTsp2.setText(tsp);
                }
            }
            league = compItem.getLeague();
            LOGGER.log(Level.FINEST, "competitionValueChanged => league: {0}", league);
            setEditModeTeams(PermittedValues.Mode.SELECT);
        } else {
            setEditModeTeams(PermittedValues.Mode.EDIT);
        }
        LOGGER.log(Level.FINEST, "competitionValueChanged => End");
    }
    
    private void teamValueChanged(int intTeam) throws Exception {
        LOGGER.log(Level.FINEST, "teamValueChanged intTeam => {0}", intTeam);
        ComboBox<String> cb = getComboBoxTeam(intTeam);
        String selectedTeamName = cb.getSelectionModel().getSelectedItem();

        String val = cb.getValue();
        LOGGER.log(Level.FINEST, "teamValueChanged => {0, {1}", new Object[] {selectedTeamName, val});
        PermittedValues.Mode editMode;
        if (null !=selectedTeamName && !selectedTeamName.isEmpty() && selectedTeamName.equals(val)) {
            editMode = PermittedValues.Mode.SELECT;
        } else {
            editMode = PermittedValues.Mode.EDIT;
        }
        setEditModePlayers(intTeam, editMode);
    }
    
    private void playerValueChanged(int intTeam, int intPlayer) throws Exception {
        ComboBox<String> cbName = getComboboxPlayerName(intTeam, intPlayer);
        String selectedPlayerName = cbName.getSelectionModel().getSelectedItem();
        String val = cbName.getValue();

        String teamName = getComboBoxTeam(intTeam).getValue();
        if(null!=compItem) {
            TeamItem team = compItem.getTeam(teamName);
            if(null != team) {
                if (null != selectedPlayerName && !selectedPlayerName.isEmpty() && selectedPlayerName.equals(val)) {
                    PlayerItem player = team.getPlayerByName(selectedPlayerName);
                    if(null != player) {
                        TextField tfLic = getTextFieldPlayerLic(intTeam, intPlayer);
                        tfLic.setText(player.getLic());
                        if(null!=compItem && compItem.getTspPlayers().isEmpty()) {
                            TextField tfTSP = getTextFieldPlayerTsp(intTeam, intPlayer);
                            if(!player.getTsp().isEmpty()) {
                                tfTSP.setText(player.getTsp());
                            } else {
                                tfTSP.setText("");
                            }
                            ChoiceBox<String> cbDiscipline = getChoiceBoxPlayerDiscipline(intTeam, intPlayer);
                            if(!player.getDiscipline().isEmpty()) {
                                cbDiscipline.getSelectionModel().select(player.getDiscipline());
                            } else {
                                cbDiscipline.getSelectionModel().clearSelection();
                            }
                        }
                    }
                } else {
                    TextField tfLic = getTextFieldPlayerLic(intTeam, intPlayer);
                    tfLic.setText("");
                    if(compItem.getTspPlayers().isEmpty()) {
                        TextField tfTSP = getTextFieldPlayerTsp(intTeam, intPlayer);
                        tfTSP.setText("");
                    }
                    if(compItem.getDiscipline().isEmpty()) {
                        ChoiceBox<String> cbDiscipline = getChoiceBoxPlayerDiscipline(intTeam, intPlayer);
                        cbDiscipline.getSelectionModel().clearSelection();
                    }
                }
            }
        }
    }
    
    private void setEditModeTeams(PermittedValues.Mode editMode) {
        this.team_1.getItems().clear();
        this.team_2.getItems().clear();
        this.team_1.setEditable(true);
        this.team_2.setEditable(true);
        if(editMode.equals(PermittedValues.Mode.SELECT)) {
            this.team_1.getItems().addAll(compItem.getTeamNames());
            this.team_2.getItems().addAll(compItem.getTeamNames());
        }
        this.team_1.setValue("");
        this.team_2.setValue("");
    }

    private void setEditModePlayers(int intTeam, PermittedValues.Mode editMode) throws Exception {
        if(editMode.equals(PermittedValues.Mode.EDIT)) {
            for (int intPlayer = 1; intPlayer < 5; intPlayer++) {
                TextField tfLic = getTextFieldPlayerLic(intTeam, intPlayer);
                tfLic.setText("");
                ComboBox<String> cbName = getComboboxPlayerName(intTeam, intPlayer);
                cbName.getItems().clear();
                cbName.setEditable(true);
                cbName.setValue("");
            }
        } else {
            String teamName = getComboBoxTeam(intTeam).getValue();
            if(null!=compItem) {
                TeamItem team = compItem.getTeam(teamName);
                for (int intPlayer = 1; intPlayer < 5; intPlayer++) {
                    TextField tfLic = getTextFieldPlayerLic(intTeam, intPlayer);
                    tfLic.setText("");
                    ComboBox<String> cbName = getComboboxPlayerName(intTeam, intPlayer);
                    cbName.getItems().clear();
                    cbName.getItems().addAll(team.getPlayerNames());
                    String[] fixedPlayers = team.getFixedPlayers().split("[:]");
                    if(fixedPlayers.length >= intPlayer) {
                        String playerOrder = fixedPlayers[intPlayer-1];
                        PlayerItem player = team.getPlayerByOrder(playerOrder);
                        if(null!=player) {
                            cbName.getSelectionModel().select(player.getName());
                        } else {
                            cbName.setValue("");
                        }
                    } else {
                        cbName.setValue("");
                    }
                }
            }
        }
    }

    private void disciplineChanged() throws Exception {
        LOGGER.log(Level.FINEST, "disciplineChanged");
        if (discipline.getSelectionModel().isEmpty()) {
            LOGGER.log(Level.FINEST, "disciplineChanged - nothing selected");
            for (int intTeam = 1; intTeam < 3; intTeam++) {
                for (int intPlayer = 1; intPlayer < 5; intPlayer++) {
                    ChoiceBox<String> cbDiscipline = getChoiceBoxPlayerDiscipline(intTeam, intPlayer);
                    cbDiscipline.getSelectionModel().clearSelection();
                    cbDiscipline.setValue("");
                }
            }
        }else{
            String disc = discipline.getSelectionModel().getSelectedItem();
            LOGGER.log(Level.FINEST, "disciplineChanged - {0} selected", disc);
            for (int intTeam = 1; intTeam < 3; intTeam++) {
                for (int intPlayer = 1; intPlayer < 5; intPlayer++) {
                    ChoiceBox<String> cbDiscipline = getChoiceBoxPlayerDiscipline(intTeam, intPlayer);
                    cbDiscipline.getSelectionModel().select(disc);
                }
            }
        }  
    }
    
    private boolean validForm() {
        boolean valid;
        
        valid=FormValidation.validateComboBox(competition)
                && FormValidation.validateTextField(matchNumber)
                && FormValidation.validateComboBox(team_1)
                && FormValidation.validateComboBox(team_2)
                && FormValidation.validateComboBox(player_11_name)
                && FormValidation.validateTextField(player_11_licentie)
                && FormValidation.validateTextField(player_11_tsp, true)
                && FormValidation.validateTextField(player_21_licentie)
                && FormValidation.validateComboBox(player_21_name)
                && FormValidation.validateTextField(player_21_tsp ,true)
                && FormValidation.validateComboBox(player_12_name)
                && FormValidation.validateTextField(player_12_licentie)
                && FormValidation.validateTextField(player_12_tsp, true)
                && FormValidation.validateTextField(player_22_licentie)
                && FormValidation.validateComboBox(player_22_name)
                && FormValidation.validateTextField(player_22_tsp, true);
        
        if(!player_13_licentie.getText().isEmpty()) {
            valid = valid
                && FormValidation.validateComboBox(player_13_name)
                && FormValidation.validateTextField(player_13_licentie)
                && FormValidation.validateTextField(player_13_tsp, true)
                && FormValidation.validateTextField(player_23_licentie)
                && FormValidation.validateComboBox(player_23_name)
                && FormValidation.validateTextField(player_23_tsp, true);

            if(!player_14_licentie.getText().isEmpty()) {
                valid = valid
                    && FormValidation.validateComboBox(player_14_name)
                    && FormValidation.validateTextField(player_14_licentie)
                    && FormValidation.validateTextField(player_14_tsp, true)
                    && FormValidation.validateTextField(player_24_licentie)
                    && FormValidation.validateComboBox(player_24_name)
                    && FormValidation.validateTextField(player_24_tsp, true);
            }
        }

        if (discipline.getSelectionModel().isEmpty()) {
            valid = valid
                    && FormValidation.validateChoiceBox(player_11_discipline)
                    && FormValidation.validateChoiceBox(player_21_discipline)
                    && FormValidation.validateChoiceBox(player_12_discipline)
                    && FormValidation.validateChoiceBox(player_22_discipline);
            if(!player_13_licentie.getText().isEmpty()) {
                valid = valid
                    && FormValidation.validateChoiceBox(player_13_discipline)
                    && FormValidation.validateChoiceBox(player_23_discipline);
                if(!player_14_licentie.getText().isEmpty()) {
                    valid = valid
                        && FormValidation.validateChoiceBox(player_14_discipline)
                        && FormValidation.validateChoiceBox(player_24_discipline);
                }
            }
        }
        
        return valid;
    }

    private void lookupPlayer(int intTeam, int intPlayer) throws Exception {
        LOGGER.log(Level.FINEST, "lookupPlayer - team: {0} player: {1}", new Object[] {intTeam, intPlayer});
        TextField tfLic = getTextFieldPlayerLic(intTeam, intPlayer);
        String lic = tfLic.getText();
 
        if(league.isEmpty()) {
            league = AppProperties.getInstance().getDefaultLeague();
        }
        
        LeagueDataManager ldmgr = LeagueDataManager.getInstance();
        LeagueItem leagueItem = ldmgr.getLeague(league);
        
        if(leagueItem!=null) {
            LOGGER.log(Level.FINEST, "lookupPlayer - (leagueItem!=null): {0}", league);
            MemberItem member = leagueItem.getMember(lic);
            if(member!=null) {
                LOGGER.log(Level.FINEST, "lookupPlayer - (member!=null): {0}", member.getName());
                ComboBox<String> cbName = getComboboxPlayerName(intTeam, intPlayer);
                cbName.getSelectionModel().select(member.getName());
                if(!member.getTsps().isEmpty()) {
                    LOGGER.log(Level.FINEST, "lookupPlayer - (!member.getTsps().isEmpty()): {0}", member.getTsps().size());
                    ChoiceBox<String> cbDiscipline = getChoiceBoxPlayerDiscipline(intTeam, intPlayer);
                    TSPItem tspItem = null;
                    if(compItem.getTspPlayers().isEmpty()) {
                        if(null!=compItem && !compItem.getDiscipline().isEmpty()) {
                            tspItem = member.getTsp(compItem.getDiscipline());
                            LOGGER.log(Level.FINEST, "lookupPlayer - (!compItem.getDiscipline().isEmpty(): {0}", tspItem.getTsp());
                        } else if(null!=cbDiscipline.getValue()) {
                            tspItem = member.getTsp(cbDiscipline.getSelectionModel().getSelectedItem());                        
                        }

                        if(tspItem!=null) {
                            TextField tfTSP = getTextFieldPlayerTsp(intTeam, intPlayer);
                            tfTSP.setText(tspItem.getTsp());
                            cbDiscipline.getSelectionModel().select(tspItem.getDiscipline());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void initController(Stage stage) {
        this.primaryStage = stage;
    }

    public TeamCompetition getTeamCompetition() {
        return teamCompetition;
    }

    private TextField getTextFieldPlayerLic(int intTeam, int intPlayer) throws Exception {
        return (TextField) NewTeamMatchController.class.getField("player_"+intTeam+""+intPlayer+"_licentie").get(this);        
    }

    private TextField getTextFieldPlayerTsp(int intTeam, int intPlayer) throws Exception {
        return (TextField) NewTeamMatchController.class.getField("player_"+intTeam+""+intPlayer+"_tsp").get(this);        
    }
    private ChoiceBox<String> getChoiceBoxPlayerDiscipline(int intTeam, int intPlayer) throws Exception {
        return (ChoiceBox<String>) NewTeamMatchController.class.getField("player_"+intTeam+""+intPlayer+"_discipline").get(this);
    }
    
    private ComboBox<String> getComboboxPlayerName(int intTeam, int intPlayer) throws Exception {
        return (ComboBox<String>) NewTeamMatchController.class.getField("player_"+intTeam+""+intPlayer+"_name").get(this);
    }

    
    private ComboBox<String> getComboBoxTeam(int intTeam) throws Exception {
        return (ComboBox<String>) NewTeamMatchController.class.getField("team_"+intTeam).get(this);        
    }
}
