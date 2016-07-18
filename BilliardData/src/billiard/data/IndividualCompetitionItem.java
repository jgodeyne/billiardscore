/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.data;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 *
 * @author jean
 */
public class IndividualCompetitionItem {
    private static final Logger LOGGER = Logger.getLogger(IndividualCompetitionDataManager.class.getName());
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty contactName = new SimpleStringProperty("");
    private final StringProperty contactEmail = new SimpleStringProperty("");
    private final StringProperty discipline = new SimpleStringProperty("");
    private final StringProperty tableFormat = new SimpleStringProperty("");
    private final StringProperty league = new SimpleStringProperty("");
    HashMap<String, PlayerItem> players = new HashMap<>();

    public String getName() {
        return name.getValue();
    }

    public void setName(String name) {
        this.name.setValue(name);
    }
    
    public StringProperty getNameProp() {
        return name;
    }

    public String getContactName() {
        return contactName.getValue();
    }

    public void setContactName(String contactName) {
        this.contactName.setValue(contactName);
    }
    
    public StringProperty getContactNameProp() {
        return contactName;
    }

    public String getContactEmail() {
        return contactEmail.getValue();
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail.setValue(contactEmail);
    }
    
    public StringProperty getContactEmailProp() {
        return contactEmail;
    }
    
    public String getDiscipline() {
        return discipline.getValue();
    }

    public void setDiscipline(String discipline) {
        this.discipline.setValue(discipline);
    }
    
    public StringProperty getDisciplineProp() {
        return discipline;
    }

    public StringProperty getTableFormatProp() {
        return tableFormat;
    }

    public String getTableFormat() {
        return this.tableFormat.getValue();
    }
    public void setTableFormat(String tableFormat) {
        this.tableFormat.setValue(tableFormat);
    }

    public String getLeague() {
        return league.getValue();
    }
    
    public void setLeague(String league) {
        this.league.setValue(league);
    }
    
    public StringProperty getLeagueProp() {
        return league;
    }

    public HashMap<String, PlayerItem> getPlayers() {
        return players;
    }

    public void setPlayers(HashMap<String, PlayerItem> players) {
        this.players = players;
    }
    
    public PlayerItem getPlayer(String lic) {
        return this.players.get(lic);
    }
    
    public void putPlayer(PlayerItem player) {
        players.put(player.getLic(), player);
    }
    
    public void removePlayer(PlayerItem player) {
        players.remove(player.getLic());
    }

    public IndividualCompetitionItem copy() {
        IndividualCompetitionItem competitionClone = new IndividualCompetitionItem();
        competitionClone.setContactEmail(this.getContactEmail());
        competitionClone.setContactName(this.getContactName());
        competitionClone.setLeague(this.getLeague());
        competitionClone.setName(this.getName());
        
        HashMap<String, PlayerItem> playerCloneList = new HashMap<>();
        for (Map.Entry<String, PlayerItem> entry : players.entrySet()) {
            String key = entry.getKey();
            PlayerItem value = entry.getValue().copy();
            playerCloneList.put(key, value);
        }
        competitionClone.setPlayers(playerCloneList);
        return competitionClone;
    }
    
    public String toXML() throws Exception {
        StringWriter output = new StringWriter();
        XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
        XMLEventFactory eventFactory = XMLEventFactory.newFactory();
        XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(output);
        
        eventWriter.add(eventFactory.createStartDocument());
        eventWriter.add(eventFactory.createStartElement("",null, XMLTags.COMPETITION));
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.NAME));
        eventWriter.add(eventFactory.createCharacters(this.getName()));
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.NAME));
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.CONTACT_NAME));
        eventWriter.add(eventFactory.createCharacters(this.getContactName()));
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.CONTACT_NAME));
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.CONTACT_EMAIL));
        eventWriter.add(eventFactory.createCharacters(this.getContactEmail()));
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.CONTACT_EMAIL));
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.DISCIPLINE));
        eventWriter.add(eventFactory.createCharacters(this.getDiscipline()));
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.DISCIPLINE));
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.LEAGUE));
        eventWriter.add(eventFactory.createCharacters(this.getLeague()));
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.LEAGUE));
       
        for (PlayerItem player: this.getPlayers().values()) {
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
        return output.getBuffer().toString();
    }
    
    public static IndividualCompetitionItem fromXML(String xmlString) throws Exception {
        IndividualCompetitionItem competition = null;
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        Reader in = new StringReader(xmlString);
        XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            LOGGER.log(Level.FINEST, "readdata => nextEvent: {0}", event.toString());

            if (event.isStartElement()) {
                if (event.asStartElement().getName().getLocalPart().equals(XMLTags.COMPETITION)) {
                    LOGGER.log(Level.FINEST, "readData => competition start event: {0}", event.toString());
                    competition = readIndividualCompetition(eventReader);
                }
            }
        }
        return competition;
    }

    private static IndividualCompetitionItem readIndividualCompetition(XMLEventReader eventReader) throws XMLStreamException {
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
                    competition.putPlayer(readPlayers(eventReader));
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
    
    private static PlayerItem readPlayers(XMLEventReader eventReader) throws XMLStreamException {
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
}
