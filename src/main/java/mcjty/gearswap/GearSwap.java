package romelo333.notenoughwands;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Logger;
import romelo333.notenoughwands.proxy.CommonProxy;

import java.io.File;

@Mod(modid = NotEnoughWands.MODID, name="Not Enough Wands", dependencies =
        "required-after:Forge@["+ NotEnoughWands.MIN_FORGE_VER+",)",
        version = NotEnoughWands.VERSION)
public class NotEnoughWands {
    public static final String MODID = "NotEnoughWands";
    public static final String VERSION = "1.1.2";
    public static final String MIN_FORGE_VER = "10.13.2.1291";

    @SidedProxy(clientSide="romelo333.notenoughwands.proxy.ClientProxy", serverSide="romelo333.notenoughwands.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance("NotEnoughWands")
    public static NotEnoughWands instance;
    public static Logger logger;
    public static File mainConfigDir;
    public static File modConfigDir;
    public static Configuration config;

    public static CreativeTabs tabNew = new CreativeTabs("NotEnoughWands") {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return ModItems.teleportationWand;
        }
    };

    /**
     * Run before anything else. Read your config, create blocks, items, etc, and
     * register them with the GameRegistry.
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        logger = e.getModLog();
        mainConfigDir = e.getModConfigurationDirectory();
        modConfigDir = new File(mainConfigDir.getPath());
        config = new Configuration(new File(modConfigDir, "notenoughwands.cfg"));
        proxy.preInit(e);

//        FMLInterModComms.sendMessage("Waila", "register", "mcjty.wailasupport.WailaCompatibility.load");
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
