package mcjty.gearswap;

import cpw.mods.fml.common.registry.GameRegistry;
import mcjty.gearswap.blocks.ModBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ModCrafting {
    public static void init() {
        GameRegistry.addRecipe(new ItemStack(ModBlocks.gearSwapperBlock), "pCp", "pcp", "ppp", 'p', Blocks.planks, 'C', Items.comparator, 'c', Blocks.chest);
    }
}
