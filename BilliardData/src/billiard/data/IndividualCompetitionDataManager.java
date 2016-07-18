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
public class IndividualCompetitionDataManager {
    private static final Logger LOGGER = Logger.getLogger(IndividualCompetitionDataManager.class.getName());
    private static IndividualCompetitionDataManager mngr = null;
    private static String path;
    private HashMap<String, IndividualCompetitionItem> competitions;
    private XMLEventReader eventReader;


    protected IndividualCompetitionDataManager() throws Exception {
        LOGGER.log(Level.FINEST, "Init => path: {0}", path);
        
        path = AppProperties.getInstance().getDataPath() + "/ic";
        readFiles();
    }

    public static IndividualCompetitionDataManager getInstance() throws Exception {
        if (mngr == null) {
            mngr = new IndividualCompetitionDataManager();
        }
        return mngr;
    }

    private void readFiles() throws Exception {
        this.competitions = new HashMap<>();
        if(Files.exists(new File(path).toPath())) {
            java.nio.file.Path dir = Paths.get(path);
            DirectoryStream<java.nio.file.Path> stream = Files.newDirectoryStream(dir, "*.xml");
            for (java.nio.file.Path entry: stream) {
                LOGGER.log(Level.FINEST, "Init => entry: {0}", entry.toString());
                readFile(entry);
            }
        }
        
    }
    public ArrayList<String> getCompetitionNames() {
        ArrayList<String> names = new ArrayList<>();
        for (Map.Entry<String, IndividualCompetitionItem> entry : competitions.entrySet()) {
            IndividualCompetitionItem value = entry.getValue();
            names.add(value.getName());
        }
        return names;
    }

    public IndividualCompetitionItem getCompetition(String name) {
        return competitions.get(name);
    }
        
    public void addCompetition(IndividualCompetitionItem competition) throws Exception {
        competitions.put(competition.getName(),competition);
        writeFile(competition);
    }
    
    public void saveCompetition(IndividualCompetitionItem competition) throws Exception {
        competitions.put(competition.getName(),competition);
        writeFile(competition);
    }
    
    public void restore(IndividualCompetitionItem competition) {
        competitions.put(competition.getName(),competition);
    }

    public void removeCompetition(IndividualCompetitionItem competition) throws Exception {
        competitions.remove(competition.getName());
        Path filePath = Paths.get(path + "/"+ competition.getName() + ".xml");
        Files.deleteIfExists(filePath);
    }

    
    private void readFile(java.nio.file.Path inputFilePath) throws Exception {
        LOGGER.log(Level.FINEST, "readdata => inputFilePath: {0} => Start", inputFilePath);
        String xmlString = new String(Files.readAllBytes(inputFilePath));
        IndividualCompetitionItem competition = IndividualCompetitionItem.fromXML(xmlString);
        competitions.put(competition.getName(), competition);
        LOGGER.log(Level.FINEST, "readdata => inputFilePath: {0} => End", inputFilePath);
    }

    
    public void export(String competitionName, File destPath) throws Exception {
        IndividualCompetitionItem competition = getCompetition(competitionName);
        Path outputfile = Paths.get(destPath.getAbsolutePath() +  "/"+ competition.getName() + ".xml");
        IndividualCompetitionDataManager.writeFile(competition, outputfile);
    }
    
    public void importCompetition(File srcFile) throws Exception {
        Path target = Paths.get(path + "/"+ srcFile.getName());
        target.toFile().getParentFile().mkdirs();
        Files.copy(srcFile.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
        readFiles();
    }

    public void writeFile(IndividualCompetitionItem competition) throws Exception {
        Path outputFilePath = Paths.get(path + "/"+ competition.getName() + ".xml");
        writeFile(competition, outputFilePath);
    }
    
    public static void writeFile(IndividualCompetitionItem competition, java.nio.file.Path outputFilePath) throws Exception {
        LOGGER.log(Level.FINEST, "writedata => outputFilePath: {0} => Start", outputFilePath);
        outputFilePath.toFile().getParentFile().mkdirs();
        Transformer xformer = TransformerFactory.newInstance().newTransformer();
        xformer.setOutputProperty(OutputKeys.METHOD, "xml");
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
