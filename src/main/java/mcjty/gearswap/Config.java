package mcjty.gearswap;


import net.minecraftforge.common.config.Configuration;

public class Config {
    public static String CATEGORY_GEARSWAP = "gearswap";

    public static String moddedTextureName = "";
    public static boolean supportBaubles = true;

    public static void init(Configuration cfg) {
        moddedTextureName = cfg.get(CATEGORY_GEARSWAP, "moddedTextureName", "", "Put a modded texture name here (format <mod>:<texture>) to use for the modded texture gear sappwer").getString();
        supportBaubles = cfg.get(CATEGORY_GEARSWAP, "supportBaubles", supportBaubles, "If true (and if Baubles in installed) we support the baubles slots").getBoolean();
    }
}
