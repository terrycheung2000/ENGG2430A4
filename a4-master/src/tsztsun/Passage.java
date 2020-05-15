/*
    Tsz Tsun Cheung
    1047149
    tsztsun@uoguelph.ca
 */
package tsztsun;

import dnd.models.Monster;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author terry
 */
public class Passage extends Space implements Serializable {

    /**
     * instance of arrayList of passage section.
     */
    private ArrayList<PassageSection> thePassage;
    /**
     * instance of HashMap doorMap.
     */
    private HashMap<Door, PassageSection> doorMap;
    /**
     * instance of arrayList doors.
     */
    private ArrayList<Door> doors;
    /**
     * instance of arrayList spaces.
     */
    private ArrayList<Space> spaces;
    /**
     * instance of string description.
     */
    private String description;

    /**
     * default constructor of passage creates the instance variables.
     */
    public Passage() {
        thePassage = new ArrayList<PassageSection>();
        doorMap = new HashMap<Door, PassageSection>();
        doors = new ArrayList<Door>();
    }

    /**
     * @return returns all the doors in the passage
     */
    public ArrayList<Door> getDoors() {
        //gets all of the doors in the entire passage
        return doors;
    }

    /**
     * @param i section to be looked at
     * @return returns the door or null
     */
    public Door getDoor(int i) {
        //returns the door in section 'i'. If there is no door, returns null
        for (int j = 0; j < doorMap.size(); j++) {
            if (doorMap.get(doors.get(j)) == thePassage.get(i)) {
                return doors.get(j);
            }
        }
        return null;
    }

    /**
     * @param theMonster sets the monster in the specified passage section
     * @param i          passage section chosen
     */
    public void addMonster(Monster theMonster, int i) {
        // adds a monster to section 'i' of the passage
        thePassage.get(i).setMonster(theMonster);
    }

    /**
     * @param i passage section chosen
     * @return returns the monster in the passage
     */
    public Monster getMonster(int i) {
        //returns Monster door in section 'i'. If there is no Monster, returns
        return thePassage.get(i).getMonster();
    }

    /**
     * @param toAdd the passage section to add to the passage
     */
    public void addPassageSection(PassageSection toAdd) {
        //adds the passage section to the passageway
        thePassage.add(toAdd);
    }

    public ArrayList<PassageSection> getPassageSections() {
        return thePassage;
    }

    /**
     * @param newDoor door to set a connection to
     */
    @Override
    public void setDoor(Door newDoor) {
        //checks the spaces already inside the door and sets the passage to the door
        spaces = newDoor.getSpaces();
        setSpaces(newDoor);
        doors.add(newDoor);
        connectDoors(newDoor);
    }

    /**
     * @return the description of the entire passage
     */
    @Override
    public String getDescription() {
        //compiles all passage section descriptions into description
        if (description == null) {
            setDescription();
        }
        return description;
    }

    /**
     * @return returns the type passage
     */
    @Override
    public String getType() {
        return "passage";
    }

    public void setDescription(String... newDescription) {
        if (newDescription.length < 1) {
            description = "";
            for (int i = 0; i < thePassage.size(); i++) {
                description += "\t-Section " + (i + 1) + " " + thePassage.get(i).getDescription();
            }
        } else {
            description = newDescription[0];
        }
    }

    private void setSpaces(Door newDoor) {
        if (spaces.get(0) == null && spaces.get(1) != null) {
            newDoor.setSpaces(this, spaces.get(1));
        } else if (spaces.get(0) != null && spaces.get(1) == null) {
            newDoor.setSpaces(spaces.get(0), this);
        } else if (spaces.get(0) == null && spaces.get(1) == null) {
            newDoor.setSpaces(this, null);
        }
    }

    private void connectDoors(Door newDoor) {
        if (thePassage.size() > 1) {
            doorMap.put(newDoor, thePassage.get(thePassage.size() - 1));
        } else if (thePassage.size() == 0) {
            PassageSection section = new PassageSection();
            thePassage.add(section);
            doorMap.put(newDoor, thePassage.get(thePassage.size() - 1));
        }
    }
}
