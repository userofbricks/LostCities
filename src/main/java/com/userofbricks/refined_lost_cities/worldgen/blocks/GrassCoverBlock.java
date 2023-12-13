package com.userofbricks.refined_lost_cities.worldgen.blocks;

import com.userofbricks.refined_lost_cities.tags.CLCBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

import static net.minecraft.world.level.block.CropBlock.getGrowthSpeed;

@ParametersAreNonnullByDefault
public class GrassCoverBlock extends DoublePlantBlock implements BonemealableBlock {
    public static final IntegerProperty AGE = IntegerProperty.create("age", 1, 7);
    public static final IntegerProperty VARIANT = IntegerProperty.create("variant", 1, 6);
    public static final IntegerProperty MAX_AGE = IntegerProperty.create("max_age", 5, 7);
    protected GrassCoverBlock(Properties pProperties) {
        //replaceable
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(AGE, 1).setValue(VARIANT, 1).setValue(MAX_AGE, 7));
    }
    @Override
    public @NotNull VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return getShapeByAgeAndVariant(getAge(pState), getVariant(pState), getHalf(pState));
    }

    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return true;
    }

    public VoxelShape getShapeByAgeAndVariant(int age, int variant, DoubleBlockHalf half) {
        double[] x1 = new double[] {2, 4, 1, 0, 0, 1};
        double[] x2 = new double[] {14, 15, 12, 12, 15, 16};
        double[] z1 = new double[] {2, 3, 2, 0, 0, 1};
        double[] z2 = new double[] {14, 15, 14, 12, 15, 16};

        double[] y2Lower = new double[] {0, 5, 9, 15, 16, 16, 16};
        double[] y2Upper = new double[] {0, 0, 0, 0, 5, 11, 16};

        VoxelShape base = Block.box(0,0,0,16,1,16);
        return switch (half) {
            case UPPER -> Block.box(x1[variant-1], 0, z1[variant-1], x2[variant-1], y2Upper[age-1], z2[variant-1]);
            case LOWER -> Shapes.or(base, Block.box(x1[variant-1], 0, z1[variant-1], x2[variant-1], y2Lower[age-1], z2[variant-1]));
        };
    }
    public int getMaxAge(BlockState pState) {
        return pState.getValue(MAX_AGE);
    }
    public int getAge(BlockState pState) {
        return pState.getValue(AGE);
    }
    public int getVariant(BlockState pState) {
        return pState.getValue(VARIANT);
    }
    public DoubleBlockHalf getHalf(BlockState pState) {
        return pState.getValue(HALF);
    }

    /**
     * @return whether this block needs random ticking.
     */
    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return true;
    }

    /**
     * Performs a random tick on a block.
     */
    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (!pLevel.isAreaLoaded(pPos, 1)) return;// Forge: prevent loading unloaded chunks when checking neighbor's light
        if (pState.getValue(HALF) == DoubleBlockHalf.UPPER) return;
        int i = this.getAge(pState);
        if (!canBeGrass(pLevel, pPos)) {
            pLevel.setBlockAndUpdate(pPos, Blocks.AIR.defaultBlockState());
            if (i > 3) pLevel.setBlockAndUpdate(pPos.above(), Blocks.AIR.defaultBlockState());
        }
        else if (pLevel.getRawBrightness(pPos, 0) >= 1) {
            if (i < this.getMaxAge(pState) && (i + 1) <= (pLevel.getRawBrightness(pPos, 0))) {
                float f = getGrowthSpeed(this, pLevel, pPos);
                if (pRandom.nextInt((int) (40.0F / f) + 1) == 0) {
                    pLevel.setBlock(pPos, pState.setValue(AGE, i + 1), 2);
                    if (i > 3 && pLevel.getBlockState(pPos.above()).getBlock() instanceof AirBlock) pLevel.setBlock(pPos.above(), pState.setValue(AGE, i + 1).setValue(HALF, DoubleBlockHalf.UPPER), 2);
                }
            }
            spread(pLevel, pPos, pRandom);
        }
    }
    public static void spread(ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pRandom.nextInt(20) == 0) {
            BlockState blockstate = CLCBlocks.GRASS_COVER_BLOCK.get().defaultBlockState();
            BlockState blockstate2 = Blocks.GRASS_BLOCK.defaultBlockState();

            for(int j = 0; j < 4; ++j) {
                BlockPos blockpos = pPos.offset(pRandom.nextInt(3) - 1, pRandom.nextInt(5) - 3, pRandom.nextInt(3) - 1);
                BlockState currentState = pLevel.getBlockState(blockpos);
                BlockState belowState = pLevel.getBlockState(blockpos.below());
                if (canPropagate(pLevel, blockpos) && belowState.isFaceSturdy(pLevel, blockpos, Direction.UP) && !CLCBlockTags.GRASS_BLOCKS.matches(belowState)) {
                    if (currentState.is(Blocks.AIR)) {
                        pLevel.setBlockAndUpdate(blockpos, blockstate.setValue(VARIANT, pLevel.random.nextInt(1, 6)).setValue(MAX_AGE, pRandom.nextInt(5, 7)));
                    } else if (CLCBlocks.overgrown_conversions.containsKey(currentState.getBlock()) && pRandom.nextInt(3) == 0) {
                        if ((currentState.getBlock() instanceof CropBlock cropBlock && cropBlock.getAge(currentState) >= cropBlock.getMaxAge()) ||
                                (currentState.getBlock() instanceof PitcherCropBlock && currentState.getValue(PitcherCropBlock.AGE) >= PitcherCropBlock.MAX_AGE) ||
                                (!(currentState.getBlock() instanceof CropBlock) && !(currentState.getBlock() instanceof PitcherCropBlock)))
                            pLevel.setBlockAndUpdate(blockpos, CLCBlocks.overgrown_conversions.get(currentState.getBlock()).get().defaultBlockState());
                    }
                }

                blockpos = pPos.below().offset(pRandom.nextInt(3) - 1, pRandom.nextInt(5) - 3, pRandom.nextInt(3) - 1);
                if (pLevel.getBlockState(blockpos).is(Blocks.DIRT) && canPropagate(pLevel, blockpos)) {
                    pLevel.setBlockAndUpdate(blockpos, blockstate2);
                }
            }
        }
    }
    public static boolean canBeGrass(LevelReader pLevelReader, BlockPos pPos) {
        if (pLevelReader.getBlockState(pPos.above()).getFluidState().getAmount() == 8) {
            return false;
        } else {
            return pLevelReader.getRawBrightness(pPos, 0) >= 0;
        }
    }
    private static boolean canPropagate(LevelReader pLevel, BlockPos pPos) {
        BlockPos blockpos = pPos.above();
        return canBeGrass(pLevel, pPos) && !pLevel.getFluidState(blockpos).is(FluidTags.WATER);
    }
    @Override
    public boolean isValidBonemealTarget(LevelReader pLevel, BlockPos pPos, BlockState pState, boolean pIsClient) {
        return true;
    }
    @Override
    public boolean isBonemealSuccess(Level pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
        return true;
    }
    @Override
    public void performBonemeal(ServerLevel pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
        int i = Math.min(this.getAge(pState) + Mth.nextInt(pLevel.random, 2, 5), this.getMaxAge(pState));
        pLevel.setBlock(pPos, pState.setValue(AGE, i), 2);
        if (i > 3) pLevel.setBlock(pPos.above(), pLevel.getBlockState(pPos.above()).setValue(AGE, i), 2);
    }
    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }
    @Override
    protected boolean mayPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        BlockPos blockpos = pPos.below();
        BlockState blockstate = pLevel.getBlockState(blockpos);
        return (blockstate.isFaceSturdy(pLevel, blockpos, Direction.UP) || pState.is(Blocks.HOPPER)) && (pLevel.getBlockState(pPos.above()).getBlock() instanceof AirBlock || pLevel.getBlockState(pPos.above()).getBlock() instanceof GrassCoverBlock);
    }
    /**
     * Called by BlockItem after this block has been placed.
     */
    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {}
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState blockState = super.getStateForPlacement(pContext);
        return blockState != null ?  blockState.setValue(VARIANT, pContext.getLevel().random.nextInt(1,6)) : null;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(HALF).add(AGE).add(VARIANT).add(MAX_AGE);
    }
}
