/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.common;

import java.io.File;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

/**
 *
 * @author jean
 */
public class SceneUtil {

    public static void setStylesheet(Scene scene) {
        File f = InitAppConfig.getCSSFile();
        scene.getStylesheets().clear();
        scene.getStylesheets().add("file:" + f.getAbsolutePath().replace("\\", "/"));
    }
    
    public static void setStylesheet(Pane pane) {
        File f = InitAppConfig.getCSSFile();
        pane.getStylesheets().clear();
        pane.getStylesheets().add("file:" + f.getAbsolutePath().replace("\\", "/"));
    }
    
}
