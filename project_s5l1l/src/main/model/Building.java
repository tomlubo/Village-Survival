package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;
import java.util.List;

// Represents a building in a village, it can have citizens work in it,
// it has a type of FARM,MINE, or LUMBER MILL that determines its resource production
// it produces recources based on how many citizens work in it, it has a maximum number of workers
// it has name

public class Building implements Writable {
    private final String type;
    private final List<Citizen> workers;
    private int maxWorkers;
    private String name;


    //REQUIRES: type is one of "Farm", "Mine", "Lumber Mill" and name to be !null.
    //EFFECTS: Makes a new Building of type and name,
    //         initializes workers as an empty list, sets maxWorkers to 5, and initializes the type and name fields.
    public Building(String type, String name) {
        this.workers = new ArrayList<>();
        this.name = name;
        this.maxWorkers = 5;
        this.type = type;
    }

    //REQUIRES: citizen is !null and workers.size() < maxWorkers.
    //MODIFIES: this
    //EFFECTS:  adds citizen to workers if there is space, otherwise nothing.
    public void addWorker(Citizen citizen) {
        if ((workers.size() < maxWorkers)) {
            workers.add(citizen);
        }
        EventLog.getInstance().logEvent(new Event(String.format("A worker was added to %s", this.name)));
    }

    //REQUIRES: citizen is !null
    //MODIFIES: this.workers
    //EFFECTS:  removes first citizen from workers return removed citizen.
    public Citizen removeWorker() {
        if (!workers.isEmpty()) {
            Citizen c = workers.remove(0);
            c.setWorking(false);
            EventLog.getInstance().logEvent(new Event(String.format("A worker was removed from %s", this.name)));
            return c;
        }
        return null;
    }


    public List<Citizen> getWorkers() {
        return workers;
    }


    public int getNumWorkers() {
        return workers.size();
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //EFFECTS: Returns the output of this building based on the number of workers assigned.
    public int produce() {
        return this.getNumWorkers() * 3;
    }

    public int getMaxWorkers() {
        return maxWorkers;
    }

    public void setMaxWorkers(int maxWorkers) {
        this.maxWorkers = maxWorkers;
    }

    @Override
    //EFFECTS: Returns this building as a JSONObject.
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("type", type);
        json.put("name", name);
        json.put("maxWorkers", maxWorkers);
        JSONArray workersJson = new JSONArray();
        for (Citizen worker : workers) {
            workersJson.put(worker.toJson());
        }
        json.put("workers", workersJson);
        return json;
    }
}
