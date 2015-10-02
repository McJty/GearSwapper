package mcjty.gearswap.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class GhostSlot extends Slot {

    public GhostSlot(IInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canTakeStack(EntityPlayer player) {
        return false;
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        return null;
    }

    @Override
    public int getSlotStackLimit() {
        return 0;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return true;
    }

    @Override
    public void putStack(ItemStack stack) {
//        if (stack != null) {
//            stack.stackSize = 1;
//        }
        inventory.setInventorySlotContents(getSlotIndex(), stack);
        onSlotChanged();
    }
}
