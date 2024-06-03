package ui;

import model.Event;
import model.*;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * GameManagerGUI creates a graphical user interface for running the Village Survival game. The class opens a new window
 * that allows the user to interact with the game model through buttons and pop-up dialogues. The class also allows
 * users to view important chaacteristics of the village such as a list of buildings and citizens, the number of
 * resources and of Citizens. It also allows the user to load/save the game.
 */
public class GameManagerGUI extends JFrame {
    private final JLabel numCitizensLabel;
    private final JLabel numWoodLabel;
    private final JLabel numStoneLabel;
    private final JLabel numFoodLabel;
    private final JLabel numBuildingsLabel;
    private JFrame frame;
    private Village village;
    private JList<String> citizensList;
    private JList<String> buildingsList;
    private DefaultListModel<String> citizensListModel;
    private DefaultListModel<String> buildingsListModel;

    //Requires: village !null
    //Effects: initialises and displays the main game window
    public GameManagerGUI(Village village) {
        this.village = village;
        numCitizensLabel = new JLabel(String.valueOf(village.getCitizens().size()));
        numBuildingsLabel = new JLabel(String.valueOf(village.getBuildings().size()));
        numFoodLabel = new JLabel(String.valueOf(village.getTotalFood()));
        numStoneLabel = new JLabel(String.valueOf(village.getTotalStone()));
        numWoodLabel = new JLabel(String.valueOf(village.getTotalWood()));
        initGUI();

    }


    //REQUIRES: frame must be initialised
    //MODIFIES: this.frame
    //EFFECTS: makes the GUI visible
    private void showGUI() {
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                printEventLogAndExit();
            }
        });
    }

    //Effects: Prints Events to console on application exit.
    private void printEventLogAndExit() {
        System.out.println("Event Log:");
        for (Event event : EventLog.getInstance()) {
            System.out.println(event.getDescription());
        }
        System.exit(0);
    }


    //MODIFIES: this.frame
    //EFFECTS: initialises the main game window and other GUI elements
    private void initGUI() {
        frame = new JFrame("Village Survival");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        initStatusPanel();
        initButtons();
        initMenu();
        initDisplayList();
        showGUI();
    }

    //REQUIRES: village !null && frame !null
    //MODIFIES: this.frame
    //EFFECTS: makes a split pane panel and displays a list of citizens by name and employment status, displays a list
    //of buildings by type, name and num workers
    private void initDisplayList() {
        JPanel displayPanel = new JPanel(new BorderLayout());

        JPanel citizenPanel = new JPanel(new BorderLayout());
        JLabel citizenLabel = new JLabel("Citizens:");
        citizenLabel.setHorizontalAlignment(JLabel.CENTER);
        citizenPanel.add(citizenLabel, BorderLayout.NORTH);

        JPanel buildingPanel = new JPanel(new BorderLayout());
        JLabel buildingLabel = new JLabel("Buildings:");
        buildingLabel.setHorizontalAlignment(JLabel.CENTER);
        buildingPanel.add(buildingLabel, BorderLayout.NORTH);


        citizensListModel = new DefaultListModel<>();
        buildingsListModel = new DefaultListModel<>();

        citizensList = new JList(citizensListModel);
        buildingsList = new JList(buildingsListModel);

        citizenPanel.add(new JScrollPane(citizensList));
        citizenPanel.setBorder(new EmptyBorder(0, 5, 0, 0));
        buildingPanel.add(new JScrollPane(buildingsList));
        buildingPanel.setBorder(new EmptyBorder(0, 5, 0, 0));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, citizenPanel, buildingPanel);
        splitPane.setResizeWeight(0.5);

        displayPanel.add(splitPane, BorderLayout.CENTER);
        frame.add(displayPanel, BorderLayout.CENTER);

        updateCitizensList();
        updateBuildingsList();
    }

    //REQUIRES: village.buildings !null && frame !null
    //MODIFIES: this
    //EFFECTS: updates the list of buildings
    private void updateBuildingsList() {
        buildingsListModel.clear();

        for (Building b : village.getBuildings()) {
            String text = String.format("Type: %s, Name: %s, Number of Workers: %d",
                    b.getType(), b.getName(), b.getNumWorkers());
            buildingsListModel.addElement(text);
        }
    }

    //REQUIRES: village.citizens !null && frame !null
    //MODIFIES: this
    //EFFECTS: updates the list of citizens
    private void updateCitizensList() {
        citizensListModel.clear();
        for (Citizen c : village.getCitizens()) {
            String text = String.format("Name: %s, Employment Status: %s", c.getName(), c.isWorking());
            citizensListModel.addElement(text);
        }
    }


    //MODIFIES: this.frame
    //EFFECTS: makes menu options and calls functions to make specific options
    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        createPersistenceMenu(menuBar);
        createExitMenu(menuBar);
    }

    //MODIFIES: this.frame
    //EFFECTS: makes persistence menu options for saving and loading game data
    private void createPersistenceMenu(JMenuBar menuBar) {
        JMenu persistenceMenu = new JMenu("Save/Load Game");
        menuBar.add(persistenceMenu);

        JMenuItem loadItem = new JMenuItem("Load game");
        loadItem.addActionListener(this::loadGameAction);
        persistenceMenu.add(loadItem);

        JMenuItem saveItem = new JMenuItem("Save game");
        saveItem.addActionListener(this::saveGameAction);
        persistenceMenu.add(saveItem);
    }

    //MODIFIES: this.frame
    //EFFECTS: makes exit menu for quitting the game without saving
    private void createExitMenu(JMenuBar menuBar) {
        JMenu exitMenu = new JMenu("Quit Game");
        menuBar.add(exitMenu);

        JMenuItem quitGame = new JMenuItem("Quit game");
        quitGame.addActionListener(this::quitGameAction);
        exitMenu.add(quitGame);
    }

    //REQUIRES: ActionEvent triggered by user
    //MODIFIES: village
    //EFFECTS: loads game data from a save file
    private void loadGameAction(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(frame,
                "Load Saved Game?", "Load Game", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_NO_OPTION) {
            loadGame();
            updateBuildingsList();
            updateCitizensList();
        }
    }

    //REQUIRES: ActionEvent triggered by user
    //MODIFIES: savedGame.json
    //EFFECTS: saves game data to savedGame.json
    private void saveGameAction(ActionEvent e) {
        saveGame();
        JOptionPane.showMessageDialog(frame,
                "Game Saved!", "Save Game", JOptionPane.INFORMATION_MESSAGE);
    }

    //REQUIRES: ActionEvent triggered by user
    //EFFECTS: exits program
    private void quitGameAction(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(frame,
                "Unsaved Progress will be lost!", "Quit Game", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_NO_OPTION) {
            printEventLogAndExit();
        }
    }


    //MODIFIES: this, village
    //EFFECTS: if file is successfully read, loads game data to village, else an error message is shown
    private void loadGame() {
        try {
            JsonReader reader = new JsonReader("./data/savedGame.json");
            village = reader.read();
            JOptionPane.showMessageDialog(frame,
                    "Game loaded!", "Load Game", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame,
                    "Load game failed:" + e.getMessage(), "Load Game", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    //EFFECTS: if file exsists saves game data to savedGame.json else shows error message
    private void saveGame() {
        try {
            JsonWriter writer = new JsonWriter("./data/savedGame.json");
            writer.open();
            writer.write(village);
            writer.close();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(frame,
                    "Save game failed:" + e.getMessage(), "Save Game", JOptionPane.INFORMATION_MESSAGE);

        }

    }

    //MODIFIES: this
    //EFFECTS: sets up interaction buttons for the user
    private void initButtons() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton addCitizensButton = new JButton("Add Citizen");
        addCitizensButton.addActionListener(this::addCitizenPanel);

        JButton removeCitizenButton = new JButton("Remove Citizen");
        removeCitizenButton.addActionListener(this::removeCitizenPanel);

        JButton renameCitizenButton = new JButton("Rename Citizen");
        renameCitizenButton.addActionListener(this::renameCitizenPanel);

        JButton addBuildingsButton = new JButton("Add Building");
        addBuildingsButton.addActionListener(this::addBuildingPanel);

        JButton manageBuildingButton = new JButton("Manage Building");
        manageBuildingButton.addActionListener(this::manageBuildingPanel);

        JButton endTurnButton = new JButton("End Turn");
        endTurnButton.addActionListener(this::endTurnPanel);

        buttonPanel.add(addCitizensButton);
        buttonPanel.add(removeCitizenButton);
        buttonPanel.add(renameCitizenButton);
        buttonPanel.add(addBuildingsButton);
        buttonPanel.add(manageBuildingButton);
        buttonPanel.add(endTurnButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);
    }

    //REQUIRES: ActionEvent triggered by user
    //MODIFIES: village
    //EFFECTS: opens a pop-up with otions to hire/fire a worker, or rename the building.
    private void manageBuildingPanel(ActionEvent actionEvent) {
        int selectedBuilding = buildingsList.getSelectedIndex();
        if (selectedBuilding == -1) {
            JOptionPane.showMessageDialog(frame, "Select a building to manage.", "No Building Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        Object[] options = {"Hire Worker", "Fire Worker", "Rename Building"};
        int choice = JOptionPane.showOptionDialog(frame, "Choose what to do:", "Manage Building",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        switch (choice) {
            case 0:
                hireWorkerPanel();
                break;
            case 1:
                fireWorkerPanel();
                break;
            case 2:
                renameBuildingPanel();
                break;
            default:
                break;
        }
    }

    //REQUIRES: ActionEvent triggered by user/ building is selected from scroll panel
    //MODIFIES: village
    //EFFECTS: opens a pop-up and asks for the new name of the building
    private void renameBuildingPanel() {
        int selectedIndex = buildingsList.getSelectedIndex();
        if (selectedIndex != -1) {
            Building buildingToRename = village.getBuildings().get(selectedIndex);
            String name = JOptionPane.showInputDialog(frame,
                    String.format("Enter a new name for %s.", buildingToRename.getName()),
                    "Rename Building", JOptionPane.QUESTION_MESSAGE);
            if (name != null && !name.trim().isEmpty()) {
                buildingToRename.setName(name.trim());
                updateBuildingsList();
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Select a building to rename.",
                    "No Building Selected",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    //REQUIRES: ActionEvent triggered by user/ building and citizen is selected from scroll panel
    //MODIFIES: village
    //EFFECTS: opens a pop-up and guides user through adding a new worker
    private void hireWorkerPanel() {
        int selectedBuildingIndex = buildingsList.getSelectedIndex();
        if (selectedBuildingIndex == -1) {
            JOptionPane.showMessageDialog(frame, "Select a building first.",
                    "No Building Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        Building selectedBuilding = village.getBuildings().get(selectedBuildingIndex);

        int selectedCitizenIndex = citizensList.getSelectedIndex();
        if (selectedCitizenIndex == -1) {
            JOptionPane.showMessageDialog(frame, "Select a citizen to hire.",
                    "No Citizen Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        Citizen newHire = village.getCitizens().get(selectedCitizenIndex);
        selectedBuilding.addWorker(newHire);
        newHire.setWorking(true);

        JOptionPane.showMessageDialog(frame, "A citizen has been hired.",
                "Citizen Hired",
                JOptionPane.PLAIN_MESSAGE);

        updateBuildingsList();
        updateCitizensList();
    }

    //REQUIRES: ActionEvent triggered by user/ building is selected from scroll panel
    //MODIFIES: village
    //EFFECTS: fires the first worker from the selected building
    private void fireWorkerPanel() {
        int selectedBuildingIndex = buildingsList.getSelectedIndex();
        if (selectedBuildingIndex == -1) {
            JOptionPane.showMessageDialog(frame, "Select a building first.",
                    "No Building Selected",
                    JOptionPane.WARNING_MESSAGE);
        }
        Building selectedBuilding = village.getBuildings().get(selectedBuildingIndex);
        List<Citizen> workers = selectedBuilding.getWorkers();
        if (workers.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "There are no workers in this building",
                    "No Workforce",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        selectedBuilding.removeWorker();
        updateBuildingsList();
        JOptionPane.showMessageDialog(frame, "A worker has been fired",
                "Worker Fired",
                JOptionPane.WARNING_MESSAGE);
    }

    //REQUIRES: ActionEvent triggered by user/ building is selected from scroll panel
    //MODIFIES: village
    //EFFECTS: opens a pop-up and asks the user to select the type of building and then the name of the building
    private void addBuildingPanel(ActionEvent actionEvent) {
        String type = chooseType();
        String name = JOptionPane.showInputDialog(frame, "Enter the name of the new building:");
        if (type != null && name != null && !name.trim().isEmpty()) {
            boolean built = false;
            switch (type) {
                case "Farm":
                    built = village.build(type, name, 4, 1);
                    break;
                case "Mine":
                    built = village.build(type, name, 6, 3);
                    break;
                case "Lumber Mill":
                    built = village.build(type, name, 3, 2);
                    break;
            }
            if (built) {
                showBuildingImage(type);
            } else {
                JOptionPane.showMessageDialog(frame, String.format("Not enough resources to build a %s.", type),
                        "Building Not Added", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //Effect: makes a pop up window appear with the icon representing the image added
    private void showBuildingImage(String type) {
        String path = String.format("/ui/icons/%s.png", type.toLowerCase());
        ImageIcon icon = new ImageIcon(getClass().getResource(path));
        JOptionPane.showMessageDialog(frame, null, "Building Added!",
                JOptionPane.INFORMATION_MESSAGE, icon);
        updateBuildingsList();
        updateStatusPanel();
    }

    //EFFECTS: asks user to select the type of building and returns it/ helper method for addBuildingPanel()
    private String chooseType() {
        Object[] types = {"Farm", "Mine", "Lumber Mill"};
        return (String) JOptionPane.showInputDialog(frame, "Choose a type of building to add:",
                "Add Building", JOptionPane.PLAIN_MESSAGE, null, types, "Farm");
    }

    //REQUIRES: ActionEvent triggered by user
    //MODIFIES: this, village
    //EFFECTS: updates the state of the village and the list panels, represents a new day in the village
    private void endTurnPanel(ActionEvent e) {
        int numCitizensBefore = village.getCitizens().size();
        village.update();
        int numCitizensAfter = village.getCitizens().size();
        updateCitizensList();
        updateBuildingsList();
        updateStatusPanel();

        if (numCitizensBefore != numCitizensAfter) {
            JOptionPane.showMessageDialog(frame, "A CITIZEN STARVED TO DEATH DURING THE NIGHT!",
                    "Tragedy in the village", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, "Good Morning! Its a new day in the village!",
                    "New Day", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    //REQUIRES: ActionEvent triggered by user
    //EFFECTS: updates the state of the list panels
    private void updateStatusPanel() {
        numCitizensLabel.setText(String.valueOf(village.getCitizens().size()));
        numBuildingsLabel.setText(String.valueOf(village.getBuildings().size()));
        numFoodLabel.setText(String.valueOf(village.getTotalFood()));
        numStoneLabel.setText(String.valueOf(village.getTotalStone()));
        numWoodLabel.setText(String.valueOf(village.getTotalWood()));
    }

    //REQUIRES: ActionEvent triggered by user/ citizen is selected in scroll panel
    //MODIFIES: this, village
    //EFFECTS: prompts the user for a new name for the selected citizen
    private void renameCitizenPanel(ActionEvent e) {
        int selectedIndex = citizensList.getSelectedIndex();
        if (selectedIndex != -1) {
            Citizen citizenToRename = village.getCitizens().get(selectedIndex);
            String name = JOptionPane.showInputDialog(frame,
                    String.format("Enter a new name for %s.", citizenToRename.getName()),
                    "Rename Citizen", JOptionPane.QUESTION_MESSAGE);
            if (name != null && !name.trim().isEmpty()) {
                citizenToRename.setName(name.trim());
                updateCitizensList();
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Select a citizen to rename.",
                    "No Citizen Selected",
                    JOptionPane.WARNING_MESSAGE);
        }

    }

    //REQUIRES: ActionEvent triggered by user/ citizen is selected in scroll panel
    //MODIFIES: this, village
    //EFFECTS: removes selected citizen from the village
    private void removeCitizenPanel(ActionEvent e) {
        int selectedCitizen = citizensList.getSelectedIndex();
        if (selectedCitizen != -1) {
            int confirm = JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to remove this Citizen?",
                    "Remove Citizen", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_NO_OPTION) {
                village.removeCitizen(selectedCitizen);
                updateCitizensList();
            }
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Select a citizen to remove",
                    "No Citizen Selected",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    //REQUIRES: ActionEvent triggered by user
    //MODIFIES: this, village
    //EFFECTS: prompts user for the new name of the citizen and creates a new citizen to be added to the village
    private void addCitizenPanel(ActionEvent e) {
        String name = JOptionPane.showInputDialog(frame, "Enter the name of the new citizen:");
        if (name != null && !name.trim().isEmpty()) {
            village.addCitizen(new Citizen(name, false));
            updateCitizensList();
        }
    }

    //REQUIRES: ActionEvent triggered by user
    //MODIFIES: this, village
    //EFFECTS: prompts user for the new name of the citizen and creates a new citizen to be added to the village
    private void initStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        JLabel statusLabel = new JLabel("Village Status");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusLabel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JPanel statusContentPanel = new JPanel(new GridLayout(0, 2));
        statusContentPanel.setBorder(new EmptyBorder(0, 5, 10, 0));


        statusContentPanel.add(new JLabel("Number of Citizens:"));
        statusContentPanel.add(numCitizensLabel);
        statusContentPanel.add(new JLabel("Number of Buildings:"));
        statusContentPanel.add(numBuildingsLabel);
        statusContentPanel.add(new JLabel("Total Food:"));
        statusContentPanel.add(numFoodLabel);
        statusContentPanel.add(new JLabel("Total Stone:"));
        statusContentPanel.add(numStoneLabel);
        statusContentPanel.add(new JLabel("Total Wood:"));
        statusContentPanel.add(numWoodLabel);
        statusContentPanel.add(statusLabel, BorderLayout.NORTH);

        statusPanel.add(statusLabel, BorderLayout.NORTH);
        statusPanel.add(statusContentPanel, BorderLayout.CENTER);
        frame.add(statusPanel, BorderLayout.NORTH);
    }

}
