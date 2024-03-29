/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package billiard.score;

import billiard.score.controller.MenuController;
import billiard.score.controller.NewIndividualMatchController;
import billiard.score.controller.NewSimpleScoreBoard;
import billiard.score.controller.NewTeamMatchController;
import billiard.score.controller.ScoreBoardController;
import billiard.score.controller.ScoreSheetController;
import billiard.score.controller.SelectTeamCompetitionController;
import billiard.score.controller.SelectTeamMatchController;
import billiard.score.controller.TeamCompetitionSummarySheetController;
import billiard.model.IndividualCompetition;
import billiard.model.Match;
import billiard.model.PlayerMatchResult;
import billiard.model.TeamCompetition;
import billiard.model.TeamResult;
import billiard.common.hazelcast.SyncManager;
import billiard.model.IndividualCompetitionManager;
import billiard.model.MatchManager;
import billiard.model.ScoreboardManager;
import billiard.model.TeamCompetitionManager;
import billiard.common.AppProperties;
import billiard.common.CommonDialogs;
import billiard.model.pointssystem.PointSystemFactory;
import billiard.common.InitAppConfig;
import billiard.common.PermittedValues;
import billiard.common.SceneUtil;
import billiard.common.hazelcast.SendDataMessage;
import billiard.data.LeagueDataManager;
import billiard.data.LeagueItem;
import billiard.model.Competition;
import billiard.model.pointssystem.PointsSystemInterface;
import billiard.score.controller.AdminConfigurationController;
import billiard.score.controller.EnterMatchResultController;
import billiard.data.IndividualCompetitionDataManager;
import billiard.data.IndividualCompetitionItem;
import billiard.data.TeamCompetitionDataManager;
import billiard.data.TeamCompetitionItem;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author jean
 */
public class BilliardScore extends Application {
    private static final Logger LOGGER = Logger.getLogger(BilliardScore.class.getName());
    
    private String scoreboardId;
    private MatchManager matchManager;
    private IndividualCompetitionManager individualCompetitionManager;
    private TeamCompetitionManager teamCompetitionManager;
    private int menuChoice = -1;
    private ResourceBundle bundle;
    private Stage mainStage;
    
    @Override
    public void start(Stage stage) {  
        try {
            mainStage = stage;
            InitAppConfig.initAppConfig(this);
            LogManager.getLogManager().readConfiguration(new FileInputStream(InitAppConfig.getLogPropFile()));
            
            SyncManager.start();
            
            String strLocale=AppProperties.getInstance().getLocale();
            Locale locale = new Locale(strLocale);
            bundle = ResourceBundle.getBundle("languages.lang", locale);
            
            scoreboardId = AppProperties.getInstance().getScoreboardId();
            ScoreboardManager.getInstance().addScoreboard(scoreboardId);
            
            matchManager = MatchManager.getInstance();
            individualCompetitionManager = IndividualCompetitionManager.getInstance();
            teamCompetitionManager = TeamCompetitionManager.getInstance();
            
            if(SyncManager.isHazelcastEnabled()) {
                ITopic sendDataTopic = SyncManager.getHazelCastInstance().getTopic("send_data");
                sendDataTopic.addMessageListener(new MessageListener() {
                    @Override
                    public void onMessage(Message msg) {
                        if(msg.getMessageObject() instanceof SendDataMessage) {
                            SendDataMessage sdMsg = (SendDataMessage) msg.getMessageObject();
                            LOGGER.log(Level.INFO, "SendDataTopic.onMessage ", sdMsg.getActionObject());
                            String xmlString = (String) sdMsg.getDataObject();
                            if(sdMsg.getActionObject().equals(PermittedValues.ActionObject.IND_COMP)) {
                                try {
                                    IndividualCompetitionItem icItem = IndividualCompetitionItem.fromXML(xmlString);
                                    IndividualCompetitionDataManager.getInstance().writeFile(icItem);
                                    IndividualCompetitionDataManager.getInstance().addCompetition(icItem);
                                } catch (Exception ex) {
                                    LOGGER.severe(Arrays.toString(ex.getStackTrace()));
                                throw new RuntimeException(ex);
                                }
                            } else if(sdMsg.getActionObject().equals(PermittedValues.ActionObject.TEAM_COMP)) {
                                try {
                                    TeamCompetitionItem tcItem = TeamCompetitionItem.fromXML(xmlString);
                                    TeamCompetitionDataManager.getInstance().writeFile(tcItem);
                                    TeamCompetitionDataManager.getInstance().addCompetition(tcItem);
                                } catch (Exception ex) {
                                    LOGGER.severe(Arrays.toString(ex.getStackTrace()));
                                throw new RuntimeException(ex);
                                }
                            } else if(sdMsg.getActionObject().equals(PermittedValues.ActionObject.LEAGUE)) {
                                try {
                                    LeagueItem leagueItem = LeagueItem.fromXML(xmlString);
                                    LeagueDataManager.getInstance().writeFile(leagueItem);
                                    LeagueDataManager.getInstance().addLeague(leagueItem);
                                } catch (Exception ex) {
                                    LOGGER.severe(Arrays.toString(ex.getStackTrace()));
                                    throw new RuntimeException(ex);
                                }
                            }
                        }
                    }
                });                
            }
            
            do {
                //LOGGER.log(Level.FINEST, "choseAction => Start");
                FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML.MENU),bundle);
                Parent root = loader.load();
                
                Stage primaryStage = new Stage();
                MenuController controller = loader.<MenuController>getController();
                controller.initController(primaryStage);
                
                primaryStage.setTitle("BilliardScore (Version: " + this.getClass().getPackage().getImplementationVersion() +")");
                Scene scene = new Scene(root);
                SceneUtil.setStylesheet(scene);
                primaryStage.setScene(scene);
                primaryStage.centerOnScreen();
                primaryStage.showAndWait();
                menuChoice = controller.getAction();
                //LOGGER.log(Level.FINEST, "Start => menuChoice: {0}", menuChoice);
                if (menuChoice==MenuOptions.SCORE_BOARD) {
                    startScoreboard();
                } else if (menuChoice==MenuOptions.INDIVIDUAL) {
                    startIndividualCompetition();
                } else if (menuChoice==MenuOptions.TEAM) {
                    selectTeamCompetition();
                } else if (menuChoice==MenuOptions.CONF) {
                    startAdminConfiguration();
                } else if (menuChoice==MenuOptions.IMPORT) {
                    PermittedValues.ActionObject actionObject = controller.getActionObject();
                    importData(actionObject);
                }
            } while(menuChoice!=0);
            
            ScoreboardManager.getInstance().removeScoreboard(scoreboardId);
            stage.close();
            //LOGGER.log(Level.FINEST, "Start => End");
        } catch (Exception ex) {
            LOGGER.severe(Arrays.toString(ex.getStackTrace()));
            CommonDialogs.showException(ex);
        } finally {
            SyncManager.stop();
            Platform.exit();
        }
    }

    /**
     * @param args the command line arguments
     */
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private void startScoreboard() throws Exception{
        //LOGGER.log(Level.FINEST, "startScoreboard => Start");
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML.SCOREBOARD_FORM),bundle);
        Parent root;
        root = loader.load();

        Stage primaryStage = new Stage();
        NewSimpleScoreBoard controller = loader.<NewSimpleScoreBoard>getController();
        controller.initController(primaryStage);

        primaryStage.setTitle(bundle.getString("titel.nieuw.scorebord"));
        Scene scene = new Scene(root);
        SceneUtil.setStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();

        primaryStage.showAndWait();    
        
        if (controller.getMatch()==null) {
            //LOGGER.log(Level.FINEST, "startScoreboard => Cancelled");
            return;
        }
        
        Match match = controller.getMatch();
        
        openScoreboard(match, null, 5);
        //LOGGER.log(Level.FINEST, "startScoreboard => End");
    }
    
    private void startIndividualCompetition() throws Exception {
        //LOGGER.log(Level.FINEST, "startIndividualCompetition => Start");
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML.INDIVIDUAL_FORM),bundle);
        Parent root;
        root = loader.load();

        Stage primaryStage = new Stage();
        NewIndividualMatchController controller = loader.<NewIndividualMatchController>getController();
        controller.initController(primaryStage);

        primaryStage.setTitle(bundle.getString("titel.nieuwe.individuele.wedstrijd"));
        Scene scene = new Scene(root);
        SceneUtil.setStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.showAndWait();
        
        if(controller.getIndividualCompetition()==null) {
            //LOGGER.log(Level.FINEST, "startIndividualCompetition => Cancelled");
            return;
        }
        
        IndividualCompetition competition = controller.getIndividualCompetition();
        individualCompetitionManager.putIndividualCompetition(competition);
 
        Match match = competition.getMatch();
        LeagueItem league = LeagueDataManager.getInstance().getLeague(competition.getLeagueName());
        String turnindicatorsColor = null;
        int warmingupTime = 5;
        if(league!=null) {
            LOGGER.log(Level.FINEST,"league: " + league.getName()
                + " turnIndicatorColor: " + league.getTurnIndicatorsColor()
                + " warmingupTime: " + league.getWarmingUpTime());
            turnindicatorsColor = league.getTurnIndicatorsColor();
            warmingupTime = Integer.parseInt(league.getWarmingUpTime());
        } else {
            LOGGER.log(Level.FINEST, "leadue is null");
        }
        openScoreboard(match, turnindicatorsColor, warmingupTime);
        competition.setPlayer1Result(match.getPlayer1Result());
        competition.setPlayer2Result(match.getPlayer2Result());
        
        if (match.isEnded()) {
            //LOGGER.log(Level.FINEST, "startIndividualCompetition => Ended match: {0}", match);
            ScoreSheetController.showScoreSheet(match, PermittedValues.Mode.EDIT);
            individualCompetitionManager.removeIndividualCompetition(competition);
        }
        //LOGGER.log(Level.FINEST, "startIndividualCompetition => End");
    }
    
    private void startTeamCompetition(TeamCompetition competition) throws Exception {
        //LOGGER.log(Level.FINEST, "startTeamCompetition => Start");
        do {
            ArrayList<Match> selectableMatches = new ArrayList();
            for(Match teammatch : competition.getMatches()) {
                if (teammatch.isCreated()) {
                    selectableMatches.add(teammatch);
                }
            }
            if(!selectableMatches.isEmpty()) {
                if (!selectMatch(competition, selectableMatches)) {
                    break;
                }
            } else {
                break;
            }
            //LOGGER.log(Level.FINEST, "startTeamCompetition => Start match: {0}", match);
        } while (true);

        //competition = teamCompetitionManager.getTeamCompetition(competition);
        if (competition.isAllMatchedEnded()) {
            //LOGGER.log(Level.FINEST, "startTeamCompetition => AllMatchedEnded");
            calculateCompetitionResult(competition);

            //PointSystemFactory.getPointSystem(competition.getPointsSystem()).determineCompetitionPoints(competition);
            TeamCompetitionSummarySheetController.showSummarySheet(competition);
            teamCompetitionManager.removeTeamCompetition(competition);
        }
        //LOGGER.log(Level.FINEST, "startTeamCompetition => End");
    }

    private void startAdminConfiguration() throws Exception{
        //LOGGER.log(Level.FINEST, "startAdminConfiguration => Start");
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML.CONFIGURATION),bundle);
        Parent root;
        root = loader.load();

        Stage primaryStage = new Stage();
        AdminConfigurationController controller = loader.<AdminConfigurationController>getController();
        controller.initController(primaryStage);

        primaryStage.setTitle(bundle.getString("titel.admin.configuratie"));
        Scene scene = new Scene(root);
        SceneUtil.setStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();

        primaryStage.showAndWait();    
        
        //LOGGER.log(Level.FINEST, "startAdminConfiguration => End");
    }

    private void importData(PermittedValues.ActionObject actionObject) throws Exception {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Data Files", "*.xml");
        fileChooser.getExtensionFilters().add(filter);
        
        if(actionObject.equals(PermittedValues.ActionObject.LEAGUE)) {
            fileChooser.setTitle(bundle.getString("titel.selecteer.ledenlijst.bestand"));
            File inputFile = fileChooser.showOpenDialog(mainStage);
            if (null != inputFile) {
                LeagueDataManager.getInstance().importLeague(inputFile);
            }
        } else if(actionObject.equals(PermittedValues.ActionObject.IND_COMP)) {
            fileChooser.setTitle(bundle.getString("titel.selecteer.indcomp.bestand"));
            File inputFile = fileChooser.showOpenDialog(mainStage);
            if (null != inputFile) {
                IndividualCompetitionDataManager.getInstance().importCompetition(inputFile);
            }
        } else if(actionObject.equals(PermittedValues.ActionObject.TEAM_COMP)) {
            fileChooser.setTitle(bundle.getString("titel.selecteer.teamcomp.bestand"));
            File inputFile = fileChooser.showOpenDialog(mainStage);
            if (null != inputFile) {
                TeamCompetitionDataManager.getInstance().importCompetition(inputFile);
            }
        }
    }
    
    private void openScoreboard(Match match, String color, int warmingUpTime) throws Exception {
        LOGGER.finest("TurnIndicatorColor: " + color + " warmingUpTime: " + warmingUpTime);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML.SCOREBOARD),bundle);
        Parent root;
        root = loader.load();

        Stage primaryStage = new Stage();
        ScoreBoardController scoreboard = loader.<ScoreBoardController>getController();
        scoreboard.initController(primaryStage);
        scoreboard.setMatch(match);
        scoreboard.setTurnIdnicatorsColor(color);
        scoreboard.setWarmingUpTime(warmingUpTime);
        
        primaryStage.setTitle(bundle.getString("titel.scorenbord"));
        Scene scene = new Scene(root);
        SceneUtil.setStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.setMaximized(true);
        primaryStage.showAndWait();
        primaryStage.setMaximized(false);
        //LOGGER.log(Level.FINEST, "openScoreboard => End");
    }
    
    private boolean selectMatch(Competition competition, ArrayList<Match> selectableMatches) throws Exception{
        //LOGGER.log(Level.FINEST, "selectMatch => Start");
        //LOGGER.log(Level.FINEST, "selectMatch => competition: {0}", competition.getName());
        Match selectedMatch;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML.SELECT_TEAM_MATCH),bundle);
        Parent root;
        root = loader.load();

        Stage primaryStage = new Stage();
        SelectTeamMatchController controller = loader.<SelectTeamMatchController>getController();
        controller.initController(primaryStage);
        controller.setMatches(selectableMatches);

        primaryStage.setTitle(bundle.getString("titel.selecteer.wedtrijd"));
        Scene scene = new Scene(root);
        SceneUtil.setStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.showAndWait();         

        if(controller.getAction().equals(PermittedValues.Action.SELECT)) {
            selectedMatch = controller.getSelectedMatch();
            selectedMatch.reserve();
            matchManager.updateMatch(selectedMatch);
            LeagueItem league = LeagueDataManager.getInstance().getLeague(competition.getLeagueName());
            String turnindicatorsColor = null;
            int warmingupTime = 5;
            if(league!=null) {
                turnindicatorsColor = league.getTurnIndicatorsColor();
                warmingupTime = Integer.parseInt(league.getWarmingUpTime());
            }
            openScoreboard(selectedMatch, turnindicatorsColor, warmingupTime);
            if(selectedMatch.isEnded()) {
                //LOGGER.log(Level.FINEST, "startTeamCompetition => Match ended: {0}", match);
                //PointSystemFactory.getPointSystem(competition.getPointsSystem()).determineMatchPoints(match);
                ScoreSheetController.showScoreSheet(selectedMatch, PermittedValues.Mode.EDIT);
            }
            return true;
        } else if(controller.getAction().equals(PermittedValues.Action.EDIT)) {
            FXMLLoader loader2 = new FXMLLoader(getClass().getResource(FXML.ENTER_MATCH),bundle);
            Parent root2;
            root2 = loader2.load();

            Stage primaryStage2 = new Stage();
            EnterMatchResultController controller2 = loader2.<EnterMatchResultController>getController();
            controller2.initController(primaryStage2);
            controller2.setMatch(controller.getSelectedMatch());

            primaryStage2.setTitle(bundle.getString("titel.uitslag.invoeren"));
            Scene scene2 = new Scene(root2);
            SceneUtil.setStylesheet(scene2);
            primaryStage2.setScene(scene2);
            primaryStage2.centerOnScreen();
            primaryStage2.showAndWait();

            return true;
        }
        //LOGGER.log(Level.FINEST, "selectMatch => match: {0}", selectedMatch);
        return false;
    }
    
    public void calculateCompetitionResult(TeamCompetition competition){
        //LOGGER.log(Level.FINEST, "calculateCompetitionResult => Start");
        //LOGGER.log(Level.FINEST, "calculateCompetitionResult => competition: {0}", competition.getName());
        int tsp1 = 0, points1 = 0, innings1 = 0, hr1 = 0, mp1= 0, cp1 = 0;
        int tsp2 = 0, points2 = 0, innings2 = 0, hr2 = 0, mp2= 0, cp2 = 0;
        double perc1, perc2;
        
        PointsSystemInterface pointSystem = PointSystemFactory.getPointSystem(competition.getPointsSystem());
        for(Match match: competition.getMatches()) {
            //LOGGER.log(Level.FINEST, "calculateCompetitionResult => match: {0}", match);
            pointSystem.determineMatchPoints(match);
            matchManager.updateMatch(match);
            PlayerMatchResult playerResult = match.getPlayer1Result();
            if(competition.getTeam1().isPlayerOfTeam(match.getPlayer1())) {
                //LOGGER.log(Level.FINEST, "calculateCompetitionResult => player1OfTeam1", match.getPlayer1().getName());
                tsp1 += match.getPlayer1().getTsp();
                points1+= playerResult.getPoints();
                innings1 += playerResult.getInnings();
                hr1 = (hr1>playerResult.getHighestRun()?hr1:playerResult.getHighestRun());
                mp1 += playerResult.getMatchPoints();
            } else if(competition.getTeam2().isPlayerOfTeam(match.getPlayer1())) {
                //LOGGER.log(Level.FINEST, "calculateCompetitionResult => player1OfTeam2", match.getPlayer1().getName());
                tsp2 += match.getPlayer1().getTsp();
                points2+= playerResult.getPoints();
                innings2 += playerResult.getInnings();
                hr2 = (hr2>playerResult.getHighestRun()?hr2:playerResult.getHighestRun());
                mp2 += playerResult.getMatchPoints();
            }
            
            playerResult = match.getPlayer2Result();
            if(competition.getTeam1().isPlayerOfTeam(match.getPlayer2())) {
                //LOGGER.log(Level.FINEST, "calculateCompetitionResult => player2OfTeam1", match.getPlayer2().getName());
                tsp1 += match.getPlayer2().getTsp();
                points1+= playerResult.getPoints();
                innings1 += playerResult.getInnings();
                hr1 = (hr1>playerResult.getHighestRun()?hr1:playerResult.getHighestRun());
                mp1 += playerResult.getMatchPoints();
            } else if(competition.getTeam2().isPlayerOfTeam(match.getPlayer2())) {
                //LOGGER.log(Level.FINEST, "calculateCompetitionResult => player2OfTeam2", match.getPlayer2().getName());
                tsp2 += match.getPlayer2().getTsp();
                points2+= playerResult.getPoints();
                innings2 += playerResult.getInnings();
                hr2 = (hr2>playerResult.getHighestRun()?hr2:playerResult.getHighestRun());
                mp2 += playerResult.getMatchPoints();
            }
        }
        perc1 = (double) points1 / tsp1 * 100;
        competition.setTeam1Result(new TeamResult(tsp1, points1, innings1, hr1, mp1, perc1));
        perc2 = (double) points2 / tsp2 * 100;
        competition.setTeam2Result(new TeamResult(tsp2, points2, innings2, hr2, mp2, perc2));
        pointSystem.determineCompetitionPoints(competition);
        //LOGGER.log(Level.FINEST, "calculateCompetitionResult => End");
    }
    
    private void selectTeamCompetition() throws Exception {
        //LOGGER.log(Level.FINEST, "selectTeamCompetition => Start");
        TeamCompetition selectedCompetition;
        ArrayList<TeamCompetition> teamCompetionList = teamCompetitionManager.listTeamCompetitions();
        if(!teamCompetionList.isEmpty()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML.SELECT_COMPETITION),bundle);
            Parent root;
            root = loader.load();

            Stage primaryStage = new Stage();
            SelectTeamCompetitionController controller = loader.<SelectTeamCompetitionController>getController();
            controller.initController(primaryStage);
            controller.setCompetitions(teamCompetionList);

            primaryStage.setTitle(bundle.getString("titel.selecteer.ploegencompetitie"));
            Scene scene = new Scene(root);
            SceneUtil.setStylesheet(scene);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.showAndWait();         

            selectedCompetition = controller.getSelectedCompetition();
            PermittedValues.Action action = controller.getAction();
            if (action.equals(PermittedValues.Action.NEW)) {
                selectedCompetition = newTeamCompetition();
            } else if (action.equals(PermittedValues.Action.SELECT)) {
                selectedCompetition = controller.getSelectedCompetition();
            } else if (action.equals(PermittedValues.Action.DELETE)) {
                selectedCompetition = controller.getSelectedCompetition();
                teamCompetitionManager.removeTeamCompetition(selectedCompetition);
                return;
            } else if (action.equals(PermittedValues.Action.CANCEL)) {
                return;
            }
            //LOGGER.log(Level.FINEST, "selectTeamCompetition => competition: {0}", selectedCompetition.getName());
        } else {
            selectedCompetition = newTeamCompetition();
        }
        if (null!=selectedCompetition) {
            teamCompetitionManager.putTeamCompetition(selectedCompetition);
            startTeamCompetition(selectedCompetition);
        }
    }
    
    private TeamCompetition newTeamCompetition() throws Exception {
        //LOGGER.log(Level.FINEST, "startTeamCompetition => New Teamcompetition");
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML.TEAM_FORM),bundle);
        Parent root;
        root = loader.load();

        Stage primaryStage = new Stage();
        NewTeamMatchController controller = loader.<NewTeamMatchController>getController();
        controller.initController(primaryStage);

        primaryStage.setTitle(bundle.getString("titel.nieuwe.ploegwedstrijd"));
        Scene scene = new Scene(root);
        SceneUtil.setStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.showAndWait();

        if(controller.getTeamCompetition()==null) {
            //LOGGER.log(Level.FINEST, "startTeamCompetition => New Teamcompetition Cancelled");
            return null;
        }

        TeamCompetition competition = controller.getTeamCompetition();
        return competition;
    }
    
    public void setMenuChoice(int menuChoice) {
        this.menuChoice = menuChoice;
    }

    public class MenuOptions {
        public static final int EXIT = 0;
        public static final int SCORE_BOARD = 1;
        public static final int INDIVIDUAL = 2;
        public static final int TEAM = 3;
        public static final int TOURNAMENT=4;
        public static final int CONF=5;
        public static final int IMPORT=6;
    }
    
    public class FXML {
        public static final String MENU = "/billiard/score/fxml/Menu.fxml";
        public static final String SCOREBOARD = "/billiard/score/fxml/ScoreBoard.fxml";
        public static final String INDIVIDUAL_FORM = "/billiard/score/fxml/NewIndividualMatch.fxml";
        public static final String SCOREBOARD_FORM = "/billiard/score/fxml/NewSimpleScoreBoard.fxml";
        public static final String TEAM_FORM = "/billiard/score/fxml/NewTeamMatch.fxml";
        public static final String EOM_DIALOG= "/billiard/score/fxml/EndOfMatch.fxml";
        public static final String LEVELING_TURN_DIALOG = "/billiard/score/fxml/LevelingTurn.fxml";
        public static final String SELECT_TEAM_MATCH = "/billiard/score/fxml/SelectTeamMatch.fxml";
        public static final String SELECT_COMPETITION = "/billiard/score/fxml/SelectTeamCompetition.fxml";
        public static final String CHANGE_SCORE = "/billiard/score/fxml/ChangeScore.fxml";
        public static final String SHOW_HELP ="/billiard/score/fxml/ScoreboardHelp.fxml";
        public static final String LICENCE_ISSUE = "/billiard/score/fxml/LicenceIssue.fxml";
        public static final String SPLASH_SCREEN = "/billiard/score/fxml/SplashScreen.fxml";
        public static final String OK_CANCEL = "/billiard/score/fxml/OKCancelDialog.fxml";
        public static final String WARMING_UP = "/billiard/score/fxml/WarmingUpCountDown.fxml";
        public static final String CONFIGURATION = "/billiard/score/fxml/AdminConfiguration.fxml";
        public static final String SEND_MAIL = "/billiard/score/fxml/SendMail.fxml";
        public static final String ENTER_MATCH = "/billiard/score/fxml/EnterMatchResult.fxml";
    }
}
