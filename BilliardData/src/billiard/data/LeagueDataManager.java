/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.data;

import billiard.common.AppProperties;
import java.io.File;
import java.io.StringReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author jean
 */
public class LeagueDataManager {
    private static final Logger LOGGER = Logger.getLogger(LeagueDataManager.class.getName());
    private static LeagueDataManager mngr = null;
    private static String path;
    private HashMap<String, LeagueItem> leagues;


    protected LeagueDataManager() throws Exception {
        path = AppProperties.getInstance().getDataPath() + "/leagues";
        readFiles();
    }

    public static LeagueDataManager getInstance() throws Exception {
        if (mngr == null) {
            mngr = new LeagueDataManager();
        }
        return mngr;
    }

    private void readFiles() throws Exception {
        this.leagues = new HashMap<>();
        if(Files.exists(new File(path).toPath())) {
            java.nio.file.Path dir = Paths.get(path);
            DirectoryStream<java.nio.file.Path> stream = Files.newDirectoryStream(dir, "*.xml");
            for (java.nio.file.Path entry: stream) {
                LOGGER.log(Level.FINEST, "Init => entry: {0}", entry.toString());
                readFile(entry);
            }
        }

    }
    public ArrayList<String> getLeagueNames() {
        LOGGER.log(Level.FINEST, "getLeagueNames");
        ArrayList<String> names = new ArrayList<>();
        for (LeagueItem item : leagues.values()) {
            LOGGER.log(Level.FINEST, "getLeagueNames => name: {0}", item.getName());
            names.add(item.getName());
        }
        return names;
    }

    public LeagueItem getLeague(String name) {
        LOGGER.log(Level.FINEST, "getLeague => name: {0}", name);
        return leagues.get(name);
    }

    public void addLeague(LeagueItem league) throws Exception {
        leagues.put(league.getName(), league);
        writeFile(league);
    }
    
    public void saveLeague(LeagueItem league) throws Exception {
        leagues.put(league.getName(), league);
        writeFile(league);
    }

    public void removeLeague(LeagueItem league) throws Exception {
        Path filePath = Paths.get(path + "/"+ league.getName() + ".xml");
        Files.deleteIfExists(filePath);
        leagues.remove(league.getName());
    }

    public void restoreLeague(LeagueItem league) {
        leagues.put(league.getName(), league);        
    }
    
    private void readFile(java.nio.file.Path inputFilePath) throws Exception {
        LOGGER.log(Level.FINEST, "readdata => inputFilePath: {0} => Start", inputFilePath);
        String xmlString = new String(Files.readAllBytes(inputFilePath));
        LeagueItem league = LeagueItem.fromXML(xmlString);
        leagues.put(league.getName(), league);
    }

    public void exportLeague(String leagueName, File destFile) throws Exception {
        LeagueItem league = getLeague(leagueName);
        Path outputfile = Paths.get(destFile.getAbsolutePath() +  "/"+ league.getName() + ".xml");
        LeagueDataManager.writeFile(league, outputfile);
    }

    public void importLeague(File srcFile) throws Exception {
        Path target = Paths.get(path + "/"+ srcFile.getName());
        target.toFile().getParentFile().mkdirs();
        Files.copy(srcFile.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
        readFiles();
    }
    
    public void writeFile(LeagueItem league) throws Exception {
        Path outputFilePath = Paths.get(path + "/"+ league.getName() + ".xml");
        writeFile(league, outputFilePath);
    }
    
    public static void writeFile(LeagueItem league, java.nio.file.Path outputFilePath) throws Exception {
        LOGGER.log(Level.FINEST, "writedata => outputFilePath: {0} => Start", outputFilePath);
        outputFilePath.toFile().getParentFile().mkdirs();
        String xmlString = league.toXML();
        
        Transformer xformer = TransformerFactory.newInstance().newTransformer();
        xformer.setOutputProperty(OutputKeys.METHOD, "xml");
        xformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        xformer.setOutputProperty(OutputKeys.INDENT, "yes");
        xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        StringReader sr = new StringReader(xmlString);
        Source source = new StreamSource(sr);
        File outputFile = outputFilePath.toFile();
        Result result = new StreamResult(outputFile);
        xformer.transform(source, result);
        LOGGER.log(Level.FINEST, "writedata => outputFilePath: {0} => End", outputFilePath);
    }    
}
