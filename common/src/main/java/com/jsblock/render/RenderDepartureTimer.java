package com.jsblock.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import mtr.block.IBlock;
import mtr.config.Config;
import mtr.data.IGui;
import mtr.data.Platform;
import mtr.data.RailwayData;
import mtr.data.Route;
import mtr.gui.ClientData;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.BlockEntityRendererMapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class RenderDepartureTimer<T extends BlockEntityMapper> extends BlockEntityRendererMapper<T> implements IGui {

	public RenderDepartureTimer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
		final Level world = entity.getLevel();
		final BlockPos pos = entity.getBlockPos();
		if (world == null) {
			return;
		}

		final Platform platform = RailwayData.getClosePlatform(ClientData.PLATFORMS, pos, 5, 3, 3);
		if (platform == null) {
			return;
		}

		final Set<Route.ScheduleEntry> schedules = ClientData.SCHEDULES_FOR_PLATFORM.get(platform.id);
		if (schedules == null) {
			return;
		}

		String timeRemaining = "";

		final List<Route.ScheduleEntry> scheduleList = new ArrayList<>(schedules);
		if (!scheduleList.isEmpty()) {
			Collections.sort(scheduleList);
			if (scheduleList.get(0).arrivalMillis - System.currentTimeMillis() > 0) {
				return;
			}

			/* remainingSecond = Train ETA */
			int remainingSecond = (int) (scheduleList.get(0).arrivalMillis - System.currentTimeMillis()) / 1000;
			/* seconds = dwell - ETA */
			int seconds = Math.abs((platform.getDwellTime() / 2) - Math.abs(remainingSecond));
			int minutes = seconds / 60;
			timeRemaining = String.format("%d:%02d", minutes % 60, seconds % 60);
		}

		/* This defines the font style. If MTR Font is enbled, use the font "jsblock:font". Otherwise don't add any style */
		final Style style = Config.useMTRFont() ? Style.EMPTY.withFont(new ResourceLocation("jsblock", "font")) : Style.EMPTY;
		final Direction facing = IBlock.getStatePropertySafe(world, pos, HorizontalDirectionalBlock.FACING);

		matrices.pushPose();
		if (facing == Direction.SOUTH) {
			matrices.translate(0.73, 0.52, 0.43);
		}

		if (facing == Direction.NORTH) {
			matrices.translate(0.28, 0.52, 0.57);
		}

		if (facing == Direction.EAST) {
			matrices.translate(0.43, 0.52, 0.28);
		}

		if (facing == Direction.WEST) {
			matrices.translate(0.57, 0.52, 0.73);
		}

		matrices.mulPose(Vector3f.ZP.rotationDegrees(180));
		matrices.mulPose(Vector3f.YP.rotationDegrees(facing.toYRot()));
		matrices.scale(0.021F, 0.021F, 0.021F);

		final Font textRenderer = Minecraft.getInstance().font;
		final Component formattedText = new TextComponent(timeRemaining).setStyle(style);
		textRenderer.draw(matrices, formattedText, 0, 0, 0xEE2233);
		matrices.popPose();
	}
}