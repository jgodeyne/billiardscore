/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.admin.controller;

import billiard.common.ControllerInterface;
import billiard.common.FormValidation;
import billiard.common.PermittedValues;
import billiard.data.MemberItem;
import billiard.data.TSPItem;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jean
 */
public class MemberDetailController implements Initializable, ControllerInterface {
    private Stage primaryStage;
    private PermittedValues.Action action;
    private MemberItem member;

    @FXML
    private TextField tfLic;
    @FXML
    private TextField tfName;
    @FXML
    private TableView<TSPItem> tblTsps;
    @FXML
    private TableColumn<TSPItem, String> tcDiscipline;
    @FXML
    private TableColumn<TSPItem, String> tcTsp;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tcTsp.setCellFactory(TextFieldTableCell.<TSPItem>forTableColumn());
        tcTsp.setOnEditCommit(
            (TableColumn.CellEditEvent <TSPItem, String> t) -> {
                ((TSPItem) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                        ).setTsp(t.getNewValue());
        });
    }    

    @FXML
    private void onActionButtonOK(ActionEvent event) {
        if(validForm()) {
            action = PermittedValues.Action.OK;
            primaryStage.hide();
        }
    }

    @FXML
    private void onActionButtonCancel(ActionEvent event) {
        action = PermittedValues.Action.CANCEL;
        primaryStage.hide();
    }

    @Override
    public void initController(Stage stage) {
        primaryStage = stage;
    }

    public void setMember(MemberItem member) {
        this.member = member;
        tfLic.textProperty().bindBidirectional(this.member.getLicProp());
        tfName.textProperty().bindBidirectional(this.member.getNameProp());
        tcDiscipline.setCellValueFactory(cellData -> cellData.getValue().getDisciplineProp());
        tcTsp.setCellValueFactory(cellData -> cellData.getValue().getTspProp());
        tblTsps.setItems(FXCollections.observableArrayList(this.member.getTsps().values()));
        tblTsps.getSelectionModel().clearSelection();
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
