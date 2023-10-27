package mcjty.lostcities.create_lost_cities.regassets.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record HighwayBiJunctionParts(String tunnel, String open, String bridge, String tunnelMirrored, String openMirrored, String bridgeMirrored) {
    public static final Codec<HighwayBiJunctionParts> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.optionalFieldOf("tunnel_junction", "highway_tunnel_junction").forGetter(HighwayBiJunctionParts::tunnel),
                    Codec.STRING.optionalFieldOf("open_junction", "highway_open_junction").forGetter(HighwayBiJunctionParts::open),
                    Codec.STRING.optionalFieldOf("bridge_junction", "highway_bridge_junction").forGetter(HighwayBiJunctionParts::bridge),
                    Codec.STRING.optionalFieldOf("tunnel_junction_mirrored", "highway_tunnel_junction_mirrored").forGetter(HighwayBiJunctionParts::tunnelMirrored),
                    Codec.STRING.optionalFieldOf("open_junction_mirrored", "highway_open_junction_mirrored").forGetter(HighwayBiJunctionParts::openMirrored),
                    Codec.STRING.optionalFieldOf("bridge_junction_mirrored", "highway_bridge_junction_mirrored").forGetter(HighwayBiJunctionParts::bridgeMirrored)
            ).apply(instance, HighwayBiJunctionParts::new));

    public static final HighwayBiJunctionParts DEFAULT = new HighwayBiJunctionParts("highway_tunnel_junction", "highway_open_junction", "highway_bridge_junction" ,
            "highway_tunnel_junction_mirrored" , "highway_open_junction_mirrored", "highway_bridge_junction_mirrored");

    public Optional<HighwayBiJunctionParts> get() {
        if (this == DEFAULT) {
            return Optional.empty();
        } else {
            return Optional.of(this);
        }
    }

    public String tunnel(boolean mirrored) {
        return mirrored ? tunnelMirrored : tunnel;
    }

    public String open(boolean city) {
        return city ? openMirrored : open;
    }
    public String bridge(boolean city) {
        return city ? bridgeMirrored : bridge;
    }
}
