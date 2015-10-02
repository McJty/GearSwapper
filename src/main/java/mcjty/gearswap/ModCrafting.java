package mcjty.gearswap;

import cpw.mods.fml.common.registry.GameRegistry;
import mcjty.gearswap.blocks.ModBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ModCrafting {
    public static void init() {
        ItemStack lapisStack = new ItemStack(Items.dye, 1, 4);

        GameRegistry.addRecipe(new ItemStack(ModBlocks.woodenGearSwapperBlock), "pCp", "pcp", "ppp", 'p', Blocks.planks, 'C', Items.comparator, 'c', Blocks.chest);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.ironGearSwapperBlock), "pCp", "pcp", "ppp", 'p', Items.iron_ingot, 'C', Items.comparator, 'c', Blocks.chest);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.lapisGearSwapperBlock), "pCp", "pcp", "ppp", 'p', lapisStack, 'C', Items.comparator, 'c', Blocks.chest);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.stoneGearSwapperBlock), "pCp", "pcp", "ppp", 'p', Blocks.stone, 'C', Items.comparator, 'c', Blocks.chest);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.glassGearSwapperBlock), "pCp", "pcp", "ppp", 'p', Blocks.glass, 'C', Items.comparator, 'c', Blocks.chest);
    }
}
