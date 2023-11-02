package mcjty.lostcities.worldgen.lost.regassets.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.userofbricks.refined_lost_cities.regassets.data.XZCords;

import java.util.List;
import java.util.Optional;

/**
 * Represents metadata that can be associated with a building part
 */
public record PartMeta(String key, Boolean bool, String chr, String str,
                       Integer i, Float f, List<XZCords> int_pair_list) {

    public static final Codec<PartMeta> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("key").forGetter(l -> l.key),
                    Codec.BOOL.optionalFieldOf("boolean").forGetter(l -> Optional.ofNullable(l.bool)),
                    Codec.STRING.optionalFieldOf("char").forGetter(l -> Optional.ofNullable(l.chr)),
                    Codec.STRING.optionalFieldOf("string").forGetter(l -> Optional.ofNullable(l.str)),
                    Codec.INT.optionalFieldOf("integer").forGetter(l -> Optional.ofNullable(l.i)),
                    Codec.FLOAT.optionalFieldOf("float").forGetter(l -> Optional.ofNullable(l.f)),
                    Codec.list(XZCords.CODEC).optionalFieldOf("int_pairs").forGetter(l -> Optional.ofNullable(l.int_pair_list))
            ).apply(instance, PartMeta::create));

    public static PartMeta create(String key, Optional<Boolean> bool, Optional<String> chr, Optional<String> str,
                                  Optional<Integer> i, Optional<Float> f, Optional<List<XZCords>> int_pair_list) {
        return new PartMeta(key,
                bool.orElse(null),
                chr.orElse(null),
                str.orElse(null),
                i.orElse(null),
                f.orElse(null),
                int_pair_list.orElse(null));
    }
}
