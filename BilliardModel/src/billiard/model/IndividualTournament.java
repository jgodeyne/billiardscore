/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author jean
 */
public class IndividualTournament extends Competition implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<Player> participants;
    private ArrayList<PlayerTournamentResult> participantsResult;
    private ArrayList<Match> matches;
    private final ArrayList<Match> matchesClosed;

    public IndividualTournament(String name, String discipline, String tableFormat) {
        super(name, discipline, tableFormat);
        participants=new ArrayList<>();
        participantsResult=new ArrayList<>();
        matches=new ArrayList<>();
        matchesClosed=new ArrayList<>();
    }  

    public ArrayList<Player> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<Player> participants) {
        this.participants = participants;
    }

    public ArrayList<PlayerTournamentResult> getParticipantsResult() {
        return participantsResult;
    }

    public void setParticipantsResult(ArrayList<PlayerTournamentResult> participantsResult) {
        this.participantsResult = participantsResult;
    }

    public ArrayList<Match> getMatches() {
        return matches;
    }

    public void setMatches(ArrayList<Match> mactches) {
        this.matches = mactches;
    }
    
    public void matchEnded(Match match) {
        if(matchesClosed.contains(match)) {
            return;
        }
        matches.set(matches.indexOf(match), match);
        matchesClosed.add(match);
        boolean found=false;
        for(PlayerTournamentResult result:participantsResult){
            if(result.getPlayerLic().equals(match.getPlayer1().getLicentie())) {
                result.addResult(match.getPlayer1Result());
                found=true;
                break;
            }
        }
        if(!found) {
            PlayerTournamentResult result = new PlayerTournamentResult(this.getId()
                    , match.getPlayer1().getLicentie());
            result.addResult(match.getPlayer1Result());
            participantsResult.add(result);
        }

        for(PlayerTournamentResult result:participantsResult){
            if(result.getPlayerLic().equals(match.getPlayer2().getLicentie())) {
                result.addResult(match.getPlayer2Result());
                found=true;
                break;
            }
        }
        if(!found) {
            PlayerTournamentResult result = new PlayerTournamentResult(this.getId()
                    , match.getPlayer2().getLicentie());
            result.addResult(match.getPlayer2Result());
            participantsResult.add(result);
        }
        match.close();
    }
    
    public String getParticipantName(String lic) {
        for(Player player:participants) {
            if(player.getLicentie().equals(lic)) {
                return player.getName();
            }
        }
        return "";
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
