/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.data.conversion;

import billiard.data.ClubItem;
import billiard.data.LeagueDataManager;
import billiard.data.LeagueItem;
import billiard.data.MemberItem;
import billiard.data.TSPItem;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
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
public class ConvertKBBBLedenLijst {
    private static final Logger LOGGER = Logger.getLogger(ConvertKBBBLedenLijst.class.getName());
    private static final String LEAGUE_NAME = "KBBB";
    private static int nbrOfClubs = 0;
    private static int nbrOfMembers = 0;

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        LogManager.getLogManager().readConfiguration(ConvertKBBBLedenLijst.class.getResourceAsStream("/logging.properties"));
        Path dir = Paths.get("input/kbbb/Ledenlijst-KBBB.csv");
        convertFile(dir);
        LOGGER.log(Level.INFO, "Nbr of clubs: {0}", nbrOfClubs);
        LOGGER.log(Level.INFO, "Nbr of members: {0}", nbrOfMembers);
    }
        
    private static void convertFile(Path inputFilePath) throws Exception {
        LOGGER.log(Level.FINEST, "convertFile => file: {0}", inputFilePath);
        LeagueItem league = loadfile(inputFilePath);
        Paths.get("output/kbbb/").toFile().mkdirs();
        Path outputFilePath = Paths.get("output/kbbb/"+ LEAGUE_NAME + ".xml");
        LeagueDataManager.writeFile(league, outputFilePath);
    }
    
    private static LeagueItem loadfile(Path inputFilePath) throws Exception {
        LOGGER.log(Level.FINEST, "loadfile => file: {0}", inputFilePath);
        LeagueItem league = new LeagueItem();
        league.setName(LEAGUE_NAME);
        league.setTurnIndicatorsColor("WHITE");
        league.setWarmingUpTime("5");
        
        File csvData = inputFilePath.toFile();
        
        try (CSVParser parser = CSVParser.parse(csvData, Charset.defaultCharset(), CSVFormat.EXCEL.withDelimiter(';'))) {
            Iterator<CSVRecord> itRecords = parser.iterator();
            while (itRecords.hasNext()) {
                CSVRecord csvRecord = itRecords.next();
                if(!csvRecord.get(0).isEmpty()) {
                    String clubLic = csvRecord.get(1);
                    if (!league.getClubs().containsKey(clubLic)) {
                        nbrOfClubs++;
                        ClubItem club = new ClubItem();
                        club.setLic(clubLic);
                        club.setName(csvRecord.get(2));
                        league.putClub(club);
                    }
                    nbrOfMembers++;
                    MemberItem member = new MemberItem(clubLic);
                    member.setLic(csvRecord.get(0));
                    member.setName(csvRecord.get(3));
                    if(!csvRecord.get(4).equals("-")) {
                        TSPItem tsp = new TSPItem();
                        tsp.setDiscipline("Vrijspel (KB)");
                        tsp.setTsp(csvRecord.get(4));
                        member.putTsp(tsp);
                    }
                    if(!csvRecord.get(5).equals("-")) {
                        TSPItem tsp = new TSPItem();
                        tsp.setDiscipline("Band (KB)");
                        tsp.setTsp(csvRecord.get(5));
                        member.putTsp(tsp);
                    }
                    if(!csvRecord.get(6).equals("-")) {
                        TSPItem tsp = new TSPItem();
                        tsp.setDiscipline("K38/2");
                        tsp.setTsp(csvRecord.get(6));
                        member.putTsp(tsp);
                    }
                    if(!csvRecord.get(7).equals("-")) {
                        TSPItem tsp = new TSPItem();
                        tsp.setDiscipline("Drieband (KB)");
                        tsp.setTsp(csvRecord.get(7));
                        member.putTsp(tsp);
                    }
                    if(!csvRecord.get(8).equals("-")) {
                        TSPItem tsp = new TSPItem();
                        tsp.setDiscipline("Vrijspel (GB)");
                        tsp.setTsp(csvRecord.get(8));
                        member.putTsp(tsp);
                    }
                    if(!csvRecord.get(9).equals("-")) {
                        TSPItem tsp = new TSPItem();
                        tsp.setDiscipline("Band (GB)");
                        tsp.setTsp(csvRecord.get(9));
                        member.putTsp(tsp);
                    }
                    if(!csvRecord.get(10).equals("-")) {
                        TSPItem tsp = new TSPItem();
                        tsp.setDiscipline("K47/2");
                        tsp.setTsp(csvRecord.get(10));
                        member.putTsp(tsp);
                    }
                    if(!csvRecord.get(11).equals("-")) {
                        TSPItem tsp = new TSPItem();
                        tsp.setDiscipline("Drieband (GB)");
                        tsp.setTsp(csvRecord.get(11));
                        member.putTsp(tsp);
                    }
                    league.putMember(member);
                }
            }
        }

        return league;
    }  
}
