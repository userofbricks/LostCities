package com.userofbricks.refined_lost_cities.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.userofbricks.refined_lost_cities.init.StructureTypeInit;
import com.userofbricks.refined_lost_cities.worldgen.blocks.BuildingGenAirBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BuildingStructure extends Structure {
    public static final Codec<BuildingStructure> CODEC = RecordCodecBuilder.<BuildingStructure>mapCodec(instance ->
            instance.group(BuildingStructure.settingsCodec(instance),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
                    ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
                    Codec.intRange(0, 50).fieldOf("size").forGetter(structure -> structure.size),
                    Codec.intRange(1, 256).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter)
            ).apply(instance, BuildingStructure::new)).codec();

    private final Holder<StructureTemplatePool> startPool;
    private final Optional<ResourceLocation> startJigsawName;
    private final int size;
    private final int maxDistanceFromCenter;
    protected BuildingStructure(Structure.StructureSettings config, Holder<StructureTemplatePool> startPool, Optional<ResourceLocation> startJigsawName, int size, int maxDistanceFromCenter)
    {
        super(config);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.size = size;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
    }
    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        int chunkMinX = chunkPos.getMinBlockX();
        int chunkMinZ = chunkPos.getMinBlockZ();

        NoiseColumn structureStartColumn = context.chunkGenerator().getBaseColumn(chunkMinX, chunkMinZ, context.heightAccessor(), context.randomState());
        int startY = 64;
        boolean found = false;
        for (int y = context.heightAccessor().getMinBuildHeight(); y < context.heightAccessor().getMaxBuildHeight(); y++) {
            if (structureStartColumn.getBlock(y).getBlock() instanceof BuildingGenAirBlock) {
                startY = y;
                found = true;
                break;
            }
        }
        if (!found) return Optional.empty();

        BlockPos structurePos = new BlockPos(chunkMinX, startY, chunkMinZ);

        return JigsawPlacement.addPieces(
                context, // Used for JigsawPlacement to get all the proper behaviors done.
                this.startPool, // The starting pool to use to create the structure layout from
                this.startJigsawName, // Can be used to only spawn from one Jigsaw block. But we don't need to worry about this.
                this.size, // How deep a branch of pieces can go away from center piece. (5 means branches cannot be longer than 5 pieces from center piece)
                structurePos, false, Optional.empty(),
                this.maxDistanceFromCenter); // in blocks
    }

    @Override
    public StructureType<?> type() {
        return StructureTypeInit.BUILDING.get();
    }
}
