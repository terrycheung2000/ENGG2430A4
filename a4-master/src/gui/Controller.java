package gui;

import database.DBConnection;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tsztsun.Chamber;
import tsztsun.DnDmain;
import tsztsun.Door;
import tsztsun.Passage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 */
public class Controller {
    private Main myGui;
    private DnDmain level;
    private static int MAX_CHAMBERS = 5;

    /**
     * @param gui the main gui
     */
    public Controller(Main gui) {
        myGui = gui;
        level = new DnDmain();
        level.init(MAX_CHAMBERS);
    }

    /**
     * @return name list
     */
    public ArrayList<String> getNameList() {
        int i = 0;
        ArrayList<String> nameList = new ArrayList<>();
        ArrayList<Chamber> allChambers = new ArrayList<>();
        ArrayList<Passage> allPassages = new ArrayList<>();
        allChambers = level.getChambers();
        allPassages = level.getPassages();
        for (Chamber c : allChambers) {
            nameList.add(level.getChamberName(c));
            i++;
        }
        i = 0;
        for (Passage p : allPassages) {
            nameList.add("Passage " + (i + 1));
            i++;
        }
        return nameList;
    }

    /**
     * @param fileChooser file to open
     * @param primaryStage stage for file chooser
     */
    public void openFile(FileChooser fileChooser, Stage primaryStage) {
        System.out.println("I would load a file here");
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            try {
                FileInputStream fileStream = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileStream);
                level = (DnDmain) in.readObject();
                in.close();
                fileStream.close();
            } catch (IOException e) {
                System.out.println("INPUT STREAM ERROR");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println("OBJECT STREAM ERROR");
                e.printStackTrace();
            }
        }
        try {
            myGui.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param fileChooser file to save
     * @param primaryStage stage for file chooser
     */
    public void saveFile(FileChooser fileChooser, Stage primaryStage) {
        System.out.println("I would save to a file here");
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try {
                FileOutputStream fileStream = new FileOutputStream(file);
                ObjectOutputStream out = new ObjectOutputStream(fileStream);
                out.writeObject(level);
                out.close();
                fileStream.close();
            } catch (FileNotFoundException e) {
                System.out.println("INPUT STREAM ERROR");
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("OBJECT STREAM ERROR");
                e.printStackTrace();
            }
        }
    }

    /**
     * @param list list to keep track of
     */
    public void listClick(ListView list) {
        if (list.getSelectionModel().getSelectedItem() != null) {
            String temp = list.getSelectionModel().getSelectedItem().toString();
            System.out.println("clicked on " + temp);
            if (temp.contains("Chamber")) {
                myGui.setTextArea(level.getChamber(temp).getDescription());
                myGui.setComboBox(level.getChamberDoors(level.getChamber(temp)));
            } else if (temp.contains("Passage")) {
                temp = temp.replaceAll("\\D+", "");
                int index = Integer.parseInt(temp);
                myGui.setTextArea(level.getPassages().get(index - 1).getDescription());
                myGui.setComboBox(level.getPassageDoors(level.getPassages().get(index - 1)));
            }
        }
    }

    /**
     * @param box box to keep track of
     * @param list list to keep track of
     */
    public void doorClick(ComboBox box, ListView list) {
        String temp;
        int index;
        System.out.println("chose " + box.getSelectionModel().getSelectedItem());
        if (box.getSelectionModel().getSelectedItem() != null) {
            temp = box.getSelectionModel().getSelectedItem().toString();
            temp = temp.replaceAll("\\D+", "");
            if (temp.isBlank() != true) {
                index = Integer.parseInt(temp);
                if (list.getSelectionModel().getSelectedItem().toString().contains("Chamber")) {
                    myGui.setTextArea("Door " + index + " " + level.getDoor(index - 1).getDescription() + "\nConnects to " + findPassage(level.getDoor(index - 1)));
                } else if (list.getSelectionModel().getSelectedItem().toString().contains("Passage")) {
                    myGui.setTextArea("Door " + index + " " + level.getDoor(index - 1).getDescription() + "\nConnects to " + findChamber(index));
                }
            }
        }
    }

    private String findPassage(Door theDoor) {
        for (int i = 0; i < level.getPassageConnections().size(); i++) {
            for (int j = 0; j < level.getPassageConnections().get(level.getPassages().get(i)).size(); j++) {
                if (level.getPassageConnections().get(level.getPassages().get(i)).get(j) == theDoor) {
                    return "Passage " + (i + 1);
                }
            }
        }
        return "PASSAGE NOT FOUND";
    }

    private String findChamber(int index) {
        for (int i = 0; i < level.getChambers().size(); i++) {
            for (int j = 0; j < level.getChamberDoors(level.getChambers().get(i)).size(); j++) {
                if (Integer.parseInt(level.getChamberDoors(level.getChambers().get(i)).get(j).replaceAll("\\D+", "")) == index) {
                    return level.getChamberName(level.getChambers().get(i));
                }
            }
        }
        return "CHAMBER NOT FOUND";
    }

    /**
     * @param connection connection to sql
     * @param monster monster to add
     * @param space space to set description
     */
    public void addMonsterDescription(DBConnection connection, String monster, String space) {
        String monsterDescription, monsterName, upper, lower, description;
        monsterDescription = connection.findMonster(monster).getDescription();
        monsterName = connection.findMonster(monster).getName();
        upper = connection.findMonster(monster).getUpper();
        lower = connection.findMonster(monster).getLower();
        if (space.contains("Chamber")) {
            description = level.getChamber(space).getDescription();
            level.getChamber(space).setDescription(description + "\nMonster is " + monsterName + " there may be " + upper + "-" + lower + " " + monsterDescription);
            myGui.setTextArea(level.getChamber(space).getDescription());
        } else if (space.contains("Passage")) {
            String temp = space;
            int index = Integer.parseInt(temp.replaceAll("\\D+", ""));
            description = level.getPassages().get(index - 1).getDescription();
            level.getPassages().get(index - 1).setDescription(description + "\nMonster is " + monsterName + " there may be " + upper + "-" + lower + " " + monsterDescription);
            myGui.setTextArea(level.getPassages().get(index - 1).getDescription());
        }
    }

    /**
     * @param space space to get monster
     * @return list of monster names
     */
    public ArrayList<String> getMonsterName(String space) {
        String description;
        String[] temp;
        ArrayList<String> names = new ArrayList<>();
        if (space.contains("Chamber")) {
            for (int i = 0; i < level.getChamber(space).getMonsters().size(); i++) {
                description = level.getChamber(space).getMonsters().get(i).getDescription();
                temp = description.split(" ");
                names.add(temp[0]);
            }
        } else if (space.contains("Passage")) {
            for (int i = 0; i < level.getPassages().size(); i++) {
                for (int j = 0; j < level.getPassages().get(i).getPassageSections().size(); j++) {
                    if (level.getPassages().get(i).getPassageSections().get(j).getMonster() != null) {
                        names.add(level.getPassages().get(i).getPassageSections().get(j).getMonster().getDescription().split(" ")[0]);
                    }
                }
            }
        }
        return names;
    }

    /**
     * @param connection connection to sql
     * @param name name of monster
     * @param upper upper bound
     * @param lower lower bound
     * @param description description
     */
    public void addNewMonster(DBConnection connection, String name, String upper, String lower, String description) {
        connection.addMonster(name, upper, lower, description);
    }

    /**
     * @param monster monster to remover
     * @param space space to remove from
     */
    public void removeMonster(String monster, String space) {
        String description;
        ArrayList<String> temp;
        if (space.contains("Chamber")) {
            description = level.getChamber(space).getDescription();
            temp = new ArrayList<>(Arrays.asList(description.split("\n")));
            for (int i = 0; i < temp.size(); i++) {
                if (temp.get(i).contains(monster)) {
                    temp.remove(i);
                }
            }
            description = temp.toString().replaceAll("[|]", "");
            level.getChamber(space).setDescription(description);
            myGui.setTextArea(level.getChamber(space).getDescription());
        } else if (space.contains("Passage")) {
            int index = Integer.parseInt(space.replaceAll("\\D+", ""));
            description = level.getPassages().get(index - 1).getDescription();
            temp = new ArrayList<>(Arrays.asList(description.split("\n")));
            for (int i = 0; i < temp.size(); i++) {
                if (temp.get(i).contains(monster)) {
                    temp.remove(i);
                }
            }
            description = temp.toString().replaceAll("[|]", "");
            level.getPassages().get(index - 1).setDescription(description);
            myGui.setTextArea(level.getPassages().get(index - 1).getDescription());
        }
    }
}
