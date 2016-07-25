/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.model;

import billiard.common.AppProperties;
import billiard.common.hazelcast.SyncManager;
import billiard.common.serialization.SerializationUtil;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author jean
 */
public class IndividualTournamentManager extends CompetitionManager{
    private static IndividualTournamentManager instance;
    private static HashMap<String, IndividualTournament> tournaments = new HashMap<>();

    private IndividualTournamentManager() throws Exception {
        loadIndividualTournaments();
    }
    
    public static IndividualTournamentManager getInstance() throws Exception {
        if(instance==null) {
            instance = new IndividualTournamentManager();
        }
        return instance;
    }
    
    public void putIndividualTournament(IndividualTournament competition) {
        tournaments.put(competition.getName(), competition);
    }
    
    public void updateIndividualTournament(IndividualTournament competition) {
        tournaments.replace(competition.getName(), competition);
    }

    public void removeIndividualTournament(IndividualTournament competition) {
        tournaments.remove(competition.getName());
    }
    
    public IndividualTournament getIndividualTournament(String name) {
        return tournaments.get(name);
    }

    public ArrayList<String> getIndividualTournamentNames() {
        ArrayList<String> names = new ArrayList<>();
        for (IndividualTournament tournament : tournaments.values()) {
            names.add(tournament.getName());
        }
        return names;
    }

    public static void writeIndividualTournament(IndividualTournament tournament) throws Exception {
        File fileLocation = new File(AppProperties.getInstance().getDataPath());
        fileLocation.mkdirs();
        String filename = AppProperties.getInstance().getDataPath() + "/it/" + tournament.getName()+ ".ser";
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }

        SerializationUtil.serialize(tournament, filename);
    }
    
    public void loadIndividualTournaments() throws Exception {
        File fileLocation = new File(AppProperties.getInstance().getDataPath() + "/it");
        fileLocation.mkdirs();
        if(fileLocation.exists()) {
            FileFilter filter = new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                   return pathname.isFile() && pathname.toString().endsWith(".ser");
                }
             };

            for(File file:fileLocation.listFiles(filter)) {
                IndividualTournament tournament = (IndividualTournament) SerializationUtil.deserialize(file.getAbsolutePath());
                tournaments.put(tournament.getName(), tournament);
            }
        }
    }

    public ArrayList listIndividualTournaments() {
        ArrayList list = new ArrayList();
        list.addAll(tournaments.values());
        return list;
    }
}
