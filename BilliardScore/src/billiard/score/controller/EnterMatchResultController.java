package billiard.score.controller;

import billiard.common.ControllerInterface;
import billiard.common.FormValidation;
import billiard.common.PermittedValues;
import billiard.model.Match;
import billiard.model.MatchManager;
import billiard.model.PlayerMatchResult;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jean
 */


public class EnterMatchResultController implements Initializable , ControllerInterface {
    private Stage stage;
    private PermittedValues.Action action = PermittedValues.Action.CANCEL;
    private Match match;
    
    @FXML
    private Label lblPlayer1Name;
    @FXML
    private Label lblPlayer1TSP;
    @FXML
    private Label lblPlayer1Discipline;
    @FXML
    private TextField tfPlayer1Points;
    @FXML
    private TextField tfPlayer1Innings;
    @FXML
    private TextField tfPlayer1HR;
    @FXML
    private Label lblPlayer2Name;
    @FXML
    private Label lblPlayer2TSP;
    @FXML
    private Label lblPlayer2Discipline;
    @FXML
    private TextField tfPlayer2Points;
    @FXML
    private TextField tfPlayer2Innings;
    @FXML
    private TextField tfPlayer2HR;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tfPlayer2Innings.textProperty().bindBidirectional(tfPlayer1Innings.textProperty());
    }    

    @FXML
    private void btnOK_OnAction(ActionEvent event) {
        if (validForm()) {
            action = PermittedValues.Action.OK;
            
            PlayerMatchResult player1Result 
                    = new PlayerMatchResult(Integer.parseInt(tfPlayer1Points.getText())
                            , Integer.parseInt(tfPlayer1Innings.getText())
                            , Integer.parseInt(tfPlayer1HR.getText()),
                            null, null);

            PlayerMatchResult player2Result 
                    = new PlayerMatchResult(Integer.parseInt(tfPlayer2Points.getText())
                            , Integer.parseInt(tfPlayer2Innings.getText())
                            , Integer.parseInt(tfPlayer2HR.getText()), 
                            null, null);

            int winner = 0;
            if(player1Result.getPoints() != match.getPlayer1().getTsp()) {
                winner = 2;
            } else if (player2Result.getPoints() != match.getPlayer2().getTsp()) {
                winner = 1;
            }
            match.setResult(winner, player1Result, player2Result);
            MatchManager.getInstance().updateMatch(match);

            stage.hide();
        }
    }

    @FXML
    private void btnCancel_OnAction(ActionEvent event) {
        action = PermittedValues.Action.CANCEL;
        stage.hide();
    }

    @Override
    public void initController(Stage stage) {
        this.stage = stage;
    }
    
    public void setMatch(Match match) {
        this.match = match;
        lblPlayer1Name.setText(match.getPlayer1().getName());
        lblPlayer1TSP.setText(match.getPlayer1().getTsp().toString());
        lblPlayer1Discipline.setText(match.getPlayer1().getDiscipline());
        lblPlayer2Name.setText(match.getPlayer2().getName());
        lblPlayer2TSP.setText(match.getPlayer2().getTsp().toString());
        lblPlayer2Discipline.setText(match.getPlayer2().getDiscipline());
    }
    
    public PermittedValues.Action getAction() {
        return action;
    }
    private boolean validForm() {
        boolean result=FormValidation.validateTextField(tfPlayer1Points, true)
                && FormValidation.validateTextField(tfPlayer1Innings, true)
                && FormValidation.validateTextField(tfPlayer1HR,true)
                && FormValidation.validateTextField(tfPlayer2Points, true)
                && FormValidation.validateTextField(tfPlayer2Innings, true)
                && FormValidation.validateTextField(tfPlayer2HR,true);
        
        if(Integer.parseInt(tfPlayer1Points.getText())>match.getPlayer1().getTsp()) {
            tfPlayer1Points.setStyle("-fx-background-color:red");
            result = false;
        } else {
            tfPlayer1Points.setStyle("-fx-background-coler:white");
        }
        
        if(Integer.parseInt(tfPlayer1HR.getText())>match.getPlayer1().getTsp()) {
            tfPlayer1HR.setStyle("-fx-background-color:red");
            result = false;
        } else {
            tfPlayer1HR.setStyle("-fx-background-coler:white");
        }

        if(Integer.parseInt(tfPlayer2Points.getText())>match.getPlayer2().getTsp()) {
            tfPlayer2Points.setStyle("-fx-background-color:red");
            result = false;
        } else {
            tfPlayer2Points.setStyle("-fx-background-coler:white");
        }
        
        if(Integer.parseInt(tfPlayer2HR.getText())>match.getPlayer2().getTsp()) {
            tfPlayer2HR.setStyle("-fx-background-color:red");
            result = false;
        } else {
            tfPlayer2HR.setStyle("-fx-background-coler:white");
        }
        return result;
    }
}
