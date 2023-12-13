package com.userofbricks.refined_lost_cities.world.level.levelgen.structure.pools;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.userofbricks.refined_lost_cities.init.StructurePoolElementTypeInit;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StructureAsPoolElement extends StructurePoolElement {
    public static final Codec<StructureAsPoolElement> CODEC = RecordCodecBuilder.create((recordCodecBuilderInstance) ->
            recordCodecBuilderInstance.group(
                            Structure.CODEC.fieldOf("structure").forGetter((p_210215_) -> p_210215_.structureHolder))
                    .apply(recordCodecBuilderInstance, StructureAsPoolElement::new));
    private final Holder<Structure> structureHolder;
    protected StructureAsPoolElement(Holder<Structure> structureHolder) {
        super(StructureTemplatePool.Projection.RIGID);
        this.structureHolder = structureHolder;
    }
    private CompoundTag fillDefaultJigsawNBT() {
        CompoundTag compoundtag = new CompoundTag();
        compoundtag.putString("name", "minecraft:bottom");
        compoundtag.putString("final_state", "minecraft:air");
        compoundtag.putString("pool", "minecraft:empty");
        compoundtag.putString("target", "minecraft:empty");
        compoundtag.putString("joint", JigsawBlockEntity.JointType.ROLLABLE.getSerializedName());
        return compoundtag;
    }

    @Override
    public Vec3i getSize(StructureTemplateManager pStructureTemplateManager, Rotation pRotation) {return Vec3i.ZERO;}

    @Override
    public List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(StructureTemplateManager pStructureTemplateManager, BlockPos pPos, Rotation pRotation, RandomSource pRandom) {
        List<StructureTemplate.StructureBlockInfo> list = Lists.newArrayList();
        list.add(new StructureTemplate.StructureBlockInfo(pPos, Blocks.JIGSAW.defaultBlockState().setValue(JigsawBlock.ORIENTATION, FrontAndTop.fromFrontAndTop(Direction.DOWN, Direction.SOUTH)), this.fillDefaultJigsawNBT()));
        return list;
    }

    @Override
    public BoundingBox getBoundingBox(StructureTemplateManager pStructureTemplateManager, BlockPos pPos, Rotation pRotation) {
        Vec3i vec3i = this.getSize(pStructureTemplateManager, pRotation);
        return new BoundingBox(pPos.getX(), pPos.getY(), pPos.getZ(), pPos.getX() + vec3i.getX(), pPos.getY() + vec3i.getY(), pPos.getZ() + vec3i.getZ());
    }

    @Override
    public boolean place(StructureTemplateManager pStructureTemplateManager, WorldGenLevel pLevel, StructureManager pStructureManager, ChunkGenerator pGenerator, BlockPos structurePosition, BlockPos p_227341_, Rotation pRotation, BoundingBox pBox, RandomSource pRandom, boolean p_227345_) {
        ChunkAccess chunkAccess = pLevel.getChunk(structurePosition);
        int i = fetchReferences(pStructureManager, chunkAccess, this.structureHolder.value());
        return this.structureHolder.value().generate(pStructureManager.registryAccess(), pGenerator, pGenerator.getBiomeSource(), pLevel.getLevel().getChunkSource().randomState(), pStructureTemplateManager, pLevel.getSeed(), new ChunkPos(structurePosition), i, pLevel, (p1) -> true) != StructureStart.INVALID_START;
    }

    private static int fetchReferences(StructureManager pStructureManager, ChunkAccess pChunk, Structure pStructure) {
        SectionPos pSectionPos = SectionPos.bottomOf(pChunk);
        StructureStart structurestart = pStructureManager.getStartForStructure(pSectionPos, pStructure, pChunk);
        return structurestart != null ? structurestart.getReferences() : 0;
    }

    @Override
    public StructurePoolElementType<?> getType() {
        return StructurePoolElementTypeInit.STRUCTURE_AS_POOL_ELEMENT.get();
    }

    public String toString() {
        return "StructureAsPoolElement[" + this.structureHolder + "]";
    }
}
