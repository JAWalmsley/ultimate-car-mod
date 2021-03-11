package de.maxhenkel.car.blocks.tileentity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.maxhenkel.car.blocks.tileentity.TileEntitySplitTank;
import de.maxhenkel.car.fluids.ModFluids;
import de.maxhenkel.corelib.client.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraftforge.fluids.FluidStack;

public class TileEntitySpecialRendererSplitTank extends TileEntityRenderer<TileEntitySplitTank> {

    private static final FluidStack BIO_DIESEL_STACK = new FluidStack(ModFluids.BIO_DIESEL, 1);
    private static final FluidStack GLYCERIN_STACK = new FluidStack(ModFluids.GLYCERIN, 1);

    public TileEntitySpecialRendererSplitTank(TileEntityRendererDispatcher tileEntityRendererDispatcher) {
        super(tileEntityRendererDispatcher);
    }

    @Override
    public void render(TileEntitySplitTank te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light, int overlay) {
        matrixStack.pushPose();
        float bioDiesel = te.getBioDieselPerc() / 2F;
        float glycerin = te.getGlycerinPerc() / 2F;

        if (bioDiesel > 0) {
            renderFluid(BIO_DIESEL_STACK, bioDiesel, 1F / 16F, matrixStack, buffer, light, overlay);
        }

        if (glycerin > 0) {
            renderFluid(GLYCERIN_STACK, glycerin, bioDiesel + 1F / 16F, matrixStack, buffer, light, overlay);
        }
        matrixStack.popPose();
    }

    public void renderFluid(FluidStack fluid, float amount, float yStart, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light, int overlay) {
        matrixStack.pushPose();
        matrixStack.scale(0.98F, 0.98F, 0.98F);
        matrixStack.translate(0.01F, 0.01F, 0.01F);

        IVertexBuilder builder = buffer.getBuffer(Atlases.translucentItemSheet());
        TextureAtlasSprite texture = Minecraft.getInstance().getModelManager().getAtlas(PlayerContainer.BLOCK_ATLAS).getSprite(fluid.getFluid().getAttributes().getStillTexture());

        float uMin = texture.getU0();
        float uMax = texture.getU1();
        float vMin = texture.getV0();
        float vMax = texture.getV1();

        float vHeight = vMax - vMin;

        float s = 0F;

        // North
        RenderUtils.vertex(builder, matrixStack, 1F - s, yStart, 0F + s, uMax, vMin, light, overlay);
        RenderUtils.vertex(builder, matrixStack, 0F + s, yStart, 0F + s, uMin, vMin, light, overlay);
        RenderUtils.vertex(builder, matrixStack, 0F + s, yStart + amount - s * 2F, 0F + s, uMin, vMin + vHeight * amount, light, overlay);
        RenderUtils.vertex(builder, matrixStack, 1F - s, yStart + amount - s * 2F, 0F + s, uMax, vMin + vHeight * amount, light, overlay);

        // South
        RenderUtils.vertex(builder, matrixStack, 1F - s, yStart, 1F - s, uMin, vMin, light, overlay);
        RenderUtils.vertex(builder, matrixStack, 1F - s, yStart + amount - s * 2F, 1F - s, uMin, vMin + vHeight * amount, light, overlay);
        RenderUtils.vertex(builder, matrixStack, 0F + s, yStart + amount - s * 2F, 1F - s, uMax, vMin + vHeight * amount, light, overlay);
        RenderUtils.vertex(builder, matrixStack, 0F + s, yStart, 1F - s, uMax, vMin, light, overlay);

        // East
        RenderUtils.vertex(builder, matrixStack, 1F - s, yStart, 0F + s, uMin, vMin, light, overlay);
        RenderUtils.vertex(builder, matrixStack, 1F - s, yStart + amount - s * 2F, 0F + s, uMin, vMin + vHeight * amount, light, overlay);
        RenderUtils.vertex(builder, matrixStack, 1F - s, yStart + amount - s * 2F, 1F - s, uMax, vMin + vHeight * amount, light, overlay);
        RenderUtils.vertex(builder, matrixStack, 1F - s, yStart, 1F - s, uMax, vMin, light, overlay);

        // West
        RenderUtils.vertex(builder, matrixStack, 0F + s, yStart, 1F - s, uMin, vMin, light, overlay);
        RenderUtils.vertex(builder, matrixStack, 0F + s, yStart + amount - s * 2F, 1F - s, uMin, vMin + vHeight * amount, light, overlay);
        RenderUtils.vertex(builder, matrixStack, 0F + s, yStart + amount - s * 2F, 0F + s, uMax, vMin + vHeight * amount, light, overlay);
        RenderUtils.vertex(builder, matrixStack, 0F + s, yStart, 0F + s, uMax, vMin, light, overlay);

        // Up
        RenderUtils.vertex(builder, matrixStack, 0F + s, yStart + amount - s * 2F, 0F + s, uMax, vMax, light, overlay);
        RenderUtils.vertex(builder, matrixStack, 0F + s, yStart + amount - s * 2F, 1F - s, uMin, vMax, light, overlay);
        RenderUtils.vertex(builder, matrixStack, 1F - s, yStart + amount - s * 2F, 1F - s, uMin, vMin, light, overlay);
        RenderUtils.vertex(builder, matrixStack, 1F - s, yStart + amount - s * 2F, 0F + s, uMax, vMin, light, overlay);

        matrixStack.popPose();
    }

}
