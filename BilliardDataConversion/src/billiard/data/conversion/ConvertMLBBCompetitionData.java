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
import billiard.data.conversion.mlbb.Club;
import billiard.data.conversion.mlbb.Player;
import billiard.data.conversion.mlbb.Team;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
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
public class ConvertMLBBCompetitionData {
    private static final Logger LOGGER = Logger.getLogger(ConvertMLBBCompetitionData.class.getName());
    private static final String LEAGUE_NAME = "MLBB";
    private static int nbrOfCompetitions = 0;
    private static int nbrOfClubs = 0;
    private static int nbrOfTeams = 0;
    private static int nbrOfPlayers = 0;
    private static HashMap<String, TeamCompetitionItem> competitions = new HashMap<>();
    private static HashMap<String, Club> clubs = new HashMap<>();

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        LogManager.getLogManager().readConfiguration(ConvertMLBBCompetitionData.class.getResourceAsStream("/logging.properties"));
        Path dir = Paths.get("input/mlbb/MLBB Bondslijst.csv");
        convertFile(dir);
        LOGGER.log(Level.INFO, "Nbr of competitions: {0}", nbrOfCompetitions);
        LOGGER.log(Level.INFO, "Nbr of clubs: {0}", nbrOfClubs);
        LOGGER.log(Level.INFO, "Nbr of teams: {0}", nbrOfTeams);
        LOGGER.log(Level.INFO, "Nbr of players: {0}", nbrOfPlayers);
    }
        
    private static void convertFile(Path inputFilePath) throws Exception {
        LOGGER.log(Level.FINEST, "convertFile => file: {0}", inputFilePath);
        Collection<TeamCompetitionItem> competitions = loadfile(inputFilePath);
        for (TeamCompetitionItem competition : competitions) {
            LOGGER.log(Level.FINEST, "convertFile => competition: {0}", competition.getName());
            Paths.get("output/mlbb/").toFile().mkdirs();
            Path outputFilePath = Paths.get("output/mlbb/"+ competition.getName() + ".xml");
            TeamCompetitionDataManager.writeFile(competition, outputFilePath);
        }
        createLeagueFile();
    }
    
    private static Collection<TeamCompetitionItem> loadfile(Path inputFilePath) throws Exception {
        LOGGER.log(Level.FINEST, "loadfile => file: {0}", inputFilePath);
        File csvData = inputFilePath.toFile();
        
        try (CSVParser parser = CSVParser.parse(csvData, Charset.defaultCharset(), CSVFormat.EXCEL.withDelimiter(';'))) {
            Iterator<CSVRecord> itRecords = parser.iterator();
            Team team = null;
            int order = 0;
            Club club = null;
            boolean reserves = false;
            boolean nonActive = false;
            while (itRecords.hasNext()) {
                CSVRecord csvRecord = itRecords.next();
                if(csvRecord.get(0).equalsIgnoreCase("CLUBKODE")) {
                    //Header => skip
                    continue;
                }
                if(!csvRecord.get(0).isEmpty()) {
                    // Club
                    order=0;
                    String lic = csvRecord.get(0).trim();
                    String name = csvRecord.get(1).trim();
                    club = clubs.get(lic);
                    if(club==null) {
                        nbrOfClubs++;
                        club= new Club();
                        club.setLic(lic);
                        club.setName(name);
                        clubs.put(lic, club);
                    }
                }
                if(!csvRecord.get(2).isEmpty()) {
                    //Team
                    String teamName = csvRecord.get(2);
                    if(teamName.equalsIgnoreCase("reserves")) {
                        reserves=true;
                        continue;
                    }
                    reserves = false;
                    if(teamName.equalsIgnoreCase("niet actief")) {
                        nonActive=true;
                        continue;
                    }
                    nonActive = false;
                    String competitionName = csvRecord.get(3).trim();
                    TeamCompetitionItem competition = competitions.get(competitionName);
                    if(competition==null) {
                        nbrOfCompetitions++;
                        competition = createCompetition(competitionName);
                        competitions.put(competitionName, competition);
                    }
                    if(team==null || !team.getName().equalsIgnoreCase(teamName)) {
                        nbrOfTeams++;
                        team = new Team();
                        team.setName(teamName);
                        competition.putTeam(team);
                        if (club!=null) {
                            team.setClub(club.getLic());
                        }
                    }
                    continue;
                }
                if(!csvRecord.get(5).isEmpty()) {
                    // Player
                    LOGGER.log(Level.FINEST, "loadfile => player: {0}", csvRecord.get(7));
                    String type = csvRecord.get(6).trim();
                    
                    nbrOfPlayers++;
                    Player player = new Player();
                    player.setLic(csvRecord.get(5).trim());
                    player.setOrder(Integer.toString(++order));
                    player.setName(csvRecord.get(7).trim());
                    player.setAvg(csvRecord.get(10).trim());
                    player.setTsp(csvRecord.get(11).trim());
                    if(!nonActive) {
                        if(club!=null) {
                            if(reserves) {
                                player.setOrder("R");
                                if(type.equalsIgnoreCase("L") || type.equalsIgnoreCase("S")) {
                                    player.setDiscipline("Vrijspel (KB)");
                                } else if (type.equalsIgnoreCase("D")) {
                                    player.setDiscipline("Drieband (KB)");
                                } else if (type.equalsIgnoreCase("K")) {
                                    player.setDiscipline("K38/2");
                                }
                            } else {                                
                                if(team!=null) {
                                    player.setTeamName(team.getName());
                                }
                            }
                            club.addPlayer(player);
                        }
                    }
                    else if (nonActive) {
                        //do nothing
                    }
                }
            }
        }

        // Add players to teams
        for(TeamCompetitionItem competition: competitions.values()) {
            for(TeamItem tmpTeamItem: competition.getTeams().values()) {
                Team tmpTeam = (Team) tmpTeamItem;
                String fixedPlayers = "";
                Club tmpClub = clubs.get(tmpTeam.getClub());
                for(Player player: tmpClub.getPlayers()) {
                    if(player.getDiscipline().isEmpty()) {
                        player.setDiscipline(competition.getDiscipline());
                    }
                    if(player.getTeamName() != null && player.getTeamName().equalsIgnoreCase(tmpTeam.getName())) {
                        fixedPlayers+=player.getOrder()+":";
                    }
                    tmpTeam.putPlayer(player);
                }
                tmpTeam.setFixedPlayers(fixedPlayers);
            }
        }
        
        return competitions.values();
    }  

        
    private static void createLeagueFile() throws Exception {
        LeagueItem league = new LeagueItem();
        league.setName("MLBB");
        league.setTurnIndicatorsColor(PermittedValues.TurnIndicatorsColor.YELLOW_WHITE.toString());
        league.setWarmingUpTime("3");
        for(Club club: clubs.values()) {
            ClubItem clubItem = new ClubItem();
            clubItem.setLic(club.getLic());
            clubItem.setName(club.getName());
            league.putClub(clubItem);
        }
        for(TeamCompetitionItem competition: competitions.values()) {
            for(TeamItem tmpTeamItem: competition.getTeams().values()) {
                Team tmpTeam = (Team) tmpTeamItem;
                for(PlayerItem tmpPlayer: tmpTeam.getPlayers().values()) {
                    Player player = (Player) tmpPlayer;
                    MemberItem member = new MemberItem(tmpTeam.getClub());
                    member.setName(player.getName());
                    member.setLic(player.getLic());
                    TSPItem tsp = new TSPItem();
                    tsp.setTsp(player.getTsp());
                    tsp.setDiscipline(player.getDiscipline());
                    member.putTsp(tsp);
                    league.putMember(member);
                }
            }
        }
        Path outputFilePath = Paths.get("output/mlbb/"+ league.getName()+ ".xml");
        LeagueDataManager.writeFile(league, outputFilePath);
    }

    private static TeamCompetitionItem createCompetition(String name) {
        TeamCompetitionItem competition = new TeamCompetitionItem();
        competition.setName(name);
        competition.setLeague(LEAGUE_NAME);

        String discipline ="Vrijspel (KB)";
        String nbrPlayers = "3";
        String tableFormat = "2,30m";
        String pointSystem = "MATCHPOINTS";
        
        if(name.equals("2e Klasse A")) {
            discipline = "Vrijspel (KB)";
        } else if (name.equals("3e Klasse D")) {            
            discipline = "Vrijspel (KB)";
        } else if(name.equals("3e Klasse B")) {
            discipline = "Vrijspel (KB)";
        } else if(name.equals("4e Klasse B")) {
            discipline = "Vrijspel (KB)";
        } else if(name.equals("2e Klasse C")) {
            discipline = "Vrijspel (KB)";
        } else if(name.equals("3e Klasse A")) {
            discipline = "Vrijspel (KB)";
        } else if(name.equals("4e Klasse A")) {
            discipline = "Vrijspel (KB)";
        } else if(name.equals("5e Klasse C")) {
            discipline = "Vrijspel (KB)";
        } else if(name.equals("1e Klasse E")) {
            discipline = "Vrijspel (KB)";
        } else if(name.equals("2e Klasse D")) {
            discipline = "Vrijspel (KB)";
        } else if(name.equals("5e Klasse B")) {
            discipline = "Vrijspel (KB)";
        } else if(name.equals("1e Klasse D")) {
            discipline = "Vrijspel (KB)";
        } else if(name.equals("3e Klasse C")) {
            discipline = "Vrijspel (KB)";
        } else if(name.equals("4e Klasse C")) {
            discipline = "Vrijspel (KB)";
        } else if(name.equals("1e Klasse C")) {
            discipline = "Vrijspel (KB)";
        } else if(name.equals("1e Klasse A")) {
            discipline = "Vrijspel (KB)";
        } else if(name.equals("1e Klasse B")) {
            discipline = "Vrijspel (KB)";
        } else if(name.equals("2e Klasse B")) {
            discipline = "Vrijspel (KB)";
        } else if(name.equals("5e Klasse A")) {
            discipline = "Vrijspel (KB)";
        } else if(name.equals("Hoofdklasse Vrijspel (KB)")) {
            discipline = "Vrijspel (KB)";            
        } else if(name.equals("1e Klasse DB")) {
            discipline = "Drieband";            
        } else if(name.equals("Hoofdklasse DB")) {
            discipline = "Drieband";            
        } else if(name.equals("Hoofdklasse Kader")) {
            discipline = "K38/2";            
        }

        competition.setDiscipline(discipline);
        competition.setNbrOfPlayers(nbrPlayers);
        competition.setTableFormat(tableFormat);
        competition.setPointsSystem(pointSystem);
        
        return competition;
    }
}
