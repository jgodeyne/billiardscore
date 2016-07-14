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
public class PlayerItem {
    private final StringProperty order= new SimpleStringProperty("");
    private final StringProperty lic = new SimpleStringProperty("");
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty discipline = new SimpleStringProperty("");
    private final StringProperty tsp = new SimpleStringProperty("");
    private final StringProperty club = new SimpleStringProperty("");

    public String getOrder() {
        return order.getValue();
    }

    public void setOrder(String order) {
        this.order.setValue(order);
    }
    
    public StringProperty getOrderProp() {
        return order;
    }

    public String getLic() {
        return lic.getValue();
    }

    public void setLic(String lic) {
        this.lic.setValue(lic);
    }
    
    public StringProperty getLicProp() {
        return lic;
    }

    public String getName() {
        return name.getValue();
    }

    public void setName(String name) {
        this.name.setValue(name);
    }
    
    public StringProperty getNameProp() {
        return name;
    }

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

    public String getClub() {
        return club.getValue();
    }

    public void setClub(String club) {
        this.club.setValue(club);
    }
    
    public StringProperty getClubProp() {
        return club;
    }

    public PlayerItem copy() {
        PlayerItem playerClone = new PlayerItem();
        playerClone.setClub(this.getClub());
        playerClone.setDiscipline(this.getDiscipline());
        playerClone.setLic(this.getLic());
        playerClone.setName(this.getName());
        playerClone.setOrder(this.getOrder());
        playerClone.setTsp(this.getTsp());
        
        return playerClone;
    }
}
