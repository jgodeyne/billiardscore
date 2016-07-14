/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.admin.controller;

import billiard.common.ControllerInterface;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
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

    public static class MenuOptions {
        public static final int EXIT = 0;
        public static final int TC_ADMIN = 1;
        public static final int IC_ADMIN = 2;
        public static final int LEAGUE_ADMIN = 3;
    }    
}
