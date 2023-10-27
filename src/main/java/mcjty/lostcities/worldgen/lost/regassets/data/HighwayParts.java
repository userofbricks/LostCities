package mcjty.lostcities.worldgen.lost.regassets.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lostcities.create_lost_cities.regassets.data.HighwayBiCornerParts;
import mcjty.lostcities.create_lost_cities.regassets.data.HighwayBiJunctionParts;

import java.util.Optional;

public record HighwayParts(String tunnel, String open, String bridge, String tunnelBi, String openBi, String bridgeBi, HighwayBiJunctionParts biJunctionParts, HighwayBiCornerParts biCornerParts) {

    public static final Codec<HighwayParts> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.optionalFieldOf("tunnel", "highway_tunnel").forGetter(HighwayParts::tunnel),
                    Codec.STRING.optionalFieldOf("open", "highway_open").forGetter(HighwayParts::open),
                    Codec.STRING.optionalFieldOf("bridge", "highway_bridge").forGetter(HighwayParts::bridge),
                    Codec.STRING.optionalFieldOf("tunnel_bi", "highway_tunnel_bi").forGetter(HighwayParts::tunnelBi),
                    Codec.STRING.optionalFieldOf("open_bi", "highway_open_bi").forGetter(HighwayParts::openBi),
                    Codec.STRING.optionalFieldOf("bridge_bi", "highway_bridge_bi").forGetter(HighwayParts::bridgeBi)
                    ,
                    HighwayBiJunctionParts.CODEC.optionalFieldOf("bi_junction_extensions").forGetter(l -> l.biJunctionParts.get()),
                    HighwayBiCornerParts.CODEC.optionalFieldOf("bi_corners").forGetter(l -> l.biCornerParts.get())

            ).apply(instance, (s, s2, s3, s4, s5, s6, highwayBiJunctionParts, highwayBiCornerParts) -> new HighwayParts(
                    s, s2, s3, s4, s5, s6,
                    highwayBiJunctionParts.orElse(HighwayBiJunctionParts.DEFAULT),
                    highwayBiCornerParts.orElse(HighwayBiCornerParts.DEFAULT)
            )));

    public static final HighwayParts DEFAULT = new HighwayParts("highway_tunnel", "highway_open", "highway_bridge",
            "highway_tunnel_bi", "highway_open_bi", "highway_bridge_bi", HighwayBiJunctionParts.DEFAULT, HighwayBiCornerParts.DEFAULT);

    public Optional<HighwayParts> get() {
        if (this == DEFAULT) {
            return Optional.empty();
        } else {
            return Optional.of(this);
        }
    }

    public String tunnel(boolean bi) {
        return bi ? tunnelBi : tunnel;
    }

    public String open(boolean bi) {
        return bi ? openBi : open;
    }

    public String bridge(boolean bi) {
        return bi ? bridgeBi : bridge;
    }
}
