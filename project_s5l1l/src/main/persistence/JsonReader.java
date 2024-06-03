/*
 *
 * This code was modified from JsonSerializationDemo provided by the course instructors of CPSC 210 at UBC
 *
 */

package persistence;

import model.Building;
import model.Citizen;
import model.Village;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

// This code was modified from JsonSerializationDemo provided by the course instructors of CPSC 210 at UBC
// Represents a reader that reads village from JSON data stored in file
public class JsonReader {

    private String source;

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {

        this.source = source;
    }

    // EFFECTS: reads village from file and returns it;
    // throws IOException if an error occurs reading data from file
    public Village read() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseVillage(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        return contentBuilder.toString();
    }

    // EFFECTS: parses village from JSON object and returns it
    private Village parseVillage(JSONObject jsonObject) {

        Village village = new Village();
        village.getCitizens().clear();
        village.getBuildings().clear();

        village.changeFood(-village.getTotalFood());
        village.changeFood(jsonObject.getInt("totalFood"));

        village.changeStone(-village.getTotalStone());
        village.changeStone(jsonObject.getInt("totalStone"));

        village.changeWood(-village.getTotalWood());
        village.changeWood(jsonObject.getInt("totalWood"));

        addBuildings(village, jsonObject);
        addCitizens(village, jsonObject);
        return village;
    }


    // EFFECTS: parses citizens JSONArray
    private void addCitizens(Village village, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("citizens");

        for (Object json : jsonArray) {
            JSONObject nextCitizen = (JSONObject) json;
            addCitizen(village, nextCitizen);
        }
    }

    // MODIFIES: village
    // EFFECTS: parses citizen from JSON object and adds them to village
    private void addCitizen(Village village, JSONObject jsonObject) {
        String name = jsonObject.getString("name");
        boolean isWorking = jsonObject.getBoolean("isWorking");
        Citizen citizen = new Citizen(name, isWorking);
        village.addCitizen(citizen);
    }

    // EFFECTS: parses buildings from JSON object and adds them to village
    private void addBuildings(Village village, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("buildings");

        for (Object json : jsonArray) {
            JSONObject nextBuilding = (JSONObject) json;
            addBuilding(village, nextBuilding);
        }
    }

    // MODIFIES: village
    // EFFECTS: parses building from JSON object and adds it to village
    private void addBuilding(Village village, JSONObject jsonObject) {
        String name = jsonObject.getString("name");
        String type = jsonObject.getString("type");

        Building building = new Building(type, name);
        village.getBuildings().add(building);
    }


}
