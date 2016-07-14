/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package billiard.score;

import billiard.common.hazelcast.StartMatchMessage;
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
import billiard.model.pointssystem.PointSystemFactory;
import billiard.common.InitAppConfig;
import billiard.common.PermittedValues;
import billiard.common.SceneUtil;
import billiard.data.LeagueDataManager;
import billiard.data.LeagueItem;
import billiard.model.Competition;
import billiard.model.pointssystem.PointsSystemInterface;
import billiard.score.controller.AdminConfigurationController;
import billiard.score.controller.EnterMatchResultController;
import billiard.data.IndividualCompetitionDataManager;
import billiard.data.TeamCompetitionDataManager;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author jean
 */
public class BilliardScore extends Application {
    private static final Logger LOGGER = Logger.getLogger(BilliardScore.class.getName());
    
    private String scoreboardId;
    private ArrayDeque<Match> matchQue;
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
            //logger.log(Level.FINEST, "Start => Start");
            
            SyncManager.start();
            
            String strLocale=AppProperties.getInstance().getLocale();
            Locale locale = new Locale(strLocale);
            bundle = ResourceBundle.getBundle("languages.lang", locale);
            
            scoreboardId = AppProperties.getInstance().getScoreboardId();
            //logger.log(Level.FINEST, "Start => scoreboardId: {0}", scoreboardId);
            ScoreboardManager.getInstance().addScoreboard(scoreboardId);
            
            matchManager = MatchManager.getInstance();
            individualCompetitionManager = IndividualCompetitionManager.getInstance();
            teamCompetitionManager = TeamCompetitionManager.getInstance();
            
            
            matchQue = new ArrayDeque<>();
            
            ITopic topic = null;
            String topicId = "";
            if(SyncManager.isHazelcastEnabled()) {
                topic = matchManager.getStartMatchTopic();
                topicId = topic.addMessageListener(new MessageListener() {
                    @Override
                    public void onMessage(Message message) {
                        //logger.log(Level.FINEST, "StartMatchTopic.onMessage => message: {0}", message.toString());
                        StartMatchMessage msg = (StartMatchMessage )message.getMessageObject();
                        //logger.log(Level.FINEST, "StartMatchTopic.onMessage => Match received: {0} - {1}", new Object[] {msg.getScoreBoardId(),msg.getMatchId()});
                        Match match = matchManager.getMatch(msg.getMatchId());
                        //logger.log(Level.FINEST, "StartMatchTopic.onMessage => Match found: {0}", match.toString());
                        try {
                            if (msg.getScoreBoardId().equals(scoreboardId)) {
                                if(!matchQue.contains(match)) {
                                    matchQue.addLast(match);
                                }
                            }
                        } catch (Exception ex) {
                            //logger.log(Level.SEVERE, "StartMatchTopic.onMessage exception: {0}", ex.getMessage());
                            throw new RuntimeException(ex);
                        }
                    }
                });
            }
            
            
            do {
                //logger.log(Level.FINEST, "choseAction => Start");
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
                //logger.log(Level.FINEST, "Start => menuChoice: {0}", menuChoice);
                if (menuChoice==MenuOptions.SCORE_BOARD) {
                    startScoreboard();
                } else if (menuChoice==MenuOptions.INDIVIDUAL) {
                    startIndividualCompetition();
                } else if (menuChoice==MenuOptions.TEAM) {
                    selectTeamCompetition();
                } else if (menuChoice==MenuOptions.TOURNAMENT) {
                    Match match = matchQue.getFirst();
                    startTournamentMatch(match);
                    matchManager.putMatch(match);
                    matchQue.remove(match);
                } else if (menuChoice==MenuOptions.CONF) {
                    startAdminConfiguration();
                } else if (menuChoice==MenuOptions.IMPORT) {
                    PermittedValues.ActionObject actionObject = controller.getActionObject();
                    importData(actionObject);
                }
                if (!matchQue.isEmpty()) {
                    do {
                        //logger.log(Level.FINEST, "Start => !matchQue.isEmpty");
                        Match match = matchQue.getFirst();
                        //logger.log(Level.FINEST, "Start => Picking match from que: {0}", match.toString());
                        startTournamentMatch(match);
                        matchQue.remove(match);
                    } while (!matchQue.isEmpty());
                }
            } while(menuChoice!=0);
            
            if(null!=topic) {
                topic.removeMessageListener(topicId);
            }
            ScoreboardManager.getInstance().removeScoreboard(scoreboardId);
            SyncManager.stop();
            stage.close();
            //logger.log(Level.FINEST, "Start => End");
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Please contact app provider and supply message below");
            alert.setContentText(ex.toString());
            LOGGER.severe(ex.toString());
            ex.printStackTrace();

            alert.showAndWait();
        } finally {
            Platform.exit();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private void startScoreboard() throws Exception{
        //logger.log(Level.FINEST, "startScoreboard => Start");
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
            //logger.log(Level.FINEST, "startScoreboard => Cancelled");
            return;
        }
        
        Match match = controller.getMatch();
        
        openScoreboard(match, null, 5);
        //logger.log(Level.FINEST, "startScoreboard => End");
    }
    
    private void startIndividualCompetition() throws Exception {
        //logger.log(Level.FINEST, "startIndividualCompetition => Start");
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
            //logger.log(Level.FINEST, "startIndividualCompetition => Cancelled");
            return;
        }
        
        IndividualCompetition competition = controller.getIndividualCompetition();
        individualCompetitionManager.putIndividualCompetition(competition);
 
        Match match = competition.getMatch();
        openScoreboard(match, null, 5);
        competition.setPlayer1Result(match.getPlayer1Result());
        competition.setPlayer2Result(match.getPlayer2Result());
        
        if (match.isEnded()) {
            //logger.log(Level.FINEST, "startIndividualCompetition => Ended match: {0}", match);
            ScoreSheetController.showScoreSheet(match, PermittedValues.Mode.EDIT);
            individualCompetitionManager.removeIndividualCompetition(competition);
        }
        //logger.log(Level.FINEST, "startIndividualCompetition => End");
    }
    
    private void startTeamCompetition(TeamCompetition competition) throws Exception {
        //logger.log(Level.FINEST, "startTeamCompetition => Start");
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
            //logger.log(Level.FINEST, "startTeamCompetition => Start match: {0}", match);
        } while (true);

        //competition = teamCompetitionManager.getTeamCompetition(competition);
        if (competition.isAllMatchedEnded()) {
            //logger.log(Level.FINEST, "startTeamCompetition => AllMatchedEnded");
            calculateCompetitionResult(competition);

            //PointSystemFactory.getPointSystem(competition.getPointsSystem()).determineCompetitionPoints(competition);
            TeamCompetitionSummarySheetController.showSummarySheet(competition);
            teamCompetitionManager.removeTeamCompetition(competition);
        }
        //logger.log(Level.FINEST, "startTeamCompetition => End");
    }

    private void startAdminConfiguration() throws Exception{
        //logger.log(Level.FINEST, "startAdminConfiguration => Start");
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
        
        //logger.log(Level.FINEST, "startAdminConfiguration => End");
    }

    private void importData(PermittedValues.ActionObject actionObject) throws Exception {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Data Files", "*.xml");
        fileChooser.getExtensionFilters().add(filter);
        
        if(actionObject.equals(PermittedValues.ActionObject.LEAGUE)) {
            fileChooser.setTitle(bundle.getString("titel.selecteer.ledenlijst.bestand"));
            File inputFile = fileChooser.showOpenDialog(mainStage);
            if (null != inputFile) {
                LeagueDataManager.getInstance(AppProperties.getInstance().getDataPath()).importLeague(inputFile);
            }
        } else if(actionObject.equals(PermittedValues.ActionObject.IND_COMP)) {
            fileChooser.setTitle(bundle.getString("titel.selecteer.indcomp.bestand"));
            File inputFile = fileChooser.showOpenDialog(mainStage);
            if (null != inputFile) {
                IndividualCompetitionDataManager.getInstance(AppProperties.getInstance().getDataPath()).importCompetition(inputFile);
            }
        } else if(actionObject.equals(PermittedValues.ActionObject.TEAM_COMP)) {
            fileChooser.setTitle(bundle.getString("titel.selecteer.teamcomp.bestand"));
            File inputFile = fileChooser.showOpenDialog(mainStage);
            if (null != inputFile) {
                TeamCompetitionDataManager.getInstance(AppProperties.getInstance().getDataPath()).importCompetition(inputFile);
            }
        }
    }
    
    private synchronized void startTournamentMatch(Match match) throws Exception{
        //logger.log(Level.FINEST, "startTournamentMatch => Start");
        //logger.log(Level.FINEST, "startTournamentMatch => match: {0}", match);
        openScoreboard(match, null, 5);

        if (match.isEnded()) {
            //logger.log(Level.FINEST, "startTournamentMatch => match ended: {0}", match);
            ScoreSheetController.showScoreSheet(match, PermittedValues.Mode.EDIT);
        }
        //logger.log(Level.FINEST, "startTournamentMatch => End");
    }
    
    private void openScoreboard(Match match, PermittedValues.TurnIndicatorsColor color, int warmingUpTime) throws Exception {
        //logger.log(Level.FINEST, "openScoreboard => Start");
        //logger.log(Level.FINEST, "openScoreboard => match: {0}", match);
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
        //logger.log(Level.FINEST, "openScoreboard => End");
    }
    
    private boolean selectMatch(Competition competition, ArrayList<Match> selectableMatches) throws Exception{
        //logger.log(Level.FINEST, "selectMatch => Start");
        //logger.log(Level.FINEST, "selectMatch => competition: {0}", competition.getName());
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
            LeagueItem league = LeagueDataManager.getInstance(AppProperties.getInstance().getDataPath()).getLeague(competition.getLeague());
            PermittedValues.TurnIndicatorsColor turnindicatorsColor = null;
            int warmingupTime = 5;
            if(league!=null) {
                turnindicatorsColor = PermittedValues.TurnIndicatorsColor.valueOf(league.getTurnIndicatorsColor());
                warmingupTime = Integer.parseInt(league.getWarmingUpTime());
            }
            openScoreboard(selectedMatch, turnindicatorsColor, warmingupTime);
            if(selectedMatch.isEnded()) {
                //logger.log(Level.FINEST, "startTeamCompetition => Match ended: {0}", match);
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
        //logger.log(Level.FINEST, "selectMatch => match: {0}", selectedMatch);
        return false;
    }
    
    public void calculateCompetitionResult(TeamCompetition competition){
        //logger.log(Level.FINEST, "calculateCompetitionResult => Start");
        //logger.log(Level.FINEST, "calculateCompetitionResult => competition: {0}", competition.getName());
        int tsp1 = 0, points1 = 0, innings1 = 0, hr1 = 0, mp1= 0, cp1 = 0;
        int tsp2 = 0, points2 = 0, innings2 = 0, hr2 = 0, mp2= 0, cp2 = 0;
        float perc1, perc2;
        
        PointsSystemInterface pointSystem = PointSystemFactory.getPointSystem(competition.getPointsSystem());
        for(Match match: competition.getMatches()) {
            //logger.log(Level.FINEST, "calculateCompetitionResult => match: {0}", match);
            pointSystem.determineMatchPoints(match);
            matchManager.updateMatch(match);
            PlayerMatchResult playerResult = match.getPlayer1Result();
            if(competition.getTeam1().isPlayerOfTeam(match.getPlayer1())) {
                //logger.log(Level.FINEST, "calculateCompetitionResult => player1OfTeam1", match.getPlayer1().getName());
                tsp1 += match.getPlayer1().getTsp();
                points1+= playerResult.getPoints();
                innings1 += playerResult.getInnings();
                hr1 = (hr1>playerResult.getHighestRun()?hr1:playerResult.getHighestRun());
                mp1 += playerResult.getMatchPoints();
            } else if(competition.getTeam2().isPlayerOfTeam(match.getPlayer1())) {
                //logger.log(Level.FINEST, "calculateCompetitionResult => player1OfTeam2", match.getPlayer1().getName());
                tsp2 += match.getPlayer1().getTsp();
                points2+= playerResult.getPoints();
                innings2 += playerResult.getInnings();
                hr2 = (hr2>playerResult.getHighestRun()?hr2:playerResult.getHighestRun());
                mp2 += playerResult.getMatchPoints();
            }
            
            playerResult = match.getPlayer2Result();
            if(competition.getTeam1().isPlayerOfTeam(match.getPlayer2())) {
                //logger.log(Level.FINEST, "calculateCompetitionResult => player2OfTeam1", match.getPlayer2().getName());
                tsp1 += match.getPlayer1().getTsp();
                points1+= playerResult.getPoints();
                innings1 += playerResult.getInnings();
                hr1 = (hr1>playerResult.getHighestRun()?hr1:playerResult.getHighestRun());
                mp1 += playerResult.getMatchPoints();
            } else if(competition.getTeam2().isPlayerOfTeam(match.getPlayer2())) {
                //logger.log(Level.FINEST, "calculateCompetitionResult => player2OfTeam2", match.getPlayer2().getName());
                tsp2 += match.getPlayer1().getTsp();
                points2+= playerResult.getPoints();
                innings2 += playerResult.getInnings();
                hr2 = (hr2>playerResult.getHighestRun()?hr2:playerResult.getHighestRun());
                mp2 += playerResult.getMatchPoints();
            }
        }
        perc1 = points1 / tsp1;
        competition.setTeam1Result(new TeamResult(points1, innings1, hr1, mp1, perc1));
        perc2 = points2 / tsp2;
        competition.setTeam2Result(new TeamResult(points2, innings2, hr2, mp2, perc2));
        pointSystem.determineCompetitionPoints(competition);
        //logger.log(Level.FINEST, "calculateCompetitionResult => End");
    }
    
    private void selectTeamCompetition() throws Exception {
        //logger.log(Level.FINEST, "selectTeamCompetition => Start");
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
            } else if (action.equals(PermittedValues.Action.CANCEL)) {
                return;
            }
            //logger.log(Level.FINEST, "selectTeamCompetition => competition: {0}", selectedCompetition.getName());
        } else {
            selectedCompetition = newTeamCompetition();
        }
        if (null!=selectedCompetition) {
            teamCompetitionManager.putTeamCompetition(selectedCompetition);
            startTeamCompetition(selectedCompetition);
        }
    }
    
    private TeamCompetition newTeamCompetition() throws Exception {
        //logger.log(Level.FINEST, "startTeamCompetition => New Teamcompetition");
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
            //logger.log(Level.FINEST, "startTeamCompetition => New Teamcompetition Cancelled");
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
