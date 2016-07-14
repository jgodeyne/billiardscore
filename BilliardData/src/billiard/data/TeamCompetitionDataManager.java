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
public class TeamCompetitionDataManager {
    private static final Logger LOGGER = Logger.getLogger(TeamCompetitionDataManager.class.getName());
    private static TeamCompetitionDataManager mngr = null;
    private static String path;
    private HashMap<String, TeamCompetitionItem> competitions;
    private XMLEventReader eventReader;


    protected TeamCompetitionDataManager(String path) throws Exception {
        LOGGER.log(Level.FINEST, "Init => path: {0}", path);
        
        path = path + "/tc";
        TeamCompetitionDataManager.path = path;
        readFiles();
    }

    public static TeamCompetitionDataManager getInstance(String path) throws Exception {
        if (mngr == null) {
            mngr = new TeamCompetitionDataManager(path);
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
                readData(entry);
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
        writeData(competition);
    }
    
    public void saveCompetition(TeamCompetitionItem competition) throws Exception {
        competitions.put(competition.getName(),competition);
        writeData(competition);
    }
    
    public void restore(TeamCompetitionItem competition) {
        competitions.put(competition.getName(),competition);
    }

    public void removeCompetition(TeamCompetitionItem competition) throws Exception {
        competitions.remove(competition.getName());
        Path filePath = Paths.get(path + "/"+ competition.getName() + ".xml");
        Files.deleteIfExists(filePath);
    }

    private void readData(Path inputFilePath) throws Exception {
        LOGGER.log(Level.FINEST, "readdata => inputFilePath: {0} => Start", inputFilePath);
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        InputStream in = new FileInputStream(inputFilePath.toFile());
        eventReader = inputFactory.createXMLEventReader(in);

        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            LOGGER.log(Level.FINEST, "readdata => nextEvent: {0}", event.toString());

            if (event.isStartElement()) {
                if (event.asStartElement().getName().getLocalPart().equals(XMLTags.COMPETITION)) {
                    LOGGER.log(Level.FINEST, "readData => competition start event: {0}", event.toString());
                    TeamCompetitionItem competition = readCompetition();
                    competitions.put(competition.getName(), competition);
                }
            }
        }
        LOGGER.log(Level.FINEST, "readdata => inputFilePath: {0} => End", inputFilePath);
    }

    private TeamCompetitionItem readCompetition() throws XMLStreamException {
        LOGGER.log(Level.FINEST, "readCompetition => Start");
        TeamCompetitionItem competition = new TeamCompetitionItem();

        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            LOGGER.log(Level.FINEST, "readCompetition => nextEvent: {0}", event.toString());
            if (event.isStartElement()) {
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.NAME)) {
                    event = eventReader.nextEvent();
                    if(event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readCompetition => name event: {0}", event.toString());
                        competition.setName(event.asCharacters().getData());
                    }
                    continue;
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.CONTACT_NAME)) {
                    event = eventReader.nextEvent();
                    if (event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readCompetition => contact name event: {0}", event.toString());
                        competition.setContactName(event.asCharacters().getData());
                    }
                    continue;
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.CONTACT_EMAIL)) {
                    event = eventReader.nextEvent();
                    if (event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readCompetition => contact email event: {0}", event.toString());
                        competition.setContactEmail(event.asCharacters().getData());
                    }
                    continue;
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.LEAGUE)) {
                    event = eventReader.nextEvent();
                    if(event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readCompetition => league event: {0}", event.toString());
                        competition.setLeague(event.asCharacters().getData());
                    }
                    continue;
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.POINTSYSTEM)) {
                    event = eventReader.nextEvent();
                    if (event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readCompetition => pointsystem event: {0}", event.toString());
                        competition.setPointsSystem(event.asCharacters().getData());
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
                        .equals(XMLTags.TABLEFORMAT)) {
                    event = eventReader.nextEvent();
                    if (event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readCompetition => tableformat event: {0}", event.toString());
                        competition.setTableFormat(event.asCharacters().getData());
                    }
                    continue;
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.GROUP)) {
                    event = eventReader.nextEvent();
                    if (event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readCompetition => group event: {0}", event.toString());
                        competition.setGroup(event.asCharacters().getData());
                    }
                    continue;
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.NBR_OF_PLAYERS)) {
                    event = eventReader.nextEvent();
                    if (event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readCompetition => nbrOfPlayers event: {0}", event.toString());
                        competition.setNbrOfPlayers(event.asCharacters().getData());
                    }
                    continue;
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.TSP_PLAYERS)) {
                    event = eventReader.nextEvent();
                    if (event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readCompetition => tspPlayers event: {0}", event.toString());
                        competition.setTspPlayers(event.asCharacters().getData());
                    }
                    continue;
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.DISCIPLINE_PLAYERS)) {
                    event = eventReader.nextEvent();
                    if (event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readCompetition => disciplinePlayers event: {0}", event.toString());
                        competition.setDisciplinePlayers(event.asCharacters().getData());
                    }
                    continue;
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.TEAM)) {
                    LOGGER.log(Level.FINEST, "readCompetition => team start event: {0}", event.toString());
                    competition.putTeam(readTeam());
                }
            } else if (event.isEndElement()) {
                if (event.asEndElement().getName().getLocalPart().equals(XMLTags.COMPETITION)) {
                    LOGGER.log(Level.FINEST, "readCompetition => competition end event: {0}", event.toString());
                    break;
                }
            }
        }
        LOGGER.log(Level.FINEST, "readCompetition => End");
        return competition;
    }

    private TeamItem readTeam() throws XMLStreamException {
        LOGGER.log(Level.FINEST, "readTeam => Start");
        TeamItem team = new TeamItem();

        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            LOGGER.log(Level.FINEST, "readTeam => nextEvent: {0}", event.toString());
            if (event.isStartElement()) {
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.NAME)) {
                    event = eventReader.nextEvent();
                    if(event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readTeam => name event: {0}", event.toString());
                        team.setName(event.asCharacters().getData());
                    }
                    continue;
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.FIXED_PLAYERS)) {
                    event = eventReader.nextEvent();
                    if(event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readTeam => fixed_players event: {0}", event.toString());
                        team.setFixedPlayers(event.asCharacters().getData());
                    }
                    continue;
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.PLAYER)) {
                    LOGGER.log(Level.FINEST, "readTeam => player start event: {0}", event.toString());
                    team.putPlayer(readPlayer());
                }
            } else if (event.isEndElement()) {
                if (event.asEndElement().getName().getLocalPart().equals(XMLTags.TEAM)) {
                    LOGGER.log(Level.FINEST, "readTeam => team end event: {0}", event.toString());
                    break;
                }
            }
        }
        LOGGER.log(Level.FINEST, "readTeam => End");
        return team;
    }
    
    private PlayerItem readPlayer() throws XMLStreamException {
        LOGGER.log(Level.FINEST, "readPlayer => Start");
        PlayerItem player = new PlayerItem();
        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            LOGGER.log(Level.FINEST, "readPlayer => nextEvent: {0}", event.toString());
            if (event.isStartElement()) {
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.ORDER)) {
                    event = eventReader.nextEvent();
                    if(event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readPlayer => order event: {0}", event.toString());
                        player.setOrder(event.asCharacters().getData());
                    }
                    continue;
                }
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
        TeamCompetitionItem competition = getCompetition(competitionName);
        Path outputfile = Paths.get(destPath.getAbsolutePath() +  "/"+ competition.getName() + ".xml");
        TeamCompetitionDataManager.writeData(competition, outputfile);
    }
    
    public void importCompetition(File srcFile) throws Exception {
        Path target = Paths.get(path + "/"+ srcFile.getName());
        target.toFile().mkdirs();
        Files.copy(srcFile.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
        readFiles();
    }

    public void writeData(TeamCompetitionItem competition) throws Exception {
        Path outputFilePath = Paths.get(path + "/"+ competition.getName() + ".xml");
        writeData(competition, outputFilePath);
    }

    public static void writeData(TeamCompetitionItem competition, java.nio.file.Path outputFilePath) throws Exception {
        LOGGER.log(Level.FINEST, "writedata => outputFilePath: {0} => Start", outputFilePath);
        XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
        XMLEventFactory eventFactory = XMLEventFactory.newFactory();
        Path tmpFilePath = Paths.get(outputFilePath.getParent()+ "/" + new Date() + ".tmp");
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
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.LEAGUE));
        eventWriter.add(eventFactory.createCharacters(competition.getLeague()));
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.LEAGUE));
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.GROUP));
        eventWriter.add(eventFactory.createCharacters(competition.getGroup()));
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.GROUP));
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.POINTSYSTEM));
        eventWriter.add(eventFactory.createCharacters(competition.getPointsSystem()));
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.POINTSYSTEM));
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.TABLEFORMAT));
        eventWriter.add(eventFactory.createCharacters(competition.getTableFormat()));
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.TABLEFORMAT));
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.DISCIPLINE));
        eventWriter.add(eventFactory.createCharacters(competition.getDiscipline()));
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.DISCIPLINE));
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.NBR_OF_PLAYERS));
        eventWriter.add(eventFactory.createCharacters(competition.getNbrOfPlayers()));
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.NBR_OF_PLAYERS));
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.DISCIPLINE_PLAYERS));
        eventWriter.add(eventFactory.createCharacters(competition.getDisciplinePlayers()));
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.DISCIPLINE_PLAYERS));
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.TSP_PLAYERS));
        eventWriter.add(eventFactory.createCharacters(competition.getTspPlayers()));
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.TSP_PLAYERS));
        
        for (TeamItem team: competition.getTeams().values()) {
            eventWriter.add(eventFactory.createStartElement("", null, XMLTags.TEAM));
            eventWriter.add(eventFactory.createStartElement("", null, XMLTags.NAME));
            eventWriter.add(eventFactory.createCharacters(team.getName()));
            eventWriter.add(eventFactory.createEndElement("", null, XMLTags.NAME));
            eventWriter.add(eventFactory.createStartElement("", null, XMLTags.FIXED_PLAYERS));
            eventWriter.add(eventFactory.createCharacters(team.getFixedPlayers()));
            eventWriter.add(eventFactory.createEndElement("", null, XMLTags.FIXED_PLAYERS));
            for(PlayerItem player: team.getPlayers().values()) {
                eventWriter.add(eventFactory.createStartElement("", null, XMLTags.PLAYER));
                eventWriter.add(eventFactory.createStartElement("", null, XMLTags.ORDER));
                eventWriter.add(eventFactory.createCharacters(player.getOrder()));
                eventWriter.add(eventFactory.createEndElement("", null, XMLTags.ORDER));
                eventWriter.add(eventFactory.createStartElement("", null, XMLTags.LIC));
                eventWriter.add(eventFactory.createCharacters(player.getLic()));
                eventWriter.add(eventFactory.createEndElement("", null, XMLTags.LIC));
                eventWriter.add(eventFactory.createStartElement("", null, XMLTags.NAME));
                eventWriter.add(eventFactory.createCharacters(player.getName()));
                eventWriter.add(eventFactory.createEndElement("", null, XMLTags.NAME));
                eventWriter.add(eventFactory.createStartElement("", null, XMLTags.DISCIPLINE));
                eventWriter.add(eventFactory.createCharacters(player.getDiscipline()));
                eventWriter.add(eventFactory.createEndElement("", null, XMLTags.DISCIPLINE));
                eventWriter.add(eventFactory.createStartElement("", null, XMLTags.TSP));
                eventWriter.add(eventFactory.createCharacters((player.getTsp())));
                eventWriter.add(eventFactory.createEndElement("", null, XMLTags.TSP));
                eventWriter.add(eventFactory.createEndElement("", null, XMLTags.PLAYER));
            }
            eventWriter.add(eventFactory.createEndElement("", null, XMLTags.TEAM));
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
