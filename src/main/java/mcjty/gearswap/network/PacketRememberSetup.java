package mcjty.gearswap.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mcjty.gearswap.blocks.GearSwapperTE;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class PacketRememberSetup implements IMessage, IMessageHandler<PacketRememberSetup, IMessage> {
    private int x;
    private int y;
    private int z;
    private int index;


    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        index = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(index);
    }

    public PacketRememberSetup() {
    }

    public PacketRememberSetup(int x, int y, int z, int index) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.index = index;
    }

    @Override
    public IMessage onMessage(PacketRememberSetup message, MessageContext ctx) {
        EntityPlayerMP playerEntity = ctx.getServerHandler().playerEntity;
        TileEntity te = playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
        if (te instanceof GearSwapperTE) {
            GearSwapperTE gearSwapperTE = (GearSwapperTE) te;
            gearSwapperTE.rememberSetup(message.index, playerEntity);
            playerEntity.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Remembered current hotbar and armor"));
        }
        return null;
    }
}
