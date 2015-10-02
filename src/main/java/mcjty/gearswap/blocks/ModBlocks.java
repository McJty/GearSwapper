package mcjty.gearswap.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import mcjty.gearswap.Config;
import net.minecraft.block.material.Material;

public class ModBlocks {
    public static GearSwapperBlock woodenGearSwapperBlock;
    public static GearSwapperBlock ironGearSwapperBlock;
    public static GearSwapperBlock lapisGearSwapperBlock;
    public static GearSwapperBlock stoneGearSwapperBlock;
    public static GearSwapperBlock moddedGearSwapperBlock;
    public static GearSwapperGlassBlock glassGearSwapperBlock;

    public static void init() {
        woodenGearSwapperBlock = new GearSwapperBlock(Material.wood, "minecraft:planks_oak", "gearSwapperWood");
        GameRegistry.registerBlock(woodenGearSwapperBlock, "gearSwapperWood");

        ironGearSwapperBlock = new GearSwapperBlock(Material.iron, "minecraft:iron_block", "gearSwapperIron");
        GameRegistry.registerBlock(ironGearSwapperBlock, "gearSwapperIron");

        lapisGearSwapperBlock = new GearSwapperBlock(Material.rock, "minecraft:lapis_block", "gearSwapperLapis");
        GameRegistry.registerBlock(lapisGearSwapperBlock, "gearSwapperLapis");

        stoneGearSwapperBlock = new GearSwapperBlock(Material.rock, "minecraft:stone", "gearSwapperStone");
        GameRegistry.registerBlock(stoneGearSwapperBlock, "gearSwapperStone");

        glassGearSwapperBlock = new GearSwapperGlassBlock(Material.glass, "minecraft:glass", "gearSwapperGlass");
        GameRegistry.registerBlock(glassGearSwapperBlock, "gearSwapperGlass");

        if (!Config.moddedTextureName.isEmpty()) {
            moddedGearSwapperBlock = new GearSwapperBlock(Material.rock, Config.moddedTextureName, "gearSwapperModded");
            GameRegistry.registerBlock(moddedGearSwapperBlock, "gearSwapperModded");
        }

        GameRegistry.registerTileEntity(GearSwapperTE.class, "gearSwapper");
    }
}
