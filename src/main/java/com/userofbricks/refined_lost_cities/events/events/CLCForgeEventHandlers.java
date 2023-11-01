package com.userofbricks.refined_lost_cities.events.events;

import com.userofbricks.refined_lost_cities.commands.ModCommands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CLCForgeEventHandlers {

    @SubscribeEvent
    public void commandRegister(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }
}
