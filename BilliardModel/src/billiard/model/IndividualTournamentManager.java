/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.model;

import billiard.common.hazelcast.SyncManager;
import billiard.common.serialization.SerializationUtil;
import com.hazelcast.core.IMap;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author jean
 */
public class IndividualTournamentManager extends CompetitionManager{
    private static final String INDIVIDUAL_TOURNAMENTS_MAP = "individual_tournaments";
    private static final String DATA_LOCATION = "data/individual_tournaments";
    private static final String EXT = ".ser";

    private static IndividualTournamentManager instance;
    private static IMap<Long, IndividualTournament> individualTournaments;
    private static HashMap<Long, IndividualTournament> localTournaments;

    private IndividualTournamentManager() {
        if(SyncManager.isHazelcastEnabled()) {
            individualTournaments =  SyncManager.getHazelCastInstance().getMap(INDIVIDUAL_TOURNAMENTS_MAP);
        }else{
            localTournaments = new HashMap<>();
        }
    }
    
    public static IndividualTournamentManager getInstance() {
        if(instance==null) {
            instance = new IndividualTournamentManager();
        }
        return instance;
    }
    
    public void putIndividualTournament(IndividualTournament competition) {
        if(SyncManager.isHazelcastEnabled()) {
            individualTournaments.put(competition.getId(), competition);
        }else{
            localTournaments.put(competition.getId(), competition);
        }
    }
    
    public void updateIndividualTournament(IndividualTournament competition) {
        if(SyncManager.isHazelcastEnabled()) {
            individualTournaments.replace(competition.getId(), competition);
        }else{
            localTournaments.replace(competition.getId(), competition);
        }
    }

    public void removeIndividualTournament(IndividualTournament competition) {
        if(SyncManager.isHazelcastEnabled()) {
            individualTournaments.remove(competition.getId());
        }else{
            localTournaments.remove(competition.getId());
        }
    }
    
    public IndividualTournament getIndividualTournament(IndividualTournament competition) {
        if(SyncManager.isHazelcastEnabled()) {
            return individualTournaments.get(competition.getId());
        }else{
            return localTournaments.get(competition.getId());
        }
    }

    public IndividualTournament getIndividualTournament(long competitionId) {
        if(SyncManager.isHazelcastEnabled()) {
            return individualTournaments.get(competitionId);
        }else{
            return localTournaments.get(competitionId);
        }
    }

    public ArrayList listIndividualTournament() {
        ArrayList competitionList = new ArrayList();
        if(SyncManager.isHazelcastEnabled()) {
            individualTournaments = SyncManager.getHazelCastInstance().getMap(INDIVIDUAL_TOURNAMENTS_MAP);
            if (null!=individualTournaments) {
                competitionList.addAll(individualTournaments.values());
            }
        } else {
            competitionList.addAll(localTournaments.values());
        }
        return competitionList;
    }

    public static void writeIndividualTournament(IndividualTournament tournament) throws Exception {
        File fileLocation = new File(DATA_LOCATION);
        if(!fileLocation.exists()) {
            fileLocation.mkdirs();
        }
        String filename = DATA_LOCATION + "/" + tournament.getName()+ EXT;
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }

        SerializationUtil.serialize(tournament, filename);
    }
    
    public static ArrayList<IndividualTournament> loadIndividualTournaments() throws Exception {
        ArrayList<IndividualTournament> list = new ArrayList();
        File fileLocation = new File(DATA_LOCATION);
        if(fileLocation.exists()) {
            FileFilter filter = new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                   return pathname.isFile() && pathname.toString().endsWith(".ser");
                }
             };

            for(File file:fileLocation.listFiles(filter)) {
                System.out.println(file.getCanonicalPath());
                IndividualTournament tournament = (IndividualTournament) SerializationUtil.deserialize(file.getAbsolutePath());
                list.add(tournament);
            }
        }
        return list;
    }
}
