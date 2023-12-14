package com.userofbricks.refined_lost_cities;

import com.mojang.logging.LogUtils;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.userofbricks.refined_lost_cities.tags.CLCBlockTags;
import com.userofbricks.refined_lost_cities.worldgen.blocks.CLCBlocks;
import com.userofbricks.refined_lost_cities.events.events.CLCForgeEventHandlers;
import com.userofbricks.refined_lost_cities.init.StructureTypeInit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(CreateLostCities.MODID)
public class CreateLostCities {
    public static final String MODID = "create_lost_cities";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final NonNullSupplier<Registrate> REGISTRATE = NonNullSupplier.lazy(() -> Registrate.create(MODID));

    public CreateLostCities() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        CLCBlockTags.init();
        CLCBlocks.loadClass();
        StructureTypeInit.STRUCTURE_TYPES.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new CLCForgeEventHandlers());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }
    }
}
