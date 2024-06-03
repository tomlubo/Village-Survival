package persistence;

import model.Building;
import model.Citizen;
import model.Village;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonTest {

    protected void checkCitizen(String name, boolean isWorking, Citizen citizen) {

        assertEquals(name, citizen.getName());
        assertEquals(isWorking, citizen.isWorking());
    }

    protected void checkBuilding(String name, String type, int maxWorkers, Building building) {
        assertEquals(name, building.getName());
        assertEquals(type, building.getType());
        assertEquals(maxWorkers, building.getMaxWorkers());
    }

    protected void checkVillageResources(int totalWood, int totalFood, int totalStone, Village village) {
        assertEquals(totalFood, village.getTotalFood());
        assertEquals(totalWood, village.getTotalWood());
        assertEquals(totalStone, village.getTotalStone());

    }
}
