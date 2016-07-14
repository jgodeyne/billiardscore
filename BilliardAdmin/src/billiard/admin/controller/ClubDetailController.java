/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.admin.controller;

import billiard.common.ControllerInterface;
import billiard.common.FormValidation;
import billiard.common.PermittedValues;
import billiard.data.ClubItem;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jean
 */
public class ClubDetailController implements Initializable, ControllerInterface {
    private Stage primaryStage;
    private PermittedValues.Action action;
    private ClubItem club;

    @FXML
    private TextField tfLic;
    @FXML
    private TextField tfName;
    @FXML
    private TextField tfContactName;
    @FXML
    private TextField tfContactEmail;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void onActionOK(ActionEvent event) {
        if (validForm()) {
            action = PermittedValues.Action.OK;
            primaryStage.hide();
        }
    }

    @FXML
    private void onActionCancel(ActionEvent event) {
        action = PermittedValues.Action.CANCEL;
        primaryStage.hide();
    }

    @Override
    public void initController(Stage stage) {
        primaryStage = stage;
    }

    public void setClub(ClubItem club) {
        this.club = club;
        tfLic.textProperty().bindBidirectional(this.club.getLicProp());
        tfName.textProperty().bindBidirectional(this.club.getNameProp());
        tfContactName.textProperty().bindBidirectional(this.club.getContactNameProp());
        tfContactEmail.textProperty().bindBidirectional(this.club.getContactEmailProp());
    }

    public PermittedValues.Action getAction() {
        return action;
    }
    
    private boolean validForm() {
        boolean result = FormValidation.validateTextField(tfLic)
                & FormValidation.validateTextField(tfName);
        return result;
    }
}
