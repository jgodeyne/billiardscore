/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.data;

import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author jean
 */
public class LeagueItem {
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty contactName = new SimpleStringProperty("");
    private final StringProperty contactEmail = new SimpleStringProperty("");
    private final StringProperty turnIndicatorsColor = new SimpleStringProperty("WHITE");
    private final StringProperty warmingUpTime = new SimpleStringProperty("5");
    HashMap<String, ClubItem> clubs = new HashMap<>();
    HashMap<String, MemberItem> members = new HashMap<>();


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

    public String getTurnIndicatorsColor() {
        return turnIndicatorsColor.getValue();
    }

    public void setTurnIndicatorsColor(String turnIndicatorsColor) {
        this.turnIndicatorsColor.setValue(turnIndicatorsColor);
    }
    
    public StringProperty getTurnIndicatorsColorProp() {
        return turnIndicatorsColor;
    }

    public String getWarmingUpTime() {
        return warmingUpTime.getValue();
    }

    public void setWarmingUpTime(String warmingUpTime) {
        this.warmingUpTime.setValue(warmingUpTime);
    }
    
    public StringProperty getWarmingUpTimeProp() {
        return warmingUpTime;
    }

    public HashMap<String, ClubItem> getClubs() {
        return clubs;
    }

    public void setClubs(HashMap<String, ClubItem> clubs) {
        this.clubs = clubs;
    }
    
    public ClubItem getClub(String lic) {
        return this.clubs.get(lic);
    }
    
    public void putClub(ClubItem club) {
        clubs.put(club.getLic(), club);
    }
    
    public void removeClub(ClubItem club) {
        clubs.remove(club.getLic());
    }

    public HashMap<String, MemberItem> getMembers() {
        return members;
    }

    public void setMembers(HashMap<String, MemberItem> members) {
        this.members = members;
    }
    
    public MemberItem getMember(String lic) {
        return this.members.get(lic);
    }
    
    public void putMember(MemberItem member) {
        members.put(member.getLic(), member);
    }
    
    public void removeMember(MemberItem member) {
        members.remove(member.getLic());
    }
    
    public HashMap<String, MemberItem> getMembersOfClub(String clubLic) {
        HashMap<String, MemberItem> selectedMembers = new HashMap();
        for (Map.Entry<String, MemberItem> memberEntry : members.entrySet()) {
            String lic = memberEntry.getKey();
            MemberItem member = memberEntry.getValue();
            if(member.getClubLic().equals(clubLic)) {
                selectedMembers.put(lic, member);
            }
            
        }
        return selectedMembers;
    }

    public LeagueItem copy() throws CloneNotSupportedException {
        LeagueItem leagueClone = new LeagueItem();
        leagueClone.setContactEmail(this.getContactEmail());
        leagueClone.setContactName(this.getContactName());
        leagueClone.setName(this.getName());
        leagueClone.setTurnIndicatorsColor(this.getTurnIndicatorsColor());
        leagueClone.setWarmingUpTime(this.getWarmingUpTime());
        
        HashMap<String, ClubItem> clubCloneList = new HashMap<>();
        for (Map.Entry<String, ClubItem> entry : clubs.entrySet()) {
            String key = entry.getKey();
            ClubItem value = entry.getValue().copy();
            clubCloneList.put(key, value);
        }
        leagueClone.setClubs(clubCloneList);
        
        HashMap<String, MemberItem> memberCloneList = new HashMap<>();
        for (Map.Entry<String, MemberItem> entry : members.entrySet()) {
            String key = entry.getKey();
            MemberItem value = entry.getValue().copy();
            memberCloneList.put(key, value);
        }
        leagueClone.setMembers(memberCloneList);
        
        return leagueClone;
    }
}
