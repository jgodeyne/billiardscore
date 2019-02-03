/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.model;

import billiard.common.hazelcast.SyncManager;
import com.hazelcast.core.IMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jean
 */
public class TeamCompetitionManager extends CompetitionManager {
    private static final String COMPETITIONS_MAP = "competitions";

    private static TeamCompetitionManager instance;
    private IMap<Long, TeamCompetition> teamCompetitions;

    private TeamCompetitionManager() {
        if(SyncManager.isHazelcastEnabled()) {
            teamCompetitions = SyncManager.getHazelCastInstance().getMap(COMPETITIONS_MAP);
        } else {
//            teamCompetitions = new IMap<Long, TeamCompetition>();
        }
    }
    
    public static TeamCompetitionManager getInstance() {
        if (instance==null) {
            instance = new TeamCompetitionManager();
        }
        return instance;
    }

    public void putTeamCompetition(TeamCompetition competition) {
        teamCompetitions.put(competition.getId(), competition);
    }
    
    public void updateTeamCompetition(TeamCompetition competition) {
        teamCompetitions.replace(competition.getId(), competition);
    }

    public void removeTeamCompetition(TeamCompetition competition) {
        teamCompetitions.remove(competition.getId());
    }
    
    public TeamCompetition getTeamCompetition(TeamCompetition competition) {
        return teamCompetitions.get(competition.getId());
    }

    public TeamCompetition getTeamCompetition(long competitionId) {
        return teamCompetitions.get(competitionId);
    }

    public ArrayList listTeamCompetitions() {
        ArrayList competitionList = new ArrayList();
        if(SyncManager.isHazelcastEnabled()) {
//            teamCompetitions = SyncManager.getHazelCastInstance().getMap(COMPETITIONS_MAP);
        }
        if (null!=teamCompetitions) {
            competitionList.addAll(teamCompetitions.values());
        }
        return competitionList;
    }
}
