package com.userofbricks.refined_lost_cities.init;

import com.mojang.serialization.Codec;
import com.userofbricks.refined_lost_cities.CreateLostCities;
import com.userofbricks.refined_lost_cities.world.level.levelgen.structure.pools.StructureAsPoolElement;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class StructurePoolElementTypeInit {

    /**
     * We are using the Deferred Registry system to register our structure as this is the preferred way on Forge.
     * This will handle registering the base structure for us at the correct time so we don't have to handle it ourselves.
     */
    public static final DeferredRegister<StructurePoolElementType<?>> STRUCTURE_POOL_ELEMENT_TYPES = DeferredRegister.create(Registries.STRUCTURE_POOL_ELEMENT, CreateLostCities.MODID);

    /**
     * Registers the base structure itself and sets what its path is. In this case,
     * this base structure will have the resourcelocation of structure_tutorial:sky_structures.
     */
    public static final RegistryObject<StructurePoolElementType<StructureAsPoolElement>> STRUCTURE_AS_POOL_ELEMENT = STRUCTURE_POOL_ELEMENT_TYPES.register("structure", () -> explicitStructureTypeTyping(StructureAsPoolElement.CODEC));

    private static <T extends StructurePoolElement> StructurePoolElementType<T> explicitStructureTypeTyping(Codec<T> structureCodec) {
        return () -> structureCodec;
    }
}
