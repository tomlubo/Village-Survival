package model;


import org.json.JSONObject;
import persistence.Writable;

// Represents a citizen in a village, a citizen can be employed represented by the boolean isWorking
// A citizen can work in a building that produces resources
// A citizen consumes food every turn
public class Citizen implements Writable {

    private String name;
    private boolean isWorking;

    //REQUIRES: name !null.
    //EFFECTS: Makes a new Citizen with name and isWorking == false.
    public Citizen(String name, boolean isWorking) {
        this.name = name;
        this.isWorking = isWorking;

    }

    //MODIFIES: village
    //EFFECTS:  decreases village food by 2, return true if there is enough food for citizen
    //          otherwise false
    public boolean eat(Village village) {
        EventLog.getInstance().logEvent(new Event("A citizen was able to eat"));
        return village.changeFood(-2);
    }

    //EFFECTS: Returns true if the citizen is currently working,
    //         otherwise false.
    public boolean isWorking() {
        return isWorking;
    }

    //MODIFIES: this
    //EFFECTS: Sets working status to the specified value.
    public void setWorking(boolean working) {
        EventLog.getInstance().logEvent(new Event(String.format("%s is now working", this.name)));
        isWorking = working;
    }

    public String getName() {
        return name;
    }

    //MODIFIES: this
    //EFFECTS: Sets this.name to name.
    public void setName(String name) {
        EventLog.getInstance().logEvent(new Event(String.format("%s was renamed to %s", this.name, name)));
        this.name = name;
    }

    //EFFECTS: Returns this Citizen as a JSONObject.
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("isWorking", isWorking);
        return json;
    }
}
