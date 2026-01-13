package net.zhuoweizhang.raspberryjuice;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class VersionManagerTest {

    @Test
    public void testVersionManager() {
        // Test CSV data
        String csvData = "name,id,min_version,max_version\n" +
                         "stone,1,1.0,\n" +
                         "end_rod,196,1.9,\n" +
                         "magma,211,1.10,1.12\n";
        InputStream stream = new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));
        VersionManager versionManager = new VersionManager(stream);

        // Test block availability
        assertTrue(versionManager.isBlockAvailable("stone", "1.8"));
        assertFalse(versionManager.isBlockAvailable("end_rod", "1.8"));
        assertTrue(versionManager.isBlockAvailable("end_rod", "1.9"));
        assertTrue(versionManager.isBlockAvailable("magma", "1.11"));
        assertFalse(versionManager.isBlockAvailable("magma", "1.13"));

        // Test block IDs
        assertEquals(versionManager.getBlockId("stone"), 1);
        assertEquals(versionManager.getBlockId("end_rod"), 196);
        assertEquals(versionManager.getBlockId("magma"), 211);
        assertEquals(versionManager.getBlockId("non_existent_block"), -1);
    }

    @Test
    public void testMalformedCSV() {
        // Test CSV data with a malformed line
        String csvData = "name,id,min_version,max_version\n" +
                         "stone,1,1.0,\n" +
                         "corrupted_entry\n" +
                         "end_rod,196,1.9,\n";
        InputStream stream = new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));
        VersionManager versionManager = new VersionManager(stream);

        // Test that the valid entries are still loaded
        assertTrue(versionManager.isBlockAvailable("stone", "1.8"));
        assertTrue(versionManager.isBlockAvailable("end_rod", "1.9"));
        assertEquals(versionManager.getBlockId("stone"), 1);
        assertEquals(versionManager.getBlockId("end_rod"), 196);

        // Test that the malformed entry is not loaded
        assertFalse(versionManager.isBlockAvailable("corrupted_entry", "1.0"));
        assertEquals(versionManager.getBlockId("corrupted_entry"), -1);
    }

    @Test
    public void testServerVersionParsing() {
        String csvData = "name,id,min_version,max_version\n" +
                         "stone,1,1.0,\n" +
                         "end_rod,196,1.9,\n";
        InputStream stream = new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));
        VersionManager versionManager = new VersionManager(stream);

        assertTrue(versionManager.isBlockAvailable("stone", "git-Spigot-b1e6da2-f2334c4 (MC: 1.15.2)"));
        assertFalse(versionManager.isBlockAvailable("end_rod", "git-Spigot-b1e6da2-f2334c4 (MC: 1.8.8)"));
    }
}
