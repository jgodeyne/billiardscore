/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.admin;

import billiard.admin.controller.ClubDetailController;
import billiard.admin.controller.IndividualCompetitionDetailController;
import billiard.admin.controller.IndividualCompetitionAdminController;
import billiard.admin.controller.IndividualPlayerDetailController;
import billiard.admin.controller.LeagueDetailController;
import billiard.admin.controller.LeagueAdminController;
import billiard.admin.controller.MemberDetailController;
import billiard.admin.controller.MenuController;
import billiard.admin.controller.TeamCompetitionDetailController;
import billiard.admin.controller.TeamCompetitionAdminController;
import billiard.admin.controller.TeamPlayerDetailController;
import billiard.admin.controller.TeamDetailController;
import billiard.common.AppProperties;
import billiard.common.CommonDialogs;
import billiard.common.InitAppConfig;
import billiard.common.PermittedValues;
import billiard.common.SceneUtil;
import billiard.common.hazelcast.SendDataMessage;
import billiard.common.hazelcast.SyncManager;
import billiard.data.ClubItem;
import billiard.data.IndividualCompetitionDataManager;
import billiard.data.IndividualCompetitionItem;
import billiard.data.LeagueDataManager;
import billiard.data.LeagueItem;
import billiard.data.MemberItem;
import billiard.data.TeamCompetitionDataManager;
import billiard.data.TeamCompetitionItem;
import billiard.data.PlayerItem;
import billiard.data.TeamItem;
import com.hazelcast.core.ITopic;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author jean
 */
public class BilliardAdmin extends Application {
    private static final Logger LOGGER = Logger.getLogger(BilliardAdmin.class.getName());

    private ResourceBundle bundle;
    private TeamCompetitionDataManager teamCompetitionManager;
    private LeagueDataManager leagueManager;
    private IndividualCompetitionDataManager individualCompetitionManager;
    private LeagueItem selectedLeagueItem;
    private String selectedDiscipline = "";
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
            
            leagueManager = LeagueDataManager.getInstance();
            
            int menuChoice;
            do {
                menuChoice = choseAction();
                if (menuChoice==MenuController.MenuOptions.TC_ADMIN) {
                    startTeamCompetitionAdmin();
                } else if (menuChoice==MenuController.MenuOptions.IC_ADMIN) {
                    startIndividualCompetitionAdmin();
                } else if (menuChoice==MenuController.MenuOptions.LEAGUE_ADMIN) {
                    startLeagueAdmin();
                }
            } while(menuChoice!=MenuController.MenuOptions.EXIT);
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
    public static void main(String[] args) {
        launch(args);
    }

    private int choseAction() throws Exception{
        //logger.log(Level.FINEST, "choseAction => Start");
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML.MENU),bundle);
        Parent root = loader.load();

        Stage primaryStage = new Stage();
        MenuController controller = loader.<MenuController>getController();
        controller.initController(primaryStage);
        
        primaryStage.setTitle("BilliardAdmin" + " - (Version: " + this.getClass().getPackage().getImplementationVersion() +")");
        Scene scene = new Scene(root);
        SceneUtil.setStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.showAndWait();
        int action = controller.getAction();
        //logger.log(Level.FINEST, "choseAction => action: {0}", action);
        //logger.log(Level.FINEST, "choseAction => End");
        return action;
    }
    
    private void startTeamCompetitionAdmin() throws Exception {
        //logger.log(Level.FINEST, "startTeamCompetitionAdmin => Start");
        teamCompetitionManager = TeamCompetitionDataManager.getInstance();
        PermittedValues.Action action;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML.TC_ADMIN),bundle);
        Parent root;
        root = loader.load();

        Stage primaryStage = new Stage();
        TeamCompetitionAdminController controller = loader.<TeamCompetitionAdminController>getController();
        controller.initController(primaryStage);

        primaryStage.setTitle(bundle.getString("titel.admin.ploegencompetitie"));
        Scene scene = new Scene(root);
        SceneUtil.setStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        do {
            ArrayList<String> teamCompetitionList =  teamCompetitionManager.getCompetitionNames();
            controller.setCompetitions(teamCompetitionList);
            primaryStage.showAndWait();
            primaryStage.requestFocus();
            
            action = controller.getAction();
            if(action.equals(PermittedValues.Action.NEW)) {
                TeamCompetitionItem competition = new TeamCompetitionItem();
                openTeamCompetitionDetail(PermittedValues.Mode.NEW, competition);
            } else if (action.equals(PermittedValues.Action.EDIT)) {
                String competitionName = controller.getSelectedCompetitionName();
                TeamCompetitionItem competition = teamCompetitionManager.getCompetition(competitionName);
                TeamCompetitionItem competitionBackup = competition.copy();
                if (!openTeamCompetitionDetail(PermittedValues.Mode.EDIT, competition)) {
                    //restore from backup
                    teamCompetitionManager.restore(competitionBackup);
                }
            } else if (action.equals(PermittedValues.Action.DELETE)) {
                String competitionName = controller.getSelectedCompetitionName();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                SceneUtil.setStylesheet(alert.getDialogPane());
                alert.setTitle(bundle.getString("titel.verwijderen.competitie"));
                alert.setHeaderText(null);
                alert.setContentText(competitionName + " "+ bundle.getString("msg.verwijderen"));

                Optional<ButtonType> answer = alert.showAndWait();
                if (answer.get() == ButtonType.OK){
                    TeamCompetitionItem competition = teamCompetitionManager.getCompetition(competitionName);
                    teamCompetitionManager.removeCompetition(competition);
                }

            } else if (action.equals(PermittedValues.Action.EXPORT)) {
                String competitionName = controller.getSelectedCompetitionName();
                DirectoryChooser dirChooser = new DirectoryChooser();
                dirChooser.setTitle(bundle.getString("titel.kies.locatie"));
                File destDir = dirChooser.showDialog(mainStage);
                if(null!=destDir) {
                    teamCompetitionManager.export(competitionName, destDir);
                }
            } else if (action.equals(PermittedValues.Action.IMPORT)) {
                importData(PermittedValues.ActionObject.TEAM_COMP);
            } else if (action.equals(PermittedValues.Action.SEND)) {
                String name = controller.getSelectedCompetitionName();
                TeamCompetitionItem item = teamCompetitionManager.getCompetition(name);
                sendDataAsXML(PermittedValues.ActionObject.TEAM_COMP, item.toXML());
            }      
        } while (!action.equals(PermittedValues.Action.CLOSE));     
    }
    
    private void startIndividualCompetitionAdmin() throws Exception {
        //logger.log(Level.FINEST, "startIndividualCompetitionAdmin => Start");
        individualCompetitionManager = IndividualCompetitionDataManager.getInstance();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML.IC_ADMIN),bundle);
        Parent root;
        root = loader.load();

        Stage primaryStage = new Stage();
        IndividualCompetitionAdminController controller = loader.<IndividualCompetitionAdminController>getController();
        controller.initController(primaryStage);

        primaryStage.setTitle(bundle.getString("titel.admin.individuelecompetitie"));
        Scene scene = new Scene(root);
        SceneUtil.setStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        PermittedValues.Action action;
        do {
            ArrayList<String> competitionList =  individualCompetitionManager.getCompetitionNames();
            controller.setCompetitions(competitionList);
            primaryStage.showAndWait();
            
            action = controller.getAction();
            if(action.equals(PermittedValues.Action.NEW)) {
                IndividualCompetitionItem competition = new IndividualCompetitionItem();
                openIndividualCompetitionDetail(PermittedValues.Mode.NEW, competition);
            } else if (action.equals(PermittedValues.Action.EDIT)) {
                String competitionName = controller.getSelectedCompetitionName();
                IndividualCompetitionItem competition = individualCompetitionManager.getCompetition(competitionName);
                IndividualCompetitionItem competitionBackup = competition.copy();
                if (!openIndividualCompetitionDetail(PermittedValues.Mode.EDIT, competition)) {
                    //restore from backup
                    individualCompetitionManager.restore(competitionBackup);
                }
            } else if (action.equals(PermittedValues.Action.DELETE)) {
                String competitionName = controller.getSelectedCompetitionName();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                SceneUtil.setStylesheet(alert.getDialogPane());
                alert.setTitle(bundle.getString("titel.verwijderen.competitie"));
                alert.setHeaderText(null);
                alert.setContentText(competitionName + " "+ bundle.getString("msg.verwijderen"));
                
                Optional<ButtonType> answer = alert.showAndWait();
                if (answer.get() == ButtonType.OK){
                    IndividualCompetitionItem competition = individualCompetitionManager.getCompetition(competitionName);
                    individualCompetitionManager.removeCompetition(competition);
                }
            } else if (action.equals(PermittedValues.Action.EXPORT)) {
                String competitionName = controller.getSelectedCompetitionName();
                DirectoryChooser dirChooser = new DirectoryChooser();
                dirChooser.setTitle(bundle.getString("titel.kies.locatie"));
                File destDir = dirChooser.showDialog(mainStage);
                individualCompetitionManager.export(competitionName, destDir);
            } else if (action.equals(PermittedValues.Action.IMPORT)) {
                importData(PermittedValues.ActionObject.IND_COMP);
            } else if (action.equals(PermittedValues.Action.SEND)) {
                String name = controller.getSelectedCompetitionName();
                IndividualCompetitionItem item = individualCompetitionManager.getCompetition(name);
                sendDataAsXML(PermittedValues.ActionObject.IND_COMP, item.toXML());
            }      
        } while (!action.equals(PermittedValues.Action.CLOSE));     
    }
    
    private void startLeagueAdmin() throws Exception {
        PermittedValues.Action action;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML.LEAGUE_ADMIN),bundle);
        Parent root;
        root = loader.load();

        Stage primaryStage = new Stage();
        LeagueAdminController controller = loader.<LeagueAdminController>getController();
        controller.initController(primaryStage);

        primaryStage.setTitle(bundle.getString("titel.admin.ledenlijst"));
        Scene scene = new Scene(root);
        SceneUtil.setStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        do {
            ArrayList<String> leagueList =  leagueManager.getLeagueNames();
            controller.setLeagues(leagueList);
            primaryStage.showAndWait();
            primaryStage.requestFocus();
            
            action = controller.getAction();
            if(action.equals(PermittedValues.Action.NEW)) {
                LeagueItem league = new LeagueItem();
                openLeagueDetail(PermittedValues.Mode.NEW, league);
            } else if (action.equals(PermittedValues.Action.EDIT)) {
                String leagueName = controller.getSelectedLeagueName();
                LeagueItem league = leagueManager.getLeague(leagueName);
                LeagueItem leagueBackup = league.copy();
                if(!openLeagueDetail(PermittedValues.Mode.EDIT, league)) {
                    //restore from backup
                    leagueManager.restoreLeague(leagueBackup);
                }
            } else if (action.equals(PermittedValues.Action.DELETE)) {
                String leagueName = controller.getSelectedLeagueName();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                SceneUtil.setStylesheet(alert.getDialogPane());
                alert.setTitle(bundle.getString("titel.verwijderen.ledenlijst"));
                alert.setHeaderText(null);
                alert.setContentText(leagueName + " "+ bundle.getString("msg.verwijderen"));
                
                Optional<ButtonType> answer = alert.showAndWait();
                if (answer.get() == ButtonType.OK){
                    LeagueItem league = leagueManager.getLeague(leagueName);
                    leagueManager.removeLeague(league);
                }
            } else if (action.equals(PermittedValues.Action.EXPORT)) {
                String leagueName = controller.getSelectedLeagueName();
                DirectoryChooser dirChooser = new DirectoryChooser();
                dirChooser.setTitle(bundle.getString("titel.kies.locatie"));
                File destDir = dirChooser.showDialog(mainStage);
                if(null!=destDir) {
                    leagueManager.exportLeague(leagueName, destDir);
                }
            } else if (action.equals(PermittedValues.Action.IMPORT)) {
                importData(PermittedValues.ActionObject.LEAGUE);
            } else if (action.equals(PermittedValues.Action.SEND)) {
                String leagueName = controller.getSelectedLeagueName();
                LeagueItem league = leagueManager.getLeague(leagueName);
                sendDataAsXML(PermittedValues.ActionObject.LEAGUE, league.toXML());
            }      
        } while (!action.equals(PermittedValues.Action.CLOSE));     
    }
    
    private boolean openTeamCompetitionDetail(PermittedValues.Mode mode, TeamCompetitionItem competition) throws Exception {
        boolean result = true;
        PermittedValues.Action action;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML.TC_DETAIL),bundle);
        Parent root;
        root = loader.load();

        Stage primaryStage = new Stage();
        TeamCompetitionDetailController controller = loader.<TeamCompetitionDetailController>getController();
        controller.initController(primaryStage);

        if(mode.equals(PermittedValues.Mode.NEW)) {
            primaryStage.setTitle(bundle.getString("titel.nieuwe.ploegencompetitie"));
        }else if (mode.equals(PermittedValues.Mode.EDIT)) {
            primaryStage.setTitle(bundle.getString("titel.wijzigen.ploegencompetitie")+ " - " + competition.getName());
        }
        Scene scene = new Scene(root);
        SceneUtil.setStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        do {
            controller.setCompetition(competition);
            primaryStage.showAndWait();
            primaryStage.requestFocus();
            
            selectedLeagueItem = leagueManager.getLeague(competition.getLeague());
            selectedDiscipline = competition.getDiscipline();
            
            action = controller.getAction();
            if(action.equals(PermittedValues.Action.NEW)) {
                TeamItem team = new TeamItem();
                if(openTeamDetail(PermittedValues.Mode.NEW, team)) {
                    competition.putTeam(team);
                }
            } else if (action.equals(PermittedValues.Action.EDIT)) {
                String teamName = controller.getSelectedTeam();
                TeamItem team = competition.getTeam(teamName);
                TeamItem teamBackup = team.copy();
                if(!openTeamDetail(PermittedValues.Mode.EDIT, team)) {
                    //restore from backup
                    competition.putTeam(teamBackup);
                }
            } else if (action.equals(PermittedValues.Action.DELETE)) {
                String teamName = controller.getSelectedTeam();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                SceneUtil.setStylesheet(alert.getDialogPane());
                alert.setTitle(bundle.getString("titel.verwijderen.ploeg"));
                alert.setHeaderText(null);
                alert.setContentText(teamName + " "+ bundle.getString("msg.verwijderen"));
                
                Optional<ButtonType> answer = alert.showAndWait();
                if (answer.get() == ButtonType.OK){
                    TeamItem team = competition.getTeam(teamName);
                    competition.removeTeam(team);
                }
            } else if (action.equals(PermittedValues.Action.SAVE)) {
                if(mode.equals(PermittedValues.Mode.NEW)) {
                    teamCompetitionManager.addCompetition(competition);
                } else {
                    teamCompetitionManager.saveCompetition(competition);
                }
                break;
            } else if (action.equals(PermittedValues.Action.CANCEL)) {
                result=false;
                break;
            }
        } while (true);
        return result;
    }
            
    private boolean openIndividualCompetitionDetail(PermittedValues.Mode mode, IndividualCompetitionItem competition) throws Exception {
        boolean result = true;
        PermittedValues.Action action;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML.IC_DETAIL),bundle);
        Parent root;
        root = loader.load();

        Stage primaryStage = new Stage();
        IndividualCompetitionDetailController controller = loader.<IndividualCompetitionDetailController>getController();
        controller.initController(primaryStage);

        if(mode.equals(PermittedValues.Mode.NEW)) {
            primaryStage.setTitle(bundle.getString("titel.nieuwe.individuelecompetitie"));
        }else if (mode.equals(PermittedValues.Mode.EDIT)) {
            primaryStage.setTitle(bundle.getString("titel.wijzigen.individuelecompetitie")+ " - " + competition.getName());
        }
        Scene scene = new Scene(root);
        SceneUtil.setStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        do {
            controller.setCompetition(competition);
            primaryStage.showAndWait();
            primaryStage.requestFocus();
            
            selectedLeagueItem = leagueManager.getLeague(competition.getLeague());
            selectedDiscipline = competition.getDiscipline();

            action = controller.getAction();
            if(action.equals(PermittedValues.Action.NEW)) {
                PlayerItem player = new PlayerItem();
                if(openIndividualPlayerDetail(PermittedValues.Mode.NEW, player)) {
                    competition.putPlayer(player);
                }
            } else if (action.equals(PermittedValues.Action.EDIT)) {
                PlayerItem player = controller.getSelectedPlayer();
                PlayerItem playerBackup = player.copy();
                if(!openIndividualPlayerDetail(PermittedValues.Mode.EDIT, player)) {
                    //restore from backup
                    competition.putPlayer(playerBackup);
                }
            } else if (action.equals(PermittedValues.Action.DELETE)) {
                PlayerItem player = controller.getSelectedPlayer();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                SceneUtil.setStylesheet(alert.getDialogPane());
                alert.setTitle(bundle.getString("titel.verwijderen.speler"));
                alert.setHeaderText(null);
                alert.setContentText(player.getName() + " "+ bundle.getString("msg.verwijderen"));
                
                Optional<ButtonType> answer = alert.showAndWait();
                if (answer.get() == ButtonType.OK){
                    competition.removePlayer(player);
                }
            } else if (action.equals(PermittedValues.Action.SAVE)) {
                if(mode.equals(PermittedValues.Mode.NEW)) {
                    individualCompetitionManager.addCompetition(competition);
                } else {
                    individualCompetitionManager.saveCompetition(competition);
                }
                break;
            } else if (action.equals(PermittedValues.Action.CANCEL)) {
                result=false;
                break;
            }
        } while (true);
        return result;
    }

    private boolean openTeamDetail(PermittedValues.Mode mode, TeamItem team) throws Exception {
        boolean result = true;
        PermittedValues.Action action;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML.TEAM_DETAIL),bundle);
        Parent root;
        root = loader.load();

        Stage primaryStage = new Stage();
        TeamDetailController controller = loader.<TeamDetailController>getController();
        controller.initController(primaryStage);

        if(mode.equals(PermittedValues.Mode.NEW)) {
            primaryStage.setTitle(bundle.getString("titel.nieuwe.ploeg"));
        }else if (mode.equals(PermittedValues.Mode.EDIT)) {
            primaryStage.setTitle(bundle.getString("titel.wijzigen.ploeg")+ " - " + team.getName());
        }
        Scene scene = new Scene(root);
        SceneUtil.setStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        do {
            controller.setTeam(team);
            primaryStage.showAndWait();
            primaryStage.requestFocus();
            
            action = controller.getAction();
            if(action.equals(PermittedValues.Action.NEW)) {
                PlayerItem player = new PlayerItem();
                if (openTeamPlayerDetail(PermittedValues.Mode.NEW, player)) {
                    team.putPlayer(player);
                }
            } else if (action.equals(PermittedValues.Action.EDIT)) {
                PlayerItem player = controller.getSelectedPlayer();
                PlayerItem playerBackup = player.copy();
                if (!openTeamPlayerDetail(PermittedValues.Mode.EDIT, player)) {
                    //restore from backup
                    team.putPlayer(playerBackup);
                }
            } else if (action.equals(PermittedValues.Action.DELETE)) {
                PlayerItem player = controller.getSelectedPlayer();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                SceneUtil.setStylesheet(alert.getDialogPane());
                alert.setTitle(bundle.getString("titel.verwijderen.speler"));
                alert.setHeaderText(null);
                alert.setContentText(player.getName() + " "+ bundle.getString("msg.verwijderen"));
                
                Optional<ButtonType> answer = alert.showAndWait();
                if (answer.get() == ButtonType.OK){
                    team.removePlayer(player);
                }
            } else if (action.equals(PermittedValues.Action.OK)) {
                break;
            } else if (action.equals(PermittedValues.Action.CANCEL)) {
                result=false;
                break;
            }
        } while (true);
        return result;
    }
        
    private boolean openTeamPlayerDetail(PermittedValues.Mode mode, PlayerItem player) throws Exception {
        boolean result = true;
        PermittedValues.Action action;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML.TEAM_PLAYER_DETAIL),bundle);
        Parent root;
        root = loader.load();

        if (!selectedDiscipline.isEmpty()) {
            player.setDiscipline(selectedDiscipline);
        }

        Stage primaryStage = new Stage();
        TeamPlayerDetailController controller = loader.<TeamPlayerDetailController>getController();
        controller.initController(primaryStage);

        if(mode.equals(PermittedValues.Mode.NEW)) {
            primaryStage.setTitle(bundle.getString("titel.nieuwe.speler"));
        }else if (mode.equals(PermittedValues.Mode.EDIT)) {
            primaryStage.setTitle(bundle.getString("titel.wijzigen.speler")+ " - " + player.getName());
        }
        Scene scene = new Scene(root);
        SceneUtil.setStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        do {
            controller.setPlayer(player);
            controller.setLeague(selectedLeagueItem);
            primaryStage.showAndWait();
            primaryStage.requestFocus();
            
            action = controller.getAction();
            if(action.equals(PermittedValues.Action.OK)) {
                break;
            } else if (action.equals(PermittedValues.Action.CANCEL)) {
                result = false;
                break;
            }
        } while (true); 
        return result;
    }

    private boolean openIndividualPlayerDetail(PermittedValues.Mode mode, PlayerItem player) throws Exception {
        boolean result = true;
        PermittedValues.Action action;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML.INDIVIDUAL_PLAYER_DETAIL),bundle);
        Parent root;
        root = loader.load();

        if (!selectedDiscipline.isEmpty()) {
            player.setDiscipline(selectedDiscipline);
        }

        Stage primaryStage = new Stage();
        IndividualPlayerDetailController controller = loader.<IndividualPlayerDetailController>getController();
        controller.initController(primaryStage);

        if(mode.equals(PermittedValues.Mode.NEW)) {
            primaryStage.setTitle(bundle.getString("titel.nieuwe.speler"));
        }else if (mode.equals(PermittedValues.Mode.EDIT)) {
            primaryStage.setTitle(bundle.getString("titel.wijzigen.speler")+ " - " + player.getName());
        }
        Scene scene = new Scene(root);
        SceneUtil.setStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        do {
            controller.setPlayer(player);
            controller.setLeague(selectedLeagueItem);
            primaryStage.showAndWait();
            primaryStage.requestFocus();
            
            action = controller.getAction();
            if(action.equals(PermittedValues.Action.OK)) {
                break;
            } else if (action.equals(PermittedValues.Action.CANCEL)) {
                result = false;
                break;
            }
        } while (true); 
        return result;
    }

    private boolean openLeagueDetail(PermittedValues.Mode mode, LeagueItem league) throws Exception {
        boolean result = true;
        PermittedValues.Action action;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML.LEAGUE_DETAIL),bundle);
        Parent root;
        root = loader.load();

        Stage primaryStage = new Stage();
        LeagueDetailController controller = loader.<LeagueDetailController>getController();
        controller.initController(primaryStage);

        if(mode.equals(PermittedValues.Mode.NEW)) {
            primaryStage.setTitle(bundle.getString("titel.nieuwe.ledenlijst"));
        }else if (mode.equals(PermittedValues.Mode.EDIT)) {
            primaryStage.setTitle(bundle.getString("titel.wijzigen.ledenlijst")+ " - " + league.getName());
        }
        Scene scene = new Scene(root);
        SceneUtil.setStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        do {
            controller.setLeague(league);
            primaryStage.showAndWait();
            primaryStage.requestFocus();
            
            PermittedValues.ActionObject actionObject = controller.getActionObject();
            action = controller.getAction();
            
            if (action.equals(PermittedValues.Action.SAVE)) {
                if(mode.equals(PermittedValues.Mode.NEW)) {
                    leagueManager.addLeague(league);
                } else {
                    leagueManager.saveLeague(league);
                }
                break;
            } else if (action.equals(PermittedValues.Action.CANCEL)) {
                result = false;
                break;
            } else if(null!=actionObject && actionObject == PermittedValues.ActionObject.CLUB) {
                if(action.equals(PermittedValues.Action.NEW)) {
                    ClubItem club = new ClubItem();
                    if(openClubDetail(PermittedValues.Mode.NEW, club)) {
                        league.putClub(club);
                    }
                } else if (action.equals(PermittedValues.Action.EDIT)) {
                    ClubItem club = controller.getSelectedClub();
                    ClubItem clubBackup = club.copy();
                    if (!openClubDetail(PermittedValues.Mode.EDIT, club)) {
                       //restore from backup
                       league.putClub(clubBackup);
                    }
                } else if (action.equals(PermittedValues.Action.DELETE)) {
                    ClubItem club = controller.getSelectedClub();
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    SceneUtil.setStylesheet(alert.getDialogPane());
                    alert.setTitle(bundle.getString("titel.verwijderen.club"));
                    alert.setHeaderText(null);
                    alert.setContentText(club.getName() + " "+ bundle.getString("msg.verwijderen"));

                    Optional<ButtonType> answer = alert.showAndWait();
                    if (answer.get() == ButtonType.OK){
                        league.removeClub(club);
                    }
                } 
            } else if(null!=actionObject && actionObject == PermittedValues.ActionObject.MEMBER) {
                if(action.equals(PermittedValues.Action.NEW)) {
                    MemberItem member = new MemberItem(controller.getSelectedClub().getLic());
                    if (openMemberDetail(PermittedValues.Mode.NEW, member)) {
                        league.putMember(member);
                    }
                } else if (action.equals(PermittedValues.Action.EDIT)) {
                    MemberItem member = controller.getSelectedMember();
                    MemberItem memberBackup = member.copy();
                    if(!openMemberDetail(PermittedValues.Mode.EDIT, member)) {
                        //restore from backup
                        league.putMember(memberBackup);
                    }
                } else if (action.equals(PermittedValues.Action.DELETE)) {
                    MemberItem member = controller.getSelectedMember();
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    SceneUtil.setStylesheet(alert.getDialogPane());
                    alert.setTitle(bundle.getString("titel.verwijderen.lid"));
                    alert.setHeaderText(null);
                    alert.setContentText(member.getName() + " "+ bundle.getString("msg.verwijderen"));

                    Optional<ButtonType> answer = alert.showAndWait();
                    if (answer.get() == ButtonType.OK){
                        league.removeMember(member);
                    }
                } 
            } 
        } while (true);  
        return result;
    }

    private boolean openClubDetail(PermittedValues.Mode mode, ClubItem club) throws Exception {
        boolean result = true;
        PermittedValues.Action action;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML.CLUB_DETAIL),bundle);
        Parent root;
        root = loader.load();

        Stage primaryStage = new Stage();
        ClubDetailController controller = loader.<ClubDetailController>getController();
        controller.initController(primaryStage);

        if(mode.equals(PermittedValues.Mode.NEW)) {
            primaryStage.setTitle(bundle.getString("titel.nieuwe.club"));
        }else if (mode.equals(PermittedValues.Mode.EDIT)) {
            primaryStage.setTitle(bundle.getString("titel.wijzigen.club")+ " - " + club.getName());
        }
        Scene scene = new Scene(root);
        SceneUtil.setStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        do {
            controller.setClub(club);
            primaryStage.showAndWait();
            primaryStage.requestFocus();
            
            action = controller.getAction();
            if(action.equals(PermittedValues.Action.OK)) {
                break;
            } else if (action.equals(PermittedValues.Action.CANCEL)) {
                result = false;
                break;
            }
        } while (true); 
        return result;
    }
    
    private boolean openMemberDetail(PermittedValues.Mode mode, MemberItem member) throws Exception {
        boolean result = true;
        PermittedValues.Action action;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML.MEMBER_DETAIL),bundle);
        Parent root;
        root = loader.load();

        Stage primaryStage = new Stage();
        MemberDetailController controller = loader.<MemberDetailController>getController();
        controller.initController(primaryStage);

        if(mode.equals(PermittedValues.Mode.NEW)) {
            primaryStage.setTitle(bundle.getString("titel.nieuw.lid"));
        }else if (mode.equals(PermittedValues.Mode.EDIT)) {
            primaryStage.setTitle(bundle.getString("titel.wijzigen.lid")+ " - " + member.getName());
        }
        Scene scene = new Scene(root);
        SceneUtil.setStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        do {
            controller.setMember(member);
            primaryStage.showAndWait();
            primaryStage.requestFocus();
            
            action = controller.getAction();
            if(action.equals(PermittedValues.Action.OK)) {
                break;
            } else if (action.equals(PermittedValues.Action.CANCEL)) {
                result = false;
                break;
            }
        } while (true); 
        return result;
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
    
    private void sendDataAsXML(PermittedValues.ActionObject actionObject, Object dataObject) {
        ITopic sendDataTopic = SyncManager.getHazelCastInstance().getTopic("send_data");
        SendDataMessage msg = new SendDataMessage(actionObject, dataObject);
        sendDataTopic.publish(msg);
    }
    
    public class FXML {
        private static final String FXML_LOC = "/billiard/admin/fxml/";
        public static final String MENU = FXML_LOC + "Menu.fxml";
        public static final String TC_ADMIN = FXML_LOC + "TeamCompetitionAdmin.fxml";
        public static final String TC_DETAIL = FXML_LOC + "TeamCompetitionDetail.fxml";
        public static final String TEAM_DETAIL = FXML_LOC + "TeamDetail.fxml";
        public static final String TEAM_PLAYER_DETAIL = FXML_LOC + "TeamPlayerDetail.fxml";
        public static final String INDIVIDUAL_PLAYER_DETAIL = FXML_LOC + "IndividualPlayerDetail.fxml";
        public static final String LEAGUE_ADMIN = FXML_LOC + "LeagueAdmin.fxml";        
        public static final String LEAGUE_DETAIL = FXML_LOC + "LeagueDetail.fxml";
        public static final String CLUB_DETAIL = FXML_LOC + "ClubDetail.fxml";
        public static final String MEMBER_DETAIL = FXML_LOC + "MemberDetail.fxml";
        public static final String IC_ADMIN = FXML_LOC + "IndividualCompetitionAdmin.fxml";
        public static final String IC_DETAIL = FXML_LOC + "IndividualCompetitionDetail.fxml";
        public static final String SEARCH_MEMBER = FXML_LOC + "SearchMember.fxml";
    }   
}
