package com.userofbricks.refined_lost_cities.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import static com.userofbricks.refined_lost_cities.CreateLostCities.MODID;

public class ModCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> commands = dispatcher.register(
                Commands.literal(MODID)
                        .then(CommandExportPart.register(dispatcher))
                        .then(CommandExportNBT.register(dispatcher))
        );

        dispatcher.register(Commands.literal("b_lost").redirect(commands));
    }
}
