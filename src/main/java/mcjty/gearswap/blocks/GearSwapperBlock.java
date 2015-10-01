package mcjty.gearswap.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.gearswap.GearSwap;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.common.util.ForgeDirection.DOWN;

public class GearSwapperBlock extends Block implements ITileEntityProvider {
    private IIcon iconFront;
    private IIcon iconSide;

    public GearSwapperBlock() {
        super(Material.wood);
        setBlockName("gearSwapper");
        setHardness(2.0f);
        setHarvestLevel("pickaxe", 0);
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
        iconSide = iconRegister.registerIcon("minecraft:planks_oak");
    }

    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
        if (!world.isRemote && player.isSneaking()) {
            MovingObjectPosition mouseOver = Minecraft.getMinecraft().objectMouseOver;
            ForgeDirection k = getOrientation(world, x, y, z);
            if (mouseOver.sideHit == k.ordinal()) {
                float sx = (float) (mouseOver.hitVec.xCoord - x);
                float sy = (float) (mouseOver.hitVec.yCoord - y);
                float sz = (float) (mouseOver.hitVec.zCoord - z);
                int index = calculateHitIndex(sx, sy, sz, k);
                GearSwapperTE gearSwapperTE = (GearSwapperTE) world.getTileEntity(x, y, z);
                gearSwapperTE.rememberSetup(index, player);
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Remembered current hotbar and armor"));
            }
        }
    }


    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float sx, float sy, float sz) {
        if (!world.isRemote) {
            ForgeDirection k = getOrientation(world, x, y, z);
            if (side == k.ordinal()) {
                TileEntity tileEntity = world.getTileEntity(x, y, z);
                if (tileEntity instanceof GearSwapperTE) {
                    GearSwapperTE gearSwapperTE = (GearSwapperTE) tileEntity;

                    if (sy < .1) {
                        player.openGui(GearSwap.instance, GearSwap.GUI_GEARSWAP, world, x, y, z);
                        return true;
                    }

                    int index = calculateHitIndex(sx, sy, sz, k);

                    gearSwapperTE.restoreSetup(index, player);
                    player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Restored hotbar and armor"));
                }
            }
        }
        return true;
    }

    private int calculateHitIndex(float sx, float sy, float sz, ForgeDirection k) {
        int index = 0;
        switch (k) {
            case DOWN:
                break;
            case UP:
                break;
            case NORTH:
                index = (sx < .5 ? 1 : 0) + (sy < .5 ? 2 : 0);
                break;
            case SOUTH:
                index = (sx > .5 ? 1 : 0) + (sy < .5 ? 2 : 0);
                break;
            case WEST:
                index = (sz > .5 ? 1 : 0) + (sy < .5 ? 2 : 0);
                break;
            case EAST:
                index = (sz < .5 ? 1 : 0) + (sy < .5 ? 2 : 0);
                break;
            case UNKNOWN:
                break;
        }
        return index;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase, ItemStack itemStack) {
        ForgeDirection dir = determineOrientation(x, y, z, entityLivingBase);
        int meta = world.getBlockMetadata(x, y, z);
        world.setBlockMetadataWithNotify(x, y, z, setOrientation(meta, dir), 2);

        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof GearSwapperTE) {
                ((GearSwapperTE)te).readRestorableFromNBT(tagCompound);
            }
        }
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        if (tileEntity instanceof GearSwapperTE) {
            ItemStack stack = new ItemStack(Item.getItemFromBlock(this));
            NBTTagCompound tagCompound = new NBTTagCompound();
            ((GearSwapperTE)tileEntity).writeRestorableToNBT(tagCompound);

            stack.setTagCompound(tagCompound);
            ArrayList<ItemStack> result = new ArrayList<ItemStack>();
            result.add(stack);
            return result;
        } else {
            return super.getDrops(world, x, y, z, metadata, fortune);
        }
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        if (willHarvest) return true; // If it will harvest, delay deletion of the block until after getDrops
        return super.removedByPlayer(world, player, x, y, z, willHarvest);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta) {
        super.harvestBlock(world, player, x, y, z, meta);
        world.setBlockToAir(x, y, z);
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

    public static ForgeDirection determineOrientation(int x, int y, int z, EntityLivingBase entityLivingBase) {
        if (MathHelper.abs((float) entityLivingBase.posX - x) < 2.0F && MathHelper.abs((float)entityLivingBase.posZ - z) < 2.0F) {
            double d0 = entityLivingBase.posY + 1.82D - entityLivingBase.yOffset;

            if (d0 - y > 2.0D) {
                return ForgeDirection.UP;
            }

            if (y - d0 > 0.0D) {
                return DOWN;
            }
        }
        int l = MathHelper.floor_double((entityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        return l == 0 ? ForgeDirection.NORTH : (l == 1 ? ForgeDirection.EAST : (l == 2 ? ForgeDirection.SOUTH : (l == 3 ? ForgeDirection.WEST : DOWN)));
    }


    private static ForgeDirection getOrientation(int meta) {
        return ForgeDirection.getOrientation(meta & 0x7);
    }

    private static int setOrientation(int metadata, ForgeDirection orientation) {
        return (metadata & ~0x7) | orientation.ordinal();
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    @Override
    public boolean isOpaqueCube() {
        return false;
    }

}
