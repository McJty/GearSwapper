package mcjty.gearswap.blocks;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class RememberedState {
    private ItemStack[] hotbar = new ItemStack[9];
    private ItemStack[] armor = new ItemStack[4];

    public ItemStack getItemStack(int i) {
        if (i < hotbar.length) {
            return hotbar[i];
        } else {
            return armor[i-hotbar.length];
        }
    }

    public void setItemStack(int i, ItemStack stack) {
        if (i < hotbar.length) {
            hotbar[i] = stack;
        } else {
            armor[i-hotbar.length] = stack;
        }
    }

    public void readFromNBT(NBTTagCompound tagCompound, String suffix) {
        for (int i = 0 ; i < hotbar.length ; i++) {
            NBTTagCompound compoundTag = tagCompound.getCompoundTag("hotbar" + suffix + "_" + i);
            if (compoundTag == null) {
                hotbar[i] = null;
            } else {
                hotbar[i] = ItemStack.loadItemStackFromNBT(compoundTag);
            }
        }
        for (int i = 0 ; i < armor.length ; i++) {
            NBTTagCompound compoundTag = tagCompound.getCompoundTag("armor" + suffix + "_" + i);
            if (compoundTag == null) {
                armor[i] = null;
            } else {
                armor[i] = ItemStack.loadItemStackFromNBT(compoundTag);
            }
        }
    }

    public void writeToNBT(NBTTagCompound tagCompound, String suffix) {
        for (int i = 0 ; i < hotbar.length ; i++) {
            if (hotbar[i] != null) {
                NBTTagCompound nbtTagCompound = new NBTTagCompound();
                hotbar[i].writeToNBT(nbtTagCompound);
                tagCompound.setTag("hotbar" + suffix + "_" + i, nbtTagCompound);
            }
        }
        for (int i = 0 ; i < armor.length ; i++) {
            if (armor[i] != null) {
                NBTTagCompound nbtTagCompound = new NBTTagCompound();
                armor[i].writeToNBT(nbtTagCompound);
                tagCompound.setTag("armor" + suffix + "_" + i, nbtTagCompound);
            }
        }
    }
}
