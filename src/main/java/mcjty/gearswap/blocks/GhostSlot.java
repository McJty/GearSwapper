package mcjty.gearswap.blocks;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import mcjty.gearswap.GearSwap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GhostSlot extends Slot {

    public static final int ANY = -1;
    public static final int ARMOR_HELMET = 0;
    public static final int ARMOR_CHESTPLATE = 1;
    public static final int ARMOR_LEGGINGS = 2;
    public static final int ARMOR_BOOTS = 3;
    public static final int BAUBLE_RING = 4;
    public static final int BAUBLE_AMULET = 5;
    public static final int BAUBLE_BELT = 6;

    private int type;

    public GhostSlot(IInventory inventory, int index, int x, int y, int type) {
        super(inventory, index, x, y);
        this.type = type;
    }

    @Override
    public boolean canTakeStack(EntityPlayer player) {
        return false;
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        return null;
    }

    @Override
    public int getSlotStackLimit() {
        return 0;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        Item item = stack.getItem();
        if (type >= ARMOR_HELMET && type <= ARMOR_BOOTS) {
            return item != null && item.isValidArmor(stack, type, null);
        } else if (GearSwap.baubles && type >= BAUBLE_RING && type <= BAUBLE_BELT) {
            if (item == null) {
                return false;
            }
            if (!(item instanceof IBauble)) {
                return false;
            }
            IBauble bauble = (IBauble) item;
            BaubleType baubleType = bauble.getBaubleType(stack);
            return (baubleType == BaubleType.AMULET && type == BAUBLE_AMULET) ||
                    (baubleType == BaubleType.RING && type == BAUBLE_RING) ||
                    (baubleType == BaubleType.BELT && type == BAUBLE_BELT);
        } else {
            return true;
        }
    }

    @Override
    public void putStack(ItemStack stack) {
//        if (stack != null) {
//            stack.stackSize = 1;
//        }
        inventory.setInventorySlotContents(getSlotIndex(), stack);
        onSlotChanged();
    }
}
