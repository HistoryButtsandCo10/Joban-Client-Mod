package com.jsblock.render;

import com.jsblock.blocks.KCRNameSign;
import com.jsblock.config.ClientConfig;
import com.jsblock.gui.IDrawingJoban;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Vector3f;
import mtr.block.IBlock;
import mtr.client.ClientData;
import mtr.data.IGui;
import mtr.data.RailwayData;
import mtr.data.Station;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.BlockEntityRendererMapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;

public class RenderKCRStationName<T extends BlockEntityMapper> extends BlockEntityRendererMapper<T> implements IGui {

	public RenderKCRStationName(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
		final Level world = entity.getLevel();
		final BlockPos pos = entity.getBlockPos();
		if (world == null || ClientConfig.getRenderDisabled()) {
			return;
		}

		Station station = RailwayData.getStation(ClientData.STATIONS, ClientData.DATA_CACHE, pos);
		final Direction facing = IBlock.getStatePropertySafe(world, pos, HorizontalDirectionalBlock.FACING);
		Boolean exitOnLeft = IBlock.getStatePropertySafe(world, pos, KCRNameSign.EXIT_ON_LEFT);
		double offset = exitOnLeft ? 0.5 : 0;

		for (int i = 0; i < 2; i++) {
			Direction newFacing = i == 1 ? facing.getOpposite() : facing;
			offset = i == 1 ? !exitOnLeft ? 0.5 : 0 : offset;

			matrices.pushPose();
			if (newFacing == Direction.SOUTH) {
				matrices.translate(0.69 - offset, 0.53, 0.33);
			}

			if (newFacing == Direction.NORTH) {
				matrices.translate(0.31 + offset, 0.53, 0.67);
			}

			if (newFacing == Direction.EAST) {
				matrices.translate(0.33, 0.53, 0.31 + offset);
			}

			if (newFacing == Direction.WEST) {
				matrices.translate(0.67, 0.53, 0.69 - offset);
			}

			matrices.mulPose(Vector3f.ZP.rotationDegrees(180));
			matrices.mulPose(Vector3f.YP.rotationDegrees(newFacing.toYRot()));
			matrices.scale(0.021F, 0.021F, 0.021F);

			final Font textRenderer = Minecraft.getInstance().font;
			final String stationName = station == null ? new TranslatableComponent("gui.mtr.untitled").getString() : station.name;
			final MultiBufferSource.BufferSource immediate = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
			IDrawingJoban.drawStringWithFont(matrices, textRenderer, immediate, stationName, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 0, 0, 60, 32, 1, false, 0xEEEEEE, false, MAX_LIGHT_GLOWING, ClientConfig.getKCRSignChinFont(), ClientConfig.getKCRSignEngFont());
			immediate.endBatch();
			matrices.popPose();
		}
	}
}
