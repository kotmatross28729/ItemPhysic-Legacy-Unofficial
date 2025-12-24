package com.creativemd.itemphysic.mixins.early;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.creativemd.itemphysic.config.ItemPhysicConfig;
import com.creativemd.itemphysic.physics.ClientPhysic;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

@Mixin(value = RenderItem.class, priority = 456)
public abstract class MixinRenderItem extends Render {
    // Applies all the rotations (only clientside!)

    @Inject(method = "doRender(Lnet/minecraft/entity/item/EntityItem;DDDFF)V", at = @At(value = "HEAD"))
    public void setUpRotations(EntityItem item, double x, double y, double z, float par8, float par9, CallbackInfo ci) {
        ClientPhysic.rotation = (double) (System.nanoTime() - ClientPhysic.tick) / 2_500_000
            * ItemPhysicConfig.rotationSpeed;
        if (!Minecraft.getMinecraft().inGameHasFocus) ClientPhysic.rotation = 0;
    }

    /**
     * @author kotmatross
     * @reason disable item floating
     */
    @Overwrite(remap = false)
    public boolean shouldBob() {
        return false;
    }

    @ModifyArg(
        method = "doRender(Lnet/minecraft/entity/item/EntityItem;DDDFF)V",
        at = @At(
            value = "INVOKE",
            target = "net/minecraftforge/client/ForgeHooksClient.renderEntityItem (Lnet/minecraft/entity/item/EntityItem;Lnet/minecraft/item/ItemStack;FFLjava/util/Random;Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/client/renderer/RenderBlocks;I)Z"),
        index = 3,
        remap = false)
    private float disableRotationForCustom(float original) {
        return 0;
    }

    @WrapWithCondition(
        method = "doRender(Lnet/minecraft/entity/item/EntityItem;DDDFF)V",
        at = @At(value = "INVOKE", target = "org/lwjgl/opengl/GL11.glRotatef(FFFF)V", ordinal = 0),
        remap = false)
    public boolean disableRotation(float angle, float x, float y, float z) {
        return false;
    }

    @Inject(
        method = "doRender(Lnet/minecraft/entity/item/EntityItem;DDDFF)V",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/block/Block.getRenderType ()I",
            shift = At.Shift.BEFORE,
            ordinal = 1))
    public void injectRotations(EntityItem item, double x, double y, double z, float par8, float par9,
        CallbackInfo ci) {
        if (item.prevPosY != item.posY || item.onGround) {
            GL11.glRotatef(item.rotationYaw, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(item.rotationPitch, 1.0F, 0.0F, 0.0F);
        }
    }

    @Inject(
        method = "doRender(Lnet/minecraft/entity/item/EntityItem;DDDFF)V",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/client/renderer/RenderBlocks.renderBlockAsItem (Lnet/minecraft/block/Block;IF)V",
            shift = At.Shift.BEFORE))
    public void applyRotationsBeforeRenderBlock(EntityItem item, double x, double y, double z, float par8, float par9,
        CallbackInfo ci) {
        if (item.rotationPitch > 360) item.rotationPitch = 0;
        ClientPhysic.applyRotations(item);
    }

    @Inject(
        method = "doRender(Lnet/minecraft/entity/item/EntityItem;DDDFF)V",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/item/Item.requiresMultipleRenderPasses ()Z",
            shift = At.Shift.BEFORE,
            ordinal = 0),
        cancellable = true)
    public void disableFurtherIfCustom(EntityItem item, double x, double y, double z, float par8, float par9,
        CallbackInfo ci) {
        if (MinecraftForgeClient.getItemRenderer(item.getEntityItem(), IItemRenderer.ItemRenderType.ENTITY) != null) {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
            this.bindEntityTexture(item);
            TextureUtil.func_147945_b();
            ci.cancel();
        }
    }

    @Inject(
        method = "renderDroppedItem(Lnet/minecraft/entity/item/EntityItem;Lnet/minecraft/util/IIcon;IFFFFI)V",
        at = @At(
            value = "INVOKE",
            target = "org/lwjgl/opengl/GL11.glRotatef(FFFF)V",
            shift = At.Shift.BEFORE,
            ordinal = 1),
        remap = false)
    public void injectRotationsItem(EntityItem item, IIcon p_77020_2_, int p_77020_3_, float p_77020_4_,
        float p_77020_5_, float p_77020_6_, float p_77020_7_, int pass, CallbackInfo ci) {
        if (item.prevPosY != item.posY || item.onGround) {
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(item.rotationYaw, 0.0F, 0.0F, 1.0F);
        }
    }

    @WrapWithCondition(
        method = "renderDroppedItem(Lnet/minecraft/entity/item/EntityItem;Lnet/minecraft/util/IIcon;IFFFFI)V",
        at = @At(value = "INVOKE", target = "org/lwjgl/opengl/GL11.glRotatef(FFFF)V", ordinal = 1),
        remap = false)
    public boolean disableRotationItem(float angle, float x, float y, float z) {
        return false;
    }

    @Inject(
        method = "renderDroppedItem(Lnet/minecraft/entity/item/EntityItem;Lnet/minecraft/util/IIcon;IFFFFI)V",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/entity/item/EntityItem.getEntityItem ()Lnet/minecraft/item/ItemStack;",
            shift = At.Shift.BEFORE,
            ordinal = 1))
    public void applyRotationsItem(EntityItem item, IIcon p_77020_2_, int p_77020_3_, float p_77020_4_,
        float p_77020_5_, float p_77020_6_, float p_77020_7_, int pass, CallbackInfo ci) {
        ClientPhysic.applyRotations(item);
        GL11.glRotatef(item.rotationPitch, 1.0F, 0.0F, 0.0F);
    }

}
