package mcjty.gearswap.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.gearswap.GearSwap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GearSwapperTESR extends TileEntitySpecialRenderer {

    private RenderItem itemRender = new RenderItem();
    private static final ResourceLocation texture = new ResourceLocation(GearSwap.MODID, "textures/blocks/gearSwapperFront.png");

    private static int xx[] = new int[] { 9, 40, 9, 40 };
    private static int yy[] = new int[] { 7, 7, 36, 36 };

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
        MovingObjectPosition mouseOver = Minecraft.getMinecraft().objectMouseOver;
        int index;
        if (mouseOver.blockX == tileEntity.xCoord && mouseOver.blockY == tileEntity.yCoord && mouseOver.blockZ == tileEntity.zCoord) {
            index = GearSwapperBlock.getSlot(mouseOver, tileEntity.getWorldObj());
        } else {
            index = -1;
        }

        GL11.glPushMatrix();
        int meta = tileEntity.getBlockMetadata();
        float rotY = 0.0F;
        float rotX = 0.0F;

        if (meta == ForgeDirection.NORTH.ordinal()) {
            rotY = 180.0F;
        } else if (meta == ForgeDirection.WEST.ordinal()) {
            rotY = 90.0F;
        } else if (meta == ForgeDirection.EAST.ordinal()) {
            rotY = -90.0F;
        } else if (meta == ForgeDirection.UP.ordinal()) {
            rotX = -90.0F;
        } else if (meta == ForgeDirection.DOWN.ordinal()) {
            rotX = 90.0F;
        }

        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.75F, (float) z + 0.5F);

        if (Math.abs(rotX) < 0.001f) {
            GL11.glRotatef(-rotY, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(0.0F, -0.2500F, -0.4375F);
        } else {
            GL11.glRotatef(rotX, 1.0F, 0.0F, 0.0F);
            GL11.glTranslatef(0.0F, -0.4375F, -0.2500F);
        }

        GearSwapperTE gearSwapperTE = (GearSwapperTE) tileEntity;

        GL11.glTranslatef(0.0F, 0.0F, 0.9F);

        GL11.glDepthMask(true);

        boolean depthTest = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        if (!depthTest) {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }

        renderSlotHilight(index);
        renderSlots(gearSwapperTE);

        if (!depthTest) {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        }
        GL11.glDepthMask(true);
        RenderHelper.disableStandardItemLighting();

        GL11.glPopMatrix();
    }

    private void renderSlotHilight(int index) {
        GL11.glPushMatrix();

        GL11.glTranslatef(-0.5F, 0.5F, 0.04F);
        float factor = 2.0f;
        float f3 = 0.0075F;
        GL11.glScalef(f3 * factor, -f3 * factor, f3);
        GL11.glNormal3f(0.0F, 0.0F, -1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_LIGHTING);

        for (int i = 0 ; i < 4 ; i++) {
            Gui.drawRect(xx[i]-4, yy[i]-4, xx[i] + 22, yy[i]-3, 0xff222222);
            Gui.drawRect(xx[i]-4, yy[i]+21, xx[i] + 22, yy[i]+22, 0xff222222);
            Gui.drawRect(xx[i]-4, yy[i]-4, xx[i]-3, yy[i] + 22, 0xff222222);
            Gui.drawRect(xx[i]+21, yy[i]-4, xx[i]+22, yy[i] + 22, 0xff222222);
            Gui.drawRect(xx[i]-3, yy[i]-3, xx[i] + 21, yy[i] + 21, index == i ? 0x55666666 : 0x55000000);
        }

        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslatef(-0.5F, 0.5F, 0.06F);
        factor = 1.0f;
        GL11.glScalef(f3 * factor, -f3 * factor, f3);
        FontRenderer fontrenderer = this.func_147498_b();
        fontrenderer.drawString("Settings...", 10, 120, index == -1 ? 0xffffffff : 0xff888888);

        GL11.glPopMatrix();
    }

    private void renderSlots(GearSwapperTE gearSwapperTE) {
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        float factor = 2.0f;
        float f3 = 0.0075F;
        GL11.glTranslatef(-0.5F, 0.5F, 0.04F);
        GL11.glScalef(f3 * factor, -f3 * factor, 0.0001f);

        FontRenderer fontRenderer = this.func_147498_b();
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

        for (int i = 0 ; i < 4 ; i++) {
            ItemStack stack = gearSwapperTE.getStackInSlot(i);
            if (stack != null) {
                itemRender.renderItemAndEffectIntoGUI(fontRenderer, textureManager, stack, xx[i], yy[i]);
            }
        }
    }

}
