/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.competition.controller;

import billiard.common.AppProperties;
import billiard.common.CommonDialogs;
import billiard.common.ControllerInterface;
import billiard.common.PermittedValues;
import billiard.common.SceneUtil;
import billiard.competition.BilliardCompetition;
import billiard.model.IndividualTournament;
import billiard.model.IndividualTournamentManager;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jean
 */
public class IndividualTournamentManagementController implements Initializable, ControllerInterface {
    private static final Logger LOGGER = Logger.getLogger(IndividualTournamentManagementController.class.getName());

    private Stage primaryStage;
    private ResourceBundle bundle;
    private ObservableList<String> tournamentNames;
    @FXML
    private ListView<String> lvTournaments;
    @FXML
    private Button pbNew;
    @FXML
    private Button pbEdit;
    @FXML
    private Button pbDelete;
    @FXML
    private Button pbExit;
    
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
            
            lvTournaments.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            lvTournaments.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if(null!=newValue && !newValue.isEmpty()) {
                        pbEdit.setDisable(false);
                        pbDelete.setDisable(false);
                    } else {
                        pbEdit.setDisable(true);
                        pbDelete.setDisable(true);
                    }
                }
            });
            tournamentNames = FXCollections.observableArrayList(IndividualTournamentManager.getInstance().getIndividualTournamentNames());
            lvTournaments.setItems(tournamentNames);
            
            pbEdit.setDisable(true);
            pbDelete.setDisable(true);
        } catch (Exception ex) {
            LOGGER.severe(Arrays.toString(ex.getStackTrace()));
            CommonDialogs.showException(ex);
        }
    }

    @Override
    public void initController(Stage stage) {
        this.primaryStage = stage;
    }

    @FXML
    private void onActionButtonNew(ActionEvent event) throws Exception {
        IndividualTournament tournament = new IndividualTournament("","","");
        openIndividualCompetitionDetail(PermittedValues.Mode.NEW, tournament);
        tournamentNames.setAll(IndividualTournamentManager.getInstance().getIndividualTournamentNames());
    }

    @FXML
    private void onActionButtonEdit(ActionEvent event) throws Exception {
        String selectedTournament=lvTournaments.getSelectionModel().getSelectedItem();
        IndividualTournament tournament = IndividualTournamentManager.getInstance().getIndividualTournament(selectedTournament);
        openIndividualCompetitionDetail(PermittedValues.Mode.EDIT, tournament);
        tournamentNames.setAll(IndividualTournamentManager.getInstance().getIndividualTournamentNames());
    }

    @FXML
    private void onActionButtonDelete(ActionEvent event) throws Exception {
        String selectedTournament=lvTournaments.getSelectionModel().getSelectedItem();
        IndividualTournament tournament = IndividualTournamentManager.getInstance().getIndividualTournament(selectedTournament);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        SceneUtil.setStylesheet(alert.getDialogPane());
        alert.setTitle(bundle.getString("titel.verwijderen.competitie"));
        alert.setHeaderText(null);
        alert.setContentText(tournament.getName() + " "+ bundle.getString("msg.verwijderen"));

        Optional<ButtonType> answer = alert.showAndWait();
        if (answer.get() == ButtonType.OK) {
            IndividualTournamentManager.getInstance().removeIndividualTournament(tournament);
            tournamentNames.setAll(IndividualTournamentManager.getInstance().getIndividualTournamentNames());
        }
    }

    @FXML
    private void onActionButtonExit(ActionEvent event) {
        primaryStage.hide();
    }
    
    private void openIndividualCompetitionDetail(PermittedValues.Mode mode, IndividualTournament tournament) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(BilliardCompetition.FXML.INDIVIDUAL_TOURNAMENT_DETAIL));
        Parent root;
        root = loader.load();

        IndividualTournamentDetailController controller = loader.<IndividualTournamentDetailController>getController();
        Stage stage = new Stage();
        controller.initController(stage);
        controller.setTournament(tournament);

        stage.setTitle("Individueel tornooi");
        Scene scene = new Scene(root);
        SceneUtil.setStylesheet(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(primaryStage);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.showAndWait();
    }

}
