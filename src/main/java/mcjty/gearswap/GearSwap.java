package mcjty.gearswap;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.gearswap.commands.ItemInfoCommand;
import mcjty.gearswap.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = GearSwap.MODID, name="Gear Swapper", dependencies =
        "required-after:Forge@["+ GearSwap.MIN_FORGE_VER+",)",
        version = GearSwap.VERSION)
public class GearSwap {
    public static final String MODID = "gearswap";
    public static final String VERSION = "1.0.0";
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

    public static boolean baubles = false;


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

        baubles = Loader.isModLoaded("Baubles");
        if (baubles) {
            if (Config.supportBaubles) {
                logger.log(Level.INFO, "Gear Swapper Detected Baubles: enabling support");
            } else {
                logger.log(Level.INFO, "Gear Swapper Detected Baubles but it is disabled in config anyway: disabling support");
                baubles = false;
            }
        }
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new ItemInfoCommand());
    }


}
