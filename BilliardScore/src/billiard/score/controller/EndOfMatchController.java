/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.score.controller;

import billiard.model.Match;
import billiard.common.ControllerInterface;
import billiard.common.AppProperties;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jean
 */
public class EndOfMatchController implements Initializable, ControllerInterface {

    @FXML
    private Text text_message;
    @FXML
    private Text text_player;
    
    private Stage primaryStage;
    private ResourceBundle bundle;
    private Reply reply = EndOfMatchController.Reply.OK;
    @FXML
    private Label player1_points;
    @FXML
    private Label player1_innings;
    @FXML
    private Label player1_average;
    @FXML
    private Label player1_highrun;
    @FXML
    private Label player2_points;
    @FXML
    private Label player2_innings;
    @FXML
    private Label player2_average;
    @FXML
    private Label player2_highrun;
    @FXML
    private Label player1_name;
    @FXML
    private Label player2_name;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    public void setData(Match match) throws Exception {
        String strLocale=AppProperties.getInstance().getLocale();
        Locale locale = new Locale(strLocale);
        bundle = ResourceBundle.getBundle("languages.lang", locale);
        if (match.getWinner() == 0) {
            text_message.setText(bundle.getString("msg.endofmatch.gelijk"));
            text_player.setText(match.getPlayer1().getName() + " " + match.getPlayer2().getName());
        } else {
            text_message.setText(bundle.getString("msg.endofmatch.winnaar"));
            if (match.getWinner() == 1) {
                text_player.setText(match.getPlayer1().getName());
            }else if (match.getWinner() == 2) {
                text_player.setText(match.getPlayer2().getName());
            }
        }
        String dfFormat = "#0.000";
        DecimalFormat df = new DecimalFormat(dfFormat);
        df = new DecimalFormat(dfFormat);
        df.setRoundingMode(RoundingMode.DOWN);

        player1_name.setText(match.getPlayer1().getName());
        player1_points.setText(String.valueOf(match.getPlayer1Result().getPoints()));
        player1_innings.setText(String.valueOf(match.getPlayer1Result().getInnings()));
        player1_average.setText(df.format(match.getPlayer1Result().getAverage()));
        player1_highrun.setText(String.valueOf(match.getPlayer1Result().getHighestRun()));
        
        player2_name.setText(match.getPlayer2().getName());
        player2_points.setText(String.valueOf(match.getPlayer2Result().getPoints()));
        player2_innings.setText(String.valueOf(match.getPlayer2Result().getInnings()));
        player2_average.setText(df.format(match.getPlayer2Result().getAverage()));
        player2_highrun.setText(String.valueOf(match.getPlayer2Result().getHighestRun()));
        
    }

    @FXML
    private void btnOkOnAction(ActionEvent event) {
        reply = Reply.OK;
        primaryStage.hide();
    }

    @Override
    public void initController(Stage stage) {
        this.primaryStage = stage;
    }

    @FXML
    private void btnCorrect_OnAction(ActionEvent event) {
        reply = Reply.CORRECT;
        primaryStage.hide();
    }

    public Reply getReply() {
        return reply;
    }
    
    public enum Reply {
            OK, CORRECT;
    }
}
