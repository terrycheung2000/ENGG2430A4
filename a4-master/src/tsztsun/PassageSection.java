/*
    Tsz Tsun Cheung
    1047149
    tsztsun@uoguelph.ca
 */
package tsztsun;

import dnd.die.D20;
import dnd.die.Percentile;
import dnd.models.Exit;
import dnd.models.Monster;
import dnd.models.Stairs;

import java.io.Serializable;

/**
 *
 * @author Terry
 */
public class PassageSection implements Serializable {

    /**
     * instance of string description.
     */
    private String description;
    /**
     * instance of door.
     */
    private Door door;
    /**
     * instance of monster.
     */
    private Monster monster;
    /**
     * instance of stairs.
     */
    private Stairs stairs;
    private D20 d20;
    private Percentile d100;

    /**
     * sets default description for one 10ft passage section.
     */
    public PassageSection() {
        //sets up the 10 foot section with default settings
        this.description = "passage goes straight for 10 ft";
        d100 = new Percentile();
        d20 = new D20();
    }

    /**
     * sets description of passage section and creates contents accordingly.
     *
     * @param setDescription description of a passage section
     */
    public PassageSection(String setDescription) {
        //sets up a specific passage based on the values sent in from modified table 1
        this.description = setDescription;
        d100 = new Percentile();
        d20 = new D20();
        makeContents();
    }

    /**
     *
     * @return returns door
     */
    public Door getDoor() {
        //returns the door that is in the passage section, if there is one
        return this.door;
    }

    /**
     *
     * @return returns monster
     */
    public Monster getMonster() {
        //returns the monster that is in the passage section, if there is one
        return this.monster;
    }

    /**
     *
     * @return returns description of the passage section and its contents
     */
    public String getDescription() {
        setDescription();
        return description;
    }

    //private method to check which description was entered and creates contents accordingly
    private void makeContents() {
        //creates door at the end of the passage to later connect to chamber
        if (description.equals("passage ends in Door to a Chamber")) {
            Exit exit = setExit("opposite wall", "straight ahead");
            door = new Door(exit);
            //creates archway in hallway and continues the passage
        } else if (description.equals("archway (door) to right (main passage continues straight for 10 ft)")) {
            //add door to passage but find the correct exit direction: right
            Exit exit = setExit("right wall", "straight ahead");
            door = new Door(exit);
            door.setArchway(true);
        } else if (description.equals("archway (door) to left (main passage continues straight for 10ft)")) {
            //add door to passage but find the correct exit direction: left
            Exit exit = setExit("left wall", "straight ahead");
            door = new Door(exit);
            door.setArchway(true);
        } else if (description.equals("passage ends in archway to chamber")) {
            // create chamber and connect door but set to archway
            Exit exit = setExit("opposite wall", "straight ahead");
            door = new Door(exit);
        } else if (description.equals("stairs, (passage continues straight for 10ft)")) {
            //create stairs and continue path
            stairs = newStairs();
        } else if (description.equals("Wandering Monster (passage continues straight for 10 ft)")) {
            //monster is created and set
            monster = newMonster();
        }
    }

    /**
     * sets monster of the passage.
     *
     * @param newMonster monster is passed through to be set
     */
    public void setMonster(Monster newMonster) {
        this.monster = newMonster;
    }

    //setting specific locations for exit
    private Exit setExit(String location, String direction) {
        Exit exit;
        do {
            exit = new Exit();
        } while (!direction.equals(exit.getDirection()) || !location.equals(exit.getLocation()));
        return exit;
    }

    private void setDescription() {
        description += "\n";
        if (monster != null) {
            description += "\t\tContains: " + monster.getDescription() + ". There may be " + monster.getMaxNum() + "-" + monster.getMinNum() + "\n";
        }
        if (stairs != null) {
            description += "\t\tStairs: " + stairs.getDescription() + "\n";
        }
        if (door != null) {
            description += "\t\tDoor: " + door.getDescription() + "\n";
        }
    }

    private Monster newMonster() {
        Monster nMonster;
        nMonster = new Monster();
        nMonster.setType(d100.roll());
        return nMonster;
    }

    private Stairs newStairs() {
        Stairs nStairs;
        nStairs = new Stairs();
        nStairs.setType(d20.roll());
        return nStairs;
    }
}
