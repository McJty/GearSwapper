package mcjty.gearswap.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

class ItemOrBlock {
    private int item = 0;
    private int block = 0;
    private int meta = 0;

    public ItemOrBlock(NBTTagCompound tagCompound, String suffix) {
        readFromNBT(tagCompound, suffix);
    }

    public ItemOrBlock() {

    }

    public ItemOrBlock(ItemStack stack) {
        if (stack == null) {
            return;
        }
        Item stackItem = stack.getItem();
        if (stackItem instanceof ItemBlock) {
            ItemBlock itemBlock = (ItemBlock) stackItem;
            if (itemBlock.field_150939_a != null) {
                block = Block.blockRegistry.getIDForObject(itemBlock.field_150939_a);
            }
        } else if (stackItem != null) {
            item = Item.itemRegistry.getIDForObject(stackItem);
        }
        meta = stack.getItemDamage();
    }

    public ItemStack getStack() {
        if (item != 0) {
            return new ItemStack((Item) Item.itemRegistry.getObjectById(item), 1, meta);
        } else if (block != 0) {
            return new ItemStack((Block) Block.blockRegistry.getObjectById(block), 1, meta);
        } else {
            return null;
        }
    }

    public void readFromNBT(NBTTagCompound tagCompound, String suffix) {
        block = tagCompound.getInteger("faceblock" + suffix);
        item = tagCompound.getInteger("faceitem" + suffix);
        meta = tagCompound.getInteger("facemeta" + suffix);
    }

    public void writeToNBT(NBTTagCompound tagCompound, String suffix) {
        tagCompound.setInteger("faceblock" + suffix, block);
        tagCompound.setInteger("faceitem" + suffix, item);
        tagCompound.setInteger("facemeta" + suffix, meta);
    }
}
