/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.data;

import billiard.common.PermittedValues;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author jean
 */
public class MemberItem {
    private final StringProperty lic = new SimpleStringProperty("");
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty clubLic = new SimpleStringProperty("");
    private HashMap<String, TSPItem> tsps = new HashMap<>();

    public MemberItem(String clubLic) {
        this.clubLic.setValue(clubLic);
        for (String discipline : PermittedValues.DISCIPLINES) {
            if(!discipline.isEmpty()) {
                TSPItem tsp = new TSPItem();
                tsp.setDiscipline(discipline);
                tsps.put(discipline, tsp);
            }
        }
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

    public String getClubLic() {
        return clubLic.getValue();
    }

    public void setClubLic(String club) {
        this.clubLic.setValue(club);
    }
    
    public StringProperty getClubLicProp() {
        return clubLic;
    }

    public HashMap<String, TSPItem> getTsps() {
        return tsps;
    }

    public void setTsps(HashMap<String, TSPItem> tsps) {
        this.tsps = tsps;
    }
    
    public void putTsp(TSPItem tsp) {
        tsps.put(tsp.getDiscipline(), tsp);
    }
    
    public void removeTsp(TSPItem tsp) {
        tsps.remove(tsp.getDiscipline());
    }
    
    public TSPItem getTsp(String discipline) {
        return tsps.get(discipline);
    }

    public MemberItem copy() {
        MemberItem memberClone = new MemberItem(this.getClubLic());
        memberClone.setLic(this.getLic());
        memberClone.setName(this.getName());
        
        HashMap<String, TSPItem> tspCloneList = new HashMap<>();
        for (Map.Entry<String, TSPItem> entry : tsps.entrySet()) {
            String key = entry.getKey();
            TSPItem value = entry.getValue().copy();
            tspCloneList.put(key, value);
        }
        memberClone.setTsps(tspCloneList);
        return memberClone;
    }
}
