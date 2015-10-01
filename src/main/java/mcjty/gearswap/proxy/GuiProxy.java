package mcjty.gearswap.proxy;

import cpw.mods.fml.common.network.IGuiHandler;
import mcjty.gearswap.GearSwap;
import mcjty.gearswap.blocks.GearSwapperContainer;
import mcjty.gearswap.blocks.GearSwapperTE;
import mcjty.gearswap.blocks.GuiGearSwapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiProxy implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int guiid, EntityPlayer entityPlayer, World world, int x, int y, int z) {
        if (guiid == GearSwap.GUI_GEARSWAP) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof GearSwapperTE) {
                return new GearSwapperContainer(entityPlayer, (GearSwapperTE) te);
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int guiid, EntityPlayer entityPlayer, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof GearSwapperTE) {
            GearSwapperTE gearSwapperTE = (GearSwapperTE) te;
            return new GuiGearSwapper(gearSwapperTE, new GearSwapperContainer(entityPlayer, gearSwapperTE));
        }
        return null;
    }
}
