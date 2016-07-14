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
public class ClubItem {
    private final StringProperty lic = new SimpleStringProperty("");
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty contactName = new SimpleStringProperty("");
    private final StringProperty contactEmail = new SimpleStringProperty("");

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

    public String getContactName() {
        return contactName.getValue();
    }

    public void setContactName(String contactName) {
        this.contactName.setValue(contactName);
    }
    
    public StringProperty getContactNameProp() {
        return contactName;
    }

    public String getContactEmail() {
        return contactEmail.getValue();
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail.setValue(contactEmail);
    }
    
    public StringProperty getContactEmailProp() {
        return contactEmail;
    }

    public ClubItem copy() throws CloneNotSupportedException {
        ClubItem clubClone = new ClubItem();
        clubClone.setContactEmail(this.getContactEmail());
        clubClone.setContactName(this.getContactName());
        clubClone.setLic(this.getLic());
        clubClone.setName(this.getName());
        
        return clubClone; 
    }
}
