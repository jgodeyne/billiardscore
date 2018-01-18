/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.data;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.Property;
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
public class TeamCompetitionItem {
    private static final Logger LOGGER = Logger.getLogger(TeamCompetitionItem.class.getName());
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty contactName = new SimpleStringProperty("");
    private final StringProperty contactEmail = new SimpleStringProperty("");
    private final StringProperty discipline = new SimpleStringProperty("");
    private final StringProperty pointsSystem = new SimpleStringProperty("");
    private final StringProperty tableFormat = new SimpleStringProperty("");
    private final StringProperty group = new SimpleStringProperty("");
    private final StringProperty nbrOfPlayers = new SimpleStringProperty("");
    private final StringProperty tspPlayers = new SimpleStringProperty("");
    private final StringProperty disciplinePlayers = new SimpleStringProperty("");
    private final StringProperty league = new SimpleStringProperty("");
    private HashMap<String, TeamItem> teams = new HashMap<>();

    public HashMap<String, TeamItem> getTeams() {
        return teams;
    }

    public void setTeams(HashMap<String, TeamItem> teams) {
        this.teams = teams;
    }

    public String getName() {
        return name.getValue();
    }

    public void setName(String name) {
        this.name.setValue(name);
    }    

    public Property<String> getNameProp() {
        return this.name;
    }
    
    public String getPointsSystem() {
        return pointsSystem.getValue();
    }

    public void setPointsSystem(String pointsSystem) {
        this.pointsSystem.setValue(pointsSystem);
    }
    
    public StringProperty getPointSystemProp() {
        return pointsSystem;
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

    public String getTableFormat() {
        return tableFormat.getValue();
    }

    public void setTableFormat(String tableFormat) {
        this.tableFormat.setValue(tableFormat);
    }
    
    public StringProperty getTableFormatProp() {
        return tableFormat;
    }

    public String getGroup() {
        return group.getValue();
    }

    public void setGroup(String group) {
        this.group.setValue(group);
    }
    
    public StringProperty getGroupProp() {
        return group;
    }

    public String getNbrOfPlayers() {
        return nbrOfPlayers.getValue();
    }

    public void setNbrOfPlayers(String nbrOfPlayers) {
        this.nbrOfPlayers.setValue(nbrOfPlayers);
    }
    
    public StringProperty getNbrOfPlayersProp() {
        return nbrOfPlayers;
    }

    public String getTspPlayers() {
        return tspPlayers.getValue();
    }

    public void setTspPlayers(String tsps) {
        this.tspPlayers.setValue(tsps);
    }
    
    public StringProperty getTspPlayersProp() {
        return tspPlayers;
    }

    public String getDisciplinePlayers() {
        return disciplinePlayers.getValue();
    }

    public void setDisciplinePlayers(String disciplinePlayers) {
        this.disciplinePlayers.setValue(disciplinePlayers);
    }
    
    public StringProperty getDisciplinePlayersProp() {
        return disciplinePlayers;
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

    public StringProperty getContactNameProp() {
        return contactName;
    }

    public String getContactName() {
        return contactName.getValue();
    }

    public void setContactName(String contactName) {
        this.contactName.setValue(contactName);
    }

    public StringProperty getContactEmailProp() {
        return contactEmail;
    }

    public String getContactEmail() {
        return contactEmail.getValue();
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail.setValue(contactEmail);
    }

    public ArrayList getTeamNames() {
        ArrayList<String> names = new ArrayList<>();
        List<TeamItem> list = new ArrayList<>(teams.values());
        Collections.sort(list, Comparator.comparing(TeamItem::getName));
        for (TeamItem teamItem : list) {
            names.add(teamItem.getName());
        }
        return names;
    }

    public TeamItem getTeam(String name) {
        return teams.get(name);
    }
    
    public void putTeam(TeamItem team) {
        teams.put(team.getName(), team);
    }
    
    public void removeTeam(TeamItem team)  {
        teams.remove(team.getName());
    }

    public TeamCompetitionItem copy() throws CloneNotSupportedException {
        TeamCompetitionItem teamCompetitionClone = new TeamCompetitionItem();
        teamCompetitionClone.setDiscipline(this.getDiscipline());
        teamCompetitionClone.setDisciplinePlayers(this.getDisciplinePlayers());
        teamCompetitionClone.setGroup(this.getGroup());
        teamCompetitionClone.setLeague(this.getLeague());
        teamCompetitionClone.setName(this.getName());
        teamCompetitionClone.setNbrOfPlayers(this.getNbrOfPlayers());
        teamCompetitionClone.setPointsSystem(this.getPointsSystem());
        teamCompetitionClone.setTableFormat(this.getTableFormat());
        teamCompetitionClone.setTspPlayers(this.getTspPlayers());
 
        HashMap<String, TeamItem> teamCloneList = new HashMap<>();
        for (Map.Entry<String, TeamItem> entry : teams.entrySet()) {
            String key = entry.getKey();
            TeamItem value = entry.getValue().copy();
            teamCloneList.put(key, value);
        }
        teamCompetitionClone.setTeams(teamCloneList);
        
        return teamCompetitionClone;
    }
    
    public String toXML() throws Exception {
        StringWriter output = new StringWriter();
        XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
        XMLEventFactory eventFactory = XMLEventFactory.newFactory();
        XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(output);
 
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
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.LEAGUE));
        eventWriter.add(eventFactory.createCharacters(this.getLeague()));
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.LEAGUE));
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.GROUP));
        eventWriter.add(eventFactory.createCharacters(this.getGroup()));
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.GROUP));
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.POINTSYSTEM));
        eventWriter.add(eventFactory.createCharacters(this.getPointsSystem()));
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.POINTSYSTEM));
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.TABLEFORMAT));
        eventWriter.add(eventFactory.createCharacters(this.getTableFormat()));
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.TABLEFORMAT));
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.DISCIPLINE));
        eventWriter.add(eventFactory.createCharacters(this.getDiscipline()));
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.DISCIPLINE));
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.NBR_OF_PLAYERS));
        eventWriter.add(eventFactory.createCharacters(this.getNbrOfPlayers()));
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.NBR_OF_PLAYERS));
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.DISCIPLINE_PLAYERS));
        eventWriter.add(eventFactory.createCharacters(this.getDisciplinePlayers()));
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.DISCIPLINE_PLAYERS));
        eventWriter.add(eventFactory.createStartElement("", null, XMLTags.TSP_PLAYERS));
        eventWriter.add(eventFactory.createCharacters(this.getTspPlayers()));
        eventWriter.add(eventFactory.createEndElement("", null, XMLTags.TSP_PLAYERS));
        
        for (TeamItem team: this.getTeams().values()) {
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
        return output.getBuffer().toString();
    }
    
    public static TeamCompetitionItem fromXML(String xmlString) throws Exception {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        Reader in = new StringReader(xmlString);
        XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
        
        TeamCompetitionItem competition= null;
        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            LOGGER.log(Level.FINEST, "readdata => nextEvent: {0}", event.toString());

            if (event.isStartElement()) {
                if (event.asStartElement().getName().getLocalPart().equals(XMLTags.COMPETITION)) {
                    LOGGER.log(Level.FINEST, "readData => competition start event: {0}", event.toString());
                    competition = readCompetition(eventReader);
                }
            }
        }
        return competition;
    }

    private static TeamCompetitionItem readCompetition(XMLEventReader eventReader) throws XMLStreamException {
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
                    competition.putTeam(readTeam(eventReader));
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

    private static TeamItem readTeam(XMLEventReader eventReader) throws XMLStreamException {
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
                    team.putPlayer(readPlayer(eventReader));
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
    
    private static PlayerItem readPlayer(XMLEventReader eventReader) throws XMLStreamException {
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
}
