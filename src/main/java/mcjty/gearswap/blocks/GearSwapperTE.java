package mcjty.gearswap.blocks;

import mcjty.gearswap.items.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class GearSwapperTE extends TileEntity implements IInventory {

    // First 4 slots are the ghost slots for the front icons
    // Next there are 4 times 9+4 slots for the remembered states.
    // Finally there are 16 slots general inventory.
    private ItemStack stacks[] = new ItemStack[4 + 4*(9+4) + 16];

    public static final int SLOT_GHOST = 4;
    public static final int SLOT_BUFFER = SLOT_GHOST + 4*(9+4);


    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        readRestorableFromNBT(tagCompound);
    }

    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        readBufferFromNBT(tagCompound);
    }

    private void readBufferFromNBT(NBTTagCompound tagCompound) {
        NBTTagList bufferTagList = tagCompound.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0 ; i < bufferTagList.tagCount() ; i++) {
            NBTTagCompound nbtTagCompound = bufferTagList.getCompoundTagAt(i);
            setStackInSlot(i, ItemStack.loadItemStackFromNBT(nbtTagCompound));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        writeRestorableToNBT(tagCompound);
    }

    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        writeBufferToNBT(tagCompound);
    }

    private void writeBufferToNBT(NBTTagCompound tagCompound) {
        NBTTagList bufferTagList = new NBTTagList();
        for (int i = 0 ; i < stacks.length ; i++) {
            ItemStack stack = getStackInSlot(i);
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            if (stack != null) {
                stack.writeToNBT(nbtTagCompound);
            }
            bufferTagList.appendTag(nbtTagCompound);
        }
        tagCompound.setTag("Items", bufferTagList);
    }



    public void setFaceIconSlot(int index, ItemStack stack) {
        setInventorySlotContents(index, stack);
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    private int getInventoryIndex(int i) {
        if (i < 9) {
            return i;
        } else {
            return (i-9) + 36;
        }
    }

    private int getInternalInventoryIndex(int index, int i) {
        return 4 + index * (9+4) + i;
    }

    public void rememberSetup(int index, EntityPlayer player) {
        setFaceIconSlot(index, player.getHeldItem());

        InventoryPlayer inventory = player.inventory;
        for (int i = 0 ; i < 9 + 4 ; i++) {
            int inventoryIndex = getInventoryIndex(i);
            int internalInventoryIndex = getInternalInventoryIndex(index, i);
            ItemStack stack = inventory.getStackInSlot(inventoryIndex);
            setInventorySlotContents(internalInventoryIndex, stack);
        }
    }

    public void restoreSetup(int index, EntityPlayer player) {
        InventoryPlayer inventory = player.inventory;

        // Set aside the current hotbar + armor slots.
        ItemStack[] currentStacks = new ItemStack[9+4];
        for (int i = 0 ; i < 9+4 ; i++) {
            int inventoryIndex = getInventoryIndex(i);
            currentStacks[i] = inventory.getStackInSlot(inventoryIndex);
            inventory.setInventorySlotContents(inventoryIndex, null);
        }

        // Find stacks in all possible sources to replace the current selection
        for (int i = 0 ; i < 9+4 ; i++) {
            int internalInventoryIndex = getInternalInventoryIndex(index, i);
            ItemStack desiredStack = getStackInSlot(internalInventoryIndex);
            if (desiredStack == null || desiredStack.getItem() == ModItems.forceEmptyItem) {
                // Either we don't have specific needs for this slot or we want it to be cleared.
                // In both cases we keep the slot empty here.
            } else {
                ItemStack foundStack = findBestMatchingStack(desiredStack, currentStacks, inventory);
                // Can be that we didn't find anything. In any csae, we simply put whatever we found in the slot.
                int playerIndex = getInventoryIndex(i);
                inventory.setInventorySlotContents(playerIndex, foundStack);
            }
        }

        // First we check all slots that we don't need to be cleared and we put back the item
        // from currentStacks if that's still there. In all other slots we temporarily set
        // our dummy item so that we don't accidently overwrite that in the next step.
        for (int i = 0 ; i < 9+4 ; i++) {
            int playerIndex = getInventoryIndex(i);
            if (inventory.getStackInSlot(playerIndex) == null) {
                if (currentStacks[i] != null) {
                    int internalInventoryIndex = getInternalInventoryIndex(index, i);
                    ItemStack desiredStack = getStackInSlot(internalInventoryIndex);
                    // First check if we don't want to force the slot to be empty
                    if (desiredStack == null || desiredStack.getItem() != ModItems.forceEmptyItem) {
                        inventory.setInventorySlotContents(playerIndex, currentStacks[i]);
                        currentStacks[i] = null;
                    } else {
                        inventory.setInventorySlotContents(playerIndex, new ItemStack(ModItems.forceEmptyItem));
                    }
                } else {
                    inventory.setInventorySlotContents(playerIndex, new ItemStack(ModItems.forceEmptyItem));
                }
            }
        }

        // All items that we didn't find a place for we need to put in the inventory
        // somewhere.
        for (int i = 0 ; i < 9+4 ; i++) {
            if (currentStacks[i] != null) {
                if (inventory.addItemStackToInventory(currentStacks[i])) {
                    currentStacks[i] = null;
                }
            }
        }

        // Now we clear the dummy items from our slots.
        for (int i = 0 ; i < 9+4 ; i++) {
            int playerIndex = getInventoryIndex(i);
            if (inventory.getStackInSlot(playerIndex) != null && inventory.getStackInSlot(playerIndex).getItem() == ModItems.forceEmptyItem) {
                inventory.setInventorySlotContents(playerIndex, null);
            }
        }

        // Now it is possible that some of the items we couldn't place back because the slots in the hotbar
        // were locked. Now that they are unlocked we can try again.
        for (int i = 0 ; i < 9+4 ; i++) {
            if (currentStacks[i] != null) {
                if (inventory.addItemStackToInventory(currentStacks[i])) {
                    currentStacks[i] = null;
                }
            }
        }

        // Finally it is possible that some items could not be placed anywhere.
        // @todo: spawn the remaining items in the world

        markDirty();
        player.openContainer.detectAndSendChanges();
    }

    private ItemStack findBestMatchingStack(ItemStack desired, ItemStack[] currentStacks, InventoryPlayer inventoryPlayer) {
        for (int i = 0 ; i < currentStacks.length ; i++) {
            ItemStack current = currentStacks[i];
            if (current != null && current.isItemEqual(desired)) {
                currentStacks[i] = null;
                return current;
            }
        }

        // We don't look in the armor or hotbar because we don't want to remove something
        // there that we just placed.
        for (int i = 9 ; i < 9*4 ; i++) {
            ItemStack current = inventoryPlayer.getStackInSlot(i);
            if (current != null && current.isItemEqual(desired)) {
                inventoryPlayer.setInventorySlotContents(i, null);
                return current;
            }
        }

        // Check our own internal inventory.
        for (int i = SLOT_BUFFER ; i < SLOT_BUFFER + 16 ; i++) {
            ItemStack current = getStackInSlot(i);
            if (current != null && current.isItemEqual(desired)) {
                setInventorySlotContents(i, null);
                return current;
            }
        }

        // @todo: check external inventory

        return null;
    }


    private ItemStack findAndRemoveItemStack(ItemStack desired, InventoryPlayer inventory) {
        for (int i = 0 ; i < 9*4 + 4 ; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack != null && desired.isItemEqual(stack)) {
                inventory.setInventorySlotContents(i, null);
                return stack;
            }
        }
        return null;
    }


    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        readFromNBT(packet.func_148857_g());
    }

    // IInventory

    @Override
    public int getSizeInventory() {
        return stacks.length;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        if (index >= stacks.length) {
            return null;
        }

        return stacks[index];
    }

    public boolean isGhostSlot(int index) {
        return index >= 0 && index < (4 + 4 * (9+4));
    }

    public boolean isIconSlot(int index) {
        return index >= 0 && index < 4;
    }

    @Override
    public ItemStack decrStackSize(int index, int amount) {
        if (index >= stacks.length) {
            return null;
        }

        if (isGhostSlot(index)) {
            ItemStack old = stacks[index];
            stacks[index] = null;
            if (old == null) {
                return null;
            }
            old.stackSize = 0;
            return old;
        } else {
            if (stacks[index] != null) {
                if (stacks[index].stackSize <= amount) {
                    ItemStack old = stacks[index];
                    stacks[index] = null;
                    markDirty();
                    return old;
                }
                ItemStack its = stacks[index].splitStack(amount);
                if (stacks[index].stackSize == 0) {
                    stacks[index] = null;
                }
                markDirty();
                return its;
            }
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return null;
    }

    public void setStackInSlot(int index, ItemStack stack) {
        if (index >= stacks.length) {
            return;
        }
        stacks[index] = stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index >= stacks.length) {
            return;
        }

        if (isGhostSlot(index)) {
            if (stack != null) {
                stacks[index] = stack.copy();
            } else {
                stacks[index] = null;
            }
        } else {
            stacks[index] = stack;
            if (stack != null && stack.stackSize > getInventoryStackLimit()) {
                stack.stackSize = getInventoryStackLimit();
            }
        }
        markDirty();
    }

    @Override
    public String getInventoryName() {
        return "Gear Swapper";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return !isGhostSlot(index);
    }
}
