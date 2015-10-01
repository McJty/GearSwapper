package mcjty.gearswap.blocks;

import mcjty.gearswap.GearSwap;
import mcjty.gearswap.network.PacketHandler;
import mcjty.gearswap.network.PacketToggleMode;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiGearSwapper extends GuiContainer {
    public static final int WIDTH = 256;
    public static final int HEIGHT = 251;

    private final GearSwapperTE gearSwapperTE;

    private static final ResourceLocation iconLocation = new ResourceLocation(GearSwap.MODID, "textures/gui/gearswapper.png");
    private static final ResourceLocation guiElements = new ResourceLocation(GearSwap.MODID, "textures/gui/guielements.png");

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
    protected void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);

        x -= guiLeft;
        y -= guiTop;

        if (x >= 9 && x <= 9+16) {
            if (y >= 8 && y <= 8+16) {
                toggleMode(0);
            } else if (y >= 28 && y <= 28+16) {
                toggleMode(1);
            } else if (y >= 48 && y <= 48+16) {
                toggleMode(2);
            }
        }
    }

    private void toggleMode(int i) {
        PacketHandler.INSTANCE.sendToServer(new PacketToggleMode(gearSwapperTE.xCoord, gearSwapperTE.yCoord, gearSwapperTE.zCoord, i));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int ii, int i2) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(iconLocation);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, WIDTH, HEIGHT);

        mc.getTextureManager().bindTexture(guiElements);
        int y = 8;
        for (int i = 0 ; i < 3 ; i++) {
            int u = gearSwapperTE.getExportMode(i) * 16;
            drawTexturedModalRect(guiLeft + 9, guiTop + y, u, 0, 16, 16);
            y += 20;
        }
    }
}
