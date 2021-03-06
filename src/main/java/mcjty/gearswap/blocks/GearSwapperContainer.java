package mcjty.gearswap.blocks;

import mcjty.gearswap.items.ModItems;
import mcjty.gearswap.varia.ShadowInventory;
import mcjty.gearswap.varia.Tools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class GearSwapperContainer extends Container {
    private IInventory playerInventory;
    private GearSwapperTE gearInventory;
    private IInventory baublesInventory;

    public GearSwapperContainer(EntityPlayer player, GearSwapperTE gearSwapperTE) {
        playerInventory = player.inventory;
        gearInventory = gearSwapperTE;

        baublesInventory = Tools.getBaubles(player);
        if (baublesInventory != null && Tools.WhoAmI.whoAmI(player.worldObj) == Tools.WhoAmI.SPCLIENT) {
            baublesInventory = new ShadowInventory(baublesInventory);
        }

        int index = 0;
        addSlotToContainer(new GhostSlot(gearInventory, index++, 48, 6, GhostSlot.ANY));
        addSlotToContainer(new GhostSlot(gearInventory, index++, 66, 45, GhostSlot.ANY));
        addSlotToContainer(new GhostSlot(gearInventory, index++, 48, 102, GhostSlot.ANY));
        addSlotToContainer(new GhostSlot(gearInventory, index++, 66, 141, GhostSlot.ANY));

        for (int i = 0 ; i < 4 ; i++) {
            int x = 87;
            int y = 24 + i * 39;
            for (int h = 0 ; h < 9 ; h++) {
                addSlotToContainer(new GhostSlot(gearInventory, index++, x, y, GhostSlot.ANY));
                x += 18;
            }
            x = 177;
            for (int a = 0 ; a < 4 ; a++) {
                addSlotToContainer(new GhostSlot(gearInventory, index++, x, y-18, GhostSlot.ARMOR_BOOTS - a));
                x += 18;
            }
        }

        for (int y = 0 ; y < 4 ; y++) {
            for (int x = 0 ; x < 4 ; x++) {
                addSlotToContainer(new Slot(gearInventory, index++, 10 + x*18, 167 + y*18));
            }
        }

        // The optional baubles ghost slots:
        if (baublesInventory != null) {
            for (int i = 0 ; i < 4 ; i++) {
                int x = 87;
                int y = 6 + i * 39;
                addSlotToContainer(new GhostSlot(gearInventory, index++, x, y, GhostSlot.BAUBLE_AMULET)); x += 18;
                addSlotToContainer(new GhostSlot(gearInventory, index++, x, y, GhostSlot.BAUBLE_RING)); x += 18;
                addSlotToContainer(new GhostSlot(gearInventory, index++, x, y, GhostSlot.BAUBLE_RING)); x += 18;
                addSlotToContainer(new GhostSlot(gearInventory, index++, x, y, GhostSlot.BAUBLE_BELT)); x += 18;
            }
        }

        index = 0;
        // Hotbar slots
        for (int i = 0 ; i < 9 ; i++) {
            addSlotToContainer(new Slot(playerInventory, index++, 87 + i*18, 225));
        }
        // Inventory slots
        for (int j = 0 ; j < 3 ; j++) {
            for (int i = 0; i < 9; i++) {
                addSlotToContainer(new Slot(playerInventory, index++, 87 + i*18, 167 + j*18));
            }
        }
        // Armor slots
        for (int i = 0 ; i < 4 ; i++) {
            addSlotToContainer(new Slot(playerInventory, index++, 10, 141 - i*18));
        }

        // Baubles
        if (baublesInventory != null) {
            index = 0;
            for (int i = 0 ; i < 4 ; i++) {
                addSlotToContainer(new Slot(baublesInventory, index++, 28, 87 + i * 18) {
                    @Override
                    public boolean isItemValid(ItemStack stack) {
                        return inventory.isItemValidForSlot(getSlotIndex(), stack);
                    }
                });
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        if (!playerInventory.isUseableByPlayer(player)) {
            return false;
        }
        if (!gearInventory.isUseableByPlayer(player)) {
            return false;
        }
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack origStack = slot.getStack();
            itemstack = origStack.copy();

            if (gearInventory.isGhostSlot(index)) {
                return null;
            } else if (gearInventory.isBufferSlot(index)) {
                if (!this.mergeItemStack(origStack, GearSwapperTE.SLOT_BUFFER + 16, this.inventorySlots.size(), true)) {
                    return null;
                }
            } else if (!this.mergeItemStack(origStack, GearSwapperTE.SLOT_BUFFER, GearSwapperTE.SLOT_BUFFER + 16, false)) {
                return null;
            }

            if (origStack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }


    @Override
    public ItemStack slotClick(int index, int button, int mode, EntityPlayer player) {
        if (gearInventory.isGhostSlot(index)) {
            Slot slot = getSlot(index);     // Index of slot matches index in gear inventory!
            if (slot.getHasStack()) {
                slot.putStack(null);
            } else if (!gearInventory.isIconSlot(index) && player.inventory.getItemStack() == null) {
                slot.putStack(new ItemStack(ModItems.forceEmptyItem));
                return null;
            } else {
                slot.putStack(player.inventory.getItemStack());
            }
            return null;
        }
        return super.slotClick(index, button, mode, player);
    }
}
