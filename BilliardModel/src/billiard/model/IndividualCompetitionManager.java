/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jean
 */
public class IndividualCompetitionManager extends CompetitionManager {
    private static Map<Long, IndividualCompetition> individualCompetitions;
    private static IndividualCompetitionManager instance;

    private IndividualCompetitionManager() {
        individualCompetitions = new HashMap<>();
    }
    
    public static IndividualCompetitionManager getInstance() {
        if(instance==null) {
            instance = new IndividualCompetitionManager();
        }
        return instance;
    }

    public void putIndividualCompetition(IndividualCompetition competition) {
        individualCompetitions.put(competition.getId(), competition);
    }
    
    public void updateIndividualCompetition(IndividualCompetition competition) {
        individualCompetitions.replace(competition.getId(), competition);
    }
    public void removeIndividualCompetition(IndividualCompetition competition) {
        individualCompetitions.remove(competition.getId());
    }
    
    public IndividualCompetition getIndividualCompetition(IndividualCompetition competition) {
        return individualCompetitions.get(competition.getId());
    }

    public IndividualCompetition getIndividualCompetition(long competitionId) {
        return individualCompetitions.get(competitionId);
    }

    public ArrayList listIndividualCompetitions() {
        ArrayList competitionList = new ArrayList();
        if (null!=individualCompetitions) {
            competitionList.addAll(individualCompetitions.values());
        }
        return competitionList;
    }
    
}
