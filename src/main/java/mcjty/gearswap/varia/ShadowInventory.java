package mcjty.gearswap.varia;

import codechicken.lib.inventory.InventoryUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * This code is copied from EnderIO. Thanks!
 * It is used to solve some problems with the baubles api (the
 * inventory misbehaving).
 */
public class ShadowInventory implements IInventory {
    private final ItemStack[] items;
    private final IInventory master;

    public ShadowInventory(IInventory master) {
        this.master = master;
        items = new ItemStack[master.getSizeInventory()];
        for (int i = 0; i < master.getSizeInventory(); i++) {
            items[i] = master.getStackInSlot(i);
        }
    }

    @Override
    public int getSizeInventory() {
        return master.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return items[index];
    }

    @Override
    public ItemStack decrStackSize(int index, int amount) {
        return InventoryUtils.decrStackSize(this, index, amount);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return InventoryUtils.getStackInSlotOnClosing(this, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        items[index] = stack;
    }

    @Override
    public String getInventoryName() {
        return master.getInventoryName();
    }

    @Override
    public boolean hasCustomInventoryName() {
        return master.hasCustomInventoryName();
    }

    @Override
    public int getInventoryStackLimit() {
        return master.getInventoryStackLimit();
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return master.isUseableByPlayer(player);
    }

    @Override
    public void openInventory() {
        master.openInventory();
    }

    @Override
    public void closeInventory() {
        master.closeInventory();
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return master.isItemValidForSlot(index, stack);
    }

}