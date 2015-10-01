package mcjty.gearswap.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;

public class GearSwapperContainer extends Container {
    private IInventory playerInventory;
    private IInventory gearInventory;

    public GearSwapperContainer(EntityPlayer player, GearSwapperTE gearSwapperTE) {
        playerInventory = player.inventory;
        gearInventory = gearSwapperTE;

        int index = 0;
        addSlotToContainer(new GhostSlot(gearInventory, index++, 9, 7));
        addSlotToContainer(new GhostSlot(gearInventory, index++, 28, 46));
        addSlotToContainer(new GhostSlot(gearInventory, index++, 9, 104));
        addSlotToContainer(new GhostSlot(gearInventory, index++, 28, 142));

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

//        for (int y = 0 ; y < 4 ; y++) {
//            for (int x = 0 ; x < 4 ; x++) {
//                addSlotToContainer()
//            }
//        }
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
