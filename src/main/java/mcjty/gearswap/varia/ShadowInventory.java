package mcjty.gearswap.varia;

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
        return decrStackSize(this, index, amount);
    }

    public static ItemStack decrStackSize(IInventory inv, int slot, int size) {
        ItemStack item = inv.getStackInSlot(slot);
        if(item != null) {
            if(item.stackSize <= size) {
                inv.setInventorySlotContents(slot, null);
                inv.markDirty();
                return item;
            } else {
                ItemStack itemstack1 = item.splitStack(size);
                if(item.stackSize == 0) {
                    inv.setInventorySlotContents(slot, null);
                } else {
                    inv.setInventorySlotContents(slot, item);
                }

                inv.markDirty();
                return itemstack1;
            }
        } else {
            return null;
        }
    }



    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        ItemStack stack = getStackInSlot(index);
        setInventorySlotContents(index, (ItemStack) null);
        return stack;
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