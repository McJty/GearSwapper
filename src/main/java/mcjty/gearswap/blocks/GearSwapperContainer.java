package mcjty.gearswap.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class GearSwapperContainer extends Container {
    private IInventory playerInventory;
    private IInventory gearInventory;

    public GearSwapperContainer(EntityPlayer player, GearSwapperTE gearSwapperTE) {
        playerInventory = player.inventory;
        gearInventory = gearSwapperTE;

        int index = 0;
        addSlotToContainer(new GhostSlot(gearInventory, index++, 10, 8));
        addSlotToContainer(new GhostSlot(gearInventory, index++, 28, 47));
        addSlotToContainer(new GhostSlot(gearInventory, index++, 10, 104));
        addSlotToContainer(new GhostSlot(gearInventory, index++, 28, 143));

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
        for (int i = 0 ; i < 9 ; i++) {
            addSlotToContainer(new Slot(playerInventory, index++, 87 + i*18, 227));
        }
        for (int j = 0 ; j < 3 ; j++) {
            for (int i = 0; i < 9; i++) {
                addSlotToContainer(new Slot(playerInventory, index++, 87 + i*18, 169 + j*18));
            }
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
}
