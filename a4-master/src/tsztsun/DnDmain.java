/*
    Tsz Tsun Cheung
    1047149
    tsztsun@uoguelph.ca
 */
package tsztsun;

import dnd.die.D20;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * @author Terry
 */
public class DnDmain implements Serializable {

    private ArrayList<Chamber> allChambers;
    private ArrayList<Chamber> finalChambers;
    private HashMap<Door, Door> matches;
    private HashMap<Door, Chamber> toConnect;
    private HashMap<Door, ArrayList<Chamber>> connections;
    private HashMap<Passage, ArrayList<Door>> passageConnections;
    private ArrayList<ArrayList<Door>> doors;
    private ArrayList<Passage> passages;
    private Door[] finalDoors;
    private Chamber chamber1;
    private Chamber chamber2;
    private Chamber chamber3;
    private Chamber chamber4;
    private Chamber chamber5;

    public DnDmain() {
        toConnect = new HashMap<Door, Chamber>();
        allChambers = new ArrayList<Chamber>();
        connections = new HashMap<Door, ArrayList<Chamber>>();
        passageConnections = new HashMap<>();
        matches = new HashMap<>();
        finalChambers = new ArrayList<Chamber>();
        passages = new ArrayList<Passage>();
        doors = new ArrayList<ArrayList<Door>>();
        chamber1 = new Chamber();
        chamber2 = new Chamber();
        chamber3 = new Chamber();
        chamber4 = new Chamber();
        chamber5 = new Chamber();
    }

    /**
     * @param maxChambers number of chambers
     */
    public void init(int maxChambers) {
        finalDoors = new Door[maxChambers];
        allChambers.add(chamber1);
        allChambers.add(chamber2);
        allChambers.add(chamber3);
        allChambers.add(chamber4);
        allChambers.add(chamber5);
        Collections.sort(allChambers, new SortChamber());
        finalChambers.addAll(allChambers);
        mapDoors(doors);
        setConnect();

        match();
        setRemainingDoors();
        setPassages();
    }

    private void match() {
        int rand;
        //loop through until there is one remainder door set
        do {
            //loop through until there are no doors or if there is one remainder door set
            while (doors.get(0).size() > 0 && doors.size() > 1) {
                //choose random door set that does not point to self
                rand = (int) (Math.random() * (doors.size() - 1)) + 1;
                //connect current door to the rand chamber and vice versa
                ArrayList<Chamber> connects = new ArrayList<Chamber>();
                connects.add(toConnect.get(doors.get(0).get(0)));
                connects.add(toConnect.get(doors.get(rand).get(0)));
                matches.put(doors.get(0).get(0), doors.get(rand).get(0));
                connections.put(doors.get(0).get(0), connects);
                connections.put(doors.get(rand).get(0), connects);
                if (connections.get(doors.get(0).get(0)).size() > 1) {
                    Door temp1 = doors.get(0).get(0);
                    toConnect.remove(temp1);
                    doors.get(0).remove(temp1);
                }
                if (connections.get(doors.get(rand).get(0)).size() > 1) {
                    Door temp1 = doors.get(rand).get(0);
                    toConnect.remove(temp1);
                    doors.get(rand).remove(temp1);
                }
                //if the rand set is empty after removal of last door then remove the set and chamber to avoid doubling up
                if (doors.get(rand).isEmpty()) {
                    doors.remove(rand);
                    allChambers.remove(rand);
                }
            }
            //remove the current door set and corresponding chamber
            if (doors.get(0).size() == 0) {
                doors.remove(0);
            }
            //sort the remaining doors sets in increasing order
            Collections.sort(doors, new SortDoor());
        } while (doors.size() > 1);
        finalDoors = connections.keySet().toArray(new Door[0]);
    }

    private void setRemainingDoors() {
        if (doors.size() > 0) {
            for (int i = 0; i < doors.get(0).size(); i++) {
                ArrayList<Chamber> connects = new ArrayList<>();
                connects.add(toConnect.get(doors.get(0).get(i)));
                connections.put(doors.get(0).get(i), connects);
            }
        }
        finalDoors = connections.keySet().toArray(new Door[0]);
    }

    private void setPassages() {
        ArrayList<Door> passageDoors;
        Passage temp;
        PassageSection addSection;
        for (int i = 0; i < finalDoors.length; i++) {
            if (matches.containsKey(finalDoors[i])) {
                passageDoors = new ArrayList<>();
                passageDoors.add(finalDoors[i]);
                passageDoors.add(matches.get(finalDoors[i]));
                temp = randPassages();
                passageConnections.put(temp, passageDoors);
                passages.add(temp);
            }
        }
        for (int i = 0; i < toConnect.size(); i++) {
            temp = passages.get((int) Math.random() * (passages.size() - 1));
            addSection = new PassageSection("archway (door) to right (main passage continues straight for 10 ft)");
            temp.addPassageSection(addSection);
            passageDoors = passageConnections.get(temp);
            passageDoors.add(doors.get(0).get(i));
        }
    }

    /**
     * @return passage connections
     */
    public HashMap<Passage, ArrayList<Door>> getPassageConnections() {
        return passageConnections;
    }

    private Passage randPassages() {
        D20 d20 = new D20();
        Passage passage = new Passage();
        PassageSection section;
        boolean end = false;
        int roll, pCount = 0;
        while (end == false) {
            roll = d20.roll();
            if (pCount >= 10) {
                roll = 5;
            }
            switch (roll) {
                case 1:
                case 2:
                    //continues random generated passage
                    section = new PassageSection("passage goes straight for 10 ft");
                    passage.addPassageSection(section);
                    pCount++;
                    break;
                case 3:
                case 4:
                case 5:
                    //passage ends in door to chamber
                    section = new PassageSection("passage ends in Door to a Chamber");
                    passage.addPassageSection(section);
                    end = true;
                    break;
                case 6:
                case 7:
                    //sets archway right and continues passage
                    section = new PassageSection("archway (door) to right (main passage continues straight for 10 ft)");
                    passage.addPassageSection(section);
                    pCount++;
                    break;
                case 8:
                case 9:
                    //sets archway to left and continues passage
                    section = new PassageSection("archway (door) to left (main passage continues straight for 10 ft)");
                    passage.addPassageSection(section);
                    pCount++;
                    break;
                case 10:
                case 11:
                    //continues path with left turn description
                    section = new PassageSection("passage turns to left and continues for 10 ft");
                    passage.addPassageSection(section);
                    break;
                case 12:
                case 13:
                    //coninues path with right turn description
                    section = new PassageSection("passage turns to right and continues for 10 ft");
                    passage.addPassageSection(section);
                    break;
                case 17:
                    //create stairs and continues path
                    section = new PassageSection("Stairs, (passage continues straight for 10 ft)");
                    passage.addPassageSection(section);
                    break;
                case 20:
                    //wandering monster create passage and monster
                    section = new PassageSection("Wandering Monster (passage continues straight for 10 ft)");
                    passage.addPassageSection(section);
                    break;
                default:
                    break;
            }
        }
        return passage;
    }

    private void mapDoors(ArrayList<ArrayList<Door>> doors) {
        for (int i = 0; i < allChambers.size(); i++) {
            doors.add(allChambers.get(i).getDoors());
        }
    }

    private void setConnect() {
        for (int i = 0; i < allChambers.size(); i++) {
            for (int j = 0; j < allChambers.get(i).getDoors().size(); j++) {
                toConnect.put(allChambers.get(i).getDoors().get(j), allChambers.get(i));
            }
        }
    }

    /**
     * @param theChamber the chamber to check
     * @return the name of the chamber
     */
    public String getChamberName(Chamber theChamber) {
        if (theChamber == chamber1) {
            return "Chamber 1";
        } else if (theChamber == chamber2) {
            return "Chamber 2";
        } else if (theChamber == chamber3) {
            return "Chamber 3";
        } else if (theChamber == chamber4) {
            return "Chamber 4";
        } else if (theChamber == chamber5) {
            return "Chamber 5";
        } else {
            return "CHAMBER DOESN'T EXIST";
        }
    }

    /**
     * @param chamberName chamber to check
     * @return the chamber object
     */
    public Chamber getChamber(String chamberName) {
        if (chamberName.equals("Chamber 1")) {
            return chamber1;
        } else if (chamberName.equals("Chamber 2")) {
            return chamber2;
        } else if (chamberName.equals("Chamber 3")) {
            return chamber3;
        } else if (chamberName.equals("Chamber 4")) {
            return chamber4;
        } else if (chamberName.equals("Chamber 5")) {
            return chamber5;
        } else {
            return null;
        }
    }

    /**
     * @param chamber the chamber to check
     * @return doors mapped to the chamber
     */
    public ArrayList<String> getChamberDoors(Chamber chamber) {
        ArrayList<String> chamberDoors = new ArrayList<>();
        for (int i = 0; i < connections.size(); i++) {
            if (connections.get(finalDoors[i]).get(0) == chamber) {
                chamberDoors.add("Door " + (i + 1));
            }
        }
        return chamberDoors;
    }

    /**
     * @param index the door index
     * @return the door
     */
    public Door getDoor(int index) {
        return finalDoors[index];
    }

    /**
     * @return the chambers
     */
    public ArrayList<Chamber> getChambers() {
        return finalChambers;
    }

    private void unLock(Door newDoor) {
        newDoor.setLock(false);
    }

    /**
     * @return the passages
     */
    public ArrayList<Passage> getPassages() {
        return passages;
    }

    /**
     * @param thePassage the passage to check
     * @return the doors connected
     */
    public ArrayList<String> getPassageDoors(Passage thePassage) {
        ArrayList<String> temp = new ArrayList<>();
        ArrayList<Door> passageDoors = new ArrayList<>();
        passageDoors = passageConnections.get(thePassage);
        for (int i = 0; i < passageDoors.size(); i++) {
            for (int j = 0; j < finalDoors.length; j++) {
                if (passageDoors.get(i) == finalDoors[j]) {
                    temp.add("Door " + (j + 1));
                }
            }
        }
        return temp;
    }
}

class SortChamber implements Comparator<Chamber> {

    public int compare(Chamber a, Chamber b) {
        return a.getDoors().size() - b.getDoors().size();
    }
}

class SortDoor implements Comparator<ArrayList<Door>> {

    public int compare(ArrayList<Door> a, ArrayList<Door> b) {
        return a.size() - b.size();
    }
}
