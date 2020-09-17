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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLEventReader;
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
public class TeamCompetitionDataManager {
    private static final Logger LOGGER = Logger.getLogger(TeamCompetitionDataManager.class.getName());
    private static TeamCompetitionDataManager mngr = null;
    private static String path;
    private HashMap<String, TeamCompetitionItem> competitions;
    private XMLEventReader eventReader;


    protected TeamCompetitionDataManager() throws Exception {
        LOGGER.log(Level.FINEST, "Init => path: {0}", path);
        
        path = AppProperties.getInstance().getDataPath() + "/tc";
        readFiles();
    }

    public static TeamCompetitionDataManager getInstance() throws Exception {
        if (mngr == null) {
            mngr = new TeamCompetitionDataManager();
        }
        return mngr;
    }

    private void readFiles() throws Exception {
        competitions = new HashMap();
        if(Files.exists(new File(path).toPath())) {
            Path dir = Paths.get(path);
            DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.xml");
            LOGGER.log(Level.FINEST, "Init => dirStream: {0}", stream.toString());
            for (Path entry: stream) {
                LOGGER.log(Level.FINEST, "Init => entry: {0}", entry.toString());
                readFile(entry);
            }
        }
    }
    public ArrayList getCompetitionNames() {
        LOGGER.log(Level.FINEST, "getCompetitionNames");
        ArrayList<String> names = new ArrayList<>();
        for (Map.Entry<String, TeamCompetitionItem> entry : competitions.entrySet()) {
            String key = entry.getKey();
            TeamCompetitionItem value = entry.getValue();
            names.add(key);
        }
        return names;
    }

    public TeamCompetitionItem getCompetition(String name) {
        return competitions.get(name);
    }
    
    public void addCompetition(TeamCompetitionItem competition) throws Exception {
        competitions.put(competition.getName(),competition);
        writeFile(competition);
    }
    
    public void saveCompetition(TeamCompetitionItem competition) throws Exception {
        competitions.put(competition.getName(),competition);
        writeFile(competition);
    }
    
    public void restore(TeamCompetitionItem competition) {
        competitions.put(competition.getName(),competition);
    }

    public void removeCompetition(TeamCompetitionItem competition) throws Exception {
        competitions.remove(competition.getName());
        Path filePath = Paths.get(path + "/"+ competition.getName() + ".xml");
        Files.deleteIfExists(filePath);
    }

    private void readFile(Path inputFilePath) throws Exception {
        LOGGER.log(Level.FINEST, "readdata => inputFilePath: {0} => Start", inputFilePath);
        String xmlString = new String(Files.readAllBytes(inputFilePath));
        TeamCompetitionItem competition = TeamCompetitionItem.fromXML(xmlString);
        competitions.put(competition.getName(), competition);
        LOGGER.log(Level.FINEST, "readdata => inputFilePath: {0} => End", inputFilePath);
    }

    
    public void export(String competitionName, File destPath) throws Exception {
        TeamCompetitionItem competition = getCompetition(competitionName);
        Path outputfile = Paths.get(destPath.getAbsolutePath() +  "/"+ competition.getName() + ".xml");
        TeamCompetitionDataManager.writeFile(competition, outputfile);
    }
    
    public void importCompetition(File srcFile) throws Exception {
        Path target = Paths.get(path + "/"+ srcFile.getName());
        target.toFile().getParentFile().mkdirs();
        Files.copy(srcFile.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
        readFiles();
    }

    public void writeFile(TeamCompetitionItem competition) throws Exception {
        Path outputFilePath = Paths.get(path + "/"+ competition.getName() + ".xml");
        TeamCompetitionDataManager.writeFile(competition, outputFilePath);
    }

    public static void writeFile(TeamCompetitionItem competition, java.nio.file.Path outputFilePath) throws Exception {
        LOGGER.log(Level.FINEST, "writedata => outputFilePath: {0} => Start", outputFilePath);
        outputFilePath.toFile().getParentFile().mkdirs();
        Transformer xformer = TransformerFactory.newInstance().newTransformer();
        xformer.setOutputProperty(OutputKeys.METHOD, "xml");
        xformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        xformer.setOutputProperty(OutputKeys.INDENT, "yes");
        xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        String xmlString = competition.toXML();
        StringReader sr = new StringReader(xmlString);
        Source source = new StreamSource(sr);
        File outputFile = outputFilePath.toFile();
        Result result = new StreamResult(outputFile);
        xformer.transform(source, result);
        LOGGER.log(Level.FINEST, "writedata => outputFilePath: {0} => End", outputFilePath);
    }
    
}
