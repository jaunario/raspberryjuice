package net.zhuoweizhang.raspberryjuice;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNotNull;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import pi.Block;
import pi.Item;
import java.nio.charset.StandardCharsets;

public class VersionManagerTest {

    @Test
    public void testVersionManager() {
        // Test block CSV data
        String blockCsvData = "name,id,min_version,max_version,category,group,subgroup,blast_resistance,hardness,water_loggable,renewable,stackable,tool,flammable,luminance\n" +
                              "stone,1,1.0,,building_block,stone,,6,1.5,false,true,true,pickaxe,false,0\n" +
                              "\"quoted,stone\",2,1.0,,building_block,stone,,6,1.5,false,true,true,pickaxe,false,0\n";
        InputStream blockStream = new ByteArrayInputStream(blockCsvData.getBytes(StandardCharsets.UTF_8));

        // Test item CSV data
        String itemCsvData = "name,id,min_version,max_version,category,group,subgroup,stackability,renewable,damage,durability,hunger,saturation\n" +
                             "iron_sword,267,1.0,,weapon,iron,sword,1,true,6,250,0,0\n";
        InputStream itemStream = new ByteArrayInputStream(itemCsvData.getBytes(StandardCharsets.UTF_8));

        VersionManager versionManager = new VersionManager(blockStream, itemStream);

        // Test block data
        BlockData stoneData = versionManager.getBlockData("stone");
        assertEquals(stoneData.id, 1);
        assertEquals(stoneData.blastResistance, 6.0f);
        assertTrue(stoneData.stackable);

        // Test item data
        ItemData ironSwordData = versionManager.getItemData("iron_sword");
        assertEquals(ironSwordData.id, 267);
        assertEquals(ironSwordData.damage, 6);
        assertEquals(ironSwordData.durability, 250);

        // Test ID-based lookup
        BlockData stoneById = versionManager.getBlockData(1);
        assertEquals(stoneById.name, "STONE");

        // Test quoted CSV field
        BlockData quotedStone = versionManager.getBlockData("QUOTED,STONE");
        assertNotNull(quotedStone);
        assertEquals(quotedStone.id, 2);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidBlock() {
        String blockCsvData = "name,id,min_version,max_version,category,group,subgroup,blast_resistance,hardness,water_loggable,renewable,stackable,tool,flammable,luminance\n" +
                              "stone,1,1.0,,building_block,stone,,6,1.5,false,true,true,pickaxe,false,0\n";
        InputStream blockStream = new ByteArrayInputStream(blockCsvData.getBytes(StandardCharsets.UTF_8));
        String itemCsvData = "name,id,min_version,max_version,category,group,subgroup,stackability,renewable,damage,durability,hunger,saturation\n";
        InputStream itemStream = new ByteArrayInputStream(itemCsvData.getBytes(StandardCharsets.UTF_8));
        ApiManager.setVersionManager(new VersionManager(blockStream, itemStream));
        new Block("invalid_block", 0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidItem() {
        String blockCsvData = "name,id,min_version,max_version,category,group,subgroup,blast_resistance,hardness,water_loggable,renewable,stackable,tool,flammable,luminance\n";
        InputStream blockStream = new ByteArrayInputStream(blockCsvData.getBytes(StandardCharsets.UTF_8));
        String itemCsvData = "name,id,min_version,max_version,category,group,subgroup,stackability,renewable,damage,durability,hunger,saturation\n" +
                             "iron_sword,267,1.0,,weapon,iron,sword,1,true,6,250,0,0\n";
        InputStream itemStream = new ByteArrayInputStream(itemCsvData.getBytes(StandardCharsets.UTF_8));
        ApiManager.setVersionManager(new VersionManager(blockStream, itemStream));
        new Item("invalid_item");
    }

    @Test
    public void testGetBlockWithData() {
        // Test block CSV data
        String blockCsvData = "name,id,min_version,max_version,category,group,subgroup,blast_resistance,hardness,water_loggable,renewable,stackable,tool,flammable,luminance\n" +
                              "stone,1,1.0,,building_block,stone,,6,1.5,false,true,true,pickaxe,false,0\n";
        InputStream blockStream = new ByteArrayInputStream(blockCsvData.getBytes(StandardCharsets.UTF_8));
        String itemCsvData = "name,id,min_version,max_version,category,group,subgroup,stackability,renewable,damage,durability,hunger,saturation\n";
        InputStream itemStream = new ByteArrayInputStream(itemCsvData.getBytes(StandardCharsets.UTF_8));
        ApiManager.setVersionManager(new VersionManager(blockStream, itemStream));

        Block block = Block.fromId(1).withData(5);
        assertEquals(block.toString(), "1,5");
    }

    @Test
    public void testMalformedCSV() {
        // Test block CSV data
        String blockCsvData = "name,id,min_version,max_version,category,group,subgroup,blast_resistance,hardness,water_loggable,renewable,stackable,tool,flammable,luminance\n" +
                              "stone,1,1.0,,building_block,stone,,6,1.5,false,true,true,pickaxe,false,0\n" +
                              "malformed_line\n";
        InputStream blockStream = new ByteArrayInputStream(blockCsvData.getBytes(StandardCharsets.UTF_8));
        String itemCsvData = "name,id,min_version,max_version,category,group,subgroup,stackability,renewable,damage,durability,hunger,saturation\n";
        InputStream itemStream = new ByteArrayInputStream(itemCsvData.getBytes(StandardCharsets.UTF_8));
        VersionManager versionManager = new VersionManager(blockStream, itemStream);

        // Test that the valid entries are still loaded
        BlockData stoneData = versionManager.getBlockData("stone");
        assertEquals(stoneData.id, 1);
    }
}
