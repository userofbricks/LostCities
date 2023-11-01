package com.userofbricks.refined_lost_cities.tags;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.utility.Lang;
import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider.IntrinsicTagAppender;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import static com.simibubi.create.AllTags.optionalTag;
import static com.userofbricks.refined_lost_cities.CreateLostCities.REGISTRATE;
import static com.userofbricks.refined_lost_cities.tags.NameSpace.LOSTCITIES;
import static com.userofbricks.refined_lost_cities.tags.NameSpace.MOD;

public enum CLCBlockTags {

    GRASS_BLOCKS,
    FOLIAGE(LOSTCITIES),
    ROTATABLE(LOSTCITIES),
    EASY_BREAKABLE(LOSTCITIES),
    NOT_BREAKABLE(LOSTCITIES),
    LIGHTS(LOSTCITIES)
    ;

    public final TagKey<Block> tag;

    CLCBlockTags() {
        this(MOD);
    }

    CLCBlockTags(NameSpace namespace) {
        this(namespace, namespace.optionalDefault);
    }

    CLCBlockTags(NameSpace namespace, String path) {
        this(namespace, path, namespace.optionalDefault);
    }

    CLCBlockTags(NameSpace namespace, boolean optional) {
        this(namespace, null, optional);
    }

    CLCBlockTags(NameSpace namespace, String path, boolean optional) {
        ResourceLocation id = new ResourceLocation(namespace.id, path == null ? Lang.asId(name()) : path);
        if (optional) {
            tag = optionalTag(ForgeRegistries.BLOCKS, id);
        } else {
            tag = BlockTags.create(id);
        }
    }

    @SuppressWarnings("deprecation")
    public boolean matches(Block block) {
        return block.builtInRegistryHolder()
                .is(tag);
    }

    public boolean matches(ItemStack stack) {
        return stack != null && stack.getItem() instanceof BlockItem blockItem && matches(blockItem.getBlock());
    }

    public boolean matches(BlockState state) {
        return state.is(tag);
    }

    public static void init() {
        REGISTRATE.get().addDataGenerator(ProviderType.BLOCK_TAGS, tagsProvider -> {
            tagsProvider.addTag(GRASS_BLOCKS.tag).add(Blocks.GRASS_BLOCK);


            //lost cities tags but created completely differently for ease of keeping track of how things work
            tagsProvider.addTag(FOLIAGE.tag).addTags(BlockTags.CORAL_PLANTS, BlockTags.BAMBOO_BLOCKS, BlockTags.LOGS,
                    BlockTags.LEAVES, BlockTags.SAPLINGS, BlockTags.FLOWERS);
            tagsProvider.addTag(ROTATABLE.tag).addTag(net.minecraft.tags.BlockTags.STAIRS).add(AllBlocks.TRACK.get()).add(AllBlocks.METAL_GIRDER.get());
            tagsProvider.addTag(EASY_BREAKABLE.tag).addTag(Tags.Blocks.GLASS);
            tagsProvider.addTag(NOT_BREAKABLE.tag).add(Blocks.BEDROCK, Blocks.END_PORTAL, Blocks.END_PORTAL_FRAME, Blocks.END_GATEWAY);
            IntrinsicTagAppender<Block> lights = tagsProvider.addTag(LIGHTS.tag);
            for (Block block : ForgeRegistries.BLOCKS.getValues()) {
                if (block.defaultBlockState().getLightEmission() > 0) {
                    lights.add(block);
                }
            }
        });
    }
}
