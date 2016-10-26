/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.competition.controller;

import billiard.common.AppProperties;
import billiard.common.CommonDialogs;
import billiard.common.ControllerInterface;
import billiard.common.FormValidation;
import billiard.common.PermittedValues;
import billiard.common.SceneUtil;
import billiard.data.IndividualCompetitionDataManager;
import billiard.data.IndividualCompetitionItem;
import billiard.data.LeagueDataManager;
import billiard.data.LeagueItem;
import billiard.data.MemberItem;
import billiard.data.PlayerItem;
import billiard.model.IndividualTournament;
import billiard.model.IndividualTournamentManager;
import billiard.model.Match;
import billiard.model.MatchManager;
import billiard.model.Player;
import billiard.model.PlayerTournamentResult;
import billiard.model.ScoreboardManager;
import billiard.model.pointssystem.PointSystemFactory;
import billiard.model.pointssystem.PointsSystemInterface;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.MapEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jean
 */
public class IndividualTournamentDetailController implements Initializable, ControllerInterface {
    private static final Logger LOGGER = Logger.getLogger(IndividualTournamentDetailController.class.getName());
    private static MatchManager matchManager;
    private static IndividualTournamentManager individualTournamentManager;
    private static ScoreboardManager scoreboardManager;
    
    private static final String SELECT_SCOREBOARD = "/billiard/competition/fxml/SelectScoreboard.fxml";

    private Stage primaryStage;
    private IndividualTournament tournament;
    private IndividualCompetitionItem selectedCompetition;

    private ObservableList<Player> participants;
    private Player selectedParticipant;
    private int selectedParticipantIdx;

    private static enum EditMode {READ, UPDATE, CREATE};
    private EditMode participantEditMode;

    private ObservableList<Match> matches;
    private Match selectedMatch;
    private int selectedMatchIdx;
    private EditMode matchEditMode;
    
    private ObservableList<PlayerTournamentResult> ranking;

    @FXML
    private ComboBox<String> cbCompetition;
    @FXML
    private Button pbSelectMember;
    @FXML
    private ChoiceBox<String> cbMatchPointsystem;
    @FXML
    private Button btnNewParticipant;
    @FXML
    private Button btnNewMatch;
    @FXML
    private Button btnUitslag;
    @FXML
    private Tab tabRanking;
    @FXML
    private Tab tabInfo;
    @FXML
    private Tab tabParticipants;
    @FXML
    private Tab tabMatches;
    @FXML
    private ChoiceBox<String> cbDiscipline;
    @FXML
    private ChoiceBox<String> cbTableFormat;
    @FXML
    private TextField tfGroup;
    @FXML
    private TextField tfLic;
    @FXML
    private TextField tfName;
    @FXML
    private TextField tfClub;
    @FXML
    private TextField tfTSP;
    @FXML
    private TableView<Player> tblVwParticipants;
    @FXML
    private TableColumn<Player, String> tblColLic;
    @FXML
    private TableColumn<Player, String> tblColNaam;
    @FXML
    private TableColumn<Player, String> tblColClub;
    @FXML
    private TableColumn<Player, String> tblColTsp;
    @FXML
    private TextField tfNbr;
    @FXML
    private ChoiceBox<Player> cbPlayer1;
    @FXML
    private ChoiceBox<Player> cbPlayer2;
    @FXML
    private Button btnValidate;
    @FXML
    private Button btnInvalidate;
    @FXML
    private Button btnRemoveParticipant;
    @FXML
    private Button btnRemoveMatch;
    @FXML
    private Button btnStartMatch;
    @FXML
    private TableView<Match> tblVwMatches;
    @FXML
    private TableColumn<Match, String> tblColNbr;
    @FXML
    private TableColumn<Match, Player> tblColPlayer1;
    @FXML
    private TableColumn<Match, Player> tblColPlayer2;
    @FXML
    private TableColumn<Match, String> tblColStatus;
    @FXML
    private TableView<PlayerTournamentResult> tblVRanking;
    @FXML
    private TableColumn<PlayerTournamentResult, String> tblColRankingLic;
    @FXML
    private TableColumn<PlayerTournamentResult, String> tblColName;
    @FXML
    private TableColumn<PlayerTournamentResult, String> tblColMP;
    @FXML
    private TableColumn<PlayerTournamentResult, String> tblColPoints;
    @FXML
    private TableColumn<PlayerTournamentResult, String> tblColInnings;
    @FXML
    private TableColumn<PlayerTournamentResult, String> tblColAvg;
    @FXML
    private TableColumn<PlayerTournamentResult, String> tblColHR;


    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            matchManager = MatchManager.getInstance();
            individualTournamentManager = IndividualTournamentManager.getInstance();
            scoreboardManager = ScoreboardManager.getInstance();
            
            matchManager.addEntryListener(new EntryListener() {
                
                @Override
                public void entryAdded(EntryEvent event) {
                    //System.out.println("Entry added: " + event);
                }
                
                @Override
                public void entryRemoved(EntryEvent event) {
                    //System.out.println("Entry removed: " + event);
                }
                
                @Override
                public void entryUpdated(EntryEvent event) {
                    //System.out.println("Entry updated: " + event);
                    Match match = (Match) event.getValue();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            matchUpdated(match);
                        }
                    });
                }
                
                @Override
                public void entryEvicted(EntryEvent event) {
                    //System.out.println("Entry evicted: " + event);
                }
                
                @Override
                public void mapEvicted(MapEvent event) {
                    //System.out.println("Map evicted: " + event);
                }
                
                @Override
                public void mapCleared(MapEvent event) {
                    //System.out.println("Map cleared: " + event);
                }
            });
            
            // Info Tab
            IndividualCompetitionDataManager competitionManager = IndividualCompetitionDataManager.getInstance();
            cbCompetition.getItems().addAll(competitionManager.getCompetitionNames());
            cbCompetition.getSelectionModel().clearSelection();
            
            cbDiscipline.getItems().addAll(Arrays.asList(PermittedValues.DISCIPLINES));
            cbDiscipline.getSelectionModel().clearSelection();
            cbTableFormat.getItems().addAll(Arrays.asList(PermittedValues.TABLE_FORMAT));
            cbTableFormat.getSelectionModel().clearSelection(1);
            cbMatchPointsystem.getItems().addAll(PointSystemFactory.PointSystem.stringValues());
            cbMatchPointsystem.getSelectionModel().clearSelection();
            
            cbCompetition.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if(null!=newValue && !newValue.isEmpty()) {
                        try {
                            populateTournamentFromCompetition(newValue);
                        } catch (Exception ex) {
                            LOGGER.severe(Arrays.toString(ex.getStackTrace()));
                            throw new RuntimeException(ex);
                        }
                    } else {
                        selectedCompetition = null;
                    }
                }
            });
            
            //Participants Tab
            tblColLic.setCellValueFactory(new PropertyValueFactory<>("licentie"));
            tblColNaam.setCellValueFactory(new PropertyValueFactory<>("name"));
            tblColClub.setCellValueFactory(new PropertyValueFactory<>("club"));
            tblColTsp.setCellValueFactory(new PropertyValueFactory<>("tsp"));
            btnRemoveParticipant.setDisable(true);
            
            participants = FXCollections.observableArrayList();
            tblVwParticipants.setItems(participants);
            participantEditMode = EditMode.CREATE;
            selectedParticipant = null;
            selectedParticipantIdx = -1;
            populateParticipantFields(selectedParticipant);
            tblVwParticipants.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    selectedParticipant = tblVwParticipants.getSelectionModel().getSelectedItem();
                    selectedParticipantIdx = tblVwParticipants.getSelectionModel().getSelectedIndex();
                    if (selectedParticipant!= null) {
                        participantEditModeUpdate();
                    } else {
                        participantEditModeCreate();
                    }
                }
            });
            tfLic.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    try {
                        lookupPlayer();
                    } catch (Exception ex) {
                        LOGGER.severe(Arrays.toString(ex.getStackTrace()));
                        throw new RuntimeException(ex);
                    }
                }
            });
            
            //Matches Tab
            tblColNbr.setCellValueFactory(new PropertyValueFactory<>("number"));
            tblColPlayer1.setCellValueFactory(new PropertyValueFactory<>("player1"));
            tblColPlayer2.setCellValueFactory(new PropertyValueFactory<>("player2"));
            tblColStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            cbPlayer1.setItems(participants);
            cbPlayer2.setItems(participants);
            btnRemoveMatch.setDisable(true);
            btnStartMatch.setDisable(true);
            
            matches = FXCollections.observableArrayList();
            tblVwMatches.setItems(matches);
            matchEditMode = EditMode.CREATE;
            selectedMatch = null;
            selectedMatchIdx = -1;
            populateMatchFields(selectedMatch);
            tblVwMatches.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    selectedMatch = tblVwMatches.getSelectionModel().getSelectedItem();
                    selectedMatchIdx = tblVwMatches.getSelectionModel().getSelectedIndex();
                    if (selectedMatch!=null) {
                        matchEditModeUpdate();
                    }else{
                        matchEditModeCreate();
                    }
                }
            });
            
            //Ranking Tab
            tblColRankingLic.setCellValueFactory(new PropertyValueFactory<>("playerLic"));
            tblColName.setCellValueFactory(new PropertyValueFactory<>("playerName"));
            tblColMP.setCellValueFactory(new PropertyValueFactory<>("matchPoints"));
            tblColPoints.setCellValueFactory(new PropertyValueFactory<>("points"));
            tblColInnings.setCellValueFactory(new PropertyValueFactory<>("innings"));
            tblColAvg.setCellValueFactory(new PropertyValueFactory<>("average"));
            tblColHR.setCellValueFactory(new PropertyValueFactory<>("highestRun"));
            ranking = FXCollections.observableArrayList();
            tblVRanking.setItems(ranking);
        } catch (Exception ex) {
            LOGGER.severe(Arrays.toString(ex.getStackTrace()));
            CommonDialogs.showException(ex);
        }
    }    

    @FXML
    private void pbSave_OnAction(ActionEvent event) throws Exception {
        save();
    }

    @FXML
    private void pbClose_OnAction(ActionEvent event) throws Exception {
        save();
        primaryStage.hide();
    }

    @Override
    public void initController(Stage stage) {
        primaryStage = stage;
    }

    public void setTournament(IndividualTournament tournament) {
        if (tournament!=null) {
            this.tournament = tournament;
            participants.setAll(tournament.getParticipants());
            matches.addAll(tournament.getMatches());
        }
        populateTournamentFields();
    }

    @FXML
    private void btnNewParticipant_OnAction(ActionEvent event) {
        participantEditModeCreate();
    }

    @FXML
    private void btnRemoveParticipant_OnAction(ActionEvent event) {
        if(!tblVwParticipants.getSelectionModel().getSelectedItems().isEmpty()) {
            if(!isPlayerUsed()) {
                participants.remove(tblVwParticipants.getSelectionModel().getSelectedItem());
                if(participants.isEmpty()) {
                    participantEditModeCreate();
                } else {
                    selectedParticipant = tblVwParticipants.getSelectionModel().getSelectedItem();
                    selectedParticipantIdx = tblVwParticipants.getSelectionModel().getSelectedIndex();
                    tblVwParticipants.getSelectionModel().selectFirst();
                    participantEditModeUpdate();
                }
            }
        }
    }

    @FXML
    private void btnValidateParticipant_OnAction(ActionEvent event) {
        if(!validParticipantForm()) {
            return;
        }
        if (EditMode.UPDATE == participantEditMode) {
            selectedParticipant.setLicentie(tfLic.getText());
            selectedParticipant.setName(tfName.getText());
            selectedParticipant.setClub(tfClub.getText());
            selectedParticipant.setTsp(Integer.valueOf(tfTSP.getText()));
            participants.set(selectedParticipantIdx, selectedParticipant);
            participantEditModeUpdate();
        } else {
            selectedParticipant = new Player(tfName.getText(), tfClub.getText()
                    , Integer.valueOf(tfTSP.getText()), "", tfLic.getText());
            participants.add(selectedParticipant);
            participantEditModeCreate();
        }
    }

    @FXML
    private void btnInvalidateParticipant_OnAction(ActionEvent event) {
        populateParticipantFields(selectedParticipant);
    }

    @FXML
    private void btnUitslag_OnAction(ActionEvent event) {
    }

    @FXML
    private void tblVwParticipants_OnMouseClicked(MouseEvent event) {
    }

    @FXML
    private void btnNewMatch_OnAction(ActionEvent event) {
        matchEditModeCreate();
    }

    @FXML
    private void btnRemoveMatch_OnAction(ActionEvent event) {
        if(!tblVwMatches.getSelectionModel().getSelectedItems().isEmpty()) {
            matches.remove(tblVwMatches.getSelectionModel().getSelectedItem());
            tblVwMatches.getSelectionModel().clearSelection();
            matchEditModeCreate();
        }
    }

    @FXML
    private void btnStartMatch_OnAction(ActionEvent event) throws Exception {
        String scoreboardId = selectScoreboard();
        if (scoreboardId!=null) {
            matchManager.sendMatch(selectedMatch, scoreboardId);
        }
        matches.set(selectedMatchIdx, selectedMatch);
    }

    @FXML
    private void btnValidateMatch_OnAction(ActionEvent event) {
        if (matchEditMode==EditMode.CREATE) {
            selectedMatch = new Match(tournament.getId(), tfNbr.getText()
                    , tournament.getDiscipline(), cbPlayer1.getValue(), cbPlayer2.getValue());
            matches.add(selectedMatch);
            matchEditModeCreate();
        }else {
            selectedMatch.setNumber(tfNbr.getText());
            selectedMatch.setPlayer1(cbPlayer1.getValue());
            selectedMatch.setPlayer2(cbPlayer2.getValue());
            matches.set(selectedMatchIdx, selectedMatch);
            matchEditModeUpdate();
        }
    }

    @FXML
    private void btnInvalidateMatch_OnAction(ActionEvent event) {
        populateMatchFields(selectedMatch);
    }

    @FXML
    private void tblVwMatches_OnMouseClicked(MouseEvent event) {
    }
    
    @FXML
    private void OnActionButtonSelectMember(ActionEvent event) throws Exception {
        String leagueName = "";
        if(null!=selectedCompetition) {
            leagueName = selectedCompetition.getLeague();
        }
        if(leagueName.isEmpty()) {
            leagueName = AppProperties.getInstance().getDefaultLeague();
        }
        LeagueItem league = LeagueDataManager.getInstance().getLeague(leagueName);
        if(league!=null) {
            MemberItem member = CommonDialogs.searchMember(league);
            populatePlayerFromMember(league, member);
        }
    }

    private void populateTournamentFields() {
        if (tournament!=null) {
            cbCompetition.setValue(tournament.getName());
            tfGroup.setText(tournament.getGroup());
            cbDiscipline.setValue(tournament.getDiscipline());
            cbTableFormat.setValue(tournament.getTableFormat());
            cbMatchPointsystem.setValue(tournament.getPointsSystem());
        }else{
            cbCompetition.setValue("");
            tfGroup.clear();
            cbDiscipline.getSelectionModel().selectFirst();
            cbTableFormat.getSelectionModel().selectFirst();
            cbMatchPointsystem.getSelectionModel().selectFirst();
        }
    }
    
    public boolean validForm() {
        boolean valid=FormValidation.validateComboBox(cbCompetition);
        
        return valid;
    }

    private void save() throws Exception {
        if (validForm()) {
            if(tournament==null) {
                tournament = new IndividualTournament("","","");
            }
            tournament.setName(cbCompetition.getValue());
            tournament.setTableFormat(cbTableFormat.getValue());
            tournament.setDiscipline(cbDiscipline.getValue());
            tournament.setPointsSystem(cbMatchPointsystem.getValue());
            tournament.setGroup(tfGroup.getText());
            tournament.setParticipants(new ArrayList<>(participants));
            tournament.setMatches(new ArrayList<>(matches));
            tournament.setParticipantsResult(new ArrayList<>(ranking));
            IndividualTournamentManager.writeIndividualTournament(tournament);
            individualTournamentManager.putIndividualTournament(tournament);
        }
    }
    
    private void populateParticipantFields(Player participant) {
        if (participant!=null) {
            this.tfName.setText(participant.getName());
            this.tfLic.setText(participant.getLicentie());
            this.tfClub.setText(participant.getClub());
            this.tfTSP.setText(participant.getTsp().toString());
        }else{
            tfName.clear();
            tfLic.clear();
            tfClub.clear();
            tfTSP.clear();
        }
        tfLic.requestFocus();
    }
    
    public boolean validParticipantForm() {
        boolean valid = FormValidation.validateTextField(tfLic)
                && FormValidation.validateTextField(tfName)
                && FormValidation.validateTextField(tfClub)
                && FormValidation.validateTextField(tfTSP, true);
        
        return valid;
    }

    private void populateMatchFields(Match match) {
        if (match!=null) {
            tfNbr.setText(match.getNumber());
            cbPlayer1.setValue(match.getPlayer1());
            cbPlayer2.setValue(match.getPlayer2());
        }else{
            tfNbr.setText(String.valueOf(matches.size()+1));
            cbPlayer1.getSelectionModel().clearSelection();
            cbPlayer2.getSelectionModel().clearSelection();
        }
        tfNbr.requestFocus();
    }
    
    private String selectScoreboard() throws Exception {
        ArrayList<String> list = scoreboardManager.listScoreboards();
        if(list.isEmpty()) {
            return null;
        }
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);

        FXMLLoader loader = new FXMLLoader(getClass().getResource(SELECT_SCOREBOARD));
        Parent root;
        root = loader.load();

        SelectScoreboardController controller = loader.<SelectScoreboardController>getController();
        controller.initController(dialog);
        controller.setScoreboards(list);

        dialog.setTitle("Selecteer Scorenbord");
        Scene scene = new Scene(root);
        SceneUtil.setStylesheet(scene);
        dialog.setScene(scene);
        dialog.centerOnScreen();
        dialog.showAndWait();
        
        return controller.getSelectedScoreboard();
    }

    private boolean isPlayerUsed() {
        for(Match match:matches) {
            if(match.getPlayer1().equals(selectedParticipant) || match.getPlayer2().equals(selectedParticipant)) {
                return true;
            }
        }
        return false;
    }
    
    private void participantEditModeUpdate() {
        if(isPlayerUsed()) {
            participantEditMode = EditMode.READ;
            populateParticipantFields(selectedParticipant);
            btnRemoveParticipant.setDisable(true);
            btnNewParticipant.setDisable(false);
            tfLic.setDisable(true);
            tfName.setDisable(true);
            tfClub.setDisable(true);
            tfTSP.setDisable(true);
        }else{
            participantEditMode = EditMode.UPDATE;
            populateParticipantFields(selectedParticipant);
            tfLic.setDisable(false);
            tfName.setDisable(false);
            tfClub.setDisable(false);
            tfTSP.setDisable(false);
            btnRemoveParticipant.setDisable(false);
            btnNewParticipant.setDisable(false);
        }
    }
    
    private void participantEditModeCreate() {
        participantEditMode = EditMode.CREATE;
        selectedParticipant = null;
        selectedParticipantIdx = -1;
        populateParticipantFields(selectedParticipant);
        tfLic.setDisable(false);
        tfName.setDisable(false);
        tfClub.setDisable(false);
        tfTSP.setDisable(false);
        if (participants.isEmpty()) {
            btnRemoveParticipant.setDisable(true);
        }
        btnNewParticipant.setDisable(true);
        tblVwParticipants.getSelectionModel().clearSelection();
    }

    private void matchEditModeUpdate() {
        if(selectedMatch.isCreated()) {
            matchEditMode = EditMode.UPDATE;
            populateMatchFields(selectedMatch);
            btnRemoveMatch.setDisable(false);
            btnStartMatch.setDisable(false);
            btnNewMatch.setDisable(false);
            btnUitslag.setDisable(false);
        }else{
            matchEditMode = EditMode.READ;
            populateMatchFields(selectedMatch);
            btnStartMatch.setDisable(true);
            btnRemoveMatch.setDisable(true);
            btnStartMatch.setDisable(true);
            btnNewMatch.setDisable(false);
        }
    }
    
    private void matchEditModeCreate() {
        matchEditMode = EditMode.CREATE;
        selectedMatch = null;
        selectedMatchIdx = -1;
        populateMatchFields(selectedMatch);
        if (matches.isEmpty()) {
            btnRemoveMatch.setDisable(true);
            btnStartMatch.setDisable(true);
        }
        tblVwMatches.getSelectionModel().clearSelection();
    }
    
    private void matchUpdated(Match match) {
        System.out.println("Match: " + match);
        if(matches.contains(match)) {
            System.out.println("Match found");
            if(match.isEnded()) {
                matchManager.removeMatch(match);
                PointsSystemInterface pointSystem = PointSystemFactory.getPointSystem(tournament.getPointsSystem());
                pointSystem.determineMatchPoints(match);
                tournament.matchEnded(match);
                ranking.setAll(tournament.getParticipantsResult());
                matches.setAll(tournament.getMatches());
            }
        }
    }
    
    private void populateTournamentFromCompetition(String competitionName) throws Exception {
        IndividualCompetitionDataManager mgr = IndividualCompetitionDataManager.getInstance();
        selectedCompetition = mgr.getCompetition(competitionName);
        
        this.cbDiscipline.setValue(selectedCompetition.getDiscipline());
        this.cbMatchPointsystem.setValue(selectedCompetition.getPointSystem());
        this.cbTableFormat.setValue(selectedCompetition.getTableFormat());
        
        participants.clear();
        for (PlayerItem playerItem : selectedCompetition.getPlayers().values()) {
            Player player = new Player(playerItem.getName()
                    , playerItem.getClub(), Integer.valueOf(playerItem.getTsp())
                    , playerItem.getDiscipline(), playerItem.getLic());
            participants.add(player);
        }
        
    }

    private void lookupPlayer() throws Exception {
        String lic = tfLic.getText();
        
        String leagueName = "";
        if(null!=selectedCompetition) {
            leagueName = selectedCompetition.getLeague();
        }
        if(leagueName.isEmpty()) {
            leagueName = AppProperties.getInstance().getDefaultLeague();
        }
        LeagueItem league = LeagueDataManager.getInstance().getLeague(leagueName);
        if(league!=null) {
            MemberItem member = league.getMember(lic);
            if(member!=null) {
                populatePlayerFromMember(league, member);
            }
        }
    }
    
    private void populatePlayerFromMember(LeagueItem league, MemberItem member) {
        tfName.setText(member.getName());
        tfClub.setText(league.getClub(member.getClubLic()).getName());
        if(null!=cbDiscipline.getValue()) {
            tfTSP.setText(member.getTsp(cbDiscipline.getValue()).getTsp());
        }
    }
}
