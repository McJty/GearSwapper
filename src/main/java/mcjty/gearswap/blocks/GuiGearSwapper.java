package mcjty.gearswap.blocks;

import mcjty.gearswap.GearSwap;
import mcjty.gearswap.network.PacketHandler;
import mcjty.gearswap.network.PacketToggleMode;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GuiGearSwapper extends GuiContainer {
    public static final int WIDTH = 256;
    public static final int HEIGHT = 247;

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

        if (isTopModeSlot(x, y)) {
            toggleMode(0);
        } else if (isMiddleModeslot(x, y)) {
            toggleMode(1);
        } else if (isBottomModeSlot(x, y)) {
            toggleMode(2);
        }
    }

    private boolean isBottomModeSlot(int x, int y) {
        return x >= 9 && x <= 9+16 && y >= 48 && y <= 48+16;
    }

    private boolean isMiddleModeslot(int x, int y) {
        return x >= 9 && x <= 9+16 && y >= 28 && y <= 28+16;
    }

    private boolean isTopModeSlot(int x, int y) {
        return x >= 9 && x <= 9+16 && y >= 8 && y <= 8+16;
    }

    private void toggleMode(int i) {
        PacketHandler.INSTANCE.sendToServer(new PacketToggleMode(gearSwapperTE.xCoord, gearSwapperTE.yCoord, gearSwapperTE.zCoord, i));
    }

    private void filledRect(int x1, int y1, int x2, int y2, int color) {
        drawRect(guiLeft+x1, guiTop+y1, guiLeft+x2, guiTop+y2, color);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int ii, int i2) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(iconLocation);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, WIDTH, HEIGHT);

        drawModes();

        // If needed hide the bauble slots
        if (!GearSwap.baubles) {
            filledRect(27, 86, 27 + 18, 86 + 18 * 4, 0xffc6c6c6);
            filledRect(86, 5, 86 + 18 * 4, 5 + 18, 0xffc6c6c6);
            filledRect(86, 5 + 39, 86 + 18 * 4, 5 + 39 + 18, 0xffc6c6c6);
            filledRect(86, 5 + 39 * 2, 86 + 18 * 4, 5 + 39 * 2 + 18, 0xffc6c6c6);
            filledRect(86, 5+39*3, 86+18*4, 5+39*3+18, 0xffc6c6c6);
        }

        drawTooltips();
    }

    private void drawModes() {
        mc.getTextureManager().bindTexture(guiElements);
        int y = 8;
        for (int i = 0 ; i < 3 ; i++) {
            int u = gearSwapperTE.getExportMode(i) * 16;
            drawTexturedModalRect(guiLeft + 9, guiTop + y, u, 0, 16, 16);
            y += 20;
        }
    }

    private void drawTooltips() {
        int x = Mouse.getEventX() * width / mc.displayWidth;
        int y = height - Mouse.getEventY() * height / mc.displayHeight - 1;

        x -= guiLeft;
        y -= guiTop;

        List<String> tooltips = new ArrayList<String>();
        if (isTopModeSlot(x, y)) {
            tooltips.add("Priority one export inventory");
        } else if (isMiddleModeslot(x, y)) {
            tooltips.add("Priority two export inventory");
        } else if (isBottomModeSlot(x, y)) {
            tooltips.add("Priority three export inventory");
        }

        if (!tooltips.isEmpty()) {
            x += guiLeft;
            y += guiTop;
            drawHoveringText(tooltips, x, y, mc.fontRenderer);
        }
    }
}
