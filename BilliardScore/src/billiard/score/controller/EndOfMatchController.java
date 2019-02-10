/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.score.controller;

import billiard.model.Match;
import billiard.common.ControllerInterface;
import billiard.common.AppProperties;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
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
public class EndOfMatchController implements Initializable, ControllerInterface {

    @FXML
    private Text text_message;
    @FXML
    private Text text_player;
    
    private Stage primaryStage;
    private ResourceBundle bundle;
    private Reply reply = EndOfMatchController.Reply.OK;
    
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
