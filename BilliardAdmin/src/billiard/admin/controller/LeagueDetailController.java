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
import billiard.data.LeagueItem;
import billiard.data.MemberItem;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jean
 */
public class LeagueDetailController implements Initializable, ControllerInterface {
    private Stage primaryStage;
    private LeagueItem league;
    private PermittedValues.Action action;
    private PermittedValues.ActionObject actionObject;
    private ClubItem selectedClub;
    private MemberItem selectedMember;
    
    @FXML
    private TextField tfName;
    @FXML
    private TextField tfContactName;
    @FXML
    private TextField tfContactEmail;
    @FXML
    private TextField tfWarmupTime;
    @FXML
    private Button btnNewClub;
    @FXML
    private Button btnUpdateClub;
    @FXML
    private Button btnDeleteClub;
    @FXML
    private Button btnNewMember;
    @FXML
    private Button btnUpdateMember;
    @FXML
    private Button btnDeleteMember;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnCancel;
    @FXML
    private ChoiceBox<String> cbSTartBalColor;
    @FXML
    private TableColumn<ClubItem, String> tcolClubLic;
    @FXML
    private TableColumn<ClubItem, String> tcolClubName;
    @FXML
    private TableColumn<MemberItem, String> tcolMemberLic;
    @FXML
    private TableColumn<MemberItem, String> tcolMemberName;
    @FXML
    private TableView<ClubItem> tableClubs;
    @FXML
    private TableView<MemberItem> tableMembers;
    @FXML
    private TextField tfClubLic;
    @FXML
    private TextField tfClubName;
    @FXML
    private TextField tfMemberLic;
    @FXML
    private TextField tfMemberName;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        actionObject = null;
        cbSTartBalColor.getItems().addAll(Arrays.asList(PermittedValues.COLOR_STARTING_BALL));
        cbSTartBalColor.getSelectionModel().selectFirst();

        tableClubs.getSelectionModel().selectedItemProperty()
            .addListener(new ChangeListener<ClubItem>() {
                @Override
                public void changed(ObservableValue<? extends ClubItem> observable,
                  ClubItem oldValue, ClubItem newValue) {
                    selectedClub = newValue;
                    populateMembers();
                    if(null==newValue) {
                        btnUpdateClub.setDisable(true);
                        btnDeleteClub.setDisable(true);
                        btnNewMember.setDisable(true);
                        btnUpdateMember.setDisable(true);
                        btnDeleteMember.setDisable(true);
                    } else {
                        btnUpdateClub.setDisable(false);
                        btnDeleteClub.setDisable(false);
                        btnNewMember.setDisable(false);
                        btnUpdateMember.setDisable(true);
                        btnDeleteMember.setDisable(true);
                    }
                }
            });

        tableMembers.getSelectionModel().selectedItemProperty()
            .addListener(new ChangeListener<MemberItem>() {
                @Override
                public void changed(ObservableValue<? extends MemberItem> observable,
                    MemberItem oldValue, MemberItem newValue) {
                    selectedMember=newValue;
                    if(null==newValue) {
                        btnUpdateMember.setDisable(true);
                        btnDeleteMember.setDisable(true);
                  } else {
                        btnUpdateMember.setDisable(false);
                        btnDeleteMember.setDisable(false);
                  }
              }
            });
    }    

    @Override
    public void initController(Stage stage) {
        primaryStage = stage;
    }

    public void setLeague(LeagueItem league) {
        this.league = league;
        tfName.textProperty().bindBidirectional(this.league.getNameProp());
        tfContactName.textProperty().bindBidirectional(this.league.getContactNameProp());
        tfContactEmail.textProperty().bindBidirectional(this.league.getContactEmailProp());
        tfWarmupTime.textProperty().bindBidirectional(this.league.getWarmingUpTimeProp());
        cbSTartBalColor.valueProperty().bindBidirectional(this.league.getTurnIndicatorsColorProp());
        populateClubs();
        populateMembers();

        btnDeleteClub.setDisable(true);
        btnUpdateClub.setDisable(true);
        
        btnNewMember.setDisable(true);
        btnUpdateMember.setDisable(true);
        btnDeleteMember.setDisable(true);
    }

    public PermittedValues.Action getAction() {
        return action;
    }

    public PermittedValues.ActionObject getActionObject() {
        return actionObject;
    }

    public ClubItem getSelectedClub() {
        return selectedClub;
    }

    public MemberItem getSelectedMember() {
        return selectedMember;
    }

    @FXML
    private void onActionNewClub(ActionEvent event) {
        actionObject = PermittedValues.ActionObject.CLUB;
        action=PermittedValues.Action.NEW;
        primaryStage.hide();
    }

    @FXML
    private void onActionUpdateClub(ActionEvent event) {
        actionObject = PermittedValues.ActionObject.CLUB;
        action=PermittedValues.Action.EDIT;
        primaryStage.hide();
    }

    @FXML
    private void onActionDeleteClub(ActionEvent event) {
        actionObject = PermittedValues.ActionObject.CLUB;
        action=PermittedValues.Action.DELETE;
        primaryStage.hide();
    }

    @FXML
    private void OnActionNewMember(ActionEvent event) {
        actionObject = PermittedValues.ActionObject.MEMBER;
        action=PermittedValues.Action.NEW;
        primaryStage.hide();
    }

    @FXML
    private void onActionUpdateMember(ActionEvent event) {
        actionObject = PermittedValues.ActionObject.MEMBER;
        action=PermittedValues.Action.EDIT;
        primaryStage.hide();
    }

    @FXML
    private void onActionDeleteMember(ActionEvent event) {
        actionObject = PermittedValues.ActionObject.MEMBER;
        action=PermittedValues.Action.DELETE;
        primaryStage.hide();
    }

    @FXML
    private void onActionSave(ActionEvent event) {
        if(validForm()) {
            action=PermittedValues.Action.SAVE;
            primaryStage.hide();
        }
    }

    @FXML
    private void onActionCancel(ActionEvent event) {
        action=PermittedValues.Action.CANCEL;
        primaryStage.hide();
    }
    
    private boolean validForm() {
        tfName.setText(tfName.getText().replace("/", "-"));
        boolean result = FormValidation.validateTextField(tfName);
        return result;
    }

    private void populateClubs() {
        ObservableList masterData = FXCollections.observableArrayList(this.league.getClubs().values());
        FilteredList<ClubItem> filteredData = new FilteredList<>(masterData, p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        tfClubLic.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(club -> {
                // If filter text is empty, display all persons.
                return club.getLic().contains(tfClubLic.getText().toUpperCase()) && club.getName().toLowerCase().contains(tfClubName.getText().toLowerCase()); 
            });
        });

        tfClubName.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(club -> {
                // If filter text is empty, display all persons.
                return club.getLic().contains(tfClubLic.getText().toUpperCase()) && club.getName().toLowerCase().contains(tfClubName.getText().toLowerCase()); 
            });
        });

        // 3. Wrap the FilteredList in a SortedList. 
        SortedList<ClubItem> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(tableClubs.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        tableClubs.setItems(sortedData);
        if (null!=selectedClub) {
            tableClubs.getSelectionModel().select(selectedClub);
        } else {
            tableClubs.getSelectionModel().clearSelection();
        }
        tcolClubLic.setCellValueFactory(cellData -> cellData.getValue().getLicProp());
        tcolClubName.setCellValueFactory(cellData -> cellData.getValue().getNameProp());        
    }
    
    private void populateMembers() {
        ObservableList masterData;
        if(null!= selectedClub) {
            masterData = FXCollections.observableArrayList(league.getMembersOfClub(selectedClub.getLic()).values());
        } else {
            masterData = FXCollections.observableArrayList(league.getMembers().values());
        }
        
        FilteredList<MemberItem> filteredData = new FilteredList<>(masterData, p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        tfMemberLic.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(member -> {
                // If filter text is empty, display all persons.
                return member.getLic().contains(tfMemberLic.getText().toUpperCase()) && member.getName().toLowerCase().contains(tfMemberName.getText().toLowerCase()); 
            });
        });

        tfMemberName.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(member -> {
                // If filter text is empty, display all persons.
                return member.getLic().contains(tfMemberLic.getText().toUpperCase()) && member.getName().toLowerCase().contains(tfMemberName.getText().toLowerCase()); 
            });
        });

        // 3. Wrap the FilteredList in a SortedList. 
        SortedList<MemberItem> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(tableMembers.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        tableMembers.setItems(sortedData);
        if(null!=selectedMember) {
            tableMembers.getSelectionModel().select(selectedMember);
        } else {
            tableMembers.getSelectionModel().clearSelection();
        }
        tcolMemberLic.setCellValueFactory(cellData -> cellData.getValue().getLicProp());
        tcolMemberName.setCellValueFactory(cellData -> cellData.getValue().getNameProp());
    }
}
