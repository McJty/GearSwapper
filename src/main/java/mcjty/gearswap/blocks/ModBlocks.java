package mcjty.gearswap.blocks;

import cpw.mods.fml.common.registry.GameRegistry;

public class ModBlocks {
    public static GearSwapper gearSwapper;

    public static void init() {
        gearSwapper = new GearSwapper();
        GameRegistry.registerBlock(gearSwapper, "gearSwapper");
        GameRegistry.registerTileEntity(GearSwapperTE.class, "gearSwapper");
    }
}
