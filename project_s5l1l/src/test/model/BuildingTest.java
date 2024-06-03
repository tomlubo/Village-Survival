package model;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BuildingTest {
    private Building b1;
    private Citizen c1, c2, c3, c4;

    @BeforeEach
    public void setUp() {
        b1 = new Building("FARM", "Farm 1");

        c1 = new Citizen("Tommy", false);
        c2 = new Citizen("Nane", false);
        c3 = new Citizen("Onur", false);
        c4 = new Citizen("Griffon", false);
    }

    @Test
    public void testAddWorkerWhenFull() {
        b1.setMaxWorkers(3);
        assertEquals(0, b1.getWorkers().size());
        b1.addWorker(c1);
        assertEquals(1, b1.getWorkers().size());

        b1.addWorker(c2);
        assertEquals(2, b1.getWorkers().size());

        b1.addWorker(c3);
        assertEquals(b1.getMaxWorkers(), b1.getWorkers().size());

        b1.addWorker(c4);
        assertEquals(3, b1.getWorkers().size());

    }

    @Test
    public void testRemoveWorkerWhenEmpty() {
        assertNull(b1.removeWorker());

        b1.addWorker(c1);
        b1.addWorker(c2);
        assertEquals(2, b1.getWorkers().size());

        assertEquals(c1, b1.removeWorker());
        assertEquals(1, b1.getWorkers().size());
        assertFalse(b1.getWorkers().contains(c1));
        assertTrue(b1.getWorkers().contains(c2));

    }

    @Test
    public void testSetName() {
        assertEquals("Farm 1", b1.getName());
        b1.setName("Farm 2");
        assertEquals("Farm 2", b1.getName());
    }

    @Test
    public void testProduce() {
        assertEquals(0, b1.produce());
        b1.addWorker(c1);
        assertEquals(3, b1.produce());
        b1.addWorker(c2);
        assertEquals(6, b1.produce());
    }

    @Test
    public void testEmptyBuildingToJson() {
        JSONObject json = b1.toJson();
        assertEquals("FARM", json.getString("type"));
        assertEquals("Farm 1", json.getString("name"));
        assertEquals(5, json.getInt("maxWorkers"));
        assertTrue(json.getJSONArray("workers").isEmpty());
    }

    @Test
    public void testBuildingWithWorkersToJson() {
        b1.addWorker(c1);
        b1.addWorker(c2);
        JSONObject json = b1.toJson();
        assertEquals(2, json.getJSONArray("workers").length());
        assertEquals("Tommy", json.getJSONArray("workers").getJSONObject(0).getString("name"));
        assertEquals("Nane", json.getJSONArray("workers").getJSONObject(1).getString("name"));
    }

    @Test
    public void testBuildingAttribute() {
        b1.setMaxWorkers(3);
        JSONObject json = b1.toJson();
        assertEquals(0, json.getJSONArray("workers").length());
        assertEquals("FARM", json.getString("type"));
        assertEquals("Farm 1", json.getString("name"));
        assertEquals(3, json.getInt("maxWorkers"));
    }


}