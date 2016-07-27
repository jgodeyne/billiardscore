/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.score.controller;

import billiard.model.Match;
import billiard.common.ControllerInterface;
import billiard.common.PermittedValues;
import billiard.common.AppProperties;
import billiard.common.CommonDialogs;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jean
 */
public class SelectTeamMatchController implements Initializable, ControllerInterface {
    private static final Logger LOGGER = Logger.getLogger(SelectTeamMatchController.class.getName());
    private Stage primaryStage;
    private ArrayList<Match> matches;
    private Match selectedMatch;
    private ResourceBundle bundle;
    private PermittedValues.Action action = PermittedValues.Action.CANCEL;

    @FXML
    private ListView<Match> listMatches;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            String strLocale=AppProperties.getInstance().getLocale();
            Locale locale = new Locale(strLocale);
            bundle = ResourceBundle.getBundle("languages.lang", locale);
            
            selectedMatch = null;
            listMatches.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        } catch (Exception ex) {
            LOGGER.severe(Arrays.toString(ex.getStackTrace()));
            CommonDialogs.showException(ex);
        }
    }

    public void setMatches(ArrayList<Match> matches) {
        this.matches = matches;
        ArrayList matchList = new ArrayList();
        for (Match match: matches){
            matchList.add(match.getPlayer1().getName()+ " - " + match.getPlayer2().getName());
        }
        listMatches.setItems(FXCollections.observableArrayList(matchList));
        listMatches.getSelectionModel().selectFirst();
    }

    public Match getSelectedMatch() {
        return selectedMatch;
    }

    @Override
    public void initController(Stage stage) {
        this.primaryStage = stage;
    }

    @FXML
    private void onActionButtonSelect(ActionEvent event) {
        selectedMatch=matches.get(listMatches.getSelectionModel().getSelectedIndex());
        action = PermittedValues.Action.SELECT;
        this.primaryStage.hide();
    }

    @FXML
    private void onActionButtonCancel(ActionEvent event) {
        action = PermittedValues.Action.CANCEL;
        primaryStage.hide();
    }

    @FXML
    private void btnPrint_OnAction(ActionEvent event) throws Exception {
        Match match=matches.get(listMatches.getSelectionModel().getSelectedIndex());
        ScoreSheetController.showScoreSheet(match, PermittedValues.Mode.READ_ONLY);
    }

    @FXML
    private void btnResult_OnAction(ActionEvent event) throws Exception {
        selectedMatch = matches.get(listMatches.getSelectionModel().getSelectedIndex());
        action = PermittedValues.Action.EDIT;
        primaryStage.hide();
    } 

    public PermittedValues.Action getAction() {
        return action;
    }
}
