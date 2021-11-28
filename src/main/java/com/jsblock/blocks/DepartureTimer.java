package com.jsblock.blocks;

import com.jsblock.Blocks;
import mtr.block.IBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class DepartureTimer extends HorizontalFacingBlock implements BlockEntityProvider {

    public DepartureTimer(Settings settings) {
        super(settings);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getPlayerFacing());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        return IBlock.getVoxelShapeByDirection(2.7, 0, 0, 13.3, 10.7, 13, facing);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new TileEntityDepartureTimer();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public static class TileEntityDepartureTimer extends BlockEntity {

        public TileEntityDepartureTimer() {
            super(Blocks.DEPARTURE_TIMER_TILE_ENTITY);
        }
    }
}