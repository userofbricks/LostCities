package com.userofbricks.refined_lost_cities.worldgen;

import com.userofbricks.refined_lost_cities.regassets.data.HighwayPartTripple;
import com.userofbricks.refined_lost_cities.regassets.data.HighwayParts;
import mcjty.lostcities.api.ILostCities;
import mcjty.lostcities.worldgen.LostCityTerrainFeature;
import mcjty.lostcities.worldgen.lost.BuildingInfo;
import mcjty.lostcities.worldgen.lost.Highway;
import mcjty.lostcities.worldgen.lost.Transform;
import mcjty.lostcities.worldgen.lost.cityassets.AssetRegistries;
import mcjty.lostcities.worldgen.lost.cityassets.BuildingPart;
import com.userofbricks.refined_lost_cities.regassets.data.XZCords;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

import static mcjty.lostcities.worldgen.LostCityTerrainFeature.FLOORHEIGHT;
import static mcjty.lostcities.worldgen.LostCityTerrainFeature.isEmpty;

public class HighwayPartGeneration {

    public static void generateHighways(int chunkX, int chunkZ, BuildingInfo info,
                                        LostCityTerrainFeature lCTerrainFeature) {
        int levelX = Highway.getXHighwayLevel(chunkX, chunkZ, info.provider, info.profile);
        int levelZ = Highway.getZHighwayLevel(chunkX, chunkZ, info.provider, info.profile);
        if (levelX == levelZ && levelX >= 0) {
            // Crossing
            generateHighwayPart(info, levelX, Transform.ROTATE_NONE, info.getXmax(), info.getZmax(), info.provider.getWorldStyle().getPartSelector().highwayParts().strait_crosses(), lCTerrainFeature);
        } else if (levelX >= 0 && levelZ >= 0) {
            // There are two highways on different level. Make sure the lowest one is done first because it
            // will clear out what is above it
            if (levelX < levelZ) {
                chooseStraitVariant(chunkX, chunkZ, info, levelX, true, lCTerrainFeature);
                chooseStraitVariant(chunkX, chunkZ, info, levelZ, false, lCTerrainFeature);
            } else {
                chooseStraitVariant(chunkX, chunkZ, info, levelZ, true, lCTerrainFeature);
                chooseStraitVariant(chunkX, chunkZ, info, levelX, false, lCTerrainFeature);
            }
        } else {
            if (levelX >= 0) {
                chooseStraitVariant(chunkX, chunkZ, info, levelX, true, lCTerrainFeature);
            } else if (levelZ >= 0) {
                chooseStraitVariant(chunkX, chunkZ, info, levelZ, false, lCTerrainFeature);
            }
        }
    }

    private static void chooseStraitVariant(int chunkX, int chunkZ, BuildingInfo info, int level, boolean xNotZ, LostCityTerrainFeature lCTerrainFeature) {
        HighwayParts highwayParts = info.provider.getWorldStyle().getPartSelector().highwayParts();
        if (xNotZ) {
            int levelXFront = Highway.getXHighwayLevel(chunkX+1, chunkZ, info.provider, info.profile);
            int levelZFront = Highway.getZHighwayLevel(chunkX+1, chunkZ, info.provider, info.profile);
            int levelXBack = Highway.getXHighwayLevel(chunkX-1, chunkZ, info.provider, info.profile);
            int levelZBack = Highway.getZHighwayLevel(chunkX-1, chunkZ, info.provider, info.profile);
            if (levelXFront >= 0 && levelZFront == levelXFront) {
                generateHighwayPart(info, level, Transform.ROTATE_NONE, info.getZmin(), info.getZmax(), highwayParts.strait_cross_extensions(), lCTerrainFeature);
            } else if (levelXBack >= 0 && levelZBack == levelXBack) {
                generateHighwayPart(info, level, Transform.ROTATE_NONE, info.getZmin(), info.getZmax(), highwayParts.strait_cross_mirrored_extensions(), lCTerrainFeature);
            } else {
                generateHighwayPart(info, level, Transform.ROTATE_NONE, info.getZmin(), info.getZmax(), highwayParts.straits(), lCTerrainFeature);
            }
        } else {
            int levelXFront = Highway.getXHighwayLevel(chunkX, chunkZ+1, info.provider, info.profile);
            int levelZFront = Highway.getZHighwayLevel(chunkX, chunkZ+1, info.provider, info.profile);
            int levelXBack = Highway.getXHighwayLevel(chunkX, chunkZ-1, info.provider, info.profile);
            int levelZBack = Highway.getZHighwayLevel(chunkX, chunkZ-1, info.provider, info.profile);
            if (levelZFront >= 0 && levelXFront == levelZFront) {
                generateHighwayPart(info, level, Transform.ROTATE_90, info.getZmin(), info.getZmax(), highwayParts.strait_cross_extensions(), lCTerrainFeature);
            } else if (levelZBack >= 0 && levelXBack == levelZBack) {
                generateHighwayPart(info, level, Transform.ROTATE_90, info.getZmin(), info.getZmax(), highwayParts.strait_cross_mirrored_extensions(), lCTerrainFeature);
            } else {
                generateHighwayPart(info, level, Transform.ROTATE_90, info.getZmin(), info.getZmax(), highwayParts.straits(), lCTerrainFeature);
            }
        }
    }

    private static void generateHighwayPart(BuildingInfo info, int level, Transform transform, BuildingInfo adjacent1, BuildingInfo adjacent2, HighwayPartTripple highwayPartTripple,
                                           LostCityTerrainFeature lCTerrainFeature) {
        int highwayGroundLevel = info.groundLevel + level * FLOORHEIGHT;

        BuildingPart part;
        int height = -1;
        if (info.isTunnel(level)) {
            part = AssetRegistries.PARTS.getOrThrow(info.provider.getWorld(), highwayPartTripple.tunnel());
            lCTerrainFeature.generatePart(info, part, transform, 0, highwayGroundLevel, 0, true);
        } else if (info.isCity && level <= adjacent1.cityLevel && level <= adjacent2.cityLevel && adjacent1.isCity && adjacent2.isCity) {
            part = AssetRegistries.PARTS.getOrThrow(info.provider.getWorld(), highwayPartTripple.open());
            height = lCTerrainFeature.generatePart(info, part, transform, 0, highwayGroundLevel, 0, true);
        } else {
            part = AssetRegistries.PARTS.getOrThrow(info.provider.getWorld(), highwayPartTripple.bridge());
            height = lCTerrainFeature.generatePart(info, part, transform, 0, highwayGroundLevel, 0, true);
        }

        // Clear a bit more above the highway if not tunnel and a height has been set
        if (!info.profile.isCavern() && !info.isTunnel(level) && height != -1) {
            int clearheight = 15;
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    lCTerrainFeature.clearRange(info, x, z, height, height + clearheight, info.waterLevel > info.groundLevel,
                            HighwayPartGeneration::isClearableAboveHighway);
                }
            }
        }

        List<XZCords> supportPoints = part.getMetaIntPairList(ILostCities.META_SUPPORT_POINTS);

        Character support = part.getMetaChar(ILostCities.META_SUPPORT);
        if (info.profile.HIGHWAY_SUPPORTS && support != null && supportPoints != null) {
            BlockState sup = info.getCompiledPalette().get(support);
            for (XZCords cordPair : supportPoints) {
                int x = transform.rotateX(cordPair.x(), cordPair.z());
                int z = transform.rotateZ(cordPair.x(), cordPair.z());
                lCTerrainFeature.getDriver().current(x, highwayGroundLevel - 1, z);
                for (int y = 0; y < 40; y++) {
                    if (isEmpty(lCTerrainFeature.getDriver().getBlock())) {
                        lCTerrainFeature.getDriver().block(sup);
                    } else {
                        break;
                    }
                    lCTerrainFeature.getDriver().decY();
                }
            }
        }
    }

    private static boolean isClearableAboveHighway(BlockState st) {
        return !st.is(BlockTags.LEAVES) && !st.is(BlockTags.LOGS);
    }
}
