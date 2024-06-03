package ui;


import model.*;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

// Class responsible for handling user input and game logic,
// the GameManager object takes a village and a max number of turns,
// it initializes the game and starts the game loop, it checks for end conditions and ends the game when neccessary.
public class GameManager {

    private final Scanner input;
    private Village village;
    private boolean gameOver;

    // REQUIRES: village !null, maxTurns > 0.
    // EFFECTS: Makes a GameManager with param village and maxTurns.
    //          sets currentTurn to 0, sets up a new Scanner for input, and gameOver == false.
    public GameManager(Village village) {
        this.village = village;
        this.gameOver = false;
        input = new Scanner(System.in);
        startGame();
    }

    //REQUIRES: The !gameOver, currentTurn > maxTurns
    //MODIFIES: this, village
    //EFFECTS: Starts game loop until gameOver == true,
    //         each iteration displays the main menu, processes user input,
    //         if requested updateGameState. Ends game if maxTurns is reached or the user requests.
    //         when game ends displays the village's final status.
    public void startGame() {
        System.out.println("\nGame Started.\nWelcome to your new Village!\n");
        while (!gameOver) {
            displayMainMenu();
            System.out.println();
            boolean nextTurn = mainMenuUserInput();
            if (nextTurn) {
                updateGameState();
            }
        }
        printEventLogAndExit();
        System.out.println("Game Over");
        System.out.println("Your village ended with:");
        displayStatus();
        System.out.println("Thank you for playing!");
    }

    //EFFECTS: Prints main menu options
    private void displayMainMenu() {
        System.out.println("\nMain Menu:");
        System.out.println("1. View Village Status");
        System.out.println("2. Manage Buildings");
        System.out.println("3. Manage Citizens");
        System.out.println("4. End Turn");
        System.out.println("5. Exit/Save/Load Game");
        System.out.print("Select an option(1-5): ");
    }


    //MODIFIES: this, village
    //EFFECTS: handles user input from the main menu, calls methods based on the user option
    private boolean mainMenuUserInput() {
        String choice = input.nextLine();
        System.out.println();
        switch (choice) {
            case "1":
                displayStatus();
                return false;
            case "2":
                manageBuildings();
                return false;
            case "3":
                manageCitizens();
                return false;
            case "4":
                System.out.println("Ending turn...\n");
                return true;
            case "5":
                displaySaveMenu();
                return false;
            default:
                System.out.println("Oops... Invalid choice. Try again.\n");
                return mainMenuUserInput();
        }
    }

    public void displaySaveMenu() {
        System.out.println("\nMain Menu:");
        System.out.println("1. Exit Game");
        System.out.println("2. Save Game");
        System.out.println("3. Load Game");
        saveMenuUserInput();
    }

    public boolean saveMenuUserInput() {

        String choice = input.nextLine();
        System.out.println();
        switch (choice) {
            case "1":
                gameOver = true;
                return false;
            case "2":
                saveGame();
                System.out.println("Game saved successfully.");
                return false;
            case "3":
                loadGame();
                System.out.println("Game loaded successfully.");
                return false;
            default:
                System.out.println("Oops... Invalid choice. Try again.\n");
                displaySaveMenu();
                return saveMenuUserInput();
        }
    }

    //Modifies: this, village
    //Effect: opens a reader and read data from the saveGame.json file, loads game state.
    private void loadGame() {
        try {
            JsonReader reader = new JsonReader("./data/savedGame.json");
            village = reader.read();
            //System.out.println("Game was successfully loaded!");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    //Effect: opens a writer and saves village data to a .json file
    private void saveGame() {
        try {
            JsonWriter writer = new JsonWriter("./data/savedGame.json");
            writer.open();
            writer.write(village);
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println("Unable to save game: " + e.getMessage());
        }

    }

    //MODIFIES: this, village
    //EFFECTS: calls village update method, prints message
    private void updateGameState() {
        System.out.println("It's a new day in the village!\n");
        int numCitizens = village.getCitizens().size();
        village.update();
        if (village.getCitizens().size() != numCitizens) {
            System.out.println("A CITIZEN STARVED TO DEATH DURING THE NIGHT!\n");
        }
    }

    //MODIFIES: village
    //EFFECTS:  asks user for a name and adds a new citizen to the village,
    //          prints a message
    private void addCitizen() {
        System.out.print("\nName the new citizen: ");
        String name = input.nextLine();
        Citizen newCitizen = new Citizen(name, false);
        village.addCitizen(newCitizen);
        System.out.println(newCitizen.getName() + " has been added to the village.\n");
    }

    //MODIFIES: village
    //EFFECTS: asks user for name of the citizen to remove, tries to remove the citizen with the matching name.
    //         if successful prints a confirmation message,
    //         otherwise an error message
    private void removeCitizen() {
        System.out.println();
        listCitizens();
        System.out.print("Enter the number of the citizen to remove, (0 to cancel): ");
        int citizenIndex = getIndex();
        if (citizenIndex >= 0 & citizenIndex < village.getCitizens().size()) {
            Citizen removed = village.removeCitizen(citizenIndex);
            System.out.println(removed.getName() + " was removed from the village.");
        } else if (citizenIndex == -1) {
            System.out.println("Operation cancelled.");
        } else {
            System.out.println("Oops... Invalid choice.\n");
        }
    }

    //EFFECTS: prints citizens in the village, if citizens isEmpty prints a message.
    private void listCitizens() {
        List<Citizen> citizens = village.getCitizens();
        if (citizens.isEmpty()) {
            System.out.println("Oh no! The village is empty!\n");
        } else {
            System.out.println("\nListing Citizens:");
            for (int i = 0; i < citizens.size(); i++) {
                System.out.println((i + 1) + ". " + citizens.get(i).getName());
            }
        }
    }

    //MODIFIES: village
    //EFFECTS: prints citizen management menu,
    //         handles user input for managing citizens.
    private void manageCitizens() {
        System.out.println("\nCitizen Management:");
        System.out.println("1. List Citizens");
        System.out.println("2. Add Citizen");
        System.out.println("3. Remove Citizen");
        System.out.println("4. Rename Citizen");
        System.out.println("5. Return to Main Menu");
        System.out.print("Select an option: ");
        manageCitizenUserInput();

    }

    //MODIFIES: village
    //EFFECTS: handles user input for citizen management menu.
    //         Supports listing, adding, removing, and renaming citizens
    private void manageCitizenUserInput() {
        String choice = input.nextLine();
        switch (choice) {
            case "1":
                listCitizens();
                break;
            case "2":
                addCitizen();
                break;
            case "3":
                removeCitizen();
                break;
            case "4":
                renameCitizen();
                break;
            case "5":
                break;
            default:
                System.out.println("Oops... Invalid choice. Try again.\n");
                manageCitizens();
                break;
        }
    }


    //MODIFIES: village
    //EFFECTS: prompts user for a current citizen index and new name, renames citizen.
    private void renameCitizen() {
        listCitizens();
        System.out.print("\nEnter the number of the citizen to rename, (0 to cancel): ");
        int citizenIndex = getIndex();
        System.out.print("\nEnter the new name of the citizen: ");
        String name = "";
        while (name.equals("")) {
            name = input.nextLine();
        }
        if (citizenIndex >= 0 & citizenIndex < village.getCitizens().size()) {
            Citizen renamed = village.getCitizens().get(citizenIndex);
            System.out.println(renamed.getName() + " was renamed to " + name);
            renamed.setName(name);
        } else if (citizenIndex == -1) {
            System.out.println("Operation cancelled.");
        } else {
            System.out.println("Oops... Invalid choice.\n");
            renameCitizen();
        }
    }

    //MODIFIES: village
    //EFFECTS: prints buildings management menu,
    //         handles user input for managing buildings.
    private void manageBuildings() {
        System.out.println("\nBuilding Management:");
        System.out.println("1. List Buildings");
        System.out.println("2. Add Building");
        System.out.println("3. Manage Building");
        System.out.println("4. Return to Main Menu");
        System.out.print("Select an option: ");
        manageBuildingsUserInput();
    }

    //MODIFIES: village
    //EFFECTS: handles user input for buildings management menu.
    //         Supports listing, adding, removing, and managing buildings
    private void manageBuildingsUserInput() {
        String choice = input.nextLine();
        switch (choice) {
            case "1":
                listBuildings();
                break;
            case "2":
                addBuilding();
                break;
            case "3":
                manageBuilding();
                break;
            case "4":
                break;
            default:
                System.out.println("Oops... Invalid choice. Try again.\n");
                manageCitizens();
                break;
        }
    }

    //MODIFIES: village
    //EFFECTS: prompts user to choose a building for management,
    //         prints options for that building, (hiring, firing, and renaming the building)
    private void manageBuilding() {
        System.out.println("\nSelect a building to manage:");
        List<Building> buildings = village.getBuildings();
        listBuildings();
        System.out.print("\nBuilding number (or 0 to cancel): ");
        int buildingIndex = getIndex();
        if (buildingIndex >= 0 && buildingIndex < buildings.size()) {
            Building building = buildings.get(buildingIndex);
            System.out.println("Managing Building: " + building.getName());
            System.out.println("1. Hire Worker");
            System.out.println("2. Fire Worker");
            System.out.println("3. Rename Building");
            System.out.println("4. Return to Building Menu");
            System.out.print("Select an option: ");
            manageBuildingUserInput(building);
        }
    }

    //Effects: Prints Events to console on application exit.
    private void printEventLogAndExit() {
        System.out.println("Event Log:");
        for (Iterator<Event> it = EventLog.getInstance().iterator(); it.hasNext(); ) {
            Event event = it.next();
            System.out.println(event.getDescription());
        }
        System.exit(0);
    }

    //MODIFIES: village
    //EFFECTS: handles user input for building management menu,
    //         supports hiring, firing, and renaming.
    private void manageBuildingUserInput(Building building) {
        String choice = input.nextLine();
        switch (choice) {
            case "1":
                hireWorker(building);
                break;
            case "2":
                fireWorker(building);
                break;
            case "3":
                renameBuilding(building);
                break;
            case "4":
                break;
            default:
                System.out.println("Oops... Invalid choice. Try again.\n");
                manageCitizens();
                break;
        }
    }

    //REQUIRES: building is !null
    //MODIFIES: village
    //EFFECTS: prompts user for a new name, renames param building.
    private void renameBuilding(Building building) {
        System.out.print("\nChoose new name: ");
        String name = "";
        while (name.equals("")) {
            name = input.nextLine();
        }
        System.out.println(building.getName() + " was renamed to: " + name);
        building.setName(name);
    }

    //REQUIRES: building is !null
    //MODIFIES: village, citizen
    //EFFECTS: removes a worker for list of workers, updates isWorking status
    private void fireWorker(Building building) {
        if (building.getNumWorkers() != 0) {
            Citizen c = building.removeWorker();
            System.out.println(c.getName() + " has been fired from the " + building.getType() + ".\n");
            village.getUnemployed().add(c);
        } else {
            System.out.println("\nThis building has no employees. Maybe hire someone just to fire them?\n");
        }

    }


    //REQUIRES: building is !null
    //MODIFIES: village, citizen
    //EFFECTS: checks is there are unemployed workers, prints unemployed workers
    //         prompts user to select an unemployed worker,i
    //         if there is space adds a worker to list of workers, updates isWorking status,
    //         otherwise prompts again
    private void hireWorker(Building building) {
        List<Citizen> unemployed = village.getUnemployed();
        if (unemployed.isEmpty()) {
            System.out.println("\nThere are no unemployed citizens. Maybe bring one to life an put them to work...");
            return;
        }
        System.out.println("\nSelect an unemployed citizen to hire for " + building.getType() + ":");
        for (int i = 0; i < unemployed.size(); i++) {
            System.out.println((i + 1) + ". " + unemployed.get(i).getName());
        }
        System.out.print("\nChoose a citizen: ");
        int choice = getIndex();
        if (choice >= 0 & choice < unemployed.size()) {
            Citizen c = unemployed.get(choice);
            building.addWorker(c);
            c.setWorking(true);
            village.getUnemployed().remove(c);
            System.out.println(c.getName() + " has been assigned to " + building.getType() + ". No more slacking off!");
        } else {
            System.out.println("\nInvalid selection. Please try again.");
            hireWorker(building);
        }

    }

    //EFFECTS: handles integer parsing for index selection, catches NumberFormatException
    public int getIndex() {
        int index = -1;
        try {
            index = Integer.parseInt(input.nextLine()) - 1;
        } catch (NumberFormatException e) {
            return index;
        }
        return index;

    }

    //MODIFIES: village
    //EFFECTS:  prints options, prompts user to select type of building and a name,
    //          calls input handler with user choice and name.
    private void addBuilding() {
        System.out.println("\nSelect the type of building to add:");
        System.out.println("1. Farm (4 Wood, 1 Stone)");
        System.out.println("2. Mine (6 Wood, 3 Stone)");
        System.out.println("3. Lumber Mill (3 Wood, 2 Stone)");
        System.out.println("4. Return to Main Menu");
        System.out.print("Choose a building type: ");
        String choice = input.nextLine();
        System.out.print("Choose a name: ");
        String name = input.nextLine();
        addBuildingUserInput(choice, name);
    }

    //MODIFIES: village
    //EFFECTS: handles user input for addBuilding menu,
    //         builds specified type, with name, updates resources.
    private void addBuildingUserInput(String choice, String name) {
        boolean built = false;
        switch (choice) {
            case "1":
                built = village.build("Farm", name, 4, 1);
                break;
            case "2":
                built = village.build("Mine", name, 6, 3);
                break;
            case "3":
                built = village.build("Lumber Mill", name, 3, 2);
                break;
            case "4":
                break;
            default:
                System.out.println("Oops... Invalid choice. Try again.\n");
                addBuilding();
                break;
        }
        if (!built) {
            System.out.println("\nYour village is broke! Not enough resources.");
        }

    }

    //EFFECT: prints a list of building type and name
    private void listBuildings() {
        List<Building> buildings = village.getBuildings();
        if (buildings.isEmpty()) {
            System.out.println("There are no buildings in the village. How is this possible??");
            System.out.println("Did you burn them down for the insurance money?");
        } else {
            System.out.println("\nListing buildings:");
            for (int i = 0; i < buildings.size(); i++) {
                System.out.println((i + 1) + ". " + buildings.get(i).getType() + ": " + buildings.get(i).getName());
            }
        }
    }

    //EFFECTS: Prints the current total resources, number of citizens, and number of buildings.
    public void displayStatus() {
        System.out.println("\nVillage Status:");
        System.out.println("Total Food: " + village.getTotalFood());
        System.out.println("Total Wood: " + village.getTotalWood());
        System.out.println("Total Stone: " + village.getTotalStone());
        System.out.println("Citizens: " + village.getCitizens().size());
        System.out.println("Buildings: " + village.getBuildings().size());
        System.out.println();
    }


}
