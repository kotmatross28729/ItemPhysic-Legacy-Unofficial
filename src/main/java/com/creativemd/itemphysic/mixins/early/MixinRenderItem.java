package com.creativemd.itemphysic.mixins.early;

import static net.minecraftforge.client.IItemRenderer.ItemRenderType.ENTITY;

import java.lang.reflect.Field;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fluids.Fluid;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.creativemd.itemphysic.config.ItemPhysicConfig;
import com.creativemd.itemphysic.physics.ClientPhysic;
import com.creativemd.itemphysic.physics.ServerPhysic;

import cpw.mods.fml.relauncher.ReflectionHelper;

@Mixin(value = RenderItem.class, priority = 1006)
public abstract class MixinRenderItem extends Render {

    @Shadow
    private Random random = new Random();
    @Shadow
    private RenderBlocks renderBlocksRi = new RenderBlocks();
    @Unique
    private static final Minecraft itemPhysic$mc = Minecraft.getMinecraft();
    @Unique
    private static final Field itemPhysic$isInWeb = ReflectionHelper
        .findField(Entity.class, "isInWeb", "field_70134_J");

    @Shadow
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation(
        "textures/misc/enchanted_item_glint.png");

    @Shadow
    public boolean renderWithColor = true;

    /**
     * @author kotmatross
     * @reason redirect to our system
     */
    @Overwrite
    public void doRender(EntityItem item, double x, double y, double z, float par8, float par9) {
        ClientPhysic.rotation = (double) (System.nanoTime() - ClientPhysic.tick) / 2500000
            * ItemPhysicConfig.rotationSpeed;
        if (!itemPhysic$mc.inGameHasFocus) ClientPhysic.rotation = 0;

        ItemStack itemstack = item.getEntityItem();

        if (itemstack.getItem() != null) {
            itemPhysic$mc.renderEngine.bindTexture(
                itemPhysic$mc.renderEngine.getResourceLocation(
                    item.getEntityItem()
                        .getItemSpriteNumber()));
            TextureUtil.func_152777_a(false, false, 1.0F);
            random.setSeed(187L);
            GL11.glPushMatrix();
            byte b0 = 1;

            if (item.getEntityItem().stackSize > 1) b0 = 2;
            if (item.getEntityItem().stackSize > 5) b0 = 3;
            if (item.getEntityItem().stackSize > 20) b0 = 4;
            if (item.getEntityItem().stackSize > 40) b0 = 5;

            b0 = getMiniBlockCount(itemstack, b0);

            GL11.glTranslatef((float) x, (float) y, (float) z);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            float f6;
            float f7;
            int k;

            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(itemstack, ENTITY);
            if ((!ForgeHooksClient
                .renderEntityItem(item, itemstack, 0, 0, random, itemPhysic$mc.renderEngine, renderBlocksRi, b0))
                && itemstack.getItemSpriteNumber() == 0
                && itemstack.getItem() instanceof ItemBlock
                && RenderBlocks.renderItemIn3d(
                    Block.getBlockFromItem(itemstack.getItem())
                        .getRenderType())) {
                Block block = Block.getBlockFromItem(itemstack.getItem());

                if (RenderItem.renderInFrame) {
                    GL11.glScalef(1.25F, 1.25F, 1.25F);
                    GL11.glTranslatef(0.0F, 0.05F, 0.0F);
                    GL11.glTranslatef(0.0F, 0.09F, 0.0F);
                    GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                } else if (item.prevPosY != item.posY || item.onGround) {
                    GL11.glRotatef(item.rotationYaw, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(item.rotationPitch, 1.0F, 0.0F, 0.0F);
                }

                float f9 = 0.25F;
                k = block.getRenderType();

                if (k == 1 || k == 19 || k == 12 || k == 2) {
                    f9 = 0.5F;
                }

                if (block.getRenderBlockPass() > 0) {
                    GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
                    GL11.glEnable(GL11.GL_BLEND);
                    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                }

                GL11.glScalef(f9, f9, f9);

                for (int l = 0; l < b0; ++l) {
                    GL11.glPushMatrix();

                    if (l > 0) {
                        f6 = (random.nextFloat() * 2.0F - 1.0F) * 0.2F / f9;
                        f7 = (random.nextFloat() * 2.0F - 1.0F) * 0.2F / f9;
                        float f8 = (random.nextFloat() * 2.0F - 1.0F) * 0.2F / f9;
                        GL11.glTranslatef(f6, f7, f8);
                    }

                    if (item.rotationPitch > 360) item.rotationPitch = 0;

                    // ROTATIONS
                    if (!Double.isNaN(item.posX) && !Double.isNaN(item.posY)
                        && !Double.isNaN(item.posZ)
                        && item.worldObj != null
                        && item.age != 0) {
                        if (!item.onGround) {
                            double rotation = ClientPhysic.rotation * 2;
                            Fluid fluid = ServerPhysic.getFluid(item);
                            if (fluid != null) rotation /= (float) ((fluid.getDensity() / 1000) * 10);
                            else {
                                fluid = ServerPhysic.getFluid(item, true);
                                if (fluid != null) rotation /= (float) ((fluid.getDensity() / 1000) * 10);
                            }
                            try {
                                if (itemPhysic$isInWeb.getBoolean(item)) rotation /= 50;
                            } catch (IllegalArgumentException | IllegalAccessException ignored) {}
                            item.rotationPitch += (float) rotation;
                        }
                    }

                    renderBlocksRi.renderBlockAsItem(block, itemstack.getItemDamage(), 1.0F); // BLOCK, NOT TESR
                    GL11.glPopMatrix();
                }

                if (block.getRenderBlockPass() > 0) GL11.glDisable(GL11.GL_BLEND);
            } else if (customRenderer == null) {
                float f5;
                if (/* itemstack.getItemSpriteNumber() == 1 && */ itemstack.getItem()
                    .requiresMultipleRenderPasses()) {
                    if (RenderItem.renderInFrame) {
                        GL11.glScalef(0.5128205F, 0.5128205F, 0.5128205F);
                        GL11.glTranslatef(0.0F, -0.05F, 0.0F);
                    } else GL11.glScalef(0.5F, 0.5F, 0.5F);

                    for (int j = 0; j < itemstack.getItem()
                        .getRenderPasses(itemstack.getItemDamage()); ++j) {
                        random.setSeed(187L);
                        IIcon iicon1 = itemstack.getItem()
                            .getIcon(itemstack, j);

                        if (renderWithColor) {
                            k = itemstack.getItem()
                                .getColorFromItemStack(itemstack, j);
                            f5 = (float) (k >> 16 & 255) / 255.0F;
                            f6 = (float) (k >> 8 & 255) / 255.0F;
                            f7 = (float) (k & 255) / 255.0F;
                            GL11.glColor4f(f5, f6, f7, 1.0F);
                            renderDroppedItem(item, iicon1, b0, par9, f5, f6, f7, j); // Seems that don't work
                        } else renderDroppedItem(item, iicon1, b0, par9, 1.0F, 1.0F, 1.0F, j); // Seems that don't work
                    }
                } else {
                    if (itemstack.getItem() instanceof ItemCloth) {
                        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
                        GL11.glEnable(GL11.GL_BLEND);
                        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                    }

                    if (RenderItem.renderInFrame) {
                        GL11.glScalef(0.5128205F, 0.5128205F, 0.5128205F);
                        GL11.glTranslatef(0.0F, -0.05F, 0.0F);
                    } else GL11.glScalef(0.5F, 0.5F, 0.5F);

                    IIcon iicon = itemstack.getIconIndex();

                    if (renderWithColor) {
                        int i = itemstack.getItem()
                            .getColorFromItemStack(itemstack, 0);
                        float f4 = (float) (i >> 16 & 255) / 255.0F;
                        f5 = (float) (i >> 8 & 255) / 255.0F;
                        f6 = (float) (i & 255) / 255.0F;
                        renderDroppedItem(item, iicon, b0, par9, f4, f5, f6, 0); // ITEM, NOT TESR
                    } else renderDroppedItem(item, iicon, b0, par9, 1.0F, 1.0F, 1.0F, 0); // ITEM, NOT TESR

                    if (itemstack.getItem() instanceof ItemCloth) GL11.glDisable(GL11.GL_BLEND);
                }
            }

            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
            itemPhysic$mc.renderEngine.bindTexture(
                itemPhysic$mc.renderEngine.getResourceLocation(
                    item.getEntityItem()
                        .getItemSpriteNumber()));
            TextureUtil.func_147945_b();
        }
    }

    /**
     * @author kotmatross
     * @reason redirect to our system
     */
    @Overwrite(remap = false)
    private void renderDroppedItem(EntityItem item, IIcon par2Icon, int par3, float par4, float par5, float par6,
        float par7, int pass) {
        if (item == null) {
            return;
        }

        Tessellator tessellator = Tessellator.instance;

        if (par2Icon == null) {
            TextureManager texturemanager = Minecraft.getMinecraft()
                .getTextureManager();
            ResourceLocation resourcelocation = texturemanager.getResourceLocation(
                item.getEntityItem()
                    .getItemSpriteNumber());
            par2Icon = ((TextureMap) texturemanager.getTexture(resourcelocation)).getAtlasSprite("missingno");
        }

        float f14 = ((IIcon) par2Icon).getMinU();
        float f15 = ((IIcon) par2Icon).getMaxU();
        float f4 = ((IIcon) par2Icon).getMinV();
        float f5 = ((IIcon) par2Icon).getMaxV();
        float f6 = 1.0F;
        float f7 = 0.5F;
        float f8 = 0.25F;
        float f10;

        if (RenderManager.instance.options.fancyGraphics) {
            GL11.glPushMatrix();

            if (RenderItem.renderInFrame) {
                GL11.glTranslatef(0.0F, 0.09F, 0.0F);
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            } else if (item.prevPosY != item.posY || item.onGround) {
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(item.rotationYaw, 0.0F, 0.0F, 1.0F);
            }

            if (!Double.isNaN(item.posX) && !Double.isNaN(item.posY)
                && !Double.isNaN(item.posZ)
                && item.worldObj != null
                && !RenderItem.renderInFrame
                && item.age != 0) {
                if (item.onGround || item.prevPosY == item.posY) {
                    item.rotationPitch = 0;
                } else {
                    double rotation = ClientPhysic.rotation * 2;
                    Fluid fluid = ServerPhysic.getFluid(item);
                    if (fluid != null) rotation /= (float) ((fluid.getDensity() / 1000) * 10);
                    else {
                        fluid = ServerPhysic.getFluid(item, true);
                        if (fluid != null) rotation /= (float) ((fluid.getDensity() / 1000) * 10);
                    }
                    try {
                        if (itemPhysic$isInWeb.getBoolean(item)) rotation /= 50;
                    } catch (IllegalArgumentException | IllegalAccessException ignored) {}
                    item.rotationPitch += (float) rotation;
                }
            }

            GL11.glRotatef(item.rotationPitch, 1.0F, 0.0F, 0.0F);

            float f9 = 0.0625F;
            f10 = 0.021875F;
            ItemStack itemstack = item.getEntityItem();
            int j = itemstack.stackSize;
            byte b0;

            if (j < 2) b0 = 1;
            else if (j < 16) b0 = 2;
            else if (j < 32) b0 = 3;
            else b0 = 4;

            b0 = getMiniItemCount(itemstack, b0);

            GL11.glTranslatef(-f7, -f8, -((f9 + f10) * (float) b0 / 2.0F));

            for (int k = 0; k < b0; ++k) {
                // Makes items offset when in 3D, like when in 2D, looks much better. Considered a vanilla bug...
                if (k > 0 && shouldSpreadItems()) {
                    // float x = (random.nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
                    // float y = (random.nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
                    // float z = (random.nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
                    GL11.glTranslatef(0, 0, f9 + f10);
                } else GL11.glTranslatef(0f, 0f, f9 + f10);

                if (itemstack.getItemSpriteNumber() == 0)
                    itemPhysic$mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
                else itemPhysic$mc.renderEngine.bindTexture(TextureMap.locationItemsTexture);

                GL11.glColor4f(par5, par6, par7, 1.0F);
                net.minecraft.client.renderer.ItemRenderer.renderItemIn2D(
                    tessellator,
                    f15,
                    f4,
                    f14,
                    f5,
                    ((IIcon) par2Icon).getIconWidth(),
                    ((IIcon) par2Icon).getIconHeight(),
                    f9);

                if (itemstack.hasEffect(pass)) {
                    GL11.glDepthFunc(GL11.GL_EQUAL);
                    GL11.glDisable(GL11.GL_LIGHTING);
                    itemPhysic$mc.renderEngine.bindTexture(RES_ITEM_GLINT);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                    float f11 = 0.76F;
                    GL11.glColor4f(0.5F * f11, 0.25F * f11, 0.8F * f11, 1.0F);
                    GL11.glMatrixMode(GL11.GL_TEXTURE);
                    GL11.glPushMatrix();
                    float f12 = 0.125F;
                    GL11.glScalef(f12, f12, f12);
                    float f13 = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
                    GL11.glTranslatef(f13, 0.0F, 0.0F);
                    GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                    net.minecraft.client.renderer.ItemRenderer
                        .renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, f9);
                    GL11.glPopMatrix();
                    GL11.glPushMatrix();
                    GL11.glScalef(f12, f12, f12);
                    f13 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                    GL11.glTranslatef(-f13, 0.0F, 0.0F);
                    GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                    net.minecraft.client.renderer.ItemRenderer
                        .renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, f9);
                    GL11.glPopMatrix();
                    GL11.glMatrixMode(GL11.GL_MODELVIEW);
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glEnable(GL11.GL_LIGHTING);
                    GL11.glDepthFunc(GL11.GL_LEQUAL);
                }
            }

            GL11.glPopMatrix();

        } else {
            for (int l = 0; l < par3; ++l) {
                GL11.glPushMatrix();

                if (l > 0) {
                    f10 = (random.nextFloat() * 2.0F - 1.0F) * 0.3F;
                    float f16 = (random.nextFloat() * 2.0F - 1.0F) * 0.3F;
                    float f17 = (random.nextFloat() * 2.0F - 1.0F) * 0.3F;
                    GL11.glTranslatef(f10, f16, f17);
                }

                if (!RenderItem.renderInFrame)
                    GL11.glRotatef(180.0F - RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);

                GL11.glColor4f(par5, par6, par7, 1.0F);
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, 1.0F, 0.0F);
                tessellator.addVertexWithUV(0.0F - f7, 0.0F - f8, 0.0D, f14, f5);
                tessellator.addVertexWithUV(f6 - f7, 0.0F - f8, 0.0D, f15, f5);
                tessellator.addVertexWithUV(f6 - f7, 1.0F - f8, 0.0D, f15, f4);
                tessellator.addVertexWithUV(0.0F - f7, 1.0F - f8, 0.0D, f14, f4);
                tessellator.draw();
                GL11.glPopMatrix();
            }
        }
    }

    @Shadow(remap = false)
    public boolean shouldSpreadItems() {
        return true;
    }

    @Shadow(remap = false)
    public byte getMiniBlockCount(ItemStack stack, byte original) {
        return original;
    }

    @Shadow(remap = false)
    public byte getMiniItemCount(ItemStack stack, byte original) {
        return original;
    }

}
