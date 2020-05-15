/*
    Tsz Tsun Cheung
    1047149
    tsztsun@uoguelph.ca
*/

package tsztsun;

/**
 *
 * @author terry
 */
public abstract class Space {

    /**
     *
     * @return returns description of the space
     */
    public abstract String getDescription();

    /**
     *
     * @param theDoor door to set
     */
    public abstract void setDoor(Door theDoor);

    /**
     *
     * @return string of space type
     */
    public abstract String getType();

}
