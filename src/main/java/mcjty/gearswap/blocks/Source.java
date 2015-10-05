package mcjty.gearswap.blocks;

import net.minecraft.item.ItemStack;

/**
 * This interface represents a source for items as used by the gear swapper.
 */
public interface Source {
    int getStackCount();

    ItemStack getStack(int index);

    ItemStack extractAmount(int index, int amount);
}
