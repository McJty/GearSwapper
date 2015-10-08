package mcjty.gearswap.blocks;

import mcjty.gearswap.Config;
import mcjty.gearswap.GearSwap;
import mcjty.gearswap.items.ModItems;
import mcjty.gearswap.varia.InventoryHelper;
import mcjty.gearswap.varia.Tools;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

public class GearSwapperTE extends TileEntity implements ISidedInventory {

    // First 4 slots are the ghost slots for the front icons
    // Next there are 4 times 9+4 slots for the remembered states.
    // Then there are 16 slots general inventory.
    // Finally we optionally have 4*4 slots for baubles.
    private ItemStack stacks[] = new ItemStack[4 + 4*(9+4) + 16 + 4*4];

    public static final int SLOT_GHOST = 4;
    public static final int SLOT_BUFFER = SLOT_GHOST + 4*(9+4);
    public static final int SLOT_BAUBLES = SLOT_BUFFER + 16;

    public static final int MODE_PLAYERINV = 0;
    public static final int MODE_LOCALINV = 1;
    public static final int MODE_REMOTEINV = 2;

    private int exportModes[] = new int[] { MODE_PLAYERINV, MODE_LOCALINV, MODE_REMOTEINV };

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
        exportModes[0] = tagCompound.getInteger("export0");
        exportModes[1] = tagCompound.getInteger("export1");
        exportModes[2] = tagCompound.getInteger("export2");
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
        tagCompound.setInteger("export0", exportModes[0]);
        tagCompound.setInteger("export1", exportModes[1]);
        tagCompound.setInteger("export2", exportModes[2]);
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

    public int getExportMode(int i) {
        return exportModes[i];
    }

    public void toggelExportMode(int i) {
        exportModes[i]++;
        if (exportModes[i] > MODE_REMOTEINV) {
            exportModes[i] = MODE_PLAYERINV;
        }
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void setFaceIconSlot(int index, ItemStack stack) {
        setInventorySlotContents(index, stack);
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    // Get total player inventory count. This is 9+4 (hotbar+armor) without baubles
    // and 9+4+4 with baubles.
    private int getPlayerInventorySize() {
        if (GearSwap.baubles) {
            return 9+4+4;
        } else {
            return 9+4;
        }
    }

    // Virtual inventory index. From 0 to 9 is hotbar, The four slots after that are armor
    private int getInventoryIndex(int i) {
        if (i < 9) {
            return i;
        } else {
            return (i-9) + 36;
        }
    }

    private ItemStack getStackFromPlayerInventory(int index, EntityPlayer player) {
        if (index < 9+4) {
            return player.inventory.getStackInSlot(getInventoryIndex(index));
        } else if (GearSwap.baubles) {
            IInventory baubles = Tools.getBaubles(player);
            if (baubles != null) {
                return baubles.getStackInSlot(index - (9+4));
            }
        }
        return null;
    }

    private void putStackInPlayerInventory(int index, EntityPlayer player, ItemStack stack) {
        if (index < 9+4) {
            player.inventory.setInventorySlotContents(getInventoryIndex(index), stack);
        } else if (GearSwap.baubles) {
            IInventory baubles = Tools.getBaubles(player);
            if (baubles != null) {
                baubles.setInventorySlotContents(index - (9+4), stack);
            }
        }
    }

    // Get the internal slot where we keep a ghost copy of the player inventory item.
    private int getInternalInventoryIndex(int index, int i) {
        if (i >= 9+4) {
            // We have a baubles slot.
            return SLOT_BAUBLES + index * 4 + (i-(9+4));
        } else {
            return 4 + index * (9 + 4) + i;
        }
    }

    public void rememberSetup(int index, EntityPlayer player) {
        setFaceIconSlot(index, player.getHeldItem());

        for (int i = 0 ; i < getPlayerInventorySize() ; i++) {
            ItemStack stack = getStackFromPlayerInventory(i, player);
            if (stack != null && stack.stackSize == 0) {
                // For some weird reason it seems baubles can have a 0 stacksize?
                stack = stack.copy();
                stack.stackSize = 1;
            }
            setInventorySlotContents(getInternalInventoryIndex(index, i), stack);
        }
    }

    public void restoreSetup(int index, EntityPlayer player) {
        InventoryPlayer inventory = player.inventory;

        // Set aside the current hotbar + armor slots (+ optional baubles slots).
        ItemStack[] currentStacks = new ItemStack[getPlayerInventorySize()];
        for (int i = 0 ; i < getPlayerInventorySize() ; i++) {
            currentStacks[i] = getStackFromPlayerInventory(i, player);
            putStackInPlayerInventory(i, player, null);
        }

        // Find stacks in all possible sources to replace the current selection
        for (int i = 0 ; i < getPlayerInventorySize() ; i++) {
            int internalInventoryIndex = getInternalInventoryIndex(index, i);
            ItemStack desiredStack = getStackInSlot(internalInventoryIndex);
            if (desiredStack == null || desiredStack.getItem() == ModItems.forceEmptyItem) {
                // Either we don't have specific needs for this slot or we want it to be cleared.
                // In both cases we keep the slot empty here.
            } else {
                ItemStack foundStack = findBestMatchingStack(desiredStack, currentStacks, inventory);
                // Can be that we didn't find anything. In any case, we simply put whatever we found in the slot.
                putStackInPlayerInventory(i, player, foundStack);
            }
        }

        // First we check all slots that we don't need to be cleared and we put back the item
        // from currentStacks if that's still there. In all other slots we temporarily set
        // our dummy item so that we don't accidently overwrite that in the next step.
        for (int i = 0 ; i < getPlayerInventorySize() ; i++) {
            ItemStack stack = getStackFromPlayerInventory(i, player);
            if (stack == null) {
                if (currentStacks[i] != null) {
                    int internalInventoryIndex = getInternalInventoryIndex(index, i);
                    ItemStack desiredStack = getStackInSlot(internalInventoryIndex);
                    // First check if we don't want to force the slot to be empty
                    if (desiredStack == null || desiredStack.getItem() != ModItems.forceEmptyItem) {
                        putStackInPlayerInventory(i, player, currentStacks[i]);
                        currentStacks[i] = null;
                    } else {
                        putStackInPlayerInventory(i, player, new ItemStack(ModItems.forceEmptyItem));
                    }
                } else {
                    putStackInPlayerInventory(i, player, new ItemStack(ModItems.forceEmptyItem));
                }
            }
        }

        // All items that we didn't find a place for we need to back somewhere.
        for (int i = 0 ; i < getPlayerInventorySize() ; i++) {
            if (currentStacks[i] != null) {
                if (storeItem(inventory, currentStacks[i])) {
                    currentStacks[i] = null;
                }
            }
        }

        // Now we clear the dummy items from our slots.
        for (int i = 0 ; i < getPlayerInventorySize() ; i++) {
            ItemStack stack = getStackFromPlayerInventory(i, player);
            if (stack != null && stack.getItem() == ModItems.forceEmptyItem) {
                putStackInPlayerInventory(i, player, null);
            }
        }

        // Now it is possible that some of the items we couldn't place back because the slots in the hotbar
        // were locked. Now that they are unlocked we can try again.
        for (int i = 0 ; i < getPlayerInventorySize() ; i++) {
            if (currentStacks[i] != null) {
                if (inventory.addItemStackToInventory(currentStacks[i])) {
                    currentStacks[i] = null;
                }
            }
        }

        // Finally it is possible that some items could not be placed anywhere.
        for (int i = 0 ; i < getPlayerInventorySize() ; i++) {
            if (currentStacks[i] != null) {
                EntityItem entityItem = new EntityItem(worldObj, xCoord, yCoord, zCoord, currentStacks[i]);
                worldObj.spawnEntityInWorld(entityItem);
            }
        }

        markDirty();
        player.openContainer.detectAndSendChanges();
    }

    private boolean storeItem(InventoryPlayer inventory, ItemStack item) {
        for (int exportMode : exportModes) {
            switch (exportMode) {
                case MODE_PLAYERINV:
                    if (inventory.addItemStackToInventory(item)) {
                        return true;
                    }
                    break;
                case MODE_LOCALINV: {
                    int left = InventoryHelper.mergeItemStack(this, item, SLOT_BUFFER, SLOT_BUFFER + 16, null);
                    if (left == 0) {
                        return true;
                    }
                    item.stackSize = left;
                    break;
                }
                case MODE_REMOTEINV:
                    for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
                        TileEntity te = worldObj.getTileEntity(xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ);
                        if (te instanceof IInventory) {
                            IInventory otherInventory = (IInventory) te;
                            int left = InventoryHelper.mergeItemStackSafe(otherInventory, direction.getOpposite().ordinal(), item, 0, otherInventory.getSizeInventory(), null);
                            if (left == 0) {
                                return true;
                            }
                            item.stackSize = left;
                        }
                    }
                    break;
            }
        }
        return false;
    }

    private ItemStack findBestMatchingStack(ItemStack desired, ItemStack[] currentStacks, InventoryPlayer inventoryPlayer) {
        ItemStack bestSoFar = null;
        desired = desired.copy();
        // Correct for 0 stackSize which seems to be needed in some cases.
        if (desired.stackSize == 0) {
            desired.stackSize = 1;
        }

        while (desired.stackSize > 0) {
            ItemStack stack = findBestMatchingStackWithScore(desired, currentStacks, inventoryPlayer, bestSoFar);
            if (stack == null) {
                return bestSoFar;
            }
            if (bestSoFar == null) {
                bestSoFar = stack;
            } else {
                bestSoFar.stackSize += stack.stackSize;
            }
            desired.stackSize -= stack.stackSize;
        }
        return bestSoFar;
    }

    private class BestScore {
        public int score = -1;
        public Source source = null;
        public int index = -1;
    }

    /**
     * Find the best matching item. If 'bestMatch' is already given then we already found one before and so we
     * now need an exact match for remaining items.
     * @param desired
     * @param source
     * @param bestScore
     * @param bestMatch
     */
    private void findBestMatchingStackWithScore(ItemStack desired, Source source, BestScore bestScore, ItemStack bestMatch) {
        for (int i = 0 ; i < source.getStackCount() ; i++) {
            ItemStack current = source.getStack(i);
            if (bestMatch != null && current != null) {
                if (!bestMatch.isItemEqual(current)) {
                    continue;
                }
                if (!ItemStack.areItemStackTagsEqual(bestMatch, current)) {
                    continue;
                }
            }
            int score = calculateMatchingScore(desired, current);
            if (score > bestScore.score) {
                bestScore.score = score;
                bestScore.source = source;
                bestScore.index = i;
            }
        }
    }

    private ItemStack findBestMatchingStackWithScore(final ItemStack desired, final ItemStack[] currentStacks, final InventoryPlayer inventoryPlayer, ItemStack bestMatch) {
        final BestScore bestScore = new BestScore();
        findBestMatchingStackWithScore(desired, new OriginalStackSource(currentStacks), bestScore, bestMatch);
        findBestMatchingStackWithScore(desired, new PlayerSource(inventoryPlayer), bestScore, bestMatch);
        findBestMatchingStackWithScore(desired, new InternalSource(this), bestScore, bestMatch);

        // Check external inventories.
        for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            TileEntity te = worldObj.getTileEntity(xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ);
            if (te instanceof IInventory) {
                findBestMatchingStackWithScore(desired, new ExternalInventorySource((IInventory) te, direction), bestScore, bestMatch);
            }
        }

        if (bestScore.source != null) {
            return bestScore.source.extractAmount(bestScore.index, desired.stackSize);
        }
        return null;
    }



    private int calculateMatchingScore(ItemStack desired, ItemStack current) {
        if (current == null) {
            return -1;
        }

        if (ItemStack.areItemStackTagsEqual(desired, current) && desired.isItemEqual(current)) {
            return 1000;
        }

        if (desired.getItem().equals(current.getItem()) && desired.getTagCompound() != null && current.getTagCompound() != null) {
            if (itemsMatchForSpecificTags(desired, current)) {
                return 700;
            }
        }

        if (desired.isItemEqual(current)) {
            return 500;
        }

        if (desired.getItem().equals(current.getItem())) {
            return 200;
        }
        return -1;
    }

    private boolean itemsMatchForSpecificTags(ItemStack desired, ItemStack current) {
        if (Config.tagsThatHaveToMatch.containsKey(desired.getUnlocalizedName())) {
            String[] tags = Config.tagsThatHaveToMatch.get(desired.getUnlocalizedName());
            boolean ok = true;
            for (String tag : tags) {
                NBTBase tag1 = desired.getTagCompound().getTag(tag);
                NBTBase tag2 = current.getTagCompound().getTag(tag);
                if (tag1 == null && tag2 != null) {
                    ok = false;
                    break;
                } else if (tag1 != null && tag2 == null) {
                    ok = false;
                    break;
                } else if (tag1 != null && !tag1.equals(tag2)) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                return true;
            }
        }
        return false;
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
        return (index >= 0 && index < SLOT_BUFFER) || (GearSwap.baubles && index >= SLOT_BAUBLES && index < SLOT_BAUBLES+16);
    }

    public boolean isIconSlot(int index) {
        return index >= 0 && index < SLOT_GHOST;
    }

    public boolean isBufferSlot(int index) { return index >= SLOT_BUFFER && index < SLOT_BUFFER + 16; }

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

    @Override
    public boolean canExtractItem(int index, ItemStack stack, int side) {
        return index >= SLOT_BUFFER;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return new int[] {
                SLOT_BUFFER, SLOT_BUFFER+1, SLOT_BUFFER+2, SLOT_BUFFER+3,
                SLOT_BUFFER+4, SLOT_BUFFER+5, SLOT_BUFFER+6, SLOT_BUFFER+7,
                SLOT_BUFFER+8, SLOT_BUFFER+9, SLOT_BUFFER+10, SLOT_BUFFER+11,
                SLOT_BUFFER+12, SLOT_BUFFER+13, SLOT_BUFFER+14, SLOT_BUFFER+15
        };
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, int side) {
        return index >= SLOT_BUFFER;
    }
}
