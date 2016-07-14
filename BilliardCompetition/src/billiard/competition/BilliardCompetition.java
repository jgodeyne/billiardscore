/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.competition;

import billiard.common.InitAppConfig;
import billiard.common.SceneUtil;
import billiard.competition.controller.IndividualTournamentController;
import billiard.competition.controller.SelectIndividualTournamentController;
import billiard.common.hazelcast.SyncManager;
import billiard.model.IndividualTournament;
import billiard.model.IndividualTournamentManager;
import java.util.ArrayList;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 *
 * @author jean
 */
public class BilliardCompetition extends Application {
    private static final Logger LOGGER = Logger.getLogger(BilliardCompetition.class.getName());
    private static final String INDIVIDUAL_TOURNAMENT_FORM = "/billiard/competition/fxml/IndividualTournament.fxml";
    private static final String SELECT_INDIVIDUAL_TOURNAMENT = "/billiard/competition/fxml/SelectIndividualTournament.fxml";

    private Stage mainStage;    
    
    @Override
    public void start(Stage stage) {
        try {
            InitAppConfig.initAppConfig(this);
            mainStage = new Stage();
            
            SyncManager.start();
            
            startIndividualTournament();
//        int action;
//        do {
//            action = choseAction();
//            if (action==PermittedValues.MenuOptions.SCORE_BOARD) {
//                startScoreboard();
//            } else if (action==PermittedValues.MenuOptions.INDIVIDUAL) {
//                if (LicenceManager.getLicence()==LicenceManager.FULL_LICENCE) {
//                    startIndividualCompetition();
//                } else {
//                    showLicenceIssueDialog();
//                }
//            } else if (action==PermittedValues.MenuOptions.TEAM) {
//                if (LicenceManager.getLicence()==LicenceManager.FULL_LICENCE) {
//                    startTeamCompetition();
//                } else {
//                    showLicenceIssueDialog();
//                }
//            } else if (action==PermittedValues.MenuOptions.SELECT) {
//                selectTeamCompetition();
//            }
//        } while(action!=0);

            SyncManager.stop();
            mainStage.close();
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Please contact app provider and supply message below");
            alert.setContentText(ex.toString());
            LOGGER.severe(ex.toString());

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
    
    private void startIndividualTournament() throws Exception {
        IndividualTournament tournament=selectIndividualTournament();
        if(tournament!=null) {
            IndividualTournamentManager.getInstance().putIndividualTournament(tournament);
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource(INDIVIDUAL_TOURNAMENT_FORM));
        Parent root;
        root = loader.load();

        IndividualTournamentController controller = loader.<IndividualTournamentController>getController();
        controller.initController(mainStage);
        controller.setTournament(tournament);

        mainStage.setTitle("Individueel tornooi");
        Scene scene = new Scene(root);
        SceneUtil.setStylesheet(scene);
        mainStage.setScene(scene);
        mainStage.centerOnScreen();
        mainStage.showAndWait();
    }

    private IndividualTournament selectIndividualTournament() throws Exception {
        ArrayList<IndividualTournament> list = IndividualTournamentManager.loadIndividualTournaments();
        if(list.isEmpty()) {
            return null;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource(SELECT_INDIVIDUAL_TOURNAMENT));
        Parent root;
        root = loader.load();

        SelectIndividualTournamentController controller = loader.<SelectIndividualTournamentController>getController();
        controller.initController(mainStage);
        controller.setTournaments(list);

        mainStage.setTitle("Selecteer Tornooi");
        Scene scene = new Scene(root);
        SceneUtil.setStylesheet(scene);
        mainStage.setScene(scene);
        mainStage.centerOnScreen();
        mainStage.showAndWait();
        
        return controller.getSelectedTournament();
    }
}
