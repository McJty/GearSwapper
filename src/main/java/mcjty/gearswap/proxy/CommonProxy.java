package mcjty.gearswap.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import mcjty.gearswap.Config;
import mcjty.gearswap.GearSwap;
import mcjty.gearswap.ModCrafting;
import mcjty.gearswap.blocks.ModBlocks;
import mcjty.gearswap.items.ModItems;
import mcjty.gearswap.network.PacketHandler;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;

public abstract class CommonProxy {

    private Configuration mainConfig;

    public void preInit(FMLPreInitializationEvent e) {
        mainConfig = GearSwap.config;
        ModItems.init();
        ModBlocks.init();
        readMainConfig();
        ModCrafting.init();

        PacketHandler.registerMessages("gearswapper");
    }

    private void readMainConfig() {
        Configuration cfg = mainConfig;
        try {
            cfg.load();
            cfg.addCustomCategoryComment(Config.CATEGORY_GEARSWAP, "General configuration");
            Config.init(cfg);
        } catch (Exception e1) {
            GearSwap.logger.log(Level.ERROR, "Problem loading config file!", e1);
        } finally {
            if (mainConfig.hasChanged()) {
                mainConfig.save();
            }
        }
    }

    public void init(FMLInitializationEvent e) {
        NetworkRegistry.INSTANCE.registerGuiHandler(GearSwap.instance, new GuiProxy());
    }

    public void postInit(FMLPostInitializationEvent e) {
        if (mainConfig.hasChanged()) {
            mainConfig.save();
        }
        mainConfig = null;
    }

}
