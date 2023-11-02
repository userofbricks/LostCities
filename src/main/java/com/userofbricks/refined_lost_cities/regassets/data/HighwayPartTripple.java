package com.userofbricks.refined_lost_cities.regassets.data;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record HighwayPartTripple(String tunnel, String open, String bridge) {
    public static final Codec<HighwayPartTripple> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.optionalFieldOf("tunnel", "highway_tunnel").forGetter(HighwayPartTripple::tunnel),
                    Codec.STRING.optionalFieldOf("open", "highway_open").forGetter(HighwayPartTripple::open),
                    Codec.STRING.optionalFieldOf("bridge", "highway_bridge").forGetter(HighwayPartTripple::bridge)

            ).apply(instance, HighwayPartTripple::new));

}
