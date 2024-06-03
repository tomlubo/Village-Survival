package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;
import java.util.List;


// Represents a village which is a collection of citizens and buildings, that has a certain amount of resources.
// can add and remove buildings or citizens, it updates the state of the resources and citizens every turn.
// it is the class manipulated directly by the ui.
public class Village implements Writable {
    private final List<Citizen> citizens;
    private final List<Citizen> unemployed;
    private final List<Building> buildings;
    private int totalWood;
    private int totalStone;
    private int totalFood;


    // EFFECTS: Makes a Village with initialized citizens, buildings, and wood, food, stone stores.
    //          Adds default buildings and assigns initial workers.
    public Village() {
        this.citizens = new ArrayList<>();
        this.buildings = new ArrayList<>();
        this.unemployed = new ArrayList<>();

        this.totalFood = 20;
        this.totalWood = 15;
        this.totalStone = 15;

        buildings.add(new Building("Farm", "Farm 1"));
        buildings.add(new Building("Lumber Mill", "Mill 1"));
        buildings.add(new Building("Mine", "Mine 1"));

        for (int i = 0; i < 6; i++) {
            citizens.add(new Citizen("Founder", false));
        }
        EventLog.getInstance().logEvent(new Event("A village was created"));
        buildings.get(0).addWorker(citizens.get(0));
        citizens.get(0).setWorking(true);

        buildings.get(1).addWorker(citizens.get(1));
        citizens.get(1).setWorking(true);

        buildings.get(2).addWorker(citizens.get(2));
        citizens.get(2).setWorking(true);
        unemployed.add(citizens.get(3));
        unemployed.add(citizens.get(4));
        unemployed.add(citizens.get(5));
    }

    //MODIFIES: this
    //EFFECTS: Adds a new Citizen to the citizens list.
    public void addCitizen(Citizen citizen) {
        citizens.add(citizen);
        //citizen.setWorking(true);
        if (!citizen.isWorking()) {
            unemployed.add(citizen);
        }
        EventLog.getInstance().logEvent(new Event("A Citizen was ADDED to the village"));
    }

    //REQUIRES: index to be within the bounds of the citizens list.
    //MODIFIES: this
    //EFFECTS: removes and return the Citizen from the village.
    public Citizen removeCitizen(int index) {
        Citizen removed = citizens.remove(index);
        unemployed.remove(removed);
        EventLog.getInstance().logEvent(new Event("A Citizen was REMOVED from the village"));
        return removed;
    }

    //MODIFIES: this
    //EFFECTS: if enough resources makes new building of type and name to buildings and updates resources,
    //         otherwise informs user.
    public boolean build(String type, String name, int woodCost, int stoneCost) {
        if ((totalStone > stoneCost) & (totalWood > woodCost)) {
            buildings.add(new Building(type, name));
            totalStone -= stoneCost;
            totalWood -= woodCost;
            EventLog.getInstance().logEvent(
                    new Event(String.format("A %s named %s was added from the village", type, name)));
            return true;
        } else {
            return false;
        }
    }

    //MODIFIES: this
    //EFFECTS: updates the village totalFood by amount. Returns true if totalFood won't be < 0,
    //          otherwise false.
    public boolean changeFood(int amount) {
        if (totalFood + amount >= 0) {
            totalFood += amount;
            EventLog.getInstance().logEvent(new Event(String.format("Total Food is now: %d", totalFood)));
            return true;
        } else {
            totalFood = 0;
            return false;
        }
    }

    //MODIFIES: this
    //EFFECTS: updates the village totalWood by amount. Returns true if totalWood won't be < 0,
    //          otherwise false.
    public boolean changeWood(int amount) {
        if (totalWood + amount >= 0) {
            totalWood += amount;
            EventLog.getInstance().logEvent(new Event(String.format("Total Wood is now: %d", totalWood)));
            return true;
        } else {
            return false;
        }
    }

    //MODIFIES: this
    //EFFECTS: updates the village totalStone by amount. Returns true if totalStone won't be < 0,
    //          otherwise false.
    public boolean changeStone(int amount) {
        if (totalStone + amount >= 0) {
            totalStone += amount;
            EventLog.getInstance().logEvent(new Event(String.format("Total Stone is now: %d", totalStone)));
            return true;
        } else {
            return false;
        }
    }

    public void getUpdateResources() {
        updateResources();
        EventLog.getInstance().logEvent(new Event("Resources were updated"));
    }

    //MODIFIES: this
    //EFFECTS: Updates resources based on the production of each building.
    private void updateResources() {
        for (Building b : buildings) {
            switch (b.getType().toUpperCase()) {
                case "FARM":
                    totalFood += b.produce();
                    break;
                case "MINE":
                    totalStone += b.produce();
                    break;

                case "LUMBER MILL":
                    totalWood += b.produce();
                    break;
                default:
                    break;
            }
        }
    }

    //MODIFIES: this
    //EFFECTS: Updates isWorking of citizens, adds unemployed citizens to unemployed.
    //         Each citizen tries to eat().
    //         If totalFood == 0, removes a citizen due to starvation and prints a message.

    private void updateCitizens() {

        for (Citizen c : citizens) {
            if (!c.isWorking()) {
                unemployed.add(c);

            }

            if (totalFood <= 0) {
                citizens.remove(c);
                break;
            }
            c.eat(this);
        }
        EventLog.getInstance().logEvent(new Event("Citizens were updated"));
    }

    //MODIFIES: this
    //EFFECTS: Updates village per turn by calling updateCitizens(), updateResources().
    public void update() {
        updateResources();
        updateCitizens();
        EventLog.getInstance().logEvent(new Event("Village updated for next turn"));
    }

    public List<Citizen> getCitizens() {
        return citizens;

    }


    public List<Building> getBuildings() {
        return buildings;
    }

    public List<Citizen> getUnemployed() {
        return unemployed;
    }


    public int getTotalFood() {
        return totalFood;
    }

    public int getTotalStone() {
        return totalStone;
    }

    public int getTotalWood() {
        return totalWood;
    }

    @Override
    //EFFECTS: Returns this village as a JSONObject.
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        JSONArray jsonCitizens = new JSONArray();
        JSONArray jsonBuildings = new JSONArray();

        for (Citizen c : citizens) {
            jsonCitizens.put(c.toJson());
        }


        for (Building b : buildings) {
            jsonBuildings.put(b.toJson());
        }

        json.put("citizens", jsonCitizens);
        json.put("buildings", jsonBuildings);
        json.put("totalWood", totalWood);
        json.put("totalStone", totalStone);
        json.put("totalFood", totalFood);
        EventLog.getInstance().logEvent(new Event("Village state was saved"));
        return json;
    }
}


