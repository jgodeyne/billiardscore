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
public class LeagueDataManager {
    private static final Logger LOGGER = Logger.getLogger(LeagueDataManager.class.getName());
    private static LeagueDataManager mngr = null;
    private static String path;
    private HashMap<String, LeagueItem> leagues;
    private XMLEventReader eventReader;


    protected LeagueDataManager(String path) throws Exception {
        LOGGER.log(Level.FINEST, "Init => path: {0}", path);
        
        path = path + "/leagues";
        LeagueDataManager.path = path;
        readFiles();
    }

    public static LeagueDataManager getInstance(String path) throws Exception {
        if (mngr == null) {
            mngr = new LeagueDataManager(path);
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
                readData(entry);
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
        writeData(league);
    }
    
    public void saveLeague(LeagueItem league) throws Exception {
        leagues.put(league.getName(), league);
        writeData(league);
    }

    public void removeLeague(LeagueItem league) throws Exception {
        Path filePath = Paths.get(path + "/"+ league.getName() + ".xml");
        Files.deleteIfExists(filePath);
        leagues.remove(league.getName());
    }

    public void restoreLeague(LeagueItem league) {
        leagues.put(league.getName(), league);        
    }
    
    private void readData(java.nio.file.Path inputFilePath) throws Exception {
        LOGGER.log(Level.FINEST, "readdata => inputFilePath: {0} => Start", inputFilePath);
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        InputStream in = new FileInputStream(inputFilePath.toFile());
        eventReader = inputFactory.createXMLEventReader(in);

        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            LOGGER.log(Level.FINEST, "readdata => nextEvent: {0}", event.toString());

            if (event.isStartElement()) {
                if (event.asStartElement().getName().getLocalPart().equals(XMLTags.LEAGUE)) {
                    LOGGER.log(Level.FINEST, "readData => league start event: {0}", event.toString());
                    LeagueItem league = readLeague();
                    leagues.put(league.getName(), league);
                }
            }
        }
        LOGGER.log(Level.FINEST, "readdata => inputFilePath: {0} => End", inputFilePath);
    }

    private LeagueItem readLeague() throws Exception {
        LOGGER.log(Level.FINEST, "readLeague => Start");
        LeagueItem league = new LeagueItem();

        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            LOGGER.log(Level.FINEST, "readLeague => nextEvent: {0}", event.toString());
            if (event.isStartElement()) {
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.NAME)) {
                    event = eventReader.nextEvent();
                    if(event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readLeague => name event: {0}", event.toString());
                        league.setName(event.asCharacters().getData());
                    }
                    continue;
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.CONTACT_NAME)) {
                    event = eventReader.nextEvent();
                    if (event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readLeague => contact name event: {0}", event.toString());
                        league.setContactName(event.asCharacters().getData());
                    }
                    continue;
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.CONTACT_EMAIL)) {
                    event = eventReader.nextEvent();
                    if (event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readLeague => contact email event: {0}", event.toString());
                        league.setContactEmail(event.asCharacters().getData());
                    }
                    continue;
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.TURNINDICATORS_COLOR)) {
                    event = eventReader.nextEvent();
                    if (event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readLeague => turnidicators color event: {0}", event.toString());
                        league.setTurnIndicatorsColor(event.asCharacters().getData());
                    }
                    continue;
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.WARMINGUP_TIME)) {
                    event = eventReader.nextEvent();
                    if (event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readLeague => warming-up time event: {0}", event.toString());
                        league.setWarmingUpTime(event.asCharacters().getData());
                    }
                    continue;
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.CLUBS)) {
                    LOGGER.log(Level.FINEST, "readLeague => clubs start event: {0}", event.toString());
                    readClubs(league);
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.MEMBERS)) {
                    LOGGER.log(Level.FINEST, "readLeague => members start event: {0}", event.toString());
                    readMembers(league);
                }
            } else if (event.isEndElement()) {
                if (event.asEndElement().getName().getLocalPart().equals(XMLTags.LEAGUE)) {
                    LOGGER.log(Level.FINEST, "readLeague => league end event: {0}", event.toString());
                    break;
                }
            }
        }
        LOGGER.log(Level.FINEST, "readCompetition => End");
        return league;
    }
    
    private void readClubs(LeagueItem league) throws Exception {
        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            LOGGER.log(Level.FINEST, "readLeague => nextEvent: {0}", event.toString());
            if (event.isStartElement()) {
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.CLUB)) {
                    league.putClub(readClub());
                }
            } else if (event.isEndElement()) {
                if (event.asEndElement().getName().getLocalPart().equals(XMLTags.CLUBS)) {
                    LOGGER.log(Level.FINEST, "readLeague => clubs end event: {0}", event.toString());
                    break;
                }
            }
        }
    }
    
    private void readMembers(LeagueItem league) throws Exception {
        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            LOGGER.log(Level.FINEST, "readLeague => nextEvent: {0}", event.toString());
            if (event.isStartElement()) {
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.MEMBER)) {
                    league.putMember(readMember());
                }
            } else if (event.isEndElement()) {
                if (event.asEndElement().getName().getLocalPart().equals(XMLTags.MEMBERS)) {
                    LOGGER.log(Level.FINEST, "readLeague => members end event: {0}", event.toString());
                    break;
                }
            }
        }
    }
    
    private ClubItem readClub() throws XMLStreamException {
        LOGGER.log(Level.FINEST, "readClub => Start");
        ClubItem club = new ClubItem();

        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            LOGGER.log(Level.FINEST, "readClub => nextEvent: {0}", event.toString());
            if (event.isStartElement()) {
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.LIC)) {
                    event = eventReader.nextEvent();
                    if(event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readClub => lic event: {0}", event.toString());
                        club.setLic(event.asCharacters().getData());
                    }
                    continue;
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.NAME)) {
                    event = eventReader.nextEvent();
                    if(event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readClub => name event: {0}", event.toString());
                        club.setName(event.asCharacters().getData());
                    }
                    continue;
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.CONTACT_NAME)) {
                    event = eventReader.nextEvent();
                    if (event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readClub => contact name event: {0}", event.toString());
                        club.setContactName(event.asCharacters().getData());
                    }
                    continue;
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.CONTACT_EMAIL)) {
                    event = eventReader.nextEvent();
                    if (event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readClub => contact email event: {0}", event.toString());
                        club.setContactEmail(event.asCharacters().getData());
                    }
                }
            } else if (event.isEndElement()) {
                if (event.asEndElement().getName().getLocalPart().equals(XMLTags.CLUB)) {
                    LOGGER.log(Level.FINEST, "readClub => league end event: {0}", event.toString());
                    break;
                }
            }
        }
        LOGGER.log(Level.FINEST, "readClub => End");
        return club;
    }

    private MemberItem readMember() throws XMLStreamException {
        LOGGER.log(Level.FINEST, "readMember => Start");
        MemberItem member = new MemberItem("");
        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            LOGGER.log(Level.FINEST, "readMember => nextEvent: {0}", event.toString());
            if (event.isStartElement()) {
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.LIC)) {
                    event = eventReader.nextEvent();
                    if(event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readMember => lic event: {0}", event.toString());
                        member.setLic(event.asCharacters().getData());
                    }
                    continue;
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.NAME)) {
                    event = eventReader.nextEvent();
                    if(event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readMember => name event: {0}", event.toString());
                        member.setName(event.asCharacters().getData());
                    }
                    continue;
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.CLUB)) {
                    event = eventReader.nextEvent();
                    if(event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readMember => clublic event: {0}", event.toString());
                        member.setClubLic(event.asCharacters().getData());
                    }
                    continue;
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.TSPS)) {
                    LOGGER.log(Level.FINEST, "readMember => tsps start event: {0}", event.toString());
                    member.putTsp(readTSP());
                }
            } else if (event.isEndElement()) {
                if (event.asEndElement().getName().getLocalPart().equals(XMLTags.MEMBER)) {
                    LOGGER.log(Level.FINEST, "readMember => member end event: {0}", event.toString());
                    break;
                }
            }
        }
        LOGGER.log(Level.FINEST, "readMember => End");
        return member;
    }
    
    private TSPItem readTSP() throws XMLStreamException {
        LOGGER.log(Level.FINEST, "readTSP => Start");
        TSPItem tsp= new TSPItem();
        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            LOGGER.log(Level.FINEST, "readTSP => nextEvent: {0}", event.toString());
            if (event.isStartElement()) {
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.TSP)) {
                    event = eventReader.nextEvent();
                    if (event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readTSP => tsp event: {0}", event.toString());
                        tsp.setTsp(event.asCharacters().getData());
                    }
                    continue;
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.DISCIPLINE)) {
                    event = eventReader.nextEvent();
                    if (event.isCharacters()) {
                        LOGGER.log(Level.FINEST, "readTSP => discipline event: {0}", event.toString());
                        tsp.setDiscipline(event.asCharacters().getData());
                    }
                }
            } else if (event.isEndElement()) {
                if (event.asEndElement().getName().getLocalPart().equals(XMLTags.TSPS)) {
                    LOGGER.log(Level.FINEST, "readTSP => TSPs end event: {0}", event.toString());
                    break;
                }
            }
        }
        LOGGER.log(Level.FINEST, "readTSP => End");
        return tsp;
    }

    public void exportLeague(String leagueName, File destFile) throws Exception {
        LeagueItem league = getLeague(leagueName);
        Path outputfile = Paths.get(destFile.getAbsolutePath() +  "/"+ league.getName() + ".xml");
        LeagueDataManager.writeData(league, outputfile);
    }

    public void importLeague(File srcFile) throws Exception {
        Path target = Paths.get(path + "/"+ srcFile.getName());
        target.toFile().mkdirs();
        Files.copy(srcFile.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
        readFiles();
    }
    
    public void writeData(LeagueItem league) throws Exception {
        Path outputFilePath = Paths.get(path + "/"+ league.getName() + ".xml");
        writeData(league, outputFilePath);
    }
    
    public static void writeData(LeagueItem league, java.nio.file.Path outputFilePath) throws Exception {
        LOGGER.log(Level.FINEST, "writedata => outputFilePath: {0} => Start", outputFilePath);
        XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
        XMLEventFactory eventFactory = XMLEventFactory.newFactory();
        Path tmpFilePath = Paths.get(outputFilePath.getParent()+"/" + new Date() + ".tmp");
        tmpFilePath.getParent().toFile().mkdirs();
        FileOutputStream outputStream = new FileOutputStream(tmpFilePath.toFile());
        XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(outputStream);
        eventWriter.add(eventFactory.createStartDocument());

        eventWriter.add(eventFactory.createStartElement("",null, XMLTags.LEAGUE));
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.NAME));
        eventWriter.add(eventFactory.createCharacters(league.getName()));
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.NAME));
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.CONTACT_NAME));
        eventWriter.add(eventFactory.createCharacters(league.getContactName()));
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.CONTACT_NAME));
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.CONTACT_EMAIL));
        eventWriter.add(eventFactory.createCharacters(league.getContactEmail()));
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.CONTACT_EMAIL));
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.TURNINDICATORS_COLOR));
        eventWriter.add(eventFactory.createCharacters(league.getTurnIndicatorsColor()));
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.TURNINDICATORS_COLOR));
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.WARMINGUP_TIME));
        eventWriter.add(eventFactory.createCharacters(league.getWarmingUpTime()));
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.WARMINGUP_TIME));
        
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.CLUBS));
        for (ClubItem club: league.getClubs().values()) {
            eventWriter.add(eventFactory.createStartElement("", null, XMLTags.CLUB));
            eventWriter.add(eventFactory.createStartElement("", null, XMLTags.LIC));
            eventWriter.add(eventFactory.createCharacters(String.join(":",club.getLic())));
            eventWriter.add(eventFactory.createEndElement("", null, XMLTags.LIC));
            eventWriter.add(eventFactory.createStartElement("", null, XMLTags.NAME));
            eventWriter.add(eventFactory.createCharacters(club.getName()));
            eventWriter.add(eventFactory.createEndElement("", null, XMLTags.NAME));
            eventWriter.add(eventFactory.createStartElement("", null, XMLTags.CONTACT_NAME));
            eventWriter.add(eventFactory.createCharacters(league.getContactName()));
            eventWriter.add(eventFactory.createEndElement("", null, XMLTags.CONTACT_NAME));
            eventWriter.add(eventFactory.createStartElement("", null, XMLTags.CONTACT_EMAIL));
            eventWriter.add(eventFactory.createCharacters(league.getContactEmail()));
            eventWriter.add(eventFactory.createEndElement("", null, XMLTags.CONTACT_EMAIL));
            eventWriter.add(eventFactory.createEndElement("", null, XMLTags.CLUB));
        }
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.CLUBS));
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.MEMBERS));
        for (MemberItem member: league.getMembers().values()) {
            eventWriter.add(eventFactory.createStartElement("", null, XMLTags.MEMBER));
            eventWriter.add(eventFactory.createStartElement("", null, XMLTags.LIC));
            eventWriter.add(eventFactory.createCharacters(String.join(":",member.getLic())));
            eventWriter.add(eventFactory.createEndElement("", null, XMLTags.LIC));
            eventWriter.add(eventFactory.createStartElement("", null, XMLTags.NAME));
            eventWriter.add(eventFactory.createCharacters(member.getName()));
            eventWriter.add(eventFactory.createEndElement("", null, XMLTags.NAME));
            eventWriter.add(eventFactory.createStartElement("", null, XMLTags.CLUB));
            eventWriter.add(eventFactory.createCharacters(member.getClubLic()));
            eventWriter.add(eventFactory.createEndElement("", null, XMLTags.CLUB));
           for(TSPItem tsp: member.getTsps().values()) {
                eventWriter.add(eventFactory.createStartElement("", null, XMLTags.TSPS));
                eventWriter.add(eventFactory.createStartElement("", null, XMLTags.TSP));
                eventWriter.add(eventFactory.createCharacters((tsp.getTsp())));
                eventWriter.add(eventFactory.createEndElement("", null, XMLTags.TSP));
                eventWriter.add(eventFactory.createStartElement("", null, XMLTags.DISCIPLINE));
                eventWriter.add(eventFactory.createCharacters(tsp.getDiscipline()));
                eventWriter.add(eventFactory.createEndElement("", null, XMLTags.DISCIPLINE));
                eventWriter.add(eventFactory.createEndElement("", null, XMLTags.TSPS));
            }
            eventWriter.add(eventFactory.createEndElement("", null, XMLTags.MEMBER));
        }
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.MEMBERS));

        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.LEAGUE));
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
