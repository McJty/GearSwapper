package mcjty.gearswap.varia;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;

import java.util.Map;

public class InventoryHelper {
    /**
     * Merges provided ItemStack with the first available one in this inventory. It will return the amount
     * of items that could not be merged. Also fills the undo buffer in case you want to undo the operation.
     * This version also checks for ISidedInventory if that's implemented by the inventory
     */
    public static int mergeItemStackSafe(IInventory inventory, int side, ItemStack result, int start, int stop, Map<Integer,ItemStack> undo) {
        if (inventory instanceof ISidedInventory) {
            return mergeItemStackInternal(inventory, (ISidedInventory) inventory, side, result, start, stop, undo);
        } else {
            return mergeItemStackInternal(inventory, null, side, result, start, stop, undo);
        }
    }

    /**
     * Merges provided ItemStack with the first available one in this inventory. It will return the amount
     * of items that could not be merged. Also fills the undo buffer in case you want to undo the operation.
     */
    public static int mergeItemStack(IInventory inventory, ItemStack result, int start, int stop, Map<Integer,ItemStack> undo) {
        return mergeItemStackInternal(inventory, null, 0, result, start, stop, undo);
    }

    private static int mergeItemStackInternal(IInventory inventory, ISidedInventory sidedInventory, int side, ItemStack result, int start, int stop, Map<Integer,ItemStack> undo) {
        int k = start;

        ItemStack itemstack1;
        int itemsToPlace = result.stackSize;

        if (result.isStackable()) {
            while (itemsToPlace > 0 && (k < stop)) {
                itemstack1 = inventory.getStackInSlot(k);

                if (isItemStackConsideredEqual(result, itemstack1) && (sidedInventory == null || sidedInventory.canInsertItem(k, result, side))) {
                    int l = itemstack1.stackSize + itemsToPlace;

                    if (l <= result.getMaxStackSize()) {
                        if (undo != null) {
                            // Only put on undo map if the key is not already present.
                            if (!undo.containsKey(k)) {
                                undo.put(k, itemstack1.copy());
                            }
                        }
                        itemsToPlace = 0;
                        itemstack1.stackSize = l;
                        inventory.markDirty();
                    } else if (itemstack1.stackSize < result.getMaxStackSize()) {
                        if (undo != null) {
                            if (!undo.containsKey(k)) {
                                undo.put(k, itemstack1.copy());
                            }
                        }
                        itemsToPlace -= result.getMaxStackSize() - itemstack1.stackSize;
                        itemstack1.stackSize = result.getMaxStackSize();
                        inventory.markDirty();
                    }
                }

                ++k;
            }
        }

        if (itemsToPlace > 0) {
            k = start;

            while (k < stop) {
                itemstack1 = inventory.getStackInSlot(k);

                if (itemstack1 == null && (sidedInventory == null || sidedInventory.canInsertItem(k, result, side))) {
                    if (undo != null) {
                        if (!undo.containsKey(k)) {
                            undo.put(k, null);
                        }
                    }
                    ItemStack copy = result.copy();
                    copy.stackSize = itemsToPlace;
                    inventory.setInventorySlotContents(k, copy);
                    inventory.markDirty();
                    itemsToPlace = 0;
                    break;
                }

                ++k;
            }
        }

        return itemsToPlace;
    }

    private static boolean isItemStackConsideredEqual(ItemStack result, ItemStack itemstack1) {
        return itemstack1 != null && itemstack1.getItem() == result.getItem() && (!result.getHasSubtypes() || result.getItemDamage() == itemstack1.getItemDamage()) && ItemStack.areItemStackTagsEqual(result, itemstack1);
    }

    public static void compactStacks(ItemStack[] stacks, int start, int max) {
        InventoryBasic inv = new InventoryBasic("temp", true, max);
        for (int i = 0 ; i < max ; i++) {
            ItemStack stack = stacks[i+start];
            if (stack != null) {
                mergeItemStack(inv, stack, 0, max, null);
            }
        }
        for (int i = 0 ; i < max ; i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null && stack.stackSize == 0) {
                stack = null;
            }
            stacks[i+start] = stack;
        }
    }
}
