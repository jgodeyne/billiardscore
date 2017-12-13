/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.score.controller;

import billiard.common.ControllerInterface;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jean
 */
public class WarmingUpCountDownController implements Initializable, ControllerInterface {
    private static final Logger LOGGER = Logger.getLogger(WarmingUpCountDownController.class.getName());
    private Stage primaryStage;
    private final Timer timer = new Timer();
    private TimerTask task;
    private StringProperty strTime;
    private long elapseTime = 5 * 60 * 1000;
    private final long oneMinute = 60 * 1000;
    private long remaining = 0;
    private Date start;
    private Date end;
    private Date oneMinuteLeft;
    private boolean timerRunning = false;
    private boolean paused = false;

    @FXML
    private Text txtTimer;
    @FXML
    private Button btnStart;
    @FXML
    private Button btnStop;
    @FXML
    private Button btnReset;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        strTime = new SimpleStringProperty("");
        txtTimer.textProperty().bind(strTime);
        txtTimer.setFill(Paint.valueOf("Black"));
        btnStop.setDisable(true);
        btnReset.setDisable(true);
    }    

    @Override
    public void initController(Stage stage) {
        primaryStage = stage;
    }

    @FXML
    private void btnClose_OnAction(ActionEvent event) {
        timer.cancel();
        timer.purge();
        primaryStage.close();
    }
    
    private void startTimer() {
        LOGGER.log(Level.FINEST, "startTimer");

        oneMinuteLeft = new Date(end.getTime() - oneMinute);
        task = new TimerTask() {

            @Override
            public void run() {
                LOGGER.log(Level.FINEST, "startTimer => run");
                Date current = new Date();
                remaining = end.getTime() - current.getTime();
                setStrTime(remaining);
                if(current.after(oneMinuteLeft)) {
                    txtTimer.setFill(Paint.valueOf("Red"));
                } else {
                    txtTimer.setFill(Paint.valueOf("Black"));                    
                }
                if (current.after(end)) {
                    btnStop_OnAction(null);
//                    task.cancel();
//                    timerRunning = false;
                }
            }
        };
        timer.schedule(task, 0, 1000);     
        timerRunning = true;
    }
    
    public void setWarmingUpTime(int minutes) {
        elapseTime = minutes * 60 * 1000;
        setStrTime(elapseTime);
    }

    private void setStrTime(long millis) {
        strTime.setValue(String.format("%1d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(millis),
            TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)));
    }
    @FXML
    private void btnStart_OnAction(ActionEvent event) {
        btnStart.setDisable(true);
        btnStop.setDisable(false);
        btnReset.setDisable(true);
        if(paused) {
            start = new Date();
            end = new Date(start.getTime() + remaining);
            paused = false;
        } else {
            start = new Date();
            end = new Date(start.getTime() + elapseTime);
        }
        startTimer();
    }

    @FXML
    private void btnStop_OnAction(ActionEvent event) {
        btnStop.setDisable(true);
        btnStart.setDisable(false);
        btnReset.setDisable(false);
        if(timerRunning) {
            task.cancel();
            timer.purge();
            timerRunning = false;
            paused = true;
        }
    }

    @FXML
    private void btnReset_OnAction(ActionEvent event) {
        btnReset.setDisable(true);
        btnStop.setDisable(true);
        btnStart.setDisable(false);
        start = new Date();
        end = new Date(start.getTime() + elapseTime);
        txtTimer.setFill(Paint.valueOf("Black"));                    
        setStrTime(elapseTime);
        paused = false;
    }

}
