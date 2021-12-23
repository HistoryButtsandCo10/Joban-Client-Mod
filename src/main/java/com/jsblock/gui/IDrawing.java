package com.jsblock.gui;

import com.jsblock.Joestu;
import minecraftmappings.UtilitiesClient;
import mtr.config.Config;
import mtr.data.IGui;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public interface IDrawing {
    static void drawStringWithFont(MatrixStack matrices, TextRenderer textRenderer, VertexConsumerProvider.Immediate immediate, String text, IGui.HorizontalAlignment horizontalAlignment, IGui.VerticalAlignment verticalAlignment, float x, float y, float maxWidth, float maxHeight, float scale, int textColor, boolean shadow, int light, String fontChinese, String fontEnglish) {
        drawStringWithFont(matrices, textRenderer, immediate, text, horizontalAlignment, verticalAlignment, horizontalAlignment, x, y, maxWidth, maxHeight, scale, textColor, shadow, light, fontChinese, fontEnglish, null);
    }

    static void drawStringWithFont(MatrixStack matrices, TextRenderer textRenderer, VertexConsumerProvider.Immediate immediate, String text, IGui.HorizontalAlignment horizontalAlignment, IGui.VerticalAlignment verticalAlignment, IGui.HorizontalAlignment xAlignment, float x, float y, float maxWidth, float maxHeight, float scale, int textColor, boolean shadow, int light, String fontChinese, String fontEnglish, mtr.gui.IDrawing.DrawingCallback drawingCallback) {
        final Style styleChinese = Config.useMTRFont() ? Style.EMPTY.withFont(new Identifier(Joestu.MOD_ID, fontChinese)) : Style.EMPTY;
        final Style styleEnglish = Config.useMTRFont() ? Style.EMPTY.withFont(new Identifier(Joestu.MOD_ID, fontEnglish)) : Style.EMPTY;

        while (text.contains("||")) {
            text = text.replace("||", "|");
        }
        final String[] stringSplit = text.split("\\|");

        final List<Boolean> isCJKList = new ArrayList<>();
        final List<OrderedText> orderedTexts = new ArrayList<>();
        int totalHeight = 0, totalWidth = 0;
        for (final String stringSplitPart : stringSplit) {
            final boolean isCJK = stringSplitPart.codePoints().anyMatch(Character::isIdeographic);
            isCJKList.add(isCJK);

            final OrderedText orderedText = isCJK ? new LiteralText(stringSplitPart).fillStyle(styleChinese).asOrderedText() : new LiteralText(stringSplitPart).fillStyle(styleEnglish).asOrderedText();
            orderedTexts.add(orderedText);

            totalHeight += IGui.LINE_HEIGHT * (isCJK ? 2 : 1);
            final int width = textRenderer.getWidth(orderedText) * (isCJK ? 2 : 1);
            if (width > totalWidth) {
                totalWidth = width;
            }
        }

        if (maxHeight >= 0 && totalHeight / scale > maxHeight) {
            scale = totalHeight / maxHeight;
        }

        matrices.push();

        final float totalWidthScaled;
        final float scaleX;
        if (maxWidth >= 0 && totalWidth > maxWidth * scale) {
            totalWidthScaled = maxWidth * scale;
            scaleX = totalWidth / maxWidth;
        } else {
            totalWidthScaled = totalWidth;
            scaleX = scale;
        }
        matrices.scale(1 / scaleX, 1 / scale, 1 / scale);

        float offset = verticalAlignment.getOffset(y * scale, totalHeight);
        for (int i = 0; i < orderedTexts.size(); i++) {
            final boolean isCJK = isCJKList.get(i);
            final int extraScale = isCJK ? 2 : 1;
            if (isCJK) {
                matrices.push();
                matrices.scale(extraScale, extraScale, 1);
            }

            final float xOffset = horizontalAlignment.getOffset(xAlignment.getOffset(x * scaleX, totalWidth), textRenderer.getWidth(orderedTexts.get(i)) * extraScale - totalWidth);

            final float shade = light == IGui.MAX_LIGHT_GLOWING ? 1 : Math.min(LightmapTextureManager.getBlockLightCoordinates(light) / 16F * 0.1F + 0.7F, 1);
            final int a = (textColor >> 24) & 0xFF;
            final int r = (int) (((textColor >> 16) & 0xFF) * shade);
            final int g = (int) (((textColor >> 8) & 0xFF) * shade);
            final int b = (int) ((textColor & 0xFF) * shade);

            if (immediate != null) {
                textRenderer.draw(orderedTexts.get(i), xOffset / extraScale, offset / extraScale, (a << 24) + (r << 16) + (g << 8) + b, shadow, UtilitiesClient.getModel(matrices.peek()), immediate, false, 0, light);
            }

            if (isCJK) {
                matrices.pop();
            }

            offset += IGui.LINE_HEIGHT * extraScale;
        }

        matrices.pop();

        if (drawingCallback != null) {
            final float x1 = xAlignment.getOffset(x, totalWidthScaled / scale);
            final float y1 = verticalAlignment.getOffset(y, totalHeight / scale);
            drawingCallback.drawingCallback(x1, y1, x1 + totalWidthScaled / scale, y1 + totalHeight / scale);
        }
    }

    static void drawRectangle(VertexConsumer vertexConsumer, double x1, double y1, double x2, double y2, int color) {
        final int a = (color >> 24) & 0xFF;
        final int r = (color >> 16) & 0xFF;
        final int g = (color >> 8) & 0xFF;
        final int b = color & 0xFF;
        if (a == 0) {
            return;
        }
        vertexConsumer.vertex(x1, y1, 0).color(r, g, b, a).next();
        vertexConsumer.vertex(x1, y2, 0).color(r, g, b, a).next();
        vertexConsumer.vertex(x2, y2, 0).color(r, g, b, a).next();
        vertexConsumer.vertex(x2, y1, 0).color(r, g, b, a).next();
    }
}