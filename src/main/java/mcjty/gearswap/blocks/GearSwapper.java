package mcjty.gearswap.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.gearswap.GearSwap;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public class GearSwapper extends Block implements ITileEntityProvider {
    private IIcon iconFront;
    private IIcon iconSide;

    public GearSwapper() {
        super(Material.wood);
        setBlockName("gearSwapper");
        setBlockTextureName(GearSwap.MODID + ":gearSwapper");
        setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return null;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new GearSwapperTE();
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null) {

        }
        list.add("This block can remember four different sets of tools, weapons");
        list.add("and armor and allows you to quickly switch between them.");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        iconFront = iconRegister.registerIcon(GearSwap.MODID + ":gearSwapperFront");
//        side = iconRegister.registerIcon(GearSwap.MODID + ":gearSwapperSide");
        iconSide = iconRegister.registerIcon("minecraft:planks_oak");
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase, ItemStack itemStack) {
        ForgeDirection dir = getOrientation(world, x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        world.setBlockMetadataWithNotify(x, y, z, setOrientation(meta, dir), 2);
//        restoreBlockFromNBT(world, x, y, z, itemStack);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (side == ForgeDirection.SOUTH.ordinal()) {
            return iconFront;
        } else {
            return iconSide;
        }
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        ForgeDirection k = getOrientation(world, x, y, z);
        if (side == k.ordinal()) {
            return iconFront;
        } else {
            return iconSide;
        }
    }

    private static ForgeDirection getOrientation(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        return getOrientation(meta);
    }

    private static ForgeDirection getOrientation(int meta) {
        return ForgeDirection.getOrientation(meta & 0x7);
    }

    private static int setOrientation(int metadata, ForgeDirection orientation) {
        return (metadata & ~0x7) | orientation.ordinal();
    }
}
