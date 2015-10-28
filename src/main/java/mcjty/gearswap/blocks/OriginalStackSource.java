package mcjty.gearswap.blocks;

import net.minecraft.item.ItemStack;

class OriginalStackSource implements Source {
    private final ItemStack[] currentStacks;

    public OriginalStackSource(ItemStack[] currentStacks) {
        this.currentStacks = currentStacks;
    }

    @Override
    public int getStackCount() {
        return currentStacks.length;
    }

    @Override
    public ItemStack getStack(int index) {
        return currentStacks[index];
    }

    @Override
    public ItemStack extractAmount(int index, int amount) {
        ItemStack current = currentStacks[index];
        if (amount < current.stackSize) {
            current = current.copy();
            currentStacks[index].stackSize -= amount;
            current.stackSize = amount;
        } else {
            currentStacks[index] = null;
        }
        return current;
    }
}
