/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.competition;

import billiard.common.AppProperties;
import billiard.common.CommonDialogs;
import billiard.common.InitAppConfig;
import billiard.common.SceneUtil;
import billiard.common.hazelcast.SyncManager;
import billiard.competition.controller.MenuController;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
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

    @Override
    public void start(Stage stage) {
        try {
            InitAppConfig.initAppConfig(this);
            Stage mainStage = new Stage();
            
            SyncManager.start();

            String strLocale=AppProperties.getInstance().getLocale();
            Locale locale = new Locale(strLocale);
            ResourceBundle bundle = ResourceBundle.getBundle("languages.lang", locale);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML.MENU),bundle);
            Parent root = loader.load();

            Stage primaryStage = new Stage();
            MenuController controller = loader.<MenuController>getController();
            controller.initController(primaryStage);

            primaryStage.setTitle("BilliardCompetition" + " - (Version: " + this.getClass().getPackage().getImplementationVersion() +")");
            Scene scene = new Scene(root);
            SceneUtil.setStylesheet(scene);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.showAndWait();

            SyncManager.stop();
            mainStage.close();
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
    
    public class FXML {
        private static final String FXML_LOC = "/billiard/competition/fxml/";
        public static final String MENU = FXML_LOC + "Menu.fxml";
        public static final String INDIVIDUAL_TOURNAMENT_DETAIL = FXML_LOC + "IndividualTournamentDetail.fxml";
        public static final String INDIVIDUAL_TOURNAMENT_MANAGEMENT = FXML_LOC + "IndividualTournamentManagement.fxml";
    }
}
