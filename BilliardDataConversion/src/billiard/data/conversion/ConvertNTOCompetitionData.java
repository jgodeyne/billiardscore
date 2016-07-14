/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.data.conversion;

import billiard.common.PermittedValues;
import billiard.data.ClubItem;
import billiard.data.TeamCompetitionItem;
import billiard.data.LeagueDataManager;
import billiard.data.LeagueItem;
import billiard.data.MemberItem;
import billiard.data.PlayerItem;
import billiard.data.TSPItem;
import billiard.data.TeamCompetitionDataManager;
import billiard.data.TeamItem;
import billiard.data.conversion.nto.Club;
import billiard.data.conversion.nto.Player;
import billiard.data.conversion.nto.Team;
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
public class ConvertNTOCompetitionData {
    private static final Logger LOGGER = Logger.getLogger(ConvertNTOCompetitionData.class.getName());
    private static final String LEAGUE_NAME = "NTO";
    private static int nbrOfCompetitions = 0;
    private static int nbrOfClubs = 0;
    private static int nbrOfTeams = 0;
    private static int nbrOfPlayers = 0;
    private static int licNbr = 0;
    private static int clubLicNbr = 0;
    private static HashMap<String, Club> clubs = new HashMap<>(); 
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        LogManager.getLogManager().readConfiguration(ConvertNTOCompetitionData.class.getResourceAsStream("/logging.properties"));
        loadReservePlayers(Paths.get("input/nto/NTO - Reserve Spelers.csv"));
        convertFiles();
        LOGGER.log(Level.INFO, "Nbr of competitions: {0}", nbrOfCompetitions);
        LOGGER.log(Level.INFO, "Nbr of clubs: {0}", nbrOfClubs);
        LOGGER.log(Level.INFO, "Nbr of teams: {0}", nbrOfTeams);
        LOGGER.log(Level.INFO, "Nbr of players: {0}", nbrOfPlayers);
    }
        
    private static void convertFiles() throws Exception {
        ArrayList<TeamCompetitionItem> competitions = new ArrayList<>();
        LOGGER.log(Level.FINEST, "convertFile => file: {0}", "input/nto/NTO - Hoofdklasse.csv");
        competitions.add(loadfile(Paths.get("input/nto/NTO - Hoofdklasse.csv")));
        LOGGER.log(Level.FINEST, "convertFile => file: {0}", "input/nto/NTO - Klasse A.csv");
        competitions.add(loadfile(Paths.get("input/nto/NTO - Klasse A.csv")));
        LOGGER.log(Level.FINEST, "convertFile => file: {0}", "input/nto/NTO - Klasse B.csv");
        competitions.add(loadfile(Paths.get("input/nto/NTO - Klasse B.csv")));
        LOGGER.log(Level.FINEST, "convertFile => file: {0}", "input/nto/NTO - Klasse C.csv");
        competitions.add(loadfile(Paths.get("input/nto/NTO - Klasse C.csv")));

        // Add reserver players to teams
        for(TeamCompetitionItem competitionItem: competitions) {
            for(TeamItem tmpTeamItem: competitionItem.getTeams().values()) {
                Team tmpTeam = (Team) tmpTeamItem;
                Club tmpClub = clubs.get(tmpTeam.getClub());
                LOGGER.log(Level.FINEST, "convertFile => team: {0} club: {1}", new String[]{tmpTeam.getName(), tmpTeam.getClub()});
                for(Player player: tmpClub.getPlayers()) {
                    if(player.getDiscipline().isEmpty()) {
                        player.setDiscipline(competitionItem.getDiscipline());
                    }
                    tmpTeam.putPlayer(player);
                }
            }
        }
        
        for (TeamCompetitionItem competition : competitions) {
            LOGGER.log(Level.FINEST, "convertFile => competition: {0}", competition.getName());
            String outputPath = "output/nto/";
            Paths.get(outputPath).toFile().mkdirs();
            Path outputFilePath = Paths.get(outputPath+ competition.getName() + " - "+ competition.getGroup() + ".xml");
            TeamCompetitionDataManager.writeData(competition, outputFilePath);
        }
        createLeagueFile(competitions);
    }
    
    private static void loadReservePlayers(Path inputFilePath) throws Exception {
        LOGGER.log(Level.FINEST, "loadfile => file: {0}", inputFilePath);
        File csvData = inputFilePath.toFile();

        try (CSVParser parser = CSVParser.parse(csvData, Charset.defaultCharset(), CSVFormat.EXCEL.withDelimiter(';'))) {
            Iterator<CSVRecord> itRecords = parser.iterator();
            Club club = null;
            while (itRecords.hasNext()) {
                CSVRecord csvRecord = itRecords.next();
                if(!csvRecord.get(0).isEmpty()) {
                    LOGGER.log(Level.FINEST, "loadfile => club: {0}", csvRecord.get(0));
                    String name = csvRecord.get(0).trim();
                    // Club
                    nbrOfClubs++;
                    club= new Club();
                    club.setLic(Integer.toString(++clubLicNbr));
                    club.setName(name);
                    clubs.put(name, club);
                    
                    Player player = new Player();
                    player.setLic(Integer.toString(++licNbr));
                    player.setName(csvRecord.get(1).trim());
                    player.setAvg(csvRecord.get(3).trim());
                    player.setTsp(csvRecord.get(2).trim());
                    player.setDiscipline("Libre");
                    player.setOrder("R");
                    club.addPlayer(player);
                } else {
                    // Player
                    LOGGER.log(Level.FINEST, "loadfile => player: {0}", csvRecord.get(1));
                    nbrOfPlayers++;
                    Player player = new Player();
                    player.setLic(Integer.toString(++licNbr));
                    player.setName(csvRecord.get(1).trim());
                    player.setAvg(csvRecord.get(3).trim());
                    player.setTsp(csvRecord.get(2).trim());
                    player.setDiscipline("Libre");
                    player.setOrder("R");
                    if(club!=null) {
                        club.addPlayer(player);
                    }
                }
            }
        }
    }
        
    private static TeamCompetitionItem loadfile(Path inputFilePath) throws Exception {
        LOGGER.log(Level.FINEST, "loadfile => file: {0}", inputFilePath);
        File csvData = inputFilePath.toFile();

        String competitionName = inputFilePath.getFileName().toString().split("[.]")[0];
        LOGGER.log(Level.FINEST, "loadfile => competition: {0}", competitionName);
        nbrOfCompetitions++;
        TeamCompetitionItem competition = createCompetition(competitionName);
        
        try (CSVParser parser = CSVParser.parse(csvData, Charset.defaultCharset(), CSVFormat.EXCEL.withDelimiter(';'))) {
            Iterator<CSVRecord> itRecords = parser.iterator();
            Team team = null;
            int order = 0;
            while (itRecords.hasNext()) {
                CSVRecord csvRecord = itRecords.next();
                if(csvRecord.get(1).isEmpty()) {
                    //Team
                    order = 0;
                    String name = csvRecord.get(0).trim();
                    LOGGER.log(Level.FINEST, "loadfile => team: {0}", name);
                    nbrOfTeams++;
                    team = new Team();
                    team.setName(name);
                    team.setFixedPlayers("1:2::3:4");
                    competition.putTeam(team);
                    team.setClub(name.substring(0, name.length()-2));
                } else {
                    // Player
                    LOGGER.log(Level.FINEST, "loadfile => player: {0}", csvRecord.get(0));
                    nbrOfPlayers++;
                    Player player = new Player();
                    player.setLic(Integer.toString(++licNbr));
                    player.setOrder(Integer.toString(++order));
                    player.setName(csvRecord.get(0).trim());
                    player.setAvg(csvRecord.get(2).trim());
                    player.setTsp(csvRecord.get(1).trim());
                    player.setDiscipline("Libre");
                    if(team != null) {
                        team.putPlayer(player);
                    }
                }
            }
        }
        return competition;
    }  

        
    private static void createLeagueFile(Collection<TeamCompetitionItem> competitions) throws Exception {
        LeagueItem leagueItem = new LeagueItem();
        leagueItem.setName(LEAGUE_NAME);
        leagueItem.setTurnIndicatorsColor(PermittedValues.TurnIndicatorsColor.YELLOW_WHITE.toString());
        leagueItem.setWarmingUpTime("3");
        LOGGER.log(Level.FINEST, "createLeagueFile => league: {0}", "NTO");
        for(Club club: clubs.values()) {
            ClubItem clubItem = new ClubItem();
            clubItem.setLic(club.getLic());
            clubItem.setName(club.getName());
            leagueItem.putClub(clubItem);
        }
        for(TeamCompetitionItem competition: competitions) {
            LOGGER.log(Level.FINEST, "createLeagueFile => competition: {0}", competition.getName());
            for(TeamItem tmpTeamItem: competition.getTeams().values()) {
                Team tmpTeam = (Team) tmpTeamItem;
                LOGGER.log(Level.FINEST, "createLeagueFile => team: {0}", tmpTeam.getName());
                for(PlayerItem tmpPlayer: tmpTeam.getPlayers().values()) {
                    LOGGER.log(Level.FINEST, "createLeagueFile => player: {0}", tmpPlayer.getName());
                    Player player = (Player) tmpPlayer;
                    MemberItem member = new MemberItem(tmpTeam.getClub());
                    member.setName(player.getName());
                    member.setLic(player.getLic());
                    TSPItem tsp = new TSPItem();
                    tsp.setTsp(player.getTsp());
                    tsp.setDiscipline(player.getDiscipline());
                    member.putTsp(tsp);
                    leagueItem.putMember(member);
                }
            }
        }
        Path outputFilePath = Paths.get("output/nto/"+ leagueItem.getName()+ ".xml");
        LeagueDataManager.writeData(leagueItem, outputFilePath);
    }

    private static TeamCompetitionItem createCompetition(String name) {
        TeamCompetitionItem competition = new TeamCompetitionItem();
        competition.setName(name);
        competition.setLeague(LEAGUE_NAME);

        String discipline ="Vrijspel (KB)";
        String nbrPlayers = "4";
        String tableFormat = "2,30m";
        String pointSystem = "MATCHPOINTS";
        
        competition.setDiscipline(discipline);
        competition.setNbrOfPlayers(nbrPlayers);
        competition.setTableFormat(tableFormat);
        competition.setPointsSystem(pointSystem);
        
        return competition;
    }
}
