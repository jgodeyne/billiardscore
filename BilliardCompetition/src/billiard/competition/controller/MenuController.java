/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.competition.controller;

import billiard.common.ControllerInterface;
import billiard.common.PermittedValues;
import billiard.common.SceneUtil;
import billiard.common.hazelcast.SyncManager;
import billiard.competition.BilliardCompetition;
import billiard.model.ScoreboardManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author jean
 */
public class MenuController implements Initializable,  ControllerInterface {
    private Stage primaryStage;

    @FXML
    private HBox hbMenu1;
    @FXML
    private HBox hbMenu2;
    @FXML
    private HBox hbMenu4;
    @FXML
    private Tooltip ttConnected;
    @FXML
    private Circle ledConnected;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if(SyncManager.isHazelcastEnabled()) {
            ledConnected.setFill(Paint.valueOf("green"));
        } else {
            ledConnected.setFill(Paint.valueOf("red"));
        }
    }    

    @Override
    public void initController(Stage stage) {
        this.primaryStage = stage;
    }

    @FXML
    private void adminPC() {
        //TODO: 
    }

    @FXML
    private void adminIC() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(BilliardCompetition.FXML.INDIVIDUAL_TOURNAMENT_MANAGEMENT));
        Parent root;
        root = loader.load();

        IndividualTournamentManagementController controller = loader.<IndividualTournamentManagementController>getController();
        Stage stage = new Stage();
        controller.initController(stage);

        stage.setTitle("Selecteer Tornooi");
        Scene scene = new Scene(root);
        SceneUtil.setStylesheet(scene);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.showAndWait();        
    }

    @FXML
    private void exitApp() {
        primaryStage.hide();
    }

    @FXML
    public void changeCursorToHand(MouseEvent event) {
        Scene scene = primaryStage.getScene();
        scene.setCursor(Cursor.HAND);
        HBox box = (HBox) event.getSource();
        box.setStyle("-fx-background-color: #0093ff;");
    }

    @FXML
    private void changeCursorToDefault(MouseEvent event) {
        Scene scene = primaryStage.getScene();
        scene.setCursor(Cursor.DEFAULT);
        HBox box = (HBox) event.getSource();
        box.setStyle("-fx-background-color: #666666;");
    }

    @FXML
    private void onShowningttConnected(WindowEvent event) {
        int nbrOfScoreboards = ScoreboardManager.getInstance().listScoreboards().size();
        String tooltip = "Nbr of Scoreboards: " + nbrOfScoreboards;
        int idx = 0;
        for (String name : ScoreboardManager.getInstance().listScoreboards()) {
            tooltip += System.lineSeparator() + ++idx +": " + name;
        }
        ttConnected.setText(tooltip);
    }

    public static enum MenuOption {
        EXIT, TEAM_COMP , IND_COMP;
    }    
}
