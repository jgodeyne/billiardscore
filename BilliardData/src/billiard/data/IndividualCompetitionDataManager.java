/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
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


    protected IndividualCompetitionDataManager(String path) throws Exception {
        LOGGER.log(Level.FINEST, "Init => path: {0}", path);
        
        path = path + "/ic";
        IndividualCompetitionDataManager.path = path;
        readFiles();
    }

    public static IndividualCompetitionDataManager getInstance(String path) throws Exception {
        if (mngr == null) {
            mngr = new IndividualCompetitionDataManager(path);
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
                readData(entry);
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
        writeData(competition);
    }
    
    public void saveCompetition(IndividualCompetitionItem competition) throws Exception {
        competitions.put(competition.getName(),competition);
        writeData(competition);
    }
    
    public void restore(IndividualCompetitionItem competition) {
        competitions.put(competition.getName(),competition);
    }

    public void removeCompetition(IndividualCompetitionItem competition) throws Exception {
        competitions.remove(competition.getName());
        Path filePath = Paths.get(path + "/"+ competition.getName() + ".xml");
        Files.deleteIfExists(filePath);
    }

    
    private void readData(java.nio.file.Path inputFilePath) throws Exception {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        InputStream in = new FileInputStream(inputFilePath.toFile());
        eventReader = inputFactory.createXMLEventReader(in);

        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            LOGGER.log(Level.FINEST, "readdata => nextEvent: {0}", event.toString());

            if (event.isStartElement()) {
                if (event.asStartElement().getName().getLocalPart().equals(XMLTags.COMPETITION)) {
                    LOGGER.log(Level.FINEST, "readData => competition start event: {0}", event.toString());
                    IndividualCompetitionItem competition = readIndividualCompetition();
                    competitions.put(competition.getName(), competition);
                }
            }
        }
        LOGGER.log(Level.FINEST, "readdata => inputFilePath: {0} => End", inputFilePath);
    }

    private IndividualCompetitionItem readIndividualCompetition() throws XMLStreamException {
        LOGGER.log(Level.FINEST, "readIndividualCompetition => Start");
        IndividualCompetitionItem competition = new IndividualCompetitionItem();

        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            LOGGER.log(Level.FINEST, "readIndividualCompetition => nextEvent: {0}", event.toString());
            if (event.isStartElement()) {
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.NAME)) {
                    event = eventReader.nextEvent();
                    if(event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readIndividualCompetition => name event: {0}", event.toString());
                        competition.setName(event.asCharacters().getData());
                    }
                    continue;
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.CONTACT_NAME)) {
                    event = eventReader.nextEvent();
                    if (event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readIndividualCompetition => contact name event: {0}", event.toString());
                        competition.setContactName(event.asCharacters().getData());
                    }
                    continue;
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.CONTACT_EMAIL)) {
                    event = eventReader.nextEvent();
                    if (event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readIndividualCompetition => contact email event: {0}", event.toString());
                        competition.setContactEmail(event.asCharacters().getData());
                    }
                    continue;
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.DISCIPLINE)) {
                    event = eventReader.nextEvent();
                    if (event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readCompetition => discipline event: {0}", event.toString());
                        competition.setDiscipline(event.asCharacters().getData());
                    }
                    continue;
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.LEAGUE)) {
                    event = eventReader.nextEvent();
                    if (event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readCompetition => league event: {0}", event.toString());
                        competition.setLeague(event.asCharacters().getData());
                    }
                    continue;
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.PLAYER)) {
                    LOGGER.log(Level.FINEST, "readIndividualCompetition => player start event: {0}", event.toString());
                    competition.putPlayer(readPlayers());
                }
            } else if (event.isEndElement()) {
                if (event.asEndElement().getName().getLocalPart().equals(XMLTags.COMPETITION)) {
                    LOGGER.log(Level.FINEST, "readIndividualCompetition => competition end event: {0}", event.toString());
                    break;
                }
            }
        }
        LOGGER.log(Level.FINEST, "readCompetition => End");
        return competition;
    }
    
    private PlayerItem readPlayers() throws XMLStreamException {
        LOGGER.log(Level.FINEST, "readPlayer => Start");
        PlayerItem player = new PlayerItem();
        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            LOGGER.log(Level.FINEST, "readPlayer => nextEvent: {0}", event.toString());
            if (event.isStartElement()) {
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.LIC)) {
                    event = eventReader.nextEvent();
                    if(event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readPlayer => lic event: {0}", event.toString());
                        player.setLic(event.asCharacters().getData());
                    }
                    continue;
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.NAME)) {
                    event = eventReader.nextEvent();
                    if(event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readPlayer => name event: {0}", event.toString());
                        player.setName(event.asCharacters().getData());
                    }
                    continue;
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.TSP)) {
                    event = eventReader.nextEvent();
                    if (event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readPlayer => tsp event: {0}", event.toString());
                        player.setTsp(event.asCharacters().getData());
                    }
                    continue;
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.DISCIPLINE)) {
                    event = eventReader.nextEvent();
                    if (event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readPlayer => discipline event: {0}", event.toString());
                        player.setDiscipline(event.asCharacters().getData());
                    }
                    continue;
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.CLUB)) {
                    event = eventReader.nextEvent();
                    if(event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readMember => club event: {0}", event.toString());
                        player.setClub(event.asCharacters().getData());
                    }
                }
            } else if (event.isEndElement()) {
                if (event.asEndElement().getName().getLocalPart().equals(XMLTags.PLAYER)) {
                    LOGGER.log(Level.FINEST, "readPlayer => player end event: {0}", event.toString());
                    break;
                }
            }
        }
        LOGGER.log(Level.FINEST, "readPlayer => End");
        return player;
    }
    
    public void export(String competitionName, File destPath) throws Exception {
        IndividualCompetitionItem competition = getCompetition(competitionName);
        Path outputfile = Paths.get(destPath.getAbsolutePath() +  "/"+ competition.getName() + ".xml");
        IndividualCompetitionDataManager.writeData(competition, outputfile);
    }
    
    public void importCompetition(File srcFile) throws Exception {
        Path target = Paths.get(path + "/"+ srcFile.getName());
        target.toFile().mkdirs();
        Files.copy(srcFile.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
        readFiles();
    }

    public void writeData(IndividualCompetitionItem competition) throws Exception {
        Path outputFilePath = Paths.get(path + "/"+ competition.getName() + ".xml");
        writeData(competition, outputFilePath);
    }
    
    public static void writeData(IndividualCompetitionItem competition, java.nio.file.Path outputFilePath) throws Exception {
        LOGGER.log(Level.FINEST, "writedata => outputFilePath: {0} => Start", outputFilePath);
        XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
        XMLEventFactory eventFactory = XMLEventFactory.newFactory();
        Path tmpFilePath = Paths.get(outputFilePath.getParent() + "/" + new Date() + ".tmp");
        tmpFilePath.getParent().toFile().mkdirs();
        FileOutputStream outputStream = new FileOutputStream(tmpFilePath.toFile());
        XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(outputStream);
        eventWriter.add(eventFactory.createStartDocument());

        eventWriter.add(eventFactory.createStartElement("",null, XMLTags.COMPETITION));
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.NAME));
        eventWriter.add(eventFactory.createCharacters(competition.getName()));
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.NAME));
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.CONTACT_NAME));
        eventWriter.add(eventFactory.createCharacters(competition.getContactName()));
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.CONTACT_NAME));
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.CONTACT_EMAIL));
        eventWriter.add(eventFactory.createCharacters(competition.getContactEmail()));
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.CONTACT_EMAIL));
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.DISCIPLINE));
        eventWriter.add(eventFactory.createCharacters(competition.getDiscipline()));
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.DISCIPLINE));
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.LEAGUE));
        eventWriter.add(eventFactory.createCharacters(competition.getLeague()));
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.LEAGUE));
       
        for (PlayerItem player: competition.getPlayers().values()) {
            eventWriter.add(eventFactory.createStartElement("", null, XMLTags.PLAYER));
            eventWriter.add(eventFactory.createStartElement("", null, XMLTags.LIC));
            eventWriter.add(eventFactory.createCharacters(player.getLic()));
            eventWriter.add(eventFactory.createEndElement("", null, XMLTags.LIC));
            eventWriter.add(eventFactory.createStartElement("", null, XMLTags.NAME));
            eventWriter.add(eventFactory.createCharacters(player.getName()));
            eventWriter.add(eventFactory.createEndElement("", null, XMLTags.NAME));
            eventWriter.add(eventFactory.createStartElement("", null, XMLTags.TSP));
            eventWriter.add(eventFactory.createCharacters(player.getTsp()));
            eventWriter.add(eventFactory.createEndElement("", null, XMLTags.TSP));
            eventWriter.add(eventFactory.createStartElement("", null, XMLTags.DISCIPLINE));
            eventWriter.add(eventFactory.createCharacters(player.getDiscipline()));
            eventWriter.add(eventFactory.createEndElement("", null, XMLTags.DISCIPLINE));
            eventWriter.add(eventFactory.createStartElement("", null, XMLTags.CLUB));
            eventWriter.add(eventFactory.createCharacters(player.getClub()));
            eventWriter.add(eventFactory.createEndElement("", null, XMLTags.CLUB));
            eventWriter.add(eventFactory.createEndElement("", null, XMLTags.PLAYER));
        }
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.COMPETITION));
        eventWriter.add(eventFactory.createEndDocument());
        eventWriter.close();
        
        Transformer xformer = TransformerFactory.newInstance().newTransformer();
        xformer.setOutputProperty(OutputKeys.METHOD, "xml");
        xformer.setOutputProperty(OutputKeys.INDENT, "yes");
        xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        Source source = new StreamSource(tmpFilePath.toFile());
        File outputFile = outputFilePath.toFile();
        Result result = new StreamResult(outputFile);
        xformer.transform(source, result);
        Files.deleteIfExists(tmpFilePath);
        LOGGER.log(Level.FINEST, "writedata => outputFilePath: {0} => End", outputFilePath);
    }
}
