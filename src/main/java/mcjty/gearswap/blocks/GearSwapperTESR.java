package mcjty.gearswap.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.gearswap.GearSwap;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GearSwapperTESR extends TileEntitySpecialRenderer {

    private static final ResourceLocation texture = new ResourceLocation(GearSwap.MODID, "textures/blocks/gearSwapperFront.png");
//    private final ModelScreen screenModel = new ModelScreen(ScreenTileEntity.SIZE_NORMAL);
//    private final ModelScreen screenModelLarge = new ModelScreen(ScreenTileEntity.SIZE_LARGE);
//    private final ModelScreen screenModelHuge = new ModelScreen(ScreenTileEntity.SIZE_HUGE);

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
        GL11.glPushMatrix();
        float f3;

        int meta = tileEntity.getBlockMetadata();
        f3 = 0.0F;

        if (meta == 2) {
            f3 = 180.0F;
        }

        if (meta == 4) {
            f3 = 90.0F;
        }

        if (meta == 5) {
            f3 = -90.0F;
        }

        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.75F, (float) z + 0.5F);
        GL11.glRotatef(-f3, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(0.0F, -0.2500F, -0.4375F);

        GL11.glDepthMask(false);

        boolean lightingEnabled = GL11.glIsEnabled(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_LIGHTING);

        GearSwapperTE gearSwapperTE = (GearSwapperTE) tileEntity;

        int size = 1;
        float factor = size + 1.0f;

        GL11.glPushMatrix();

        GL11.glTranslatef(-0.5F, 0.5F, 0.07F);
        f3 = 0.0075F;
        GL11.glScalef(f3 * factor, -f3 * factor, f3);
        GL11.glNormal3f(0.0F, 0.0F, -1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        FontRenderer fontrenderer = this.func_147498_b();
        int currentx = 7;
        int currenty = 7;
        fontrenderer.drawString(fontrenderer.trimStringToWidth("This is a test", 115), currentx, currenty, 0xffffff); currenty += 10;
        fontrenderer.drawString(fontrenderer.trimStringToWidth("This is a test", 115), currentx, currenty, 0xffffff); currenty += 10;
        fontrenderer.drawString(fontrenderer.trimStringToWidth("This is a test", 115), currentx, currenty, 0xffffff); currenty += 10;
        fontrenderer.drawString(fontrenderer.trimStringToWidth("This is a test", 115), currentx, currenty, 0xffffff); currenty += 10;
        fontrenderer.drawString(fontrenderer.trimStringToWidth("This is a test", 115), currentx, currenty, 0xffffff); currenty += 10;



        GL11.glPopMatrix();

        if (lightingEnabled) {
            GL11.glEnable(GL11.GL_LIGHTING);
        }

        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }
}
