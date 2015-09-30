package mcjty.gearswap.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.gearswap.GearSwap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GearSwapperTESR extends TileEntitySpecialRenderer {

    private RenderItem itemRender = new RenderItem();
    private static final ResourceLocation texture = new ResourceLocation(GearSwap.MODID, "textures/blocks/gearSwapperFront.png");

//    private final ModelScreen screenModel = new ModelScreen(ScreenTileEntity.SIZE_NORMAL);
//    private final ModelScreen screenModelLarge = new ModelScreen(ScreenTileEntity.SIZE_LARGE);
//    private final ModelScreen screenModelHuge = new ModelScreen(ScreenTileEntity.SIZE_HUGE);

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
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

        renderSlots(gearSwapperTE);

        GL11.glPopMatrix();
    }

    private void renderSlots(GearSwapperTE gearSwapperTE) {
        GL11.glTranslatef(0.0F, 0.0F, 0.9F);

        RenderHelper.enableGUIStandardItemLighting();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        GL11.glDepthMask(false);

        boolean lighting = GL11.glIsEnabled(GL11.GL_LIGHTING);
        if (!lighting) {
            GL11.glEnable(GL11.GL_LIGHTING);
        }
        boolean depthTest = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        if (!depthTest) {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }

        float factor = 2.0f;
        float f3 = 0.0075F;
        GL11.glTranslatef(-0.5F, 0.5F, 0.06F);
        GL11.glScalef(f3 * factor, -f3 * factor, 0.0001f);

        FontRenderer fontRenderer = this.func_147498_b();
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

        ItemStack stack0 = gearSwapperTE.getItemStack(0);
        if (stack0 != null) {
            itemRender.renderItemAndEffectIntoGUI(fontRenderer, textureManager, stack0, 10, 9);
        }
        ItemStack stack1 = gearSwapperTE.getItemStack(1);
        if (stack1 != null) {
            itemRender.renderItemAndEffectIntoGUI(fontRenderer, textureManager, stack1, 40, 9);
        }

        ItemStack stack2 = gearSwapperTE.getItemStack(2);
        if (stack2 != null) {
            itemRender.renderItemAndEffectIntoGUI(fontRenderer, textureManager, stack2, 10, 39);
        }
        ItemStack stack3 = gearSwapperTE.getItemStack(3);
        if (stack3 != null) {
            itemRender.renderItemAndEffectIntoGUI(fontRenderer, textureManager, stack3, 40, 39);
        }

        if (!lighting) {
            GL11.glDisable(GL11.GL_LIGHTING);
        }
        if (!depthTest) {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        }

//        GL11.glDepthMask(false);
        RenderHelper.enableStandardItemLighting();
    }

}
