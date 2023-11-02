package mcjty.lostcities.worldgen.lost.cityassets;

import com.userofbricks.refined_lost_cities.regassets.data.XZCords;
import net.minecraft.world.level.CommonLevelAccessor;

import java.util.List;

public interface IBuildingPart {
    Character getMetaChar(String key);

    Integer getMetaInteger(String key);

    boolean getMetaBoolean(String key);

    Float getMetaFloat(String key);

    String getMetaString(String key);

    List<XZCords> getMetaIntPairList(String key);

    String getName();

    char[][] getVslices();

    char[] getVSlice(int x, int z);

    Palette getLocalPalette(CommonLevelAccessor level);

    int getSliceCount();

    int getXSize();

    int getZSize();
}
