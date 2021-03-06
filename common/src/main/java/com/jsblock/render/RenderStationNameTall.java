package com.jsblock.render;

import com.jsblock.config.ClientConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import mtr.block.BlockStationNameBase;
import mtr.block.BlockStationNameTallBase;
import mtr.client.IDrawing;
import mtr.data.IGui;
import mtr.render.RenderStationNameBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;

public class RenderStationNameTall<T extends BlockStationNameTallBase.TileEntityStationNameTallBase> extends RenderStationNameBase<T> {

	public RenderStationNameTall(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	protected void drawStationName(BlockStationNameBase.TileEntityStationNameBase entity, PoseStack matrices, MultiBufferSource vertexConsumers, MultiBufferSource.BufferSource immediate, String stationName, int color, int light) {
		if(ClientConfig.getRenderDisabled()) return;
		IDrawing.drawStringWithFont(matrices, Minecraft.getInstance().font, immediate, IGui.formatVerticalChinese(stationName), HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 0, -0.12F, 0.73F, 1.0F, 80, color, false, light, null);
	}
}

