package model;


import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CitizenTest {
    private Village v1;
    private Citizen c1;

    @BeforeEach
    public void setUp() {
        v1 = new Village();
        c1 = new Citizen("Tommy", false);
    }

    @Test
    public void testSetName() {
        assertEquals("Tommy", c1.getName());
        c1.setName("Nane");
        assertEquals("Nane", c1.getName());
    }

    @Test
    public void testIsWorking() {
        assertFalse(c1.isWorking());
        c1.setWorking(true);
        assertTrue(c1.isWorking());
    }

    @Test
    public void testEat() {
        assertTrue(c1.eat(v1));
        assertEquals(18, v1.getTotalFood());

        v1.changeFood(-v1.getTotalFood());
        assertFalse(c1.eat(v1));
    }

    @Test
    public void testCitizenToJson() {
        JSONObject json = c1.toJson();
        assertEquals("Tommy", json.getString("name"));
        assertFalse(json.getBoolean("isWorking"));
    }

    @Test
    public void testAttributesToJson() {
        Citizen c2 = new Citizen("Nane", true);
        JSONObject json = c2.toJson();
        assertEquals("Nane", json.getString("name"));
        assertTrue(json.getBoolean("isWorking"));
    }

}