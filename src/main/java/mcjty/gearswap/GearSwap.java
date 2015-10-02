package mcjty.gearswap;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.gearswap.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = GearSwap.MODID, name="Gear Swapper", dependencies =
        "required-after:Forge@["+ GearSwap.MIN_FORGE_VER+",)",
        version = GearSwap.VERSION)
public class GearSwap {
    public static final String MODID = "gearswap";
    public static final String VERSION = "0.1.0";
    public static final String MIN_FORGE_VER = "10.13.2.1291";

    @SidedProxy(clientSide="mcjty.gearswap.proxy.ClientProxy", serverSide="mcjty.gearswap.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance("gearswap")
    public static GearSwap instance;
    public static Logger logger;
    public static File mainConfigDir;
    public static File modConfigDir;
    public static Configuration config;

    public static int GUI_GEARSWAP = 0;

    /**
     * Run before anything else. Read your config, create blocks, items, etc, and
     * register them with the GameRegistry.
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        logger = e.getModLog();
        mainConfigDir = e.getModConfigurationDirectory();
        modConfigDir = new File(mainConfigDir.getPath());
        config = new Configuration(new File(modConfigDir, "gearswap.cfg"));
        proxy.preInit(e);

        FMLInterModComms.sendMessage("Waila", "register", "mcjty.gearswap.WailaSupport.load");
    }

    /**
     * Do your mod setup. Build whatever data structures you care about. Register recipes.
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
    }

    /**
     * Handle interaction with other mods, complete your setup based on this.
     */
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
    }
}
