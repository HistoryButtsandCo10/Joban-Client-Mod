package com.jsblock.blocks;

import com.jsblock.Joestu;
import minecraftmappings.BlockEntityMapper;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class SignalLightGreen extends mtr.block.BlockSignalLightBase {

    public SignalLightGreen(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntitySignalLightGreen(pos, state);
    }

    public static class TileEntitySignalLightGreen extends BlockEntityMapper {

        public TileEntitySignalLightGreen(BlockPos pos, BlockState state) {
            super(Joestu.SIGNAL_LIGHT_GREEN_ENTITY, pos, state);
        }
    }
}