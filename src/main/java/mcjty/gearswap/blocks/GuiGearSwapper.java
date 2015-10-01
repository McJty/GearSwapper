package mcjty.gearswap.blocks;

import mcjty.gearswap.GearSwap;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiGearSwapper extends GuiContainer {
    public static final int WIDTH = 256;
    public static final int HEIGHT = 251;

    private final GearSwapperTE gearSwapperTE;

    private static final ResourceLocation iconLocation = new ResourceLocation(GearSwap.MODID, "textures/gui/gearswapper.png");

    public GuiGearSwapper(GearSwapperTE tileEntity, GearSwapperContainer container) {
        super(container);
        this.gearSwapperTE = tileEntity;
        xSize = WIDTH;
        ySize = HEIGHT;
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i2) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(iconLocation);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, WIDTH, HEIGHT);
    }
}
