package com.creativemd.itemphysic.packet;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

import com.creativemd.itemphysic.EventHandler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PickupPacket implements IMessage {

    public Vec3 look;
    public Vec3 pos;

    public PickupPacket(Vec3 look, Vec3 pos) {
        this.look = look;
        this.pos = pos;
    }

    // Because InstantiationException
    public PickupPacket() {}

    @Override
    public void toBytes(ByteBuf buf) {
        writeVec3(look, buf);
        writeVec3(pos, buf);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        look = readVec3(buf);
        pos = readVec3(buf);
    }

    public static void writeVec3(Vec3 vec, ByteBuf buf) {
        buf.writeDouble(vec.xCoord);
        buf.writeDouble(vec.yCoord);
        buf.writeDouble(vec.zCoord);
    }

    public static Vec3 readVec3(ByteBuf buf) {
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        return Vec3.createVectorHelper(x, y, z);
    }

    public static class Handler implements IMessageHandler<PickupPacket, IMessage> {

        @Override
        public IMessage onMessage(PickupPacket m, MessageContext ctx) {
            EventHandler.cancel = true;
            EntityPlayer player = ctx.getServerHandler().playerEntity;
            EntityItem entity = EventHandler.getEntityItem(player, m.look, m.pos);
            if (entity != null) entity.interactFirst(player);
            return null;
        }
    }

}
