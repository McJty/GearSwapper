package mcjty.gearswap.blocks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.List;

public class GearSwapperItemBlock extends ItemBlock {
    private final GearSwapperBlock gearSwapperBlock;

    public GearSwapperItemBlock(Block block) {
        super(block);
        gearSwapperBlock = (GearSwapperBlock) block;
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
        gearSwapperBlock.addInformation(itemStack, player, list, whatIsThis);
    }

}
