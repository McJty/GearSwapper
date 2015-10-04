package mcjty.gearswap.items;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.registry.GameRegistry;
import mcjty.gearswap.GearSwap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Optional.InterfaceList({
        @Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles")})
public class ForceEmptyItem extends Item implements IBauble {
    public ForceEmptyItem() {
        setMaxStackSize(1);
        setUnlocalizedName("emptyItem");
        setTextureName(GearSwap.MODID + ":emptyItem");
        GameRegistry.registerItem(this, "emptyItem");
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public BaubleType getBaubleType(ItemStack itemstack) {
        return null;
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {

    }

    @Override
    @Optional.Method(modid = "Baubles")
    public void onEquipped(ItemStack itemstack, EntityLivingBase player) {

    }

    @Override
    @Optional.Method(modid = "Baubles")
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {

    }

    @Override
    @Optional.Method(modid = "Baubles")
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }
}
