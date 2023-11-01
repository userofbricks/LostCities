package com.userofbricks.refined_lost_cities.worldgen.blocks;

import com.simibubi.create.foundation.utility.animation.Force;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.userofbricks.refined_lost_cities.CreateLostCities;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.client.model.generators.ConfiguredModel;

import static com.userofbricks.refined_lost_cities.CreateLostCities.REGISTRATE;

public class CLCBlocks {
    public static final BlockEntry<GrassCoverBlock> GRASS_COVER_BLOCK = REGISTRATE.get().block("grass_cover", properties -> new GrassCoverBlock(properties.replaceable().noCollission().instabreak().sound(SoundType.GRASS).ignitedByLava().pushReaction(PushReaction.DESTROY)))
            .blockstate((ctx, prov) -> prov.getVariantBuilder(ctx.get()).forAllStatesExcept(blockState -> {
                int age = blockState.getValue(GrassCoverBlock.AGE);
                int variant = blockState.getValue(GrassCoverBlock.VARIANT);
                boolean lowerHalf = blockState.getValue(GrassCoverBlock.HALF) == DoubleBlockHalf.LOWER;
                String s = "block/grass/grass_cover_stage_" + (age) + (age > 1 ? ("_var" + (variant)) : "");
                CreateLostCities.LOGGER.debug("about to set: " + (lowerHalf ? s : ("block/grass/grass_cover_upper")));
                return lowerHalf ? ConfiguredModel.builder().modelFile(prov.models().getExistingFile(prov.modLoc(s))).build() :
                        ConfiguredModel.builder().modelFile(prov.models().getExistingFile(prov.modLoc("block/grass/grass_cover_upper"))).build();
        }, GrassCoverBlock.MAX_AGE))
            .tag(BlockTags.MINEABLE_WITH_HOE)
            .simpleItem()
            .loot((brlt, block) -> brlt.add(block, brlt.createDoublePlantWithSeedDrops(block, block)))
            .register();
    public static void loadClass() {
    }
}
