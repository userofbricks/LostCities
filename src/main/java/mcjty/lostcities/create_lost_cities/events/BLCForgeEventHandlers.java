package mcjty.lostcities.create_lost_cities.events;

import mcjty.lostcities.create_lost_cities.commands.ModCommands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BLCForgeEventHandlers {

    @SubscribeEvent
    public void commandRegister(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }
}
