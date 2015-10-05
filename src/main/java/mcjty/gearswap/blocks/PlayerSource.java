package mcjty.gearswap.blocks;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

class PlayerSource implements Source {
    private final InventoryPlayer inventoryPlayer;

    public PlayerSource(InventoryPlayer inventoryPlayer) {
        this.inventoryPlayer = inventoryPlayer;
    }

    @Override
    public int getStackCount() {
        // We don't look in the armor or hotbar because we don't want to remove something
        // there that we just placed.
        return 9 * 4 - 9;
    }

    @Override
    public ItemStack getStack(int index) {
        return inventoryPlayer.getStackInSlot(index + 9);
    }

    @Override
    public ItemStack extractAmount(int index, int amount) {
        ItemStack current = inventoryPlayer.getStackInSlot(index + 9);
        if (amount < current.stackSize) {
            current = inventoryPlayer.decrStackSize(index + 9, amount);
        } else {
            inventoryPlayer.setInventorySlotContents(index + 9, null);
        }
        return current;
    }
}
