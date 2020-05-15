/*
    Tsz Tsun Cheung
    1047149
    tsztsun@uoguelph.ca
 */
package tsztsun;

import dnd.die.D10;
import dnd.die.D20;
import dnd.die.D6;
import dnd.models.Exit;
import dnd.models.Trap;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author terry
 */
public class Door implements Serializable {

    /**
     * instance of Boolean trapped to keep track of trapped state.
     */
    private boolean[] trapped;
    /**
     * instance of Boolean open to keep track of open state.
     */
    private boolean open;
    /**
     * instance of Boolean lock to keep track of lock state.
     */
    private boolean lock;
    /**
     * instance of Boolean arc to keep track of arc state.
     */
    private boolean arc;
    /**
     * instance of string description.
     */
    private String description;
    /**
     * instance of arrayList space.
     */
    private ArrayList<Space> spaces;
    /**
     * instance of trap.
     */
    private Trap trap;
    /**
     * instance of exit.
     */
    private Exit exit;
    /**
     * instance of d20.
     */
    private D20 d20;
    /**
     * instance of d10.
     */
    private D10 d10;
    /**
     * instance of d6.
     */
    private D6 d6;

    /**
     * default constructor to set the defaults of the door.
     */
    public Door() {
        //needs to set defaults
        this.spaces = new ArrayList<Space>();
        spaces.add(null);
        spaces.add(null);
        init();
        exit = new Exit();
    }

    /**
     *
     * @param theExit sets the door according to the exit given
     */
    public Door(Exit theExit) {
        //sets up the door based on the Exit from the tables
        this.spaces = new ArrayList<Space>();
        spaces.add(null);
        spaces.add(null);
        init();
        exit = theExit;
    }

    /**
     *
     * @param flag if the door is trapped or not
     * @param roll optional roll value
     */
    public void setTrapped(boolean flag, int... roll) {
        // true == trapped.  Trap must be rolled if no integer is given
        //check for proper roll if proper roll is entered then make the roll the description
        checkTrapped(flag, roll);
        this.trapped[0] = flag;

    }

    /**
     *
     * @param flag sets the door open or not
     */
    public void setOpen(boolean flag) {
        //true == open
        if (flag) {
            setLock(false);
        }
        this.open = flag;
    }

    /**
     *
     * @param flag sets if the door is an archway or not
     */
    public void setArchway(boolean flag) {
        //true == is archway
        if (flag) {
            setOpen(true);
        }
        this.arc = flag;
    }

    /**
     *
     * @return returns if door is trapped
     */
    public boolean isTrapped() {
        return this.trapped[0];
    }

    /**
     *
     * @return returns if the door is open
     */
    public boolean isOpen() {
        if (arc) {
            setOpen(true);
        }
        return open;
    }

    /**
     *
     * @return returns if the door is an archway
     */
    public boolean isArchway() {
        return this.arc;
    }

    /**
     *
     * @return returns the description of the trap in the door
     */
    public String getTrapDescription() {
        return this.trap.getDescription();
    }

    /**
     *
     * @return returns the spaces connected to the door
     */
    public ArrayList<Space> getSpaces() {
        //returns the two spaces that are connected by the door
        return spaces;
    }

    /**
     * setSpaces sets the two adjacent spaces of the door.
     *
     * @param spaceOne can be chamber or passage
     * @param spaceTwo can be chamber or passage
     */
    public void setSpaces(Space spaceOne, Space spaceTwo) {
        //identifies the two spaces with the door
        spaces.set(0, spaceOne);
        spaces.set(1, spaceTwo);
        //checks what the spaces are and sets the door to the space if it isn't already connected
        setInstance(spaceOne);
        setInstance(spaceTwo);
    }

    /**
     *
     * @return returns the description of the door
     */
    public String getDescription() {
        //sets up the description of the door according to the properties
        setDescription();
        return description;
    }

    //init method creates randomly generated properties according to the rules
    private void init() {
        trapped = new boolean[1];
        //1 in 10 chance for arc
        rollArchway();
        if (!arc) {
            //1 in 6 chance for locked only if it is not an archway
            rollLock();
        }
        //1 in 20 chance it is trapped
        rollTrapped();
    }

    private void checkTrapped(boolean flag, int[] roll) {
        d20 = new D20();
        if (roll.length > 0) {
            if (roll[0] >= 1 && roll[0] <= 20 && flag) {
                this.trap = new Trap();
                this.trap.chooseTrap(roll[0]);
                setArchway(false);
            }
        } else if (flag) {
            d20 = new D20();
            this.trap = new Trap();
            this.trap.chooseTrap(d20.roll());
            setArchway(false);
        }
    }

    private void setInstance(Space space) {
        if (space instanceof Chamber) {
            if ((Chamber) space != spaces.get(1)) {
                space.setDoor(this);
            }
        } else if (space instanceof Passage) {
            if ((Passage) space != spaces.get(0)) {
                space.setDoor(this);
            }
        }
    }

    private void setDescription() {
        description = "";
        if (this.arc) {
            description += "is an arc ";
        } else {
            if (this.lock) {
                description += "is locked ";
            }
            if (this.open) {
                description += "is open ";
            } else {
                description += "is closed ";
            }
            if (trapped[0]) {
                description += "trapped by " + trap.getDescription() + " ";
            }
        }
        description += "on the " + exit.getLocation() + " " + exit.getDirection();
    }

    /**
     * sets locked state for door.
     *
     * @param flag Boolean to set state
     */
    public void setLock(boolean flag) {
        if (flag) {
            setOpen(false);
        }
        this.lock = flag;
    }

    private void rollArchway() {
        d10 = new D10();
        int roll;
        int rNum;
        roll = d10.roll();
        rNum = (int) (Math.random() * 10);
        if (roll == rNum) {
            setArchway(true);
        } else {
            setArchway(false);
        }
    }

    private void rollLock() {
        d6 = new D6();
        int roll;
        int rNum;
        roll = d6.roll();
        rNum = (int) (Math.random() * 6);
        if (roll == rNum) {
            setLock(true);
        } else {
            //1 in 2 chance it is open so not locked
            rNum = (int) (Math.random() * 2);
            if (rNum == 1) {
                setOpen(true);
            } else {
                setOpen(false);
            }
        }
    }

    private void rollTrapped() {
        d20 = new D20();
        int roll;
        int rNum;
        roll = d20.roll();
        rNum = (int) (Math.random() * 20);
        if (roll == rNum) {
            this.trap = new Trap();
            setTrapped(true);
        } else {
            setTrapped(false);
        }
    }
}
