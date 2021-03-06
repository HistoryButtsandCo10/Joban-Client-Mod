package com.jsblock.blocks;

import com.jsblock.BlockEntityTypes;
import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PIDSRV extends PIDSRVBase {

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
		VoxelShape shape1 = IBlock.getVoxelShapeByDirection(5.9, -3.0, 0, 10.1, 11, 12, IBlock.getStatePropertySafe(state, FACING));
		VoxelShape shape2 = IBlock.getVoxelShapeByDirection(7.5, 11, 8.5, 8.5, 16, 9.5, IBlock.getStatePropertySafe(state, FACING));
		return Shapes.or(shape1, shape2);
	}

	@Override
	public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
		return new TileEntityBlockPIDSRV(pos, state);
	}

	public static class TileEntityBlockPIDSRV extends TileEntityBlockPIDSBase {

		public static final int MAX_ARRIVALS = 4;

		public TileEntityBlockPIDSRV(BlockPos pos, BlockState state) {
			super(BlockEntityTypes.PIDS_RV_TILE_ENTITY.get(), pos, state);
		}

		@Override
		protected int getMaxArrivals() {
			return MAX_ARRIVALS;
		}
	}
}