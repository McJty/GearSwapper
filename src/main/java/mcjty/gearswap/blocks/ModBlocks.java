package mcjty.gearswap.blocks;

import cpw.mods.fml.common.registry.GameRegistry;

public class ModBlocks {
    public static GearSwapperBlock gearSwapperBlock;

    public static void init() {
        gearSwapperBlock = new GearSwapperBlock();
        GameRegistry.registerBlock(gearSwapperBlock, "gearSwapper");
        GameRegistry.registerTileEntity(GearSwapperTE.class, "gearSwapper");
    }
}
