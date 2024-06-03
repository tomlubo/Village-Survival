package persistence;

import model.Village;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class JsonReaderTest extends JsonTest {


    @Test
    public void testNoFile() {
        JsonReader reader = new JsonReader("none");
        try {
            Village village = reader.read();
            fail("expected IOException");
        } catch (IOException e) {

        }
    }

    @Test
    public void testEmptyVillage() {
        JsonReader reader = new JsonReader("./data/EmptyVillage.json");
        try {
            Village village = reader.read();
            assertTrue(village.getCitizens().isEmpty());
            assertTrue(village.getBuildings().isEmpty());
            checkVillageResources(0, 0, 0, village);
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void testStandardVillage() {
        JsonReader reader = new JsonReader("./data/StandardVillage.json");
        try {
            Village village = reader.read();
            assertEquals(2, village.getCitizens().size());
            assertEquals(1, village.getBuildings().size());
            checkVillageResources(20, 20, 20, village);
            checkCitizen("Tommy", true, village.getCitizens().get(0));
            checkCitizen("Nane", false, village.getCitizens().get(1));
            checkBuilding("Farm 1", "FARM", 5, village.getBuildings().get(0));
        } catch (IOException e) {
            fail();
        }
    }

}
