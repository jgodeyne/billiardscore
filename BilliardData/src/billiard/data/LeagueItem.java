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
public class LeagueItem {
    private static final Logger LOGGER = Logger.getLogger(LeagueItem.class.getName());
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty contactName = new SimpleStringProperty("");
    private final StringProperty contactEmail = new SimpleStringProperty("");
    private final StringProperty turnIndicatorsColor = new SimpleStringProperty("WHITE");
    private final StringProperty warmingUpTime = new SimpleStringProperty("5");
    HashMap<String, ClubItem> clubs = new HashMap<>();
    HashMap<String, MemberItem> members = new HashMap<>();


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

    public String getTurnIndicatorsColor() {
        return turnIndicatorsColor.getValue();
    }

    public void setTurnIndicatorsColor(String turnIndicatorsColor) {
        this.turnIndicatorsColor.setValue(turnIndicatorsColor);
    }
    
    public StringProperty getTurnIndicatorsColorProp() {
        return turnIndicatorsColor;
    }

    public String getWarmingUpTime() {
        return warmingUpTime.getValue();
    }

    public void setWarmingUpTime(String warmingUpTime) {
        this.warmingUpTime.setValue(warmingUpTime);
    }
    
    public StringProperty getWarmingUpTimeProp() {
        return warmingUpTime;
    }

    public HashMap<String, ClubItem> getClubs() {
        return clubs;
    }

    public void setClubs(HashMap<String, ClubItem> clubs) {
        this.clubs = clubs;
    }
    
    public ClubItem getClub(String lic) {
        return this.clubs.get(lic);
    }
    
    public void putClub(ClubItem club) {
        clubs.put(club.getLic(), club);
    }
    
    public void removeClub(ClubItem club) {
        clubs.remove(club.getLic());
    }

    public HashMap<String, MemberItem> getMembers() {
        return members;
    }

    public void setMembers(HashMap<String, MemberItem> members) {
        this.members = members;
    }
    
    public MemberItem getMember(String lic) {
        return this.members.get(lic);
    }
    
    public void putMember(MemberItem member) {
        members.put(member.getLic(), member);
    }
    
    public void removeMember(MemberItem member) {
        members.remove(member.getLic());
    }
    
    public HashMap<String, MemberItem> getMembersOfClub(String clubLic) {
        HashMap<String, MemberItem> selectedMembers = new HashMap();
        for (Map.Entry<String, MemberItem> memberEntry : members.entrySet()) {
            String lic = memberEntry.getKey();
            MemberItem member = memberEntry.getValue();
            if(member.getClubLic().equals(clubLic)) {
                selectedMembers.put(lic, member);
            }
            
        }
        return selectedMembers;
    }

    public String toXML() throws Exception {
        StringWriter output = new StringWriter();
        XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
        XMLEventFactory eventFactory = XMLEventFactory.newFactory();
        XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(output);
        
        if(null!= eventWriter) {
            eventWriter.add(eventFactory.createStartDocument());

            eventWriter.add(eventFactory.createStartElement("",null, XMLTags.LEAGUE));
            eventWriter.add(eventFactory.createStartElement("", null, XMLTags.NAME));
            eventWriter.add(eventFactory.createCharacters(this.getName()));
            eventWriter.add(eventFactory.createEndElement("", null, XMLTags.NAME));
            eventWriter.add(eventFactory.createStartElement("", null, XMLTags.CONTACT_NAME));
            eventWriter.add(eventFactory.createCharacters(this.getContactName()));
            eventWriter.add(eventFactory.createEndElement("", null, XMLTags.CONTACT_NAME));
            eventWriter.add(eventFactory.createStartElement("", null, XMLTags.CONTACT_EMAIL));
            eventWriter.add(eventFactory.createCharacters(this.getContactEmail()));
            eventWriter.add(eventFactory.createEndElement("", null, XMLTags.CONTACT_EMAIL));
            eventWriter.add(eventFactory.createStartElement("", null, XMLTags.TURNINDICATORS_COLOR));
            eventWriter.add(eventFactory.createCharacters(this.getTurnIndicatorsColor()));
            eventWriter.add(eventFactory.createEndElement("", null, XMLTags.TURNINDICATORS_COLOR));
            eventWriter.add(eventFactory.createStartElement("", null, XMLTags.WARMINGUP_TIME));
            eventWriter.add(eventFactory.createCharacters(this.getWarmingUpTime()));
            eventWriter.add(eventFactory.createEndElement("", null, XMLTags.WARMINGUP_TIME));

            eventWriter.add(eventFactory.createStartElement("", null, XMLTags.CLUBS));
            for (ClubItem club: this.getClubs().values()) {
                eventWriter.add(eventFactory.createStartElement("", null, XMLTags.CLUB));
                eventWriter.add(eventFactory.createStartElement("", null, XMLTags.LIC));
                eventWriter.add(eventFactory.createCharacters(String.join(":",club.getLic())));
                eventWriter.add(eventFactory.createEndElement("", null, XMLTags.LIC));
                eventWriter.add(eventFactory.createStartElement("", null, XMLTags.NAME));
                eventWriter.add(eventFactory.createCharacters(club.getName()));
                eventWriter.add(eventFactory.createEndElement("", null, XMLTags.NAME));
                eventWriter.add(eventFactory.createStartElement("", null, XMLTags.CONTACT_NAME));
                eventWriter.add(eventFactory.createCharacters(this.getContactName()));
                eventWriter.add(eventFactory.createEndElement("", null, XMLTags.CONTACT_NAME));
                eventWriter.add(eventFactory.createStartElement("", null, XMLTags.CONTACT_EMAIL));
                eventWriter.add(eventFactory.createCharacters(this.getContactEmail()));
                eventWriter.add(eventFactory.createEndElement("", null, XMLTags.CONTACT_EMAIL));
                eventWriter.add(eventFactory.createEndElement("", null, XMLTags.CLUB));
            }
            eventWriter.add(eventFactory.createEndElement("", null, XMLTags.CLUBS));
            eventWriter.add(eventFactory.createStartElement("", null, XMLTags.MEMBERS));
            for (MemberItem member: this.getMembers().values()) {
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
        }
        return output.getBuffer().toString();
    }
        
    public LeagueItem copy() {
        LeagueItem leagueClone = new LeagueItem();
        leagueClone.setContactEmail(this.getContactEmail());
        leagueClone.setContactName(this.getContactName());
        leagueClone.setName(this.getName());
        leagueClone.setTurnIndicatorsColor(this.getTurnIndicatorsColor());
        leagueClone.setWarmingUpTime(this.getWarmingUpTime());
        
        HashMap<String, ClubItem> clubCloneList = new HashMap<>();
        for (Map.Entry<String, ClubItem> entry : clubs.entrySet()) {
            String key = entry.getKey();
            ClubItem value = entry.getValue().copy();
            clubCloneList.put(key, value);
        }
        leagueClone.setClubs(clubCloneList);
        
        HashMap<String, MemberItem> memberCloneList = new HashMap<>();
        for (Map.Entry<String, MemberItem> entry : members.entrySet()) {
            String key = entry.getKey();
            MemberItem value = entry.getValue().copy();
            memberCloneList.put(key, value);
        }
        leagueClone.setMembers(memberCloneList);
        
        return leagueClone;
    }
    
    public static LeagueItem fromXML(String xmlString) throws Exception {
        LeagueItem league = new LeagueItem();
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        Reader in = new StringReader(xmlString);
        XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            LOGGER.log(Level.FINEST, "readdata => nextEvent: {0}", event.toString());

            if (event.isStartElement()) {
                if (event.asStartElement().getName().getLocalPart().equals(XMLTags.LEAGUE)) {
                    LOGGER.log(Level.FINEST, "readData => league start event: {0}", event.toString());
                    league = readLeague(eventReader);
                }
            }
        }
        return league;
    }

    private static LeagueItem readLeague(XMLEventReader eventReader) throws Exception {
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
                    readClubs(eventReader, league);
                }
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.MEMBERS)) {
                    LOGGER.log(Level.FINEST, "readLeague => members start event: {0}", event.toString());
                    readMembers(eventReader,league);
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
    
    private static void readClubs(XMLEventReader eventReader, LeagueItem league) throws Exception {
        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            LOGGER.log(Level.FINEST, "readLeague => nextEvent: {0}", event.toString());
            if (event.isStartElement()) {
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.CLUB)) {
                    league.putClub(readClub(eventReader));
                }
            } else if (event.isEndElement()) {
                if (event.asEndElement().getName().getLocalPart().equals(XMLTags.CLUBS)) {
                    LOGGER.log(Level.FINEST, "readLeague => clubs end event: {0}", event.toString());
                    break;
                }
            }
        }
    }
    
    private static void readMembers(XMLEventReader eventReader, LeagueItem league) throws Exception {
        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            LOGGER.log(Level.FINEST, "readLeague => nextEvent: {0}", event.toString());
            if (event.isStartElement()) {
                if (event.asStartElement().getName().getLocalPart()
                        .equals(XMLTags.MEMBER)) {
                    league.putMember(readMember(eventReader));
                }
            } else if (event.isEndElement()) {
                if (event.asEndElement().getName().getLocalPart().equals(XMLTags.MEMBERS)) {
                    LOGGER.log(Level.FINEST, "readLeague => members end event: {0}", event.toString());
                    break;
                }
            }
        }
    }
    
    private static ClubItem readClub(XMLEventReader eventReader) throws XMLStreamException {
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

    private static MemberItem readMember(XMLEventReader eventReader) throws XMLStreamException {
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
                    member.putTsp(readTSP(eventReader));
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
    
    private static TSPItem readTSP(XMLEventReader eventReader) throws XMLStreamException {
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
}
