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
import billiard.common.CommonDialogs;
import billiard.common.hazelcast.SyncManager;
import billiard.model.ScoreboardManager;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemListener;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
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
public class MenuController implements Initializable, ControllerInterface {
    private static final Logger LOGGER = Logger.getLogger(MenuController.class.getName());
    
    private Stage primaryStage;
    private int action;
    private ITopic topic;
    private String topicId;
    private PermittedValues.ActionObject  actionObject;
            
    @FXML
    private Circle ledConnected;
    @FXML
    private Tooltip ttConnected;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            if(SyncManager.isHazelcastEnabled()) {
                MatchManager matchManager = MatchManager.getInstance();
                topic = matchManager.getStartMatchTopic();
                topicId = topic.addMessageListener(new MessageListener() {
                    @Override
                    public void onMessage(Message message) {
                        StartMatchMessage msg = (StartMatchMessage )message.getMessageObject();
                        LOGGER.log(Level.FINEST, "Match received: " + msg.getScoreBoardId() + " - " + msg.getMatchId());
                        Match match = matchManager.getMatch(msg.getMatchId());
                        LOGGER.log(Level.FINEST, "Match found: " + match.toString());
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
                            LOGGER.severe(Arrays.toString(ex.getStackTrace()));
                            CommonDialogs.showException(ex);
                        }
                    }
                });
                ScoreboardManager scoreboardManager = ScoreboardManager.getInstance();
                if(scoreboardManager.nbrOfScoreboards() > 1) {
                    ledConnected.setFill(Paint.valueOf("green"));
                } else {
                    ledConnected.setFill(Paint.valueOf("red"));
                }
                scoreboardManager.addItemListener(new ItemListener() {
                    @Override
                    public void itemAdded(ItemEvent ie) {
                        if(scoreboardManager.nbrOfScoreboards() > 1) {
                            ledConnected.setFill(Paint.valueOf("green"));
                        } else {
                            ledConnected.setFill(Paint.valueOf("red"));
                        }
                    }

                    @Override
                    public void itemRemoved(ItemEvent ie) {
                        if(scoreboardManager.nbrOfScoreboards() > 1) {
                            ledConnected.setFill(Paint.valueOf("green"));
                        } else {
                            ledConnected.setFill(Paint.valueOf("red"));
                        }
                    }
                });
                
            }
        } catch (Exception ex) {
            LOGGER.severe(Arrays.toString(ex.getStackTrace()));
            CommonDialogs.showException(ex);
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
}
