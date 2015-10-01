package mcjty.gearswap.blocks;

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
        ItemStack dummy = new ItemStack(Blocks.bedrock);
        for (int i = 0 ; i < 9+4 ; i++) {
            int inventoryIndex = getInventoryIndex(i);
            ItemStack currentStack = inventory.getStackInSlot(inventoryIndex);
            if (currentStack == null) {
                inventory.setInventorySlotContents(inventoryIndex, dummy);
            }
        }

        List<ItemStack> failedStacks = new ArrayList<ItemStack>();

        for (int i = 0 ; i < 9+4 ; i++) {
            int inventoryIndex = getInventoryIndex(i);
            int internalInventoryIndex = getInternalInventoryIndex(index, i);
            ItemStack currentStack = inventory.getStackInSlot(inventoryIndex);
            ItemStack desiredStack = getStackInSlot(internalInventoryIndex);
            if (desiredStack == null) {
                if (currentStack != dummy) {
                    inventory.setInventorySlotContents(inventoryIndex, dummy);
                    if (!inventory.addItemStackToInventory(currentStack)) {
                        failedStacks.add(currentStack);
                    }
                }
            } else {
                ItemStack realStack = findAndRemoveItemStack(desiredStack, inventory);
                currentStack = inventory.getStackInSlot(inventoryIndex);
                inventory.setInventorySlotContents(inventoryIndex, realStack);
                if (currentStack != dummy && currentStack != null) {
                    // Move this away
                    if (!inventory.addItemStackToInventory(currentStack)) {
                        failedStacks.add(currentStack);
                    }
                }
            }
        }

        for (int i = 0 ; i < 9+4 ; i++) {
            int inventoryIndex = getInventoryIndex(i);
            ItemStack currentStack = inventory.getStackInSlot(inventoryIndex);
            if (currentStack == dummy) {
                inventory.setInventorySlotContents(inventoryIndex, null);
            }
        }

        for (ItemStack stack : failedStacks) {
            inventory.addItemStackToInventory(stack);
            // Check for failure and spawn in the world?
        }


        player.openContainer.detectAndSendChanges();
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

    private boolean isGhostSlot(int index) {
        return index < (4 + 4 * (9+4));
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
    public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
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
            markDirty();
        }

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
