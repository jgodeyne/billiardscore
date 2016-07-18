/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.data;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author jean
 */
public class TSPItem {
    private final StringProperty discipline = new SimpleStringProperty("");
    private final StringProperty tsp = new SimpleStringProperty("");

    public String getDiscipline() {
        return discipline.getValue();
    }

    public void setDiscipline(String discipline) {
        this.discipline.setValue(discipline);
    }
    
    public StringProperty getDisciplineProp() {
        return discipline;
    }
    
    public String getTsp() {
        return tsp.getValue();
    }

    public void setTsp(String tsp) {
        this.tsp.setValue(tsp);
    }
    
    public StringProperty getTspProp() {
        return tsp;
    }

    protected TSPItem copy() {
        TSPItem tspClone = new TSPItem();
        tspClone.setDiscipline(this.getDiscipline());
        tspClone.setTsp(this.getTsp());
        
        return tspClone;
    }
}
