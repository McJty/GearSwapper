package mcjty.gearswap.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;

public class GearSwapperGlassBlock extends GearSwapperBlock {
    public GearSwapperGlassBlock(Material material, String textureName, String blockName) {
        super(material, textureName, blockName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass()
    {
        return 0;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side)    {
        Block block = world.getBlock(x, y, z);

        if (block == this || block == Blocks.glass) {
            return false;
        }

        return super.shouldSideBeRendered(world, x, y, z, side);
    }

}
