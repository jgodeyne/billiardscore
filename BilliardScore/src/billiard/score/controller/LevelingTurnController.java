/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.score.controller;

import billiard.model.Player;
import billiard.common.ControllerInterface;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jean
 */
public class LevelingTurnController implements Initializable, ControllerInterface {
    private Stage primaryStage;
    private Timer timer;
    private TimerTask task;
    
    @FXML
    private Text text_player;
    @FXML
    private Text text_points;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                if(null!=primaryStage) {
                    primaryStage.hide();
                }
                timer.cancel();
            }
        };
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, 10); // add 10 days
        
        timer.schedule(task, date);
    }    

    public void setData(Player player, int points) {
        text_player.setText(player.getName());
        text_points.setText("("+points+")");
    }

    @FXML
    private void btnOkOnAction(ActionEvent event) {
        primaryStage.hide();
        task.cancel();
        timer.cancel();
    }

    @Override
    public void initController(Stage stage) {
        this.primaryStage = stage;
    }
    
}
