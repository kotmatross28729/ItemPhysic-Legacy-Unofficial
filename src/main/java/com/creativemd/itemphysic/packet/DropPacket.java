package com.creativemd.itemphysic.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;

import com.creativemd.itemphysic.EventHandler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class DropPacket implements IMessage {

    public int power;
    public boolean control;

    public DropPacket(int power, boolean control) {
        this.power = power;
        this.control = control;
    }

    // Because InstantiationException
    public DropPacket() {
        power = 0;
        control = false;
    }

    @Override
    public void toBytes(ByteBuf bytes) {
        bytes.writeInt(power);
        bytes.writeBoolean(control);
    }

    @Override
    public void fromBytes(ByteBuf bytes) {
        power = bytes.readInt();
        control = bytes.readBoolean();
    }

    public static class Handler implements IMessageHandler<DropPacket, IMessage> {

        @Override
        public IMessage onMessage(DropPacket m, MessageContext ctx) {

            EntityPlayer player = ctx.getServerHandler().playerEntity;
            EventHandler.DropPower = m.power;
            dropOneItemCustom(player, m.control);
            EventHandler.DropPower = 1;

            return null;
        }
    }

    public static void dropOneItemCustom(EntityPlayer player, boolean p_71040_1_) {

        if (player == null || player.inventory == null) {
            return;
        }

        ItemStack stack = player.inventory.getCurrentItem();

        if (stack == null || stack.getItem() == null) {
            return;
        }

        if (stack.getItem()
            .onDroppedByPlayer(stack, player)) {
            int count = p_71040_1_ && player.inventory.getCurrentItem() != null
                ? player.inventory.getCurrentItem().stackSize
                : 1;
            ForgeHooks
                .onPlayerTossEvent(player, player.inventory.decrStackSize(player.inventory.currentItem, count), true);
        }

    }
}
