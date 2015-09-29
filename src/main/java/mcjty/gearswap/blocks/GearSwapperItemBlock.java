package mcjty.gearswap.blocks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.List;

public class GearSwapperItemBlock extends ItemBlock {
    private final GearSwapper gearSwapper;

    public GearSwapperItemBlock(Block block) {
        super(block);
        gearSwapper = (GearSwapper) block;
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
        gearSwapper.addInformation(itemStack, player, list, whatIsThis);
    }

}
