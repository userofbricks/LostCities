package com.userofbricks.refined_lost_cities.worldgen.blocks;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.client.model.generators.ConfiguredModel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.userofbricks.refined_lost_cities.CreateLostCities.REGISTRATE;

public class CLCBlocks {
    public static final BlockEntry<GrassCoverBlock> GRASS_COVER_BLOCK = REGISTRATE.get().block("grass_cover",
                    properties -> new GrassCoverBlock(
                            properties.mapColor(MapColor.GRASS)
                                    .replaceable().noCollission().instabreak()
                                    .sound(SoundType.GRASS).ignitedByLava().pushReaction(PushReaction.DESTROY)))
            .blockstate((ctx, prov) -> prov.getVariantBuilder(ctx.get()).forAllStatesExcept(blockState -> {
                int age = blockState.getValue(GrassCoverBlock.AGE);
                int variant = blockState.getValue(GrassCoverBlock.VARIANT);
                boolean lowerHalf = blockState.getValue(GrassCoverBlock.HALF) == DoubleBlockHalf.LOWER;
                String s = "block/grass/grass_cover_stage_" + (age) + (age > 1 ? ("_var" + (variant)) : "");
                //CreateLostCities.LOGGER.debug("about to set: " + (lowerHalf ? s : ("block/grass/grass_cover_upper")));
                return lowerHalf ? ConfiguredModel.builder().modelFile(prov.models().getExistingFile(prov.modLoc(s))).build() :
                        noModel(prov);
        }, GrassCoverBlock.MAX_AGE))
            .color(() -> CLCBlocks::getGrassColor)
            .tag(BlockTags.MINEABLE_WITH_HOE)
            .item()
            .color(() -> () -> (stack, pTintIndex) -> GrassColor.getDefaultColor())
            .build()
            .loot((brlt, block) -> brlt.add(block, brlt.createDoublePlantWithSeedDrops(block, block)))
            .register();
    public static final BlockEntry<OvergrownCropBlock> OVERGROWN_WHEAT = REGISTRATE.get().block("overgrown_wheat",
                    properties -> new OvergrownCropBlock(
                            properties.instabreak().sound(SoundType.GRASS).ignitedByLava().pushReaction(PushReaction.DESTROY),
                            Shapes.or(Block.box(0,0,0,16, 16, 16)),
                            Block.box(0,0,0,16, 16, 16),
                            Shapes.empty()))
            .blockstate(CLCBlocks::overgrownCropModel)
            .color(() -> CLCBlocks::getGrassColor)
            .tag(BlockTags.MINEABLE_WITH_HOE)
            .item()
            .color(() -> () -> ((stack, pTintIndex) -> GrassColor.getDefaultColor()))
            .build()
            .loot((r, b) -> overgrownCropLoot(r, b, Items.WHEAT, Items.WHEAT_SEEDS))
            .register();
    public static final BlockEntry<OvergrownCropBlock> OVERGROWN_TORCHFLOWER = REGISTRATE.get().block("overgrown_torchflower",
                    properties -> new OvergrownCropBlock(
                            properties.instabreak().sound(SoundType.GRASS).ignitedByLava().pushReaction(PushReaction.DESTROY),
                            Shapes.or(Block.box(0,0,0,16, 16, 16)),
                            Block.box(0,0,0,16, 16, 16),
                            Shapes.empty()))
            .blockstate(CLCBlocks::overgrownCropModel)
            .color(() -> CLCBlocks::getGrassColor)
            .tag(BlockTags.MINEABLE_WITH_HOE)
            .item()
            .color(() -> () -> ((stack, pTintIndex) -> GrassColor.getDefaultColor()))
            .build()
            .loot((r, b) -> overgrownCropLoot(r, b, Blocks.TORCHFLOWER.asItem(), Items.TORCHFLOWER_SEEDS))
            .register();
    public static final BlockEntry<OvergrownCropBlock> OVERGROWN_POTATOES = REGISTRATE.get().block("overgrown_potatoes",
                    properties -> new OvergrownCropBlock(
                            properties.instabreak().sound(SoundType.GRASS).ignitedByLava().pushReaction(PushReaction.DESTROY),
                            Shapes.or(Block.box(0,0,0,16, 16, 16)),
                            Block.box(0,0,0,16, 16, 16),
                            Shapes.empty()))
            .blockstate(CLCBlocks::overgrownCropModel)
            .color(() -> CLCBlocks::getGrassColor)
            .tag(BlockTags.MINEABLE_WITH_HOE)
            .item()
            .color(() -> () -> ((stack, pTintIndex) -> GrassColor.getDefaultColor()))
            .build()
            .loot((r, b) -> overgrownCropLoot(r, b, Items.POISONOUS_POTATO, Items.POTATO))
            .register();
    public static final BlockEntry<OvergrownCropBlock> OVERGROWN_CARROTS = REGISTRATE.get().block("overgrown_carrots",
                    properties -> new OvergrownCropBlock(
                            properties.instabreak().sound(SoundType.GRASS).ignitedByLava().pushReaction(PushReaction.DESTROY),
                            Shapes.or(Block.box(0,0,0,16, 16, 16)),
                            Block.box(0,0,0,16, 16, 16),
                            Shapes.empty()))
            .blockstate(CLCBlocks::overgrownCropModel)
            .color(() -> CLCBlocks::getGrassColor)
            .tag(BlockTags.MINEABLE_WITH_HOE)
            .item()
            .color(() -> () -> ((stack, pTintIndex) -> GrassColor.getDefaultColor()))
            .build()
            .loot((r, b) -> overgrownCropLoot(r, b, Items.CARROT, Items.CARROT))
            .register();
    public static final BlockEntry<OvergrownCropBlock> OVERGROWN_BEETROOTS = REGISTRATE.get().block("overgrown_beetroots",
                    properties -> new OvergrownCropBlock(
                            properties.instabreak().sound(SoundType.GRASS).ignitedByLava().pushReaction(PushReaction.DESTROY),
                            Shapes.or(Block.box(0,0,0,16, 16, 16)),
                            Block.box(0,0,0,16, 16, 16),
                            Shapes.empty()))
            .blockstate(CLCBlocks::overgrownCropModel)
            .color(() -> CLCBlocks::getGrassColor)
            .tag(BlockTags.MINEABLE_WITH_HOE)
            .item()
            .color(() -> () -> ((stack, pTintIndex) -> GrassColor.getDefaultColor()))
            .build()
            .loot((r, b) -> overgrownCropLoot(r, b, Items.BEETROOT, Items.BEETROOT_SEEDS))
            .register();
    public static final BlockEntry<OvergrownCropBlock> OVERGROWN_PITCHER = REGISTRATE.get().block("overgrown_pitcher",
                    properties -> new OvergrownCropBlock(
                            properties.instabreak().sound(SoundType.GRASS).ignitedByLava().pushReaction(PushReaction.DESTROY),
                            Shapes.or(Block.box(0,0,0,16, 16, 16)),
                            Block.box(0,0,0,16, 16, 16),
                            Shapes.empty()))
            .blockstate(CLCBlocks::overgrownCropModel)
            .color(() -> CLCBlocks::getGrassColor)
            .tag(BlockTags.MINEABLE_WITH_HOE)
            .item()
            .color(() -> () -> ((stack, pTintIndex) -> GrassColor.getDefaultColor()))
            .build()
            .loot((r, b) -> overgrownCropLoot(r, b, Items.PITCHER_PLANT, Items.PITCHER_POD))
            .register();

    public static final Map<Block, Supplier<Block>> overgrown_conversions = new HashMap<>();
    public static void loadClass() {
        //create a map of overgrown blocks and what creates them
        addOvergrownBlock(Blocks.WHEAT, CLCBlocks.OVERGROWN_WHEAT::get);
        addOvergrownBlock(Blocks.TORCHFLOWER, CLCBlocks.OVERGROWN_TORCHFLOWER::get);
        addOvergrownBlock(Blocks.POTATOES, CLCBlocks.OVERGROWN_POTATOES::get);
        addOvergrownBlock(Blocks.CARROTS, CLCBlocks.OVERGROWN_CARROTS::get);
        addOvergrownBlock(Blocks.BEETROOTS, CLCBlocks.OVERGROWN_BEETROOTS::get);
        addOvergrownBlock(Blocks.PITCHER_CROP, CLCBlocks.OVERGROWN_PITCHER::get);
        addOvergrownBlock(Blocks.PITCHER_PLANT, CLCBlocks.OVERGROWN_PITCHER::get);
    }

    public static void overgrownCropLoot(RegistrateBlockLootTables rblt, OvergrownCropBlock block, Item pGrownCropItem, Item pSeedsItem) {
        LootPoolEntryContainer.Builder<?> tall_grass_loot = LootItem.lootTableItem(block)
                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                .when(MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.SHEARS)))
                .otherwise(rblt.applyExplosionCondition(block, LootItem.lootTableItem(Items.WHEAT_SEEDS))
                    .when(LootItemRandomChanceCondition.randomChance(0.125F)));
        LootPoolEntryContainer.Builder<?> crop_drop = LootItem.lootTableItem(pSeedsItem)
                .apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3))
                .when(LootItemRandomChanceCondition.randomChance(0.125f))
                .otherwise(LootItem.lootTableItem(pGrownCropItem)
                        .apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 1))
                        .when(LootItemRandomChanceCondition.randomChance(0.125f)));
        LootTable.Builder builder = LootTable.lootTable();
        addDoublePlantDrops(builder, tall_grass_loot, block);
        addDoublePlantDrops(builder, crop_drop, block);
        rblt.add(block, builder);
    }

    public static void addDoublePlantDrops(LootTable.Builder loottable, LootPoolEntryContainer.Builder<?> builder, Block pBlock) {
        loottable.withPool(LootPool.lootPool().add(builder)
            .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(pBlock)
                    .setProperties(StatePropertiesPredicate.Builder.properties()
                            .hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER)))
            .when(LocationCheck.checkLocation(LocationPredicate.Builder.location()
                    .setBlock(BlockPredicate.Builder.block().of(pBlock)
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER).build())
                            .build()),
                    new BlockPos(0, 1, 0))))
        .withPool(LootPool.lootPool().add(builder)
            .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(pBlock)
                    .setProperties(StatePropertiesPredicate.Builder.properties()
                            .hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER)))
            .when(LocationCheck.checkLocation(LocationPredicate.Builder.location()
                    .setBlock(BlockPredicate.Builder.block().of(pBlock)
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER).build())
                            .build()),
                    new BlockPos(0, -1, 0))));
    }

    public static void overgrownCropModel(DataGenContext<Block, OvergrownCropBlock> ctx, RegistrateBlockstateProvider prov) {
        prov.getVariantBuilder(ctx.get()).forAllStates(blockState -> {
            boolean lowerHalf = blockState.getValue(GrassCoverBlock.HALF) == DoubleBlockHalf.LOWER;
            String modelLocation = "block/" + ctx.getName();
            return lowerHalf ? ConfiguredModel.allYRotations(prov.models().getExistingFile(prov.modLoc(modelLocation)), 0, false) :
                    noModel(prov);
        });
    }

    public static ConfiguredModel[] noModel(RegistrateBlockstateProvider prov) {
        return ConfiguredModel.builder().modelFile(prov.models().getExistingFile(prov.modLoc("block/no_model"))).build();
    }

    public static BlockColor getGrassColor() {
        return (state, world, pos, layer) -> world != null && pos != null ? BiomeColors.getAverageGrassColor(world, pos) : GrassColor.getDefaultColor();
    }

    private static void addOvergrownBlock(Block block, Supplier<Block> overgrown_block) {
        overgrown_conversions.put(block, overgrown_block);
    }
}
