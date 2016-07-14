/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.model;

import billiard.common.hazelcast.SyncManager;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author jean
 */
public abstract class CompetitionManager {
    private static final String COMPETITION_ID = "competitionId";

    private static AtomicLong competitionId;

    public CompetitionManager() {
        competitionId = new AtomicLong();
    }
    
    public static long generateCompetitionId() {
        if (SyncManager.isHazelcastEnabled()) {
            return SyncManager.getHazelCastInstance().getIdGenerator(COMPETITION_ID).newId();
        }
        return competitionId.incrementAndGet();
    }
    
    public static Competition getCompetition(long competitionId) {
        ArrayList<Competition> competitions = new ArrayList<>();
        Competition selectedCompetition = null;
        competitions.addAll(IndividualCompetitionManager.getInstance().listIndividualCompetitions());
        competitions.addAll(TeamCompetitionManager.getInstance().listTeamCompetitions());
        competitions.addAll(IndividualTournamentManager.getInstance().listIndividualTournament());
        for (Competition competition : competitions) {
            if(competition.getId()==competitionId) {
                selectedCompetition = competition;
                break;
            }
        }
        return selectedCompetition;
    }
}
