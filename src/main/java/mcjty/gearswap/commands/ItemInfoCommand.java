package mcjty.gearswap.commands;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import java.util.List;

public class ItemInfoCommand implements ICommand {

    @Override
    public String getCommandName() {
        return "iteminfo";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return getCommandName();
    }

    @Override
    public List getCommandAliases() {
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        World world = sender.getEntityWorld();
        if (world.isRemote) {
            return;
        }
        if (!(sender instanceof EntityPlayer)) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "This command only works as a player!"));
            return;
        }
        if (!sender.canCommandSenderUseCommand(2, getCommandName())) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Command is not allowed!"));
            return;
        }

        EntityPlayer player = (EntityPlayer) sender;
        ItemStack heldItem = player.getHeldItem();
        if (heldItem == null) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Player must hold an item!"));
            return;
        }

        Item item = heldItem.getItem();
        if (item instanceof ItemBlock) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Block: " + heldItem.getDisplayName() + " (#" + heldItem.stackSize + ", " + heldItem.getItemDamage() + ")"));
        } else {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Item: " + heldItem.getDisplayName() + " (#" + heldItem.stackSize + ", " + heldItem.getItemDamage() + ")"));
        }

        NBTTagCompound tagCompound = heldItem.getTagCompound();
        if (tagCompound != null) {
            for (Object tag : tagCompound.func_150296_c()) {
                NBTBase base = tagCompound.getTag((String) tag);
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "    Tag " + tag + " = '" + base.toString() + "'"));
            }
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] sender, int p_82358_2_) {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int compareTo(Object o) {
        return getCommandName().compareTo(((ICommand)o).getCommandName());
    }
}
