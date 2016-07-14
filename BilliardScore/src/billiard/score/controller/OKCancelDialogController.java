/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.score.controller;

import billiard.common.ControllerInterface;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jean
 */
public class OKCancelDialogController implements Initializable, ControllerInterface {
    private Stage primaryStage;
    private String answer="CANCEL";

    @FXML
    private Text txtMsg;
    @FXML
    private Button pbOK;
    @FXML
    private Button pbCancel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        pbOK.setVisible(false);
        pbOK.setManaged(false);
        pbCancel.setVisible(false);
        pbCancel.setManaged(false);
    }    

    @FXML
    private void btnOK_OnAction(ActionEvent event) {
        answer = "OK";
        primaryStage.hide();
    }

    @FXML
    private void btnCancel_OnAction(ActionEvent event) {
        answer = "CANCEL";
        primaryStage.hide();
    }
    
    public void setMessage(String msg) {
        txtMsg.setText(msg);
    }
    
    public void setMode(Mode mode) {
        if(mode.equals(Mode.OK)) {
            pbOK.setVisible(true);
            pbOK.setManaged(true);
        } else if (mode.equals(Mode.OKCANCEL)) {
            pbOK.setVisible(true);
            pbOK.setManaged(true);
            pbCancel.setVisible(true);
            pbCancel.setManaged(true);
        }
    }

    @Override
    public void initController(Stage stage) {
        primaryStage = stage;
    }
    
    public String getAnswer() {
        return answer;
    }
    
    public enum Mode {
        OK, OKCANCEL 
    }    
}
