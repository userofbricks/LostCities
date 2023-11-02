package com.userofbricks.refined_lost_cities.events.events;

import com.userofbricks.refined_lost_cities.CreateLostCities;
import mcjty.lostcities.api.LostChunkCharacteristics;
import mcjty.lostcities.api.LostCityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CreateLostCities.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CityStreetsEvents {
    @SubscribeEvent
    public static void preventBuildingsOnGrid(LostCityEvent.CharacteristicsEvent evt) {
        LostChunkCharacteristics chunkCharacteristics = evt.getCharacteristics();
        if (chunkCharacteristics.isCity && chunkCharacteristics.couldHaveBuilding && (evt.getChunkX() % 4 == 0 || evt.getChunkZ() % 4 == 0)) {
            chunkCharacteristics.couldHaveBuilding = false;
        }
    }
}
