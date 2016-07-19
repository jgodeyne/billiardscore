/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.admin.controller;

import billiard.common.ControllerInterface;
import billiard.common.hazelcast.SyncManager;
import billiard.model.ScoreboardManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
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
    private Stage stage;
    private int action = MenuOptions.EXIT;

    @FXML
    private HBox hbMenu1;
    @FXML
    private HBox hbMenu2;
    @FXML
    private HBox hbMenu3;
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
        this.stage = stage;
    }

    public int getAction() {
        return action;
    }

    @FXML
    private void adminPC() {
        action = MenuOptions.TC_ADMIN;
        stage.hide();
    }

    @FXML
    private void adminIC() {
        action = MenuOptions.IC_ADMIN;
        stage.hide();
    }

    @FXML
    private void adminLeague() {
        action = MenuOptions.LEAGUE_ADMIN;
        stage.hide();
    }

    @FXML
    private void exitApp() {
        action = MenuOptions.EXIT;
        stage.hide();
    }

    @FXML
    public void changeCursorToHand(MouseEvent event) {
        Scene scene = stage.getScene();
        scene.setCursor(Cursor.HAND);
        HBox box = (HBox) event.getSource();
        box.setStyle("-fx-background-color: #0093ff;");
    }

    @FXML
    private void changeCursorToDefault(MouseEvent event) {
        Scene scene = stage.getScene();
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

    public static class MenuOptions {
        public static final int EXIT = 0;
        public static final int TC_ADMIN = 1;
        public static final int IC_ADMIN = 2;
        public static final int LEAGUE_ADMIN = 3;
    }    
}
