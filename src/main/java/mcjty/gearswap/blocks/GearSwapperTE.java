package mcjty.gearswap.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.List;

public class GearSwapperTE extends TileEntity {

    private ItemOrBlock faceIcons[] = new ItemOrBlock[4];
    private RememberedState rememberedState[] = new RememberedState[4];

    public GearSwapperTE() {
        for (int i = 0 ; i < 4 ; i++) {
            faceIcons[i] = new ItemOrBlock();
            rememberedState[i] = new RememberedState();
        }
    }

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
        for (int i = 0 ; i < 4 ; i++) {
            faceIcons[i] = new ItemOrBlock(tagCompound, String.valueOf(i));
            rememberedState[i].readFromNBT(tagCompound, String.valueOf(i));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        writeRestorableToNBT(tagCompound);
    }

    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        for (int i = 0 ; i < 4 ; i++) {
            faceIcons[i].writeToNBT(tagCompound, String.valueOf(i));
            rememberedState[i].writeToNBT(tagCompound, String.valueOf(i));
        }
    }

    public ItemStack getItemStack(int index) {
        return faceIcons[index].getStack();
    }

    public void setItemStack(int index, ItemStack stack) {
        faceIcons[index] = new ItemOrBlock(stack);
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    private int getInventoryIndex(int i) {
        if (i < 9) {
            return i;
        } else {
            return (i-9) + 36;
        }
    }

    public void rememberSetup(int index, EntityPlayer player) {
        setItemStack(index, player.getHeldItem());

        InventoryPlayer inventory = player.inventory;
        for (int i = 0 ; i < 9 + 4 ; i++) {
            int inventoryIndex = getInventoryIndex(i);
            ItemStack stack = inventory.getStackInSlot(inventoryIndex);
            System.out.println("i = " + i + ", stack = " + stack);
            rememberedState[index].setItemStack(i, stack == null ? null : stack.copy());
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
            ItemStack currentStack = inventory.getStackInSlot(inventoryIndex);
            ItemStack desiredStack = rememberedState[index].getItemStack(i);
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

        System.out.println("failedStacks = " + failedStacks.size());
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

}
