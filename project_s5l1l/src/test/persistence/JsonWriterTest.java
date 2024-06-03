package persistence;

import model.Village;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class JsonWriterTest extends JsonTest {

    @Test
    public void testIllegalFileName() {
        try {
            JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail();
        } catch (IOException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testEmptyVillage() {
        Village village = new Village();
        village.getCitizens().clear();
        village.getBuildings().clear();
        village.changeFood(-village.getTotalFood());
        village.changeStone(-village.getTotalStone());
        village.changeWood(-village.getTotalWood());
        try {
            JsonWriter writer = new JsonWriter("./data/testWriterEmptyVillage.json");
            writer.open();
            writer.write(village);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterEmptyVillage.json");
            village = reader.read();

            assertTrue(village.getCitizens().isEmpty());
            assertTrue(village.getBuildings().isEmpty());
            checkVillageResources(0, 0, 0, village);

        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void testWriteStandardVillage() {
        Village village = new Village();
        try {
            JsonWriter writer = new JsonWriter("./data/testWriterGeneralVillage.json");
            writer.open();
            writer.write(village);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterGeneralVillage.json");
            village = reader.read();

            assertEquals(6, village.getCitizens().size());
            assertEquals(3, village.getBuildings().size());
            checkVillageResources(15, 20, 15, village);
            checkCitizen("Founder", true, village.getCitizens().get(0));
            checkCitizen("Founder", false, village.getCitizens().get(3));
            checkBuilding("Farm 1", "Farm", 5, village.getBuildings().get(0));

        } catch (IOException e) {
            fail();
        }
    }

}
