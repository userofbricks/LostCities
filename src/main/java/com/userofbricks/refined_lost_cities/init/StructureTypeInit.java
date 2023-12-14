package com.userofbricks.refined_lost_cities.init;

import com.mojang.serialization.Codec;
import com.userofbricks.refined_lost_cities.CreateLostCities;
import com.userofbricks.refined_lost_cities.world.level.levelgen.structure.BuildingStructure;
import com.userofbricks.refined_lost_cities.world.level.levelgen.structure.CityStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class StructureTypeInit {

    /**
     * We are using the Deferred Registry system to register our structure as this is the preferred way on Forge.
     * This will handle registering the base structure for us at the correct time so we don't have to handle it ourselves.
     */
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registries.STRUCTURE_TYPE, CreateLostCities.MODID);
    public static final RegistryObject<StructureType<BuildingStructure>> BUILDING = STRUCTURE_TYPES.register("standard_building", () -> explicitStructureTypeTyping(BuildingStructure.CODEC));
    public static final RegistryObject<StructureType<CityStructure>> CITY = STRUCTURE_TYPES.register("city", () -> explicitStructureTypeTyping(CityStructure.CODEC));

    private static <T extends Structure> StructureType<T> explicitStructureTypeTyping(Codec<T> structureCodec) {
        return () -> structureCodec;
    }
}
