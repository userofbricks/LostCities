package mcjty.lostcities.create_lost_cities.events;

import mcjty.lostcities.api.LostChunkCharacteristics;
import mcjty.lostcities.api.LostCityEvent;
import mcjty.lostcities.create_lost_cities.regassets.data.HighwayBiCornerParts;
import mcjty.lostcities.create_lost_cities.regassets.data.HighwayBiJunctionParts;
import mcjty.lostcities.worldgen.IDimensionInfo;
import mcjty.lostcities.worldgen.lost.BuildingInfo;
import mcjty.lostcities.worldgen.lost.Highway;
import mcjty.lostcities.worldgen.lost.Transform;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static mcjty.lostcities.worldgen.lost.BuildingInfo.getProfile;

@Mod.EventBusSubscriber(modid = "lostcities", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HighwayEvents {

    @SubscribeEvent
    public static void generateHighways(LostCityEvent.PostGenHighwayChunkEvent evt) {
        int chunkX = evt.getChunkX();
        int chunkZ = evt.getChunkZ();
        IDimensionInfo provider = evt.getProvider();
        BuildingInfo info = evt.getInfo();
        ResourceLocation currentHighway = evt.getPart();


        int levelX = Highway.getXHighwayLevel(chunkX, chunkZ, provider, info.profile);
        int levelZ = Highway.getZHighwayLevel(chunkX, chunkZ, provider, info.profile);
        if (levelZ == levelX) return;
        if (levelX >= 0) {
            int levelXFront = Highway.getXHighwayLevel(chunkX+1, chunkZ, provider, info.profile);
            int levelZFront = Highway.getZHighwayLevel(chunkX+1, chunkZ, provider, info.profile);
            int levelXBack = Highway.getXHighwayLevel(chunkX-1, chunkZ, provider, info.profile);
            int levelZBack = Highway.getZHighwayLevel(chunkX-1, chunkZ, provider, info.profile);
            if (levelXFront >= 0 && levelZFront == levelXFront) {
                evt.setPart(new ResourceLocation(findHighwayBiExtraPart(currentHighway, provider, false)));
                evt.setTransform(Transform.ROTATE_NONE);
                evt.addSupportPoint(0,0);
                evt.addSupportPoint(0,15);
                evt.addSupportPoint(10,0);
                evt.addSupportPoint(10,15);
            } else if (levelXBack >= 0 && levelZBack == levelXBack) {
                evt.setPart(new ResourceLocation(findHighwayBiExtraPart(currentHighway, provider, true)));
                evt.setTransform(Transform.ROTATE_NONE);
                evt.addSupportPoint(5,0);
                evt.addSupportPoint(5,15);
            }
        } else if (levelZ >= 0) {
            int levelXFront = Highway.getXHighwayLevel(chunkX, chunkZ+1, provider, info.profile);
            int levelZFront = Highway.getZHighwayLevel(chunkX, chunkZ+1, provider, info.profile);
            int levelXBack = Highway.getXHighwayLevel(chunkX, chunkZ-1, provider, info.profile);
            int levelZBack = Highway.getZHighwayLevel(chunkX, chunkZ-1, provider, info.profile);
            if (levelZFront >= 0 && levelXFront == levelZFront) {
                evt.setPart(new ResourceLocation(findHighwayBiExtraPart(currentHighway, provider, false)));
                evt.setTransform(Transform.ROTATE_90);
                evt.addSupportPoint(0,0);
                evt.addSupportPoint(0,15);
                evt.addSupportPoint(10,0);
                evt.addSupportPoint(10,15);
            } else if (levelZBack >= 0 && levelXBack == levelZBack) {
                evt.setPart(new ResourceLocation(findHighwayBiExtraPart(currentHighway, provider, true)));
                evt.setTransform(Transform.ROTATE_90);
                evt.addSupportPoint(5,0);
                evt.addSupportPoint(5,15);
            }
        }
    }

    private static String findHighwayBiExtraPart(ResourceLocation currentHighway, IDimensionInfo provider, boolean mirrored) {
        HighwayBiJunctionParts junctionParts = provider.getWorldStyle().getPartSelector().highwayParts().biJunctionParts();
        if (currentHighway.getPath().contains("tunnel")) {
            return junctionParts.tunnel(mirrored);
        } else if (currentHighway.getPath().contains("open")) {
            return junctionParts.open(mirrored);
        } else {
            return junctionParts.bridge(mirrored);
        }

    }

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
        HighwayBiCornerParts cornerParts = provider.getWorldStyle().getPartSelector().highwayParts().biCornerParts();
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

        HighwayBiCornerParts cornerParts = provider.getWorldStyle().getPartSelector().highwayParts().biCornerParts();

        if (levelXFrontLeft == levelZFrontLeft && levelXFrontLeft >= 0) {
            evt.setPart(info.isTunnel(levelXFrontLeft) ? cornerParts.cityTunnel() :
                    (info.isCity && levelXFrontLeft <= info.getXmin().cityLevel && levelXFrontLeft <= info.getZmax().cityLevel && info.getXmin().isCity && info.getZmax().isCity) ? cornerParts.cityOpen() : cornerParts.cityBridge());
            evt.setTransform(Transform.ROTATE_NONE);
            evt.setGroundLevel(levelXFrontLeft);
        } else if (levelXFrontRight == levelZFrontRight && levelXFrontRight >= 0) {
            evt.setPart(info.isTunnel(levelXFrontLeft) ? cornerParts.cityTunnel() :
                    (info.isCity && levelXFrontLeft <= info.getXmin().cityLevel && levelXFrontLeft <= info.getZmax().cityLevel && info.getXmin().isCity && info.getZmax().isCity) ? cornerParts.cityOpen() : cornerParts.cityBridge());
            evt.setTransform(Transform.ROTATE_90);
            evt.setGroundLevel(levelXFrontRight);
        } else if (levelXBackRight == levelZBackRight && levelXBackRight >= 0) {
            evt.setPart(info.isTunnel(levelXFrontLeft) ? cornerParts.cityTunnel() :
                    (info.isCity && levelXFrontLeft <= info.getXmin().cityLevel && levelXFrontLeft <= info.getZmax().cityLevel && info.getXmin().isCity && info.getZmax().isCity) ? cornerParts.cityOpen() : cornerParts.cityBridge());
            evt.setTransform(Transform.ROTATE_180);
            evt.setGroundLevel(levelXBackRight);
        } else if (levelXBackLeft == levelZBackLeft && levelXBackLeft >= 0) {
            evt.setPart(info.isTunnel(levelXFrontLeft) ? cornerParts.cityTunnel() :
                    (info.isCity && levelXFrontLeft <= info.getXmin().cityLevel && levelXFrontLeft <= info.getZmax().cityLevel && info.getXmin().isCity && info.getZmax().isCity) ? cornerParts.cityOpen() : cornerParts.cityBridge());
            evt.setTransform(Transform.ROTATE_180);
            evt.setGroundLevel(levelXBackLeft);
        }
    }
}
