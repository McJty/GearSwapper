package mcjty.gearswap;


import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.HashMap;
import java.util.Map;

public class Config {
    public static String CATEGORY_GEARSWAP = "gearswap";
    public static String CATEGORY_RULES = "rules";

    public static String moddedTextureName = "";
    public static boolean supportBaubles = true;

    public static Map<String,String[]> tagsThatHaveToMatch = new HashMap<String, String[]>();

    public static void init(Configuration cfg) {
        moddedTextureName = cfg.get(CATEGORY_GEARSWAP, "moddedTextureName", "", "Put a modded texture name here (format <mod>:<texture>) to use for the modded texture gear sappwer").getString();
        supportBaubles = cfg.get(CATEGORY_GEARSWAP, "supportBaubles", supportBaubles, "If true (and if Baubles in installed) we support the baubles slots").getBoolean();

        ConfigCategory rulesCategory = cfg.getCategory(CATEGORY_RULES);
        if (rulesCategory.isEmpty()) {
            // Now configured rules. Add defaults.
            cfg.get(CATEGORY_RULES, "item.extrautils:golden_bag", new String[] { "display" });
        }
        for (Map.Entry<String, Property> entry : rulesCategory.getValues().entrySet()) {
            String unlocItemName = entry.getKey();
            tagsThatHaveToMatch.put(unlocItemName, entry.getValue().getStringList());
        }

    }
}
