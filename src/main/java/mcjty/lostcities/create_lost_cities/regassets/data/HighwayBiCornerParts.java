package mcjty.lostcities.create_lost_cities.regassets.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record HighwayBiCornerParts(String cityTunnel, String cityOpen, String cityBridge, String tunnel, String bridge) {
    public static final Codec<HighwayBiCornerParts> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.optionalFieldOf("tunnel_corner_city", "highway_tunnel_corner_city").forGetter(HighwayBiCornerParts::cityTunnel),
                    Codec.STRING.optionalFieldOf("open_corner_city", "highway_open_corner_city").forGetter(HighwayBiCornerParts::cityOpen),
                    Codec.STRING.optionalFieldOf("bridge_corner_city", "highway_bridge_corner_city").forGetter(HighwayBiCornerParts::cityBridge),
                    Codec.STRING.optionalFieldOf("tunnel_corner_normal", "highway_tunnel_corner_normal").forGetter(HighwayBiCornerParts::tunnel),
                    Codec.STRING.optionalFieldOf("bridge_corner_normal", "highway_bridge_corner_normal").forGetter(HighwayBiCornerParts::bridge)
            ).apply(instance, HighwayBiCornerParts::new));

    public static final HighwayBiCornerParts DEFAULT = new HighwayBiCornerParts("highway_tunnel_corner_city", "highway_open_corner_city",
            "highway_bridge_corner_city" , "highway_tunnel_corner_normal" , "highway_bridge_corner_normal");

    public Optional<HighwayBiCornerParts> get() {
        if (this == DEFAULT) {
            return Optional.empty();
        } else {
            return Optional.of(this);
        }
    }

    public String tunnel(boolean city) {
        return city ? cityTunnel : tunnel;
    }

    public String bridge(boolean city) {
        return city ? cityBridge : bridge;
    }
}
