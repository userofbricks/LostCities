package mcjty.lostcities.api;

import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import mcjty.lostcities.worldgen.IDimensionInfo;
import mcjty.lostcities.worldgen.lost.BuildingInfo;
import mcjty.lostcities.worldgen.lost.Transform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * LostCityEvent is fired whenever an event involving a Lost City chunk generation occurs. <br>
 * If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class LostCityEvent extends Event {

    private final WorldGenLevel world;
    private final ILostCities lostCities;
    private final int chunkX;
    private final int chunkZ;

    public LostCityEvent(WorldGenLevel world, ILostCities lostCities, int chunkX, int chunkZ) {
        this.world = world;
        this.lostCities = lostCities;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public WorldGenLevel getWorld() {
        return world;
    }

    public ILostCities getLostCities() {
        return lostCities;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    /**
     * CharacteristicsEvent is fired when Lost Cities tries to determine chunk chracteristics.<br>
     * This event is fired right when The Lost Cities tries to decide if a chunk should contain
     * a building and what type of building. All fields in the given characteristic object can be modified.
     * Note that you can get access to the asset registries for buildings, multi buildings, and city styles
     * from the given ILostChunkGenerator instance. <br>
     * NOTE! This will be called for every chunk (city or normal). <br>
     * WARNING! Do *not* call ILostChunkGenerator.getChunkInfo() from here as that might cause infinite
     * recursion!
     * <br>
     * {@link #characteristics} contains the {@link LostChunkCharacteristics} that was generated for this chunk. <br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    public static class CharacteristicsEvent extends LostCityEvent {
        private final LostChunkCharacteristics characteristics;
        private final IDimensionInfo provider;

        public CharacteristicsEvent(IDimensionInfo provider, ILostCities lostCities, int chunkX, int chunkZ, LostChunkCharacteristics characteristics) {
            super(provider.getWorld(), lostCities, chunkX, chunkZ);
            this.characteristics = characteristics;
            this.provider = provider;
        }

        public LostChunkCharacteristics getCharacteristics() {
            return characteristics;
        }
        public IDimensionInfo getProvider() {
            return provider;
        }
    }

    /**
     * PreGenCityChunkEvent is fired right before generation of a city chunk (street or building).<br>
     * This is fired right before generation of a city chunk. If you cancel this event then The Lost Cities
     * will not generate the street or building. Everything else will still be generated (like the subways, highways,
     * and the stone up to city level for this chunk). If you don't cancel this then you can still modify the primer
     * but keep in mind that the street or building will be generated after this and might overwrite what you did.<br>
     * NOTE! This will only be called for city chunks (buildings or street). <br>
     * <br>
     * {@link #primer} contains the {@link ChunkAccess} for this chunk. This primer will already be filled with stone up to city level. <br>
     * <br>
     * This event is {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    @Cancelable
    public static class PreGenCityChunkEvent extends LostCityEvent {
        private final ChunkAccess primer;

        public PreGenCityChunkEvent(WorldGenLevel world, ILostCities lostCities, int chunkX, int chunkZ, ChunkAccess primer) {
            super(world, lostCities, chunkX, chunkZ);
            this.primer = primer;
        }

        public ChunkAccess getChunkAccess() {
            return primer;
        }
    }

    /**
     * PostGenCityChunkEvent is fired right after generation of the street or building of a city chunk (street or building).<br>
     * This is fired right after generation of the street or building but before highways, subways and other stuff like that.
     * This is mostly useful in case you want to modify the standard Lost City building/street after it has been generated.<br>
     * NOTE! This will only be called for city chunks (buildings or street). <br>
     * <br>
     * {@link #primer} contains the {@link ChunkAccess} for this chunk. This primer will already have the building and street stuff in it. <br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    public static class PostGenCityChunkEvent extends LostCityEvent {
        private final ChunkAccess primer;
        private final IDimensionInfo provider;
        private final BuildingInfo info;
        private String part;
        private Transform transform = Transform.ROTATE_NONE;
        private int groundLevel = -64;

        public PostGenCityChunkEvent(IDimensionInfo provider, ILostCities lostCities, BuildingInfo info, ChunkAccess primer) {
            super(provider.getWorld(), lostCities, info.chunkX, info.chunkZ);
            this.primer = primer;
            this.provider = provider;
            this.info = info;
        }

        public ChunkAccess getChunkAccess() {
            return primer;
        }

        public Transform getTransform() {
            return transform;
        }

        public void setTransform(Transform newTransform) {
            transform = newTransform;
        }

        public int getGroundLevel() {
            return groundLevel;
        }

        public void setGroundLevel(int newGroundLevel) {
            groundLevel = newGroundLevel;
        }

        public String getPart() {
            return part;
        }

        public void setPart(String newPart) {
            part = newPart;
        }
        public IDimensionInfo getProvider() {
            return provider;
        }
        public BuildingInfo getInfo() {
            return info;
        }
    }

    /**
     * PostGenOutsideChunkEvent is fired right after generation of a non-building or non-street chunk (i.e. an outside chunk).<br>
     * This is fired right after generation of the chunk but before highways, subways and other stuff like that.
     * NOTE! This will NOT be called for city chunks (buildings or street). <br>
     * <br>
     * {@link #primer} contains the {@link ChunkAccess} for this chunk. <br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    public static class PostGenOutsideChunkEvent extends LostCityEvent {
        private final ChunkAccess primer;
        private final IDimensionInfo provider;
        private final BuildingInfo info;
        private String part;
        private Transform transform = Transform.ROTATE_NONE;
        private int groundLevel = -64;

        public PostGenOutsideChunkEvent(IDimensionInfo provider, ILostCities lostCities, BuildingInfo info, ChunkAccess primer) {
            super(provider.getWorld(), lostCities, info.chunkX, info.chunkZ);
            this.primer = primer;
            this.provider = provider;
            this.info = info;
        }

        public ChunkAccess getChunkAccess() {
            return primer;
        }

        public Transform getTransform() {
            return transform;
        }

        public void setTransform(Transform newTransform) {
            transform = newTransform;
        }

        public int getGroundLevel() {
            return groundLevel;
        }

        public void setGroundLevel(int newGroundLevel) {
            groundLevel = newGroundLevel;
        }

        public String getPart() {
            return part;
        }

        public void setPart(String newPart) {
            part = newPart;
        }
        public IDimensionInfo getProvider() {
            return provider;
        }
        public BuildingInfo getInfo() {
            return info;
        }
    }

    /**
     * PreExplosionEvent fired after chunk generation but before explosion damage is done.<br>
     * If you cancel this event then no explosion damage will be done. This event is the final chance
     * to modify the chunk before explosion damage is calculated.
     * NOTE! This will be called for every chunk (city or normal). <br>
     * <br>
     * {@link #primer} contains the {@link ChunkAccess} for this chunk. <br>
     * <br>
     * This event is {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    @Cancelable
    public static class PreExplosionEvent extends LostCityEvent {
        private final ChunkAccess primer;

        public PreExplosionEvent(WorldGenLevel world, ILostCities lostCities, int chunkX, int chunkZ, ChunkAccess primer) {
            super(world, lostCities, chunkX, chunkZ);
            this.primer = primer;
        }

        public ChunkAccess getChunkAccess() {
            return primer;
        }
    }

    /**
     * PostGenHighwayChunkEvent is fired right after generation of a chunk containing a highway.<br>
     * NOTE! This will be called for both city and outside chunks but only if a highway has been
     * generated but before supports have been. <br>
     * <br>
     * {@link #primer} contains the {@link ChunkAccess} for this chunk. <br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    public static class PostGenHighwayChunkEvent extends LostCityEvent {
        private final ChunkAccess primer;
        private final IDimensionInfo provider;
        private final ResourceLocation oldPart;
        private final BuildingInfo info;
        private final List<IntIntImmutablePair> supportPoints = new ArrayList<>();
        private ResourceLocation newPart = new ResourceLocation("");
        private boolean changed = false, customPoints = false;

        private Transform transform;

        public PostGenHighwayChunkEvent(IDimensionInfo provider, ILostCities lostCities, BuildingInfo info, Transform transform, ChunkAccess primer, ResourceLocation part) {
            super(provider.getWorld(), lostCities, info.chunkX, info.chunkZ);
            this.primer = primer;
            this.oldPart = part;
            this.provider = provider;
            this.transform = transform;
            this.info = info;
        }

        public ChunkAccess getChunkAccess() {
            return primer;
        }

        public ResourceLocation getPart() {
            return oldPart;
        }
         public ResourceLocation getNewPart() {
            return newPart;
        }
        public void setPart(ResourceLocation part) {
            newPart = part;
            changed = true;
        }
        public boolean isChanged() {
            return changed;
        }
        public IDimensionInfo getProvider() {
            return provider;
        }
        public Transform getTransform() {
            return transform;
        }
        public void setTransform(Transform transform) {
            this.transform = transform;
        }
        public BuildingInfo getInfo() {
            return info;
        }
        public boolean isCustomPoints() {
            return customPoints;
        }
        public void addSupportPoint(int x, int y) {
            supportPoints.add(IntIntImmutablePair.of(x,y));
            customPoints = true;
        }
        public List<IntIntImmutablePair> getSupportPoints() {
            return supportPoints;
        }
    }

}
