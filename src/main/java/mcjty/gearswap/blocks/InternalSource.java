package mcjty.gearswap.blocks;

import net.minecraft.item.ItemStack;

class InternalSource implements Source {
    private GearSwapperTE gearSwapperTE;

    public InternalSource(GearSwapperTE gearSwapperTE) {
        this.gearSwapperTE = gearSwapperTE;
    }

    @Override
    public int getStackCount() {
        return 16;
    }

    @Override
    public ItemStack getStack(int index) {
        return gearSwapperTE.getStackInSlot(index + GearSwapperTE.SLOT_BUFFER);
    }

    @Override
    public ItemStack extractAmount(int index, int amount) {
        ItemStack current = gearSwapperTE.getStackInSlot(index + GearSwapperTE.SLOT_BUFFER);
        if (amount < current.stackSize) {
            current = gearSwapperTE.decrStackSize(index + GearSwapperTE.SLOT_BUFFER, amount);
        } else {
            gearSwapperTE.setInventorySlotContents(index + GearSwapperTE.SLOT_BUFFER, null);
        }
        return current;
    }
}
