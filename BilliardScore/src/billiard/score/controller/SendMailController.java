package billiard.score.controller;

import billiard.common.ControllerInterface;
import billiard.common.PermittedValues;
import billiard.common.PermittedValues.Action;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jean
 */


public class SendMailController implements Initializable, ControllerInterface {
    private Stage stage;
    private Action action;
    
    @FXML
    private TextField tfTo;
    @FXML
    private TextField tfCC;
    @FXML
    private TextArea tfMsg;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        action = Action.CANCEL;
    }    

    @FXML
    private void btnSend_OnAction(ActionEvent event) {
        action = Action.OK;
        stage.hide();
    }

    @FXML
    private void btnCancel_OnAction(ActionEvent event) {
        action = Action.CANCEL;
        stage.hide();
    }

    @Override
    public void initController(Stage stage) {
        this.stage = stage;
    }
    
    public void setData(String to) {
        tfTo.setText(to);
    }

    public PermittedValues.Action getAction() {
        return action;
    }
    
    public String getTo() {
        return  tfTo.getText();
    }
    public String getCC() {
        return tfCC.getText();
    }
    
    public String getMessage() {
        return tfMsg.getText();
    }
}
