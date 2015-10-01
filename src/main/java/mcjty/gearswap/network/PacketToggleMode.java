package mcjty.gearswap.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mcjty.gearswap.blocks.GearSwapperTE;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;

public class PacketToggleMode implements IMessage, IMessageHandler<PacketToggleMode, IMessage> {
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

    public PacketToggleMode() {
    }

    public PacketToggleMode(int x, int y, int z, int index) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.index = index;
    }

    @Override
    public IMessage onMessage(PacketToggleMode message, MessageContext ctx) {
        EntityPlayerMP playerEntity = ctx.getServerHandler().playerEntity;
        TileEntity te = playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
        if (te instanceof GearSwapperTE) {
            GearSwapperTE gearSwapperTE = (GearSwapperTE) te;
            gearSwapperTE.toggelExportMode(message.index);

        }
        return null;
    }

}
