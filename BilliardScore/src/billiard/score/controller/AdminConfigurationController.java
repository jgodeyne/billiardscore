/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.score.controller;

import billiard.common.ControllerInterface;
import billiard.data.LeagueDataManager;
import billiard.common.AppProperties;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jean
 */
public class AdminConfigurationController implements Initializable , ControllerInterface{
    private Stage stage;
    private AppProperties appProp;

    @FXML
    private TextField tfBilliardScoreId;
    @FXML
    private TextField tfLocale;
    @FXML
    private TextField tfTitle;
    @FXML
    private TextField tfSubTitle;
    @FXML
    private TextField tfClub;
    @FXML
    private TextField tfEmailServer;
    @FXML
    private TextField tfEmailSender;
    @FXML
    private TextField tfEmailUID;
    @FXML
    private TextField tfEmailPWD;
    @FXML
    private ChoiceBox<String> cbLeague;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            LeagueDataManager leagueMngr = LeagueDataManager.getInstance(AppProperties.getInstance().getDataPath());
            cbLeague.getItems().addAll(leagueMngr.getLeagueNames());
            appProp = AppProperties.getInstance();
            tfBilliardScoreId.setText(appProp.getScoreboardId());
            tfClub.setText(appProp.getClub());
            tfEmailPWD.setText(appProp.getEmailPWD());
            tfEmailSender.setText(appProp.getEmailSender());
            tfEmailServer.setText(appProp.getEmailServer());
            tfEmailUID.setText(appProp.getEmailUID());
            tfLocale.setText(appProp.getLocale());
            tfSubTitle.setText(appProp.getSubTitle());
            tfTitle.setText(appProp.getTitle());
            String leagueName = appProp.getDefaultLeague();
            cbLeague.getSelectionModel().select(leagueName);
            
        } catch (Exception ex) {
            Logger.getLogger(AdminConfigurationController.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    @FXML
    private void btnClose_OnAction(ActionEvent event) {
        stage.hide();
    }

    @Override
    public void initController(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void btnSave_OnAction(ActionEvent event) {
        try {
            appProp.setClub(tfClub.getText());
            appProp.setEmailPWD(tfEmailPWD.getText());
            appProp.setEmailSender(tfEmailSender.getText());
            appProp.setEmailServer(tfEmailServer.getText());
            appProp.setEmailUID(tfEmailUID.getText());
            appProp.setLocale(tfLocale.getText());
            appProp.setScoreboardId(tfBilliardScoreId.getText());
            appProp.setSubTitle(tfSubTitle.getText());
            appProp.setTitle(tfTitle.getText());
            appProp.setDefaultLeague(cbLeague.getValue());
            appProp.save();
        } catch (Exception ex) {
            Logger.getLogger(AdminConfigurationController.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
        this.stage.hide();
    }    
}
