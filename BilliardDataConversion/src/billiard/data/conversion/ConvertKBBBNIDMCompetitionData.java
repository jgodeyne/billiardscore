/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.data.conversion;

import billiard.common.PermittedValues;
import billiard.data.TeamCompetitionItem;
import billiard.data.LeagueDataManager;
import billiard.data.LeagueItem;
import billiard.data.TeamCompetitionDataManager;
import billiard.data.TeamItem;
import billiard.data.conversion.nidm.Club;
import billiard.data.conversion.nidm.Player;
import billiard.data.conversion.nidm.Team;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author jean
 */
public class ConvertKBBBNIDMCompetitionData {
    private static final Logger LOGGER = Logger.getLogger(ConvertKBBBNIDMCompetitionData.class.getName());
    private static final String LEAGUE_NAME = "KBBB";
    private static int nbrOfCompetitions = 0;
    private static int nbrOfClubs = 0;
    private static int nbrOfTeams = 0;
    private static int nbrOfPlayers = 0;

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        LogManager.getLogManager().readConfiguration(ConvertKBBBNIDMCompetitionData.class.getResourceAsStream("/logging.properties"));
        Path playersFile = Paths.get("input/kbbb/nidm/NIDMSPELERSLIJST.csv");
        Path teamsFile = Paths.get("input/kbbb/nidm/Poule.csv");

        Collection<TeamCompetitionItem> competitions = loaddata(playersFile, teamsFile);
        for (TeamCompetitionItem competition : competitions) {
            LOGGER.log(Level.FINEST, "convertFile => competition: {0}", competition.getName());
            Paths.get("output/kbbb/nidm").toFile().mkdirs();
            Path outputFilePath = Paths.get("output/kbbb/nidm/"+ competition.getName() + " - "+ competition.getGroup() + ".xml");
            TeamCompetitionDataManager.writeFile(competition, outputFilePath);
        }
        LOGGER.log(Level.INFO, "Nbr of competitions: {0}", nbrOfCompetitions);
        LOGGER.log(Level.INFO, "Nbr of clubs: {0}", nbrOfClubs);
        LOGGER.log(Level.INFO, "Nbr of teams: {0}", nbrOfTeams);
        LOGGER.log(Level.INFO, "Nbr of players: {0}", nbrOfPlayers);
    }
    
    private static Collection<TeamCompetitionItem> loaddata(Path inputPlayersFilePath, Path inputTeamsFilePath) throws Exception {
        LOGGER.log(Level.FINEST, "loadfile => file: {0}", inputPlayersFilePath);
        
        //Load Players
        HashMap<String, Club> clubPlayers = loadClubs(inputPlayersFilePath);

        //Load Teams
        ArrayList<TeamCompetitionItem> competitions = loadTeams(inputTeamsFilePath);
        
        //Add players to Teams
        for(TeamCompetitionItem competition : competitions) {
            addPlayers(competition, clubPlayers);
        }
        
        return competitions;
    }  
        
    private static HashMap<String, Club> loadClubs(Path inputPlayersFilePath) throws Exception {
        LOGGER.log(Level.FINEST, "loadClubs => file: {0}", inputPlayersFilePath);
        HashMap<String, Club> clubPlayers = new HashMap<>();
        File csvData = inputPlayersFilePath.toFile();
        try (CSVParser parser = CSVParser.parse(csvData, Charset.defaultCharset(), CSVFormat.EXCEL.withDelimiter(';'))) {
            Iterator<CSVRecord> itRecords = parser.iterator();
            Club club = null;
            while (itRecords.hasNext()) {
                CSVRecord csvRecord = itRecords.next();
                if(csvRecord.get(1).isEmpty()) {
                    // Club
                    nbrOfClubs++;
                    String lic = csvRecord.get(0).trim();
                    String name = csvRecord.get(2).trim();
                    LOGGER.log(Level.FINEST, "loadClubs => club: {0}", name);
                    club= new Club();
                    club.setLic(lic);
                    club.setName(name);
                    clubPlayers.put(name, club);
                } else {
                    nbrOfPlayers++;
                    Player player = new Player();
                    player.setLic(csvRecord.get(1).trim());
                    player.setOrder(csvRecord.get(0).trim());
                    player.setName(csvRecord.get(2).trim());
                    player.setTeamNbr(csvRecord.get(5).trim());
                    LOGGER.log(Level.FINEST, "loadClubs => player: {0}", player.getName());
                    if(club!=null) {
                        club.addPlayer(player);
                    }
                }
            }
        }
        return clubPlayers;
    }

    private static ArrayList<TeamCompetitionItem> loadTeams(Path inputTeamsFilePath) throws Exception {
        LOGGER.log(Level.FINEST, "loadTeams => file: {0}", inputTeamsFilePath);
        ArrayList<TeamCompetitionItem> competitions = new ArrayList<>();
        File csvData = inputTeamsFilePath.toFile();
        try (CSVParser parser = CSVParser.parse(csvData, Charset.defaultCharset(), CSVFormat.EXCEL.withDelimiter(';'))) {
            Iterator<CSVRecord> itRecords = parser.iterator();
            TeamCompetitionItem competition = null;
            while (itRecords.hasNext()) {
                CSVRecord csvRecord = itRecords.next();
                if(!csvRecord.get(0).isEmpty()) {
                    // Competition
                    nbrOfCompetitions++;
                    competition = new TeamCompetitionItem();
                    competition.setLeague(LEAGUE_NAME);
                    competition.setName(csvRecord.get(0).trim());
                    competition.setGroup(csvRecord.get(1).trim());
                    competition.setDiscipline("Drieband (GB)");
                    competition.setNbrOfPlayers("4");
                    competition.setPointsSystem("COMPETITIONPOINTS");
                    competition.setTableFormat("2,85m");
                    competition.setTspPlayers(csvRecord.get(2).trim());
                    LOGGER.log(Level.FINEST, "loadTeams => competition: {0}", competition.getName());
                    competitions.add(competition);
                } else {
                    // Team
                    nbrOfTeams++;
                    Team team = new Team();
                    String club = csvRecord.get(1).trim();
                    String teamNbr = csvRecord.get(2).trim();
                    team.setName(club+" "+teamNbr);
                    team.setTeamNbr(teamNbr);
                    team.setClub(club);
                    LOGGER.log(Level.FINEST, "loadTeams => team: {0}", team.getName());
                    if(competition!=null) {
                        competition.putTeam(team);
                    }
                }
            }
        }
        return competitions;
    }
    
    private static void addPlayers(TeamCompetitionItem competition, HashMap<String, Club> clubPlayers) {
        LOGGER.log(Level.FINEST, "addPlayers => competition: {0}", competition.getName());
        for(TeamItem teamItem : competition.getTeams().values()) {
            String fixedPlayers = "";
            Team team = (Team) teamItem;
            LOGGER.log(Level.FINEST, "addPlayers => team: {0}", team.getName());
            LOGGER.log(Level.FINEST, "addPlayers => club: {0}", team.getClub());
            Club club = clubPlayers.get(team.getClub());
            if (club!=null) {
                LOGGER.log(Level.FINEST, "addPlayers => club found: {0}", team.getClub());
                for(Player player : club.getPlayers()) {
                    team.putPlayer(player);
                    if(team.getTeamNbr().equalsIgnoreCase(player.getTeamNbr())) {
                        fixedPlayers += player.getOrder() + ":";
                    }
                }
            } else {
                LOGGER.log(Level.FINEST, "addPlayers => club NOT found: {0}", team.getClub());                
            }
            team.setFixedPlayers(fixedPlayers);
        }
    }

    private static void createLeagueFile(Collection<TeamCompetitionItem> competitions) throws Exception {
        LeagueItem leagueItem = new LeagueItem();
        leagueItem.setName(LEAGUE_NAME);
        leagueItem.setTurnIndicatorsColor(PermittedValues.TurnIndicatorsColor.WHITE_YELLOW.toString());
        leagueItem.setWarmingUpTime("5");
        LOGGER.log(Level.FINEST, "createLeagueFile => league: {0}", "KBBB");
        /*
        for(TeamCompetitionItem competition: competitions) {
            logger.log(Level.FINEST, "createLeagueFile => competition: {0}", competition.getName());
            for(TeamItem tmpTeamItem: competition.getTeams()) {
                billiard.data.conversion.nto.Team tmpTeam = (billiard.data.conversion.nto.Team) tmpTeamItem;
                logger.log(Level.FINEST, "createLeagueFile => team: {0}", tmpTeam.getName());
                for(PlayerItem tmpPlayer: tmpTeam.getPlayers()) {
                    logger.log(Level.FINEST, "createLeagueFile => player: {0}", tmpPlayer.getName());
                    billiard.data.conversion.nto.Player player = (billiard.data.conversion.nto.Player) tmpPlayer;
                    MemberItem member = new MemberItem();
                    member.setLic(player.getLic());
                    member.setName(player.getName());
                    member.setClub(tmpTeam.getClub());
                    TSPItem tsp = new TSPItem();
                    tsp.setTsp(player.getTsp());
                    tsp.setDiscipline(player.getDiscipline());
                    tsp.setAverage(player.getAvg());
                    member.addTsp(tsp);
                    leagueItem.addMember(member);
                }
            }
        }
        */
        Path outputFilePath = Paths.get("output/kbbb/"+ leagueItem.getName()+ ".xml");
        LeagueDataManager.writeFile(leagueItem, outputFilePath);
    }
}
