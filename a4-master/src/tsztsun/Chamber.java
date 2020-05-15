/*
    Tsz Tsun Cheung
    1047149
    tsztsun@uoguelph.ca
 */
package tsztsun;

import dnd.die.D20;
import dnd.die.Percentile;
import dnd.models.ChamberContents;
import dnd.models.ChamberShape;
import dnd.models.Monster;
import dnd.models.Stairs;
import dnd.models.Trap;
import dnd.models.Treasure;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author terry
 */
public class Chamber extends Space implements Serializable {

    /**
     * instance of chamberContents.
     */
    private ChamberContents myContents;
    /**
     * instance of ChamberShape.
     */
    private ChamberShape mySize;
    /**
     * instance of arrayList doors.
     */
    private ArrayList<Door> doors;
    /**
     * instance of arrayList monsters.
     */
    private ArrayList<Monster> monsters;
    /**
     * instance of arrayList treasures.
     */
    private ArrayList<Treasure> treasures;
    /**
     * instance of arrayList spaces.
     */
    private ArrayList<Space> spaces;
    /**
     * instance of monster.
     */
    private Monster monster;
    /**
     * instance of treasure.
     */
    private Treasure treasure;
    /**
     * instance of stairs.
     */
    private Stairs stairs;
    /**
     * instance of trap.
     */
    private Trap trap;
    /**
     * instance of percentile.
     */
    private Percentile d100;
    /**
     * instance of D20.
     */
    private D20 d20;
    private String description;

    /**
     * constructor sets the defaults of a chamber.
     */
    public Chamber() {
        this.treasures = new ArrayList<Treasure>();
        this.monsters = new ArrayList<Monster>();
        this.doors = new ArrayList<Door>();
        myContents = new ChamberContents();
        d20 = new D20();
        makeShape(mySize);
        d100 = new Percentile();
        stairs = null;
        trap = null;
        //choosing contents randomly
        myContents.chooseContents(d20.roll());
        //checking descriptions and creating contents accordingly
        makeContents();
        //calls setExits to set all exits of a chamber
        setExits();

    }

    /**
     * constructor to set defaults according to 'theShape' and 'theContents'.
     *
     * @param theShape    shape of the chamber
     * @param theContents contents of the chamber
     */
    public Chamber(ChamberShape theShape, ChamberContents theContents) {
        this.treasures = new ArrayList<Treasure>();
        this.monsters = new ArrayList<Monster>();
        this.doors = new ArrayList<Door>();
        d20 = new D20();
        d100 = new Percentile();
        myContents = theContents;
        makeShape(theShape);
        stairs = null;
        trap = null;
        //checking description and creating contents accordingly
        makeContents();
        //calling setExits to set all the exits of the chamber
        setExits();
    }

    /**
     * sets the shape of the chamber and changes the exits.
     *
     * @param theShape shape of the chamber
     */
    public void setShape(ChamberShape theShape) {
        mySize = theShape;
        setExits();
    }

    /**
     * @return returns the doors (Exits) of the chamber
     */
    public ArrayList<Door> getDoors() {
        return doors;
    }

    /**
     * @param theMonster adds a monster to the chamber
     */
    public void addMonster(Monster theMonster) {
        monsters.add(theMonster);
    }

    /**
     * @return returns all monsters in the chamber
     */
    public ArrayList<Monster> getMonsters() {
        return monsters;
    }

    /**
     * @param theTreasure adds a treasure to the chamber
     */
    public void addTreasure(Treasure theTreasure) {
        treasures.add(theTreasure);
    }

    /**
     * @return returns all the treasures in the chamber
     */
    public ArrayList<Treasure> getTreasureList() {
        return treasures;
    }

    /**
     * @return returns the description of the chamber
     */
    @Override
    public String getDescription() {
        if (description == null) {
            setDescription();
        }
        return description;
    }

    /**
     * @param newDescription description to set
     * @return description
     */
    public String setDescription(String... newDescription) {
        if (newDescription.length < 1) {
            //sets up the string description according to the contents
            description = myContents.getDescription() + "\n";
            //looping through all the monsters
            getMonsterDescription(setNumM());
            //looping through all the treasures checking if they are protected
            getTreasureDescription(setNumT());
            //checking if stairs or a trap exists and adding them to description
            getStairsDescription();
            getTrapDescription();
        } else {
            description = newDescription[0];
        }
        return description;
    }

    private int setNumD() {
        try {
            return doors.size();
        } catch (NullPointerException e) {
            return 0;
        }
    }

    private int setNumM() {
        try {
            return monsters.size();
        } catch (NullPointerException e) {
            return 0;
        }
    }

    private int setNumT() {
        try {
            return treasures.size();
        } catch (NullPointerException e) {
            return 0;
        }
    }

    private void getExitDescription(int numD) {
        for (int i = 0; i < numD; i++) {
            description += ("\t- Exit " + (i + 1) + " " + doors.get(i).getDescription() + "\n");
        }
    }

    private void getMonsterDescription(int numM) {
        for (int i = 0; i < numM; i++) {
            description += ("\t- Monster " + (i + 1) + " is " + monsters.get(i).getDescription() + ". There may be " + monsters.get(i).getMaxNum() + "-" + monsters.get(i).getMinNum() + "\n");
        }
    }

    private void getTreasureDescription(int numT) {
        for (int i = 0; i < numT; i++) {
            description += "\t- Treasure " + (i + 1) + " : " + treasures.get(i).getWholeDescription();
        }
    }

    private void getStairsDescription() {
        if (stairs != null) {
            description += ("\t- Stairs: " + stairs.getDescription() + "\n");
        }
    }

    private void getTrapDescription() {
        if (trap != null) {
            description += ("\t- Trap: " + trap.getDescription() + "\n");
        }
    }

    /**
     * @param newDoor new door set connection to the chamber
     */
    @Override
    public void setDoor(Door newDoor) {
        //gets the current spaces in the door and checks for current spaces
        spaces = newDoor.getSpaces();
        if (spaces.get(0) != null && spaces.get(1) == null) {
            newDoor.setSpaces(spaces.get(0), this);
        } else if (spaces.get(0) == null && spaces.get(1) != null) {
            newDoor.setSpaces(this, spaces.get(1));
        } else if (spaces.get(0) == null && spaces.get(1) == null) {
            newDoor.setSpaces(this, null);
        }
    }

    private Treasure newTreasure() {
        Treasure nTreasure;
        nTreasure = new Treasure();
        nTreasure.chooseTreasure(d20.roll());
        nTreasure.setContainer(d20.roll());
        return nTreasure;
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

    private Trap newTrap() {
        Trap nTrap;
        nTrap = new Trap();
        nTrap.chooseTrap(d20.roll());
        return nTrap;
    }

    private void makeContents() {
        if ("monster only".equals(myContents.getDescription())) {
            addMonster(newMonster());
        } else if ("monster and treasure".equals(myContents.getDescription())) {
            addMonster(newMonster());
            addTreasure(newTreasure());
        } else if ("stairs".equals(myContents.getDescription())) {
            stairs = newStairs();
        } else if ("trap".equals(myContents.getDescription())) {
            trap = newTrap();
        } else if ("treasure".equals(myContents.getDescription())) {
            addTreasure(newTreasure());
        }
    }

    /**
     * @return returns the type
     */
    @Override
    public String getType() {
        return "chamber";
    }

    //sets the exits of the chamber
    private void setExits() {
        doors.clear();
        for (int i = 0; i < mySize.getNumExits(); i++) {
            Door door = new Door();
            doors.add(door);
        }
    }

    private void makeShape(ChamberShape theShape) {
        mySize = theShape.selectChamberShape(d20.roll());
    }
}
