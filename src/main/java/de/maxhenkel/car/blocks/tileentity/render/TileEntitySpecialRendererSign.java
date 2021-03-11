package de.maxhenkel.car.blocks.tileentity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.car.blocks.BlockSign;
import de.maxhenkel.car.blocks.tileentity.TileEntitySign;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.vector.Vector3f;

public class TileEntitySpecialRendererSign extends TileEntityRenderer<TileEntitySign> {

    public TileEntitySpecialRendererSign(TileEntityRendererDispatcher tileEntityRendererDispatcher) {
        super(tileEntityRendererDispatcher);
    }

    @Override
    public void render(TileEntitySign te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light, int i) {
        matrixStack.pushPose();

        matrixStack.translate(0.5D, 1D, 0.5D);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(180F));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-te.getBlockState().getValue(BlockSign.FACING).toYRot()));

        //----------Front-----------
        matrixStack.pushPose();

        matrixStack.translate(0D, 0.27D, -0.51D / 16D);
        drawText(te.getText(0), matrixStack, buffer, light);
        matrixStack.translate(0D, 0.116D, 0D);
        drawText(te.getText(1), matrixStack, buffer, light);
        matrixStack.translate(0D, 0.116D, 0D);
        drawText(te.getText(2), matrixStack, buffer, light);
        matrixStack.translate(0D, 0.116D, 0D);
        drawText(te.getText(3), matrixStack, buffer, light);

        matrixStack.popPose();

        //----------Back-----------
        matrixStack.pushPose();

        matrixStack.mulPose(Vector3f.YP.rotationDegrees(180F));
        matrixStack.translate(0D, 0.27D, -0.51D / 16D);
        drawText(te.getText(4), matrixStack, buffer, light);
        matrixStack.translate(0D, 0.116D, 0D);
        drawText(te.getText(5), matrixStack, buffer, light);
        matrixStack.translate(0D, 0.116D, 0D);
        drawText(te.getText(6), matrixStack, buffer, light);
        matrixStack.translate(0D, 0.116D, 0D);
        drawText(te.getText(7), matrixStack, buffer, light);

        matrixStack.popPose();
        //-------------------------

        matrixStack.popPose();
    }

    private void drawText(String txt, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light) {
        FontRenderer font = renderer.getFont();
        matrixStack.pushPose();

        int textWidth = font.width(txt);
        float textScale = 0.008F;

        matrixStack.translate(-(textScale * textWidth) / 2D, 0D, 0D);
        matrixStack.scale(textScale, textScale, textScale);

        font.drawInBatch(txt, 0F, 0F, 0x0, false, matrixStack.last().pose(), buffer, false, 0, light);

        matrixStack.popPose();
    }

}
