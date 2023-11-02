package com.userofbricks.refined_lost_cities.regassets.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

import static com.userofbricks.refined_lost_cities.CreateLostCities.MODID;

public record HighwayParts(HighwayPartTripple straits, HighwayPartTripple strait_crosses, HighwayPartTripple strait_cross_extensions, HighwayPartTripple strait_cross_mirrored_extensions, HighwayPartTripple strait_cross_normal_corners, HighwayPartTripple strait_cross_city_corners) {

    public static final HighwayPartTripple STRAIT_DEFAULT = new HighwayPartTripple(MODID + ":" + "highway_tunnel", MODID + ":" + "highway_open", MODID + ":" + "highway_bridge");
    public static final HighwayPartTripple STRAIT_CROSS_DEFAULT = new HighwayPartTripple(MODID + ":" + "highway_tunnel_bi", MODID + ":" + "highway_open_bridge_bi", MODID + ":" + "highway_open_bridge_bi");
    public static final HighwayPartTripple STRAIT_CROSS_EXTENSION_DEFAULT = new HighwayPartTripple(MODID + ":" + "highway_tunnel_junction", MODID + ":" + "highway_open_junction", MODID + ":" + "highway_bridge_junction");
    public static final HighwayPartTripple STRAIT_CROSS_EXTENSION_MIRRORED_DEFAULT = new HighwayPartTripple(MODID + ":" + "highway_tunnel_junction_mirrored", MODID + ":" + "highway_open_junction_mirrored", MODID + ":" + "highway_bridge_junction_mirrored");
    public static final HighwayPartTripple STRAIT_CROSS_CORNER_DEFAULT = new HighwayPartTripple(MODID + ":" + "highway_tunnel_corner", MODID + ":" + "highway_bridge_corner_normal", MODID + ":" + "highway_bridge_corner_normal");
    public static final HighwayPartTripple STRAIT_CROSS_CORNER_CITY_DEFAULT = new HighwayPartTripple(MODID + ":" + "highway_tunnel_corner", MODID + ":" + "highway_open_corner_city", MODID + ":" + "highway_bridge_corner_city");

    public static final Codec<HighwayParts> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    HighwayPartTripple.CODEC.optionalFieldOf("straits").forGetter(l -> Optional.of(STRAIT_DEFAULT)),
                    HighwayPartTripple.CODEC.optionalFieldOf("strait_crosses").forGetter(l -> Optional.of(STRAIT_CROSS_DEFAULT)),
                    HighwayPartTripple.CODEC.optionalFieldOf("strait_cross_extensions").forGetter(l -> Optional.of(STRAIT_CROSS_EXTENSION_DEFAULT)),
                    HighwayPartTripple.CODEC.optionalFieldOf("strait_cross_mirrored_extensions").forGetter(l -> Optional.of(STRAIT_CROSS_EXTENSION_MIRRORED_DEFAULT)),
                    HighwayPartTripple.CODEC.optionalFieldOf("strait_cross_normal_corners").forGetter(l -> Optional.of(STRAIT_CROSS_CORNER_DEFAULT)),
                    HighwayPartTripple.CODEC.optionalFieldOf("strait_cross_city_corners").forGetter(l -> Optional.of(STRAIT_CROSS_CORNER_CITY_DEFAULT))

            ).apply(instance, (s, s2, s3, s4, s5, s6) -> new HighwayParts(
                    s.orElse(STRAIT_DEFAULT),
                    s2.orElse(STRAIT_CROSS_DEFAULT),
                    s3.orElse(STRAIT_CROSS_EXTENSION_DEFAULT),
                    s4.orElse(STRAIT_CROSS_EXTENSION_MIRRORED_DEFAULT),
                    s5.orElse(STRAIT_CROSS_CORNER_DEFAULT),
                    s6.orElse(STRAIT_CROSS_CORNER_CITY_DEFAULT)
            )));

    public static final HighwayParts DEFAULT = new HighwayParts(STRAIT_DEFAULT, STRAIT_CROSS_DEFAULT, STRAIT_CROSS_EXTENSION_DEFAULT,
            STRAIT_CROSS_EXTENSION_MIRRORED_DEFAULT, STRAIT_CROSS_CORNER_DEFAULT, STRAIT_CROSS_CORNER_CITY_DEFAULT);

    public Optional<HighwayParts> get() {
        if (this == DEFAULT) {
            return Optional.empty();
        } else {
            return Optional.of(this);
        }
    }
}
