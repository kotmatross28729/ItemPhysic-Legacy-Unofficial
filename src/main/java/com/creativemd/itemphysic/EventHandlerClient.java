package com.creativemd.itemphysic;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import org.lwjgl.opengl.GL11;

import com.creativemd.itemphysic.config.ItemPhysicConfig;
import com.creativemd.itemphysic.packet.DropPacket;
import com.creativemd.itemphysic.packet.PacketDispatcher;
import com.creativemd.itemphysic.physics.ClientPhysic;
import com.creativemd.itemphysic.physics.ServerPhysic;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EventHandlerClient {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void renderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ClientPhysic.tick = System.nanoTime();
            renderTickFull();
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void drawTooltip(ItemTooltipEvent event) {
        if (event.itemStack != null) {
            if (ServerPhysic.canItemIgnite(event.itemStack)) {
                event.toolTip.add(EnumChatFormatting.GOLD + "[" + I18n.format("itemphysic.igniting") + "]");
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static int power;
    @SideOnly(Side.CLIENT)
    public static Minecraft mc;
    @SideOnly(Side.CLIENT)
    private long lastTickCount = 0;

    @SideOnly(Side.CLIENT)
    public void renderTickFull() {
        if (mc == null) mc = Minecraft.getMinecraft();

        if (mc != null && mc.thePlayer != null && mc.inGameHasFocus) {
            if (ItemPhysicConfig.customPickup) {
                double distance = 100;

                if (mc.objectMouseOver != null) if (mc.objectMouseOver.typeOfHit
                    == MovingObjectPosition.MovingObjectType.BLOCK)
                    distance = mc.thePlayer
                        .getDistance(mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ);
                else if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY)
                    distance = mc.thePlayer.getDistanceToEntity(mc.objectMouseOver.entityHit);

                EntityItem entity = EventHandler.getEntityItem(distance, mc.thePlayer);

                if (entity != null && mc.inGameHasFocus && ItemPhysicConfig.showPickupTooltip) {
                    int space = 15;
                    List<String> list = new ArrayList<>();
                    try {
                        list.add(
                            entity.getEntityItem()
                                .getDisplayName());
                        entity.getEntityItem()
                            .getItem()
                            .addInformation(entity.getEntityItem(), mc.thePlayer, list, true);
                    } catch (Exception e) {
                        list = new ArrayList<>();
                        list.add("ERRORED");
                    }

                    int width = 0, height = 0;
                    for (String text : list) {
                        width = Math.max(width, mc.fontRenderer.getStringWidth(text));
                        height += mc.fontRenderer.FONT_HEIGHT;
                    }
                    width += 10; // Add padding
                    height += space * (list.size() - 1); // Add padding

                    ScaledResolution resolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
                    int centerX = resolution.getScaledWidth() / 2;
                    int centerY = resolution.getScaledHeight() / 2;

                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    GL11.glEnable(GL11.GL_ALPHA_TEST);

                    GL11.glPushMatrix();
                    GL11.glTranslated(centerX - (double) width / 2, centerY - (double) height / 2, 0);
                    double rgb = (Math.sin(Math.toRadians((double) System.nanoTime() / 10000000D)) + 1) * 0.2;
                    Vec3 color = Vec3.createVectorHelper(rgb, rgb, rgb);
                    drawRect(0, 0, width, height, color, 0.3);
                    color = Vec3.createVectorHelper(0, 0, 0);
                    drawRect(1, 1, width - 1, height - 1, color, 0.1);

                    GL11.glPopMatrix();

                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                    GL11.glDisable(GL11.GL_BLEND);
                    int y = centerY - height / 2;
                    for (String text : list) {
                        mc.fontRenderer
                            .drawString(text, centerX - mc.fontRenderer.getStringWidth(text) / 2, y, 16579836);
                        y += mc.fontRenderer.FONT_HEIGHT + space;
                    }
                }
            }

            if (ItemPhysicConfig.customThrow) {

                ScaledResolution resolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
                int width = resolution.getScaledWidth();
                int height = resolution.getScaledHeight();

                // Render outside of interval to avoid flickering
                if (ItemPhysicConfig.showPowerText) {
                    if (power > 0) {
                        int renderPower = power;
                        renderPower /= ItemPhysicConfig.throwPowerTickCoefficient;
                        if (renderPower < 1) renderPower = 1;
                        if (renderPower > ItemPhysicConfig.throwPowerMax) renderPower = ItemPhysicConfig.throwPowerMax;

                        String text = I18n.format("itemphysic.power") + ":" + " " + renderPower;

                        mc.fontRenderer.drawString(
                            text,
                            width / 2 - mc.fontRenderer.getStringWidth(text) / 2,
                            height / 2 + height / 4,
                            16579836);
                    }
                }

                // To avoid FPS influence on this. (Before 20 fps - 20 ticks per second, 60 fps - 60 ticks per second ->
                // 3 times faster)
                // Why? Because for some reason we do this in the renderer 0_____0
                long currentTickCount = mc.theWorld.getTotalWorldTime();
                if (currentTickCount != lastTickCount) {
                    lastTickCount = currentTickCount;

                    if (mc.thePlayer.getCurrentEquippedItem() != null) {

                        if (mc.gameSettings.keyBindDrop.getIsKeyPressed()) {
                            power++;
                        } else {
                            if (power > 0) {
                                power /= ItemPhysicConfig.throwPowerTickCoefficient;
                                if (power < 1) power = 1;
                                if (power > ItemPhysicConfig.throwPowerMax) power = ItemPhysicConfig.throwPowerMax;

                                PacketDispatcher.wrapper.sendToServer(new DropPacket(power, GuiScreen.isCtrlKeyDown()));
                            }
                            power = 0;
                        }
                    }
                }
            }

        }
    }

    /**
     * Draws a solid color rectangle with the specified coordinates and color. Args: x1, y1, x2, y2, color
     */
    public static void drawRect(int x1, int y1, int x2, int y2, Vec3 color, double alpha) {
        GL11.glTranslated(0, 0, 0);
        GL11.glPushMatrix();

        int j1;

        if (x1 < x2) {
            j1 = x1;
            x1 = x2;
            x2 = j1;
        }

        if (y1 < y2) {
            j1 = y1;
            y1 = y2;
            y2 = j1;
        }

        Tessellator tessellator = Tessellator.instance;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4d(color.xCoord, color.yCoord, color.zCoord, alpha);
        tessellator.startDrawingQuads();
        tessellator.addVertex((double) x1, (double) y2, 0.0D);
        tessellator.addVertex((double) x2, (double) y2, 0.0D);
        tessellator.addVertex((double) x2, (double) y1, 0.0D);
        tessellator.addVertex((double) x1, (double) y1, 0.0D);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}
