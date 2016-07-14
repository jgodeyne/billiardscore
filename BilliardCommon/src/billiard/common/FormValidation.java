/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.common;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/**
 *
 * @author jean
 */
public class FormValidation {
    public static boolean validateTextField(TextField tf) {
        return validateTextField(tf, false);
    }
    
    public static boolean validateTextField(TextField tf, boolean numericCheck) {
        if(tf.getText().isEmpty()) {
            tf.setStyle("-fx-background-color:red");
            tf.requestFocus();
            return false;
        }
        if(numericCheck && !isNumeric(tf.getText())) {
            tf.setStyle("-fx-background-color:red");
            tf.requestFocus();
            return false;            
        }
        tf.setStyle("-fxbackground-color:white");
        return true;
    }
 
    public static boolean validateChoiceBox(ChoiceBox cb) {
        if(cb.getSelectionModel().isEmpty()) {
            cb.setStyle("-fx-background-color:red");
            cb.requestFocus();
            return false;
        }
        cb.setStyle("-fxbackground-color:white");
        return true;
    }
    
    public static boolean validateComboBox(ComboBox cb) {
        if(null!=cb.getValue() && cb.getValue().equals("")) {
            if (cb.getSelectionModel().isEmpty()) {
                cb.setStyle("-fx-background-color:red");
                cb.requestFocus();
                return false;
            }
        }
        cb.setStyle("-fxbackground-color:white");
        return true;
    }
    
    public static boolean isNumeric(String s) {
        return s.matches("\\d+");
    }
}
