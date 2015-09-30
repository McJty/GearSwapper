package mcjty.gearswap.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class GearSwapperTE extends TileEntity {

    private int faceItems[] = new int[4];
    private int faceBlocks[] = new int[4];


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
            faceBlocks[i] = tagCompound.getInteger("faceBlock" + i);
            faceItems[i] = tagCompound.getInteger("faceItem" + i);
        }
    }

    public ItemStack getItemStack(int index) {
        if (faceItems[index] != 0) {
            return new ItemStack((Item) Item.itemRegistry.getObjectById(faceItems[index]));
        } else if (faceBlocks[index] != 0) {
            return new ItemStack((Block) Block.blockRegistry.getObjectById(faceBlocks[index]));
        } else {
            return null;
        }
    }

    public void setItemStack(int index, ItemStack stack) {
        faceBlocks[index] = 0;
        faceItems[index] = 0;
        if (stack != null) {
            Item item = stack.getItem();
            if (item instanceof ItemBlock) {
                ItemBlock itemBlock = (ItemBlock) item;
                if (itemBlock.field_150939_a != null) {
                    faceBlocks[index] = Block.blockRegistry.getIDForObject(itemBlock.field_150939_a);
                }
            } else if (item != null) {
                faceItems[index] = Item.itemRegistry.getIDForObject(item);
            }
        }

        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        writeRestorableToNBT(tagCompound);
    }

    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        for (int i = 0 ; i < 4 ; i++) {
            tagCompound.setInteger("faceBlock" + i, faceBlocks[i]);
            tagCompound.setInteger("faceItem" + i, faceItems[i]);
        }
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
