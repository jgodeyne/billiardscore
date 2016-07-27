/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.common;

import billiard.common.controller.SearchMemberController;
import billiard.data.LeagueItem;
import billiard.data.MemberItem;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author jean
 */
public class CommonDialogs {

    public static MemberItem searchMember(LeagueItem league) throws Exception {
        String strLocale=AppProperties.getInstance().getLocale();
        Locale locale = new Locale(strLocale);
        ResourceBundle bundle = ResourceBundle.getBundle("languages.lang", locale);

        FXMLLoader loader = new FXMLLoader(CommonDialogs.class.getResource("/billiard/common/fxml/SearchMember.fxml"),bundle);
        Parent root = loader.load();

        Stage secondaryStage = new Stage();
        SearchMemberController controller = loader.<SearchMemberController>getController();
        controller.initController(secondaryStage);
        controller.setLeague(league);
        
        secondaryStage.setTitle(bundle.getString("titel.zoek.lid"));
        Scene scene = new Scene(root);
        SceneUtil.setStylesheet(scene);
        secondaryStage.setScene(scene);
        secondaryStage.centerOnScreen();
        secondaryStage.initModality(Modality.APPLICATION_MODAL);
        secondaryStage.showAndWait();
        
        return controller.getSelectedMember();
    }
    
    public static void showException(Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Severe Application Error");
        alert.setHeaderText("Please contact app provider and supply message below");
        alert.setContentText(ex.toString());

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        ex.printStackTrace();

        alert.showAndWait();        
    }
}
