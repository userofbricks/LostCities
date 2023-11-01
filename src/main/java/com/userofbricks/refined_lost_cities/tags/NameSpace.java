package com.userofbricks.refined_lost_cities.tags;

import com.simibubi.create.Create;
import com.userofbricks.refined_lost_cities.CreateLostCities;
import mcjty.lostcities.LostCities;

public enum NameSpace {
    MOD(CreateLostCities.MODID, false),
    LOSTCITIES(LostCities.MODID, false),
    FORGE("forge")

    ;

    public final String id;
    public final boolean optionalDefault;

    NameSpace(String id) {
        this(id, true);
    }

    NameSpace(String id, boolean optionalDefault) {
        this.id = id;
        this.optionalDefault = optionalDefault;
    }
}
