package model;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VillageTest {
    private Village village;

    @BeforeEach
    public void setUp() {
        village = new Village();
    }


    @Test
    public void testUnemployed() {
        assertEquals(3, village.getUnemployed().size());
    }

    @Test
    public void testAddAndRemoveCitizen() {
        int size = village.getCitizens().size();
        village.addCitizen(new Citizen("", false));
        assertEquals(size + 1, village.getCitizens().size());
        village.removeCitizen(0);
        assertEquals(size, village.getCitizens().size());

    }

    @Test
    public void testBuildAndChangeResources() {
        int initialWood = village.getTotalWood();
        int initialStone = village.getTotalStone();

        assertTrue(village.build("FARM", "farm 1", 5, 5));
        assertEquals(initialStone - 5, village.getTotalStone());
        assertEquals(initialWood - 5, village.getTotalWood());

        assertTrue(village.changeWood(-village.getTotalWood()));

        assertTrue(village.changeStone(-village.getTotalStone()));

        assertFalse(village.build("FARM", "farm 2", 5, 5));
        assertFalse(village.changeWood(-1));
        assertFalse(village.changeStone(-1));

        village.getBuildings().add(new Building("unknown", "hehehe"));
        village.getUpdateResources();

    }

    @Test
    public void testUpdateCitizensNoFood() {
        village.changeFood(-village.getTotalFood());
        assertTrue(village.getCitizens().size() > 1);
        int initialCount = village.getCitizens().size();
        village.update();
        assertTrue(village.getCitizens().size() < initialCount);
    }

    @Test
    public void testGetUnemployed() {
        assertEquals(3, village.getUnemployed().size());
        village.addCitizen(new Citizen("test", false));
        assertEquals(4, village.getUnemployed().size());
        village.update();
        assertEquals(8, village.getUnemployed().size());
    }

    @Test
    public void testChangeFood() {
        int initialFood = village.getTotalFood();

        assertTrue(village.changeFood(-5));
        assertEquals(initialFood - 5, village.getTotalFood());
        assertFalse(village.changeFood(-village.getTotalFood() - 1));

    }

    @Test
    public void testUpdate() {
        int food = village.getTotalFood();
        int wood = village.getTotalWood();
        int stone = village.getTotalStone();
        village.update();
        assertNotEquals(food, village.getTotalFood());
        assertNotEquals(stone, village.getTotalStone());
        assertNotEquals(wood, village.getTotalWood());
    }

    @Test
    public void testVillageToJsonEmpty() {
        village.getCitizens().clear();
        village.getBuildings().clear();
        village.changeFood(-village.getTotalFood());
        village.changeStone(-village.getTotalStone());
        village.changeWood(-village.getTotalWood());

        JSONObject json = village.toJson();
        assertTrue(json.getJSONArray("citizens").isEmpty());
        assertTrue(json.getJSONArray("buildings").isEmpty());
        assertEquals(0, json.getInt("totalFood"));
        assertEquals(0, json.getInt("totalWood"));
        assertEquals(0, json.getInt("totalStone"));
    }

    @Test
    public void testVillageArraysToJson() {
        JSONObject json = village.toJson();

        assertEquals(6, json.getJSONArray("citizens").length());
        assertEquals(3, json.getJSONArray("buildings").length());
    }

    @Test
    public void testVillageResourcesToJson() {
        JSONObject json = village.toJson();
        assertEquals(village.getTotalFood(), json.getInt("totalFood"));
        assertEquals(village.getTotalWood(), json.getInt("totalWood"));
        assertEquals(village.getTotalStone(), json.getInt("totalStone"));
    }


}


