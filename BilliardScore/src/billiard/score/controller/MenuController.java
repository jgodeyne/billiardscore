/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.score.controller;

import billiard.common.ControllerInterface;
import billiard.common.PermittedValues;
import billiard.common.hazelcast.StartMatchMessage;
import billiard.model.Match;
import billiard.model.MatchManager;
import billiard.score.BilliardScore;
import billiard.common.AppProperties;
import billiard.common.hazelcast.SyncManager;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jean
 */
public class MenuController implements Initializable, ControllerInterface {
    private Stage primaryStage;
    private int action;
    private ITopic topic;
    private String topicId;
    private PermittedValues.ActionObject  actionObject;
            
    @FXML
    private Circle ledConnected;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if(SyncManager.isHazelcastEnabled()) {
            ledConnected.setFill(Paint.valueOf("green"));
            MatchManager matchManager = MatchManager.getInstance();
            topic = matchManager.getStartMatchTopic();
            topicId = topic.addMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    StartMatchMessage msg = (StartMatchMessage )message.getMessageObject();
                    System.out.println("Match received: " + msg.getScoreBoardId() + " - " + msg.getMatchId());
                    Match match = matchManager.getMatch(msg.getMatchId());
                    System.out.println("Match found: " + match.toString());
                    String scoreboardId;
                    try {
                        scoreboardId = AppProperties.getInstance().getScoreboardId();
                        if (msg.getScoreBoardId().equals(scoreboardId)) {
                            action = BilliardScore.MenuOptions.TOURNAMENT;
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    close();
                                }
                            });                        
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
        } else {
            ledConnected.setFill(Paint.valueOf("red"));
        }
    }
    
    @FXML
    public void simpleScorebord() {
        action = BilliardScore.MenuOptions.SCORE_BOARD;
        close();
    }
    
    @FXML
    public void individualMatch() {
        action =BilliardScore.MenuOptions.INDIVIDUAL;
        close();
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

    @Override
    public void initController(Stage stage) {
        this.primaryStage = stage;
    }

    @FXML
    private void teamMatch() {
        action = BilliardScore.MenuOptions.TEAM;
        close();
    }

    @FXML
    private void exit() {
        action = 0;
        close();
    }

    private void close() {
        if(null!=topic) {
            topic.removeMessageListener(topicId);
        }
        primaryStage.hide();
    }

    public int getAction() {
        return action;
    }

    public PermittedValues.ActionObject getActionObject() {
        return actionObject;
    }

    @FXML
    private void btnConf_OnAction(ActionEvent event) {
        action = BilliardScore.MenuOptions.CONF;
        close();
    }

    @FXML
    private void importLeague(ActionEvent event) {
        action = BilliardScore.MenuOptions.IMPORT;
        actionObject = PermittedValues.ActionObject.LEAGUE;
        close();
    }

    @FXML
    private void importIndividualCompetition(ActionEvent event) {
        action = BilliardScore.MenuOptions.IMPORT;
        actionObject = PermittedValues.ActionObject.IND_COMP;
        close();
    }

    @FXML
    private void importTeamCompetition(ActionEvent event) {
        action = BilliardScore.MenuOptions.IMPORT;
        actionObject = PermittedValues.ActionObject.TEAM_COMP;
        close();
    }
}
