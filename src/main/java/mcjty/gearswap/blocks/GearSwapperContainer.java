package mcjty.gearswap.blocks;

import com.google.common.collect.Range;
import mcjty.gearswap.items.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class GearSwapperContainer extends Container {
    private IInventory playerInventory;
    private GearSwapperTE gearInventory;

    public GearSwapperContainer(EntityPlayer player, GearSwapperTE gearSwapperTE) {
        playerInventory = player.inventory;
        gearInventory = gearSwapperTE;

        int index = 0;
        addSlotToContainer(new GhostSlot(gearInventory, index++, 46, 8));
        addSlotToContainer(new GhostSlot(gearInventory, index++, 64, 47));
        addSlotToContainer(new GhostSlot(gearInventory, index++, 46, 104));
        addSlotToContainer(new GhostSlot(gearInventory, index++, 64, 143));

        for (int i = 0 ; i < 4 ; i++) {
            int x = 87;
            int y = 26 + i * 39;
            for (int h = 0 ; h < 9 ; h++) {
                addSlotToContainer(new GhostSlot(gearInventory, index++, x, y));
                x += 18;
            }
            x = 177;
            for (int a = 0 ; a < 4 ; a++) {
                addSlotToContainer(new GhostSlot(gearInventory, index++, x, y-18));
                x += 18;
            }
        }

        for (int y = 0 ; y < 4 ; y++) {
            for (int x = 0 ; x < 4 ; x++) {
                addSlotToContainer(new Slot(gearInventory, index++, 10 + x*18, 169 + y*18));
            }
        }

        index = 0;
        // Hotbar slots
        for (int i = 0 ; i < 9 ; i++) {
            addSlotToContainer(new Slot(playerInventory, index++, 87 + i*18, 227));
        }
        // Inventory slots
        for (int j = 0 ; j < 3 ; j++) {
            for (int i = 0; i < 9; i++) {
                addSlotToContainer(new Slot(playerInventory, index++, 87 + i*18, 169 + j*18));
            }
        }
        // Armor slots
        for (int i = 0 ; i < 4 ; i++) {
            addSlotToContainer(new Slot(playerInventory, index++, 10, 143 - i*18));
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
//        Slot slot = (Slot)this.inventorySlots.get(index);
//
//        if (slot != null && slot.getHasStack()) {
//            ItemStack origStack = slot.getStack();
//            itemstack = origStack.copy();
//
//            if (gearInventory.isGhostSlot(index)) {
//                slot.onSlotChange(origStack, itemstack);
//            } else if (factory.isGhostSlot(index) || factory.isGhostOutputSlot(index)) {
//                return null; // @@@ Right?
//            } else if (factory.isPlayerInventorySlot(index)) {
//                if (!mergeItemStacks(origStack, SlotType.SLOT_SPECIFICITEM, false)) {
//                    if (!mergeItemStacks(origStack, SlotType.SLOT_INPUT, false)) {
//                        if (!mergeItemStacks(origStack, SlotType.SLOT_PLAYERHOTBAR, false)) {
//                            return null;
//                        }
//                    }
//                }
//            } else if (factory.isPlayerHotbarSlot(index)) {
//                if (!mergeItemStacks(origStack, SlotType.SLOT_SPECIFICITEM, false)) {
//                    if (!mergeItemStacks(origStack, SlotType.SLOT_INPUT, false)) {
//                        if (!mergeItemStacks(origStack, SlotType.SLOT_PLAYERINV, false)) {
//                            return null;
//                        }
//                    }
//                }
//            } else {
//                Logging.log("Weird slot at index: " + index);
//            }
//
//            if (origStack.stackSize == 0) {
//                slot.putStack(null);
//            } else {
//                slot.onSlotChanged();
//            }
//
//            if (origStack.stackSize == itemstack.stackSize) {
//                return null;
//            }
//
//            slot.onPickupFromSlot(player, origStack);
//        }
//
        return itemstack;
    }


    @Override
    public ItemStack slotClick(int index, int button, int mode, EntityPlayer player) {
        if (gearInventory.isGhostSlot(index)) {
            Slot slot = getSlot(index);
            if (slot.getHasStack()) {
                slot.putStack(null);
            } else if (!gearInventory.isIconSlot(index) && player.inventory.getItemStack() == null) {
                slot.putStack(new ItemStack(ModItems.forceEmptyItem));
                return null;
            }
        }
        return super.slotClick(index, button, mode, player);
    }
}
