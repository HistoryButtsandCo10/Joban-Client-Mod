package com.jsblock.forge;

import mtr.gui.ClientData;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.BlockEntityRendererMapper;
import mtr.mappings.NetworkUtilities;
import mtr.mappings.RegistryUtilitiesClient;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;
import java.util.function.Function;

public class RegistryClientImpl {

	public static void registerBlockRenderType(RenderType type, Block block) {
		RegistryUtilitiesClient.registerRenderType(type, block);
	}

	public static <T extends BlockEntityMapper> void registerTileEntityRenderer(BlockEntityType<T> type, Function<BlockEntityRenderDispatcher, BlockEntityRendererMapper<T>> function) {
		RegistryUtilitiesClient.registerTileEntityRenderer(type, function);
	}

	public static void registerBlockColors(Block block) {
		RegistryUtilitiesClient.registerBlockColors(new StationColor(), block);
	}

	public static void registerNetworkReceiver(ResourceLocation resourceLocation, Consumer<FriendlyByteBuf> consumer) {
		NetworkUtilities.registerReceiverS2C(resourceLocation, (packet, context) -> consumer.accept(packet));
	}

	public static void sendToServer(ResourceLocation id, FriendlyByteBuf packet) {
		NetworkUtilities.sendToServer(id, packet);
	}

	private static class StationColor implements BlockColor {

		@Override
		public int getColor(BlockState blockState, BlockAndTintGetter blockAndTintGetter, BlockPos pos, int i) {
			final int defaultColor = 0x7F7F7F;
			if (pos != null) {
				return ClientData.STATIONS.stream().filter(station1 -> station1.inArea(pos.getX(), pos.getZ())).findFirst().map(station2 -> station2.color).orElse(defaultColor);
			} else {
				return defaultColor;
			}
		}
	}
}
