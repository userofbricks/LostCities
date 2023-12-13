package com.userofbricks.refined_lost_cities.worldgen.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class OvergrownCropBlock extends DoublePlantBlock {
    private final VoxelShape upper, lower, collision;
    public OvergrownCropBlock(Properties pProperties, VoxelShape upper, VoxelShape lower, VoxelShape collision) {
        super(pProperties);
        this.upper = upper;
        this.lower = lower;
        this.collision = collision;
    }
    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return true;
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (!pLevel.isAreaLoaded(pPos, 1)) return;// Forge: prevent loading unloaded chunks when checking neighbor's light
        if (pState.getValue(HALF) == DoubleBlockHalf.UPPER) return;
        if (pLevel.getRawBrightness(pPos, 0) >= 1) {
            GrassCoverBlock.spread(pLevel, pPos, pRandom);
        }
    }

    public boolean canBeReplaced(BlockState pState, BlockPlaceContext pUseContext) {
        return false;
    }

    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return collision;
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return pState.getValue(HALF) == DoubleBlockHalf.UPPER ? upper : lower;
    }

    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return true;
    }
}
