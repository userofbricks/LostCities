package com.userofbricks.refined_lost_cities.events.events;

import com.userofbricks.refined_lost_cities.CreateLostCities;
import mcjty.lostcities.api.LostCityEvent;
import mcjty.lostcities.worldgen.IDimensionInfo;
import mcjty.lostcities.worldgen.lost.CityRarityMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;


@Mod.EventBusSubscriber(modid = CreateLostCities.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FieldsEvents {
    private static final Map<ResourceKey<Level>, CityRarityMap> FIELD_RARITY_MAP = new HashMap<>();

    public static CityRarityMap getCityRarityMap(ResourceKey<Level> level, long seed, double scale, double offset, double innerScale) {
        return FIELD_RARITY_MAP.computeIfAbsent(level, k -> new CityRarityMap(seed, scale, offset, innerScale));
    }
    @SubscribeEvent
    public static void generateNormalHighwayCorners(LostCityEvent.PostGenOutsideChunkEvent evt) {
        IDimensionInfo dimensionInfo = evt.getProvider();
        //used for fields instead
        CityRarityMap rarityMap = getCityRarityMap(dimensionInfo.dimension(), dimensionInfo.getSeed(),
                5, .2, .1);
        if (rarityMap.getCityFactor(evt.getChunkX(), evt.getChunkZ()) > 0.5f) {
        }
    }
}
