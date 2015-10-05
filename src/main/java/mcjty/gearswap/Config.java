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
            cfg.get(CATEGORY_RULES, "item.InfiTool.Pickaxe", new String[] { "display" });
            cfg.get(CATEGORY_RULES, "item.InfiTool.Hammer", new String[] { "display" });
            cfg.get(CATEGORY_RULES, "item.InfiTool.Cleaver", new String[] { "display" });
            cfg.get(CATEGORY_RULES, "item.InfiTool.LumberAxe", new String[] { "display" });
            cfg.get(CATEGORY_RULES, "item.InfiTool.Rapier", new String[] { "display" });
            cfg.get(CATEGORY_RULES, "item.InfiTool.Broadsword", new String[] { "display" });
            cfg.get(CATEGORY_RULES, "item.InfiTool.Longsword", new String[] { "display" });
            cfg.get(CATEGORY_RULES, "item.InfiTool.FryPan", new String[] { "display" });
            cfg.get(CATEGORY_RULES, "item.InfiTool.Chisel", new String[] { "display" });
            cfg.get(CATEGORY_RULES, "item.InfiTool.Cutlass", new String[] { "display" });
            cfg.get(CATEGORY_RULES, "item.InfiTool.Scythe", new String[] { "display" });
            cfg.get(CATEGORY_RULES, "item.InfiTool.Dagger", new String[] { "display" });
            cfg.get(CATEGORY_RULES, "item.InfiTool.Battlesign", new String[] { "display" });
            cfg.get(CATEGORY_RULES, "item.InfiTool.Battleaxe", new String[] { "display" });
            cfg.get(CATEGORY_RULES, "item.InfiTool", new String[] { "display" });
        }
        for (Map.Entry<String, Property> entry : rulesCategory.getValues().entrySet()) {
            String unlocItemName = entry.getKey();
            tagsThatHaveToMatch.put(unlocItemName, entry.getValue().getStringList());
        }

    }
}
