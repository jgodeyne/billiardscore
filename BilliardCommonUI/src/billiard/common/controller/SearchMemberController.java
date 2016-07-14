/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.common.controller;

import billiard.common.ControllerInterface;
import billiard.data.LeagueItem;
import billiard.data.MemberItem;
import java.net.URL;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jean
 */
public class SearchMemberController implements Initializable, ControllerInterface {
    private MemberItem selectedMember;
    private Stage primaryStage;
    private LeagueItem league;

    @FXML
    private TextField tfMemberName;
    @FXML
    private TableView<MemberItem> tableMembers;
    @FXML
    private TableColumn<MemberItem, String> tcolMemberLic;
    @FXML
    private TableColumn<MemberItem, String> tcolMemberName;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnCancel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tableMembers.getSelectionModel().selectedItemProperty()
            .addListener(new ChangeListener<MemberItem>() {
                @Override
                public void changed(ObservableValue<? extends MemberItem> observable,
                    MemberItem oldValue, MemberItem newValue) {
                    selectedMember=newValue;
                    if(null==newValue) {
                        btnSave.setDisable(true);
                  } else {
                        btnSave.setDisable(false);
                  }
              }
            });
    }    

    @FXML
    private void onActionSave(ActionEvent event) {
        primaryStage.hide();
    }

    @FXML
    private void onActionCancel(ActionEvent event) {
        selectedMember = null;
        primaryStage.hide();
    }

    @Override
    public void initController(Stage stage) {
        primaryStage = stage;
    }

    public void setLeague(LeagueItem league) {
        this.league = league;
        populateMembers();

        btnSave.setDisable(true);
    }

    public MemberItem getSelectedMember() {
        return selectedMember;
    }

    private void populateMembers() {
        ObservableList masterData;
        masterData = FXCollections.observableArrayList(league.getMembers().values());
        
        FilteredList<MemberItem> filteredData = new FilteredList<>(masterData, p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        tfMemberName.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(member -> {
                // If filter text is empty, display all persons.
                return member.getName().toLowerCase().contains(tfMemberName.getText().toLowerCase()); 
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
