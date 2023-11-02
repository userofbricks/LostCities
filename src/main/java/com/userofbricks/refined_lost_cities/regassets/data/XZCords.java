package com.userofbricks.refined_lost_cities.regassets.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record XZCords(int x, int z) {

    public static final Codec<XZCords> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("x").forGetter(l -> l.x),
                    Codec.INT.fieldOf("z").forGetter(l -> l.z)
            ).apply(instance, XZCords::new));
}
