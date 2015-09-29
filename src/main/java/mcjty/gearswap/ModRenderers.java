package mcjty.gearswap;

import cpw.mods.fml.client.registry.ClientRegistry;
import mcjty.gearswap.blocks.GearSwapperTE;
import mcjty.gearswap.blocks.GearSwapperTESR;

public final class ModRenderers {

    public static void init() {
        ClientRegistry.bindTileEntitySpecialRenderer(GearSwapperTE.class, new GearSwapperTESR());
    }
}
