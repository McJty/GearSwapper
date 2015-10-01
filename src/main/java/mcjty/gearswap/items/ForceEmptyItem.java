package mcjty.gearswap.items;

import cpw.mods.fml.common.registry.GameRegistry;
import mcjty.gearswap.GearSwap;
import net.minecraft.item.Item;

public class ForceEmptyItem extends Item {
    public ForceEmptyItem() {
        setMaxStackSize(1);
        setUnlocalizedName("emptyItem");
        setTextureName(GearSwap.MODID + ":emptyItem");
        GameRegistry.registerItem(this, "emptyItem");
    }
}
