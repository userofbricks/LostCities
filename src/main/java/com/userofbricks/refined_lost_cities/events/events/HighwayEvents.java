package com.userofbricks.refined_lost_cities.events.events;

import com.userofbricks.refined_lost_cities.CreateLostCities;
import com.userofbricks.refined_lost_cities.regassets.data.HighwayPartTripple;
import mcjty.lostcities.api.LostChunkCharacteristics;
import mcjty.lostcities.api.LostCityEvent;
import mcjty.lostcities.worldgen.IDimensionInfo;
import mcjty.lostcities.worldgen.lost.BuildingInfo;
import mcjty.lostcities.worldgen.lost.Highway;
import mcjty.lostcities.worldgen.lost.Transform;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static mcjty.lostcities.worldgen.lost.BuildingInfo.getProfile;

@Mod.EventBusSubscriber(modid = CreateLostCities.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HighwayEvents {
    @SubscribeEvent
    public static void generateNormalHighwayCorners(LostCityEvent.PostGenOutsideChunkEvent evt) {
        int chunkX = evt.getChunkX();
        int chunkZ = evt.getChunkZ();
        IDimensionInfo provider = evt.getProvider();
        BuildingInfo info = evt.getInfo();
        int levelXFrontLeft = Highway.getXHighwayLevel(chunkX+1, chunkZ-1, provider, info.profile);
        int levelZFrontLeft = Highway.getZHighwayLevel(chunkX+1, chunkZ-1, provider, info.profile);

        int levelXFrontRight = Highway.getXHighwayLevel(chunkX+1, chunkZ+1, provider, info.profile);
        int levelZFrontRight = Highway.getZHighwayLevel(chunkX+1, chunkZ+1, provider, info.profile);

        int levelXBackLeft = Highway.getXHighwayLevel(chunkX-1, chunkZ-1, provider, info.profile);
        int levelZBackLeft = Highway.getZHighwayLevel(chunkX-1, chunkZ-1, provider, info.profile);

        int levelXBackRight = Highway.getXHighwayLevel(chunkX-1, chunkZ+1, provider, info.profile);
        int levelZBackRight = Highway.getZHighwayLevel(chunkX-1, chunkZ+1, provider, info.profile);
        HighwayPartTripple cornerParts = provider.getWorldStyle().getPartSelector().highwayParts().strait_cross_normal_corners();
        if (levelXFrontLeft == levelZFrontLeft && levelXFrontLeft >= 0) {
            evt.setPart(info.isTunnel(levelXFrontLeft) ? cornerParts.tunnel() : cornerParts.bridge());
            evt.setTransform(Transform.ROTATE_NONE);
            evt.setGroundLevel(levelXFrontLeft);
        } else if (levelXFrontRight == levelZFrontRight && levelXFrontRight >= 0) {
            evt.setPart(info.isTunnel(levelXFrontLeft) ? cornerParts.tunnel() : cornerParts.bridge());
            evt.setTransform(Transform.ROTATE_90);
            evt.setGroundLevel(levelXFrontRight);
        } else if (levelXBackRight == levelZBackRight && levelXBackRight >= 0) {
            evt.setPart(info.isTunnel(levelXFrontLeft) ? cornerParts.tunnel() : cornerParts.bridge());
            evt.setTransform(Transform.ROTATE_180);
            evt.setGroundLevel(levelXBackRight);
        } else if (levelXBackLeft == levelZBackLeft && levelXBackLeft >= 0) {
            evt.setPart(info.isTunnel(levelXFrontLeft) ? cornerParts.tunnel() : cornerParts.bridge());
            evt.setTransform(Transform.ROTATE_270);
            evt.setGroundLevel(levelXBackLeft);
        }
    }

    @SubscribeEvent
    public static void preventBuildingsAtHighwayCorners(LostCityEvent.CharacteristicsEvent evt) {
        LostChunkCharacteristics chunkCharacteristics = evt.getCharacteristics();
        if (chunkCharacteristics.isCity && chunkCharacteristics.couldHaveBuilding) {
            int chunkX = evt.getChunkX();
            int chunkZ = evt.getChunkZ();
            IDimensionInfo provider = evt.getProvider();
            int levelX = Highway.getXHighwayLevel(chunkX, chunkZ, provider, getProfile(chunkX, chunkZ, provider));
            int levelZ = Highway.getZHighwayLevel(chunkX, chunkZ, provider, getProfile(chunkX, chunkZ, provider));
            if (levelZ >= 0 && levelX >= 0) chunkCharacteristics.couldHaveBuilding = false;
        }
    }

    @SubscribeEvent
    public static void generateCityHighwayCorners(LostCityEvent.PostGenCityChunkEvent evt) {
        int chunkX = evt.getChunkX();
        int chunkZ = evt.getChunkZ();
        IDimensionInfo provider = evt.getProvider();
        BuildingInfo info = evt.getInfo();
        int levelXFrontLeft = Highway.getXHighwayLevel(chunkX+1, chunkZ-1, provider, info.profile);
        int levelZFrontLeft = Highway.getZHighwayLevel(chunkX+1, chunkZ-1, provider, info.profile);

        int levelXFrontRight = Highway.getXHighwayLevel(chunkX+1, chunkZ+1, provider, info.profile);
        int levelZFrontRight = Highway.getZHighwayLevel(chunkX+1, chunkZ+1, provider, info.profile);

        int levelXBackLeft = Highway.getXHighwayLevel(chunkX-1, chunkZ-1, provider, info.profile);
        int levelZBackLeft = Highway.getZHighwayLevel(chunkX-1, chunkZ-1, provider, info.profile);

        int levelXBackRight = Highway.getXHighwayLevel(chunkX-1, chunkZ+1, provider, info.profile);
        int levelZBackRight = Highway.getZHighwayLevel(chunkX-1, chunkZ+1, provider, info.profile);

        HighwayPartTripple cornerParts = provider.getWorldStyle().getPartSelector().highwayParts().strait_cross_city_corners();

        if (levelXFrontLeft == levelZFrontLeft && levelXFrontLeft >= 0) {
            evt.setPart(info.isTunnel(levelXFrontLeft) ? cornerParts.tunnel() :
                    (info.isCity && levelXFrontLeft <= info.getXmin().cityLevel && levelXFrontLeft <= info.getZmax().cityLevel && info.getXmin().isCity && info.getZmax().isCity) ? cornerParts.open() : cornerParts.bridge());
            evt.setTransform(Transform.ROTATE_NONE);
            evt.setGroundLevel(levelXFrontLeft);
        } else if (levelXFrontRight == levelZFrontRight && levelXFrontRight >= 0) {
            evt.setPart(info.isTunnel(levelXFrontLeft) ? cornerParts.tunnel() :
                    (info.isCity && levelXFrontLeft <= info.getXmin().cityLevel && levelXFrontLeft <= info.getZmax().cityLevel && info.getXmin().isCity && info.getZmax().isCity) ? cornerParts.open() : cornerParts.bridge());
            evt.setTransform(Transform.ROTATE_90);
            evt.setGroundLevel(levelXFrontRight);
        } else if (levelXBackRight == levelZBackRight && levelXBackRight >= 0) {
            evt.setPart(info.isTunnel(levelXFrontLeft) ? cornerParts.tunnel() :
                    (info.isCity && levelXFrontLeft <= info.getXmin().cityLevel && levelXFrontLeft <= info.getZmax().cityLevel && info.getXmin().isCity && info.getZmax().isCity) ? cornerParts.open() : cornerParts.bridge());
            evt.setTransform(Transform.ROTATE_180);
            evt.setGroundLevel(levelXBackRight);
        } else if (levelXBackLeft == levelZBackLeft && levelXBackLeft >= 0) {
            evt.setPart(info.isTunnel(levelXFrontLeft) ? cornerParts.tunnel() :
                    (info.isCity && levelXFrontLeft <= info.getXmin().cityLevel && levelXFrontLeft <= info.getZmax().cityLevel && info.getXmin().isCity && info.getZmax().isCity) ? cornerParts.open() : cornerParts.bridge());
            evt.setTransform(Transform.ROTATE_180);
            evt.setGroundLevel(levelXBackLeft);
        }
    }
}
