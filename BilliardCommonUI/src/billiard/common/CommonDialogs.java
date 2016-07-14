/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.common;

import billiard.common.controller.SearchMemberController;
import billiard.data.LeagueItem;
import billiard.data.MemberItem;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
}
