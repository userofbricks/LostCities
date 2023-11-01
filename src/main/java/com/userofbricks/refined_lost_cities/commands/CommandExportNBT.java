package com.userofbricks.refined_lost_cities.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import mcjty.lostcities.varia.ComponentFactory;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CommandExportNBT implements Command<CommandSourceStack> {

    private static final CommandExportNBT CMD = new CommandExportNBT();
    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("exportnbt")
                .requires(cs -> cs.hasPermission(1))
                .then(Commands
                        .argument("name", StringArgumentType.word())
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(CMD)));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String fileName = context.getArgument("name", String.class);
        if (!fileName.contains(".json")) fileName = fileName + ".json";
        ServerLevel level = context.getSource().getLevel();
        BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity == null) {
            context.getSource().sendFailure(Component.literal("No BlockEntity to read nbt from at: " + pos).withStyle(ChatFormatting.RED));
            return 0;
        }

        CompoundTag compoundTag = blockEntity.serializeNBT();

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        JsonObject root = new JsonObject();

        DataResult<JsonElement> result = CompoundTag.CODEC.encodeStart(JsonOps.INSTANCE, compoundTag);
        if(result.result().isPresent()) root.add("nbt_data", result.result().get());

        String json = gson.toJson(root);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(json);
            writer.close();
            String finalFileName = fileName;
            context.getSource().sendSuccess(() -> ComponentFactory.literal("Exported part to '" + finalFileName + "'!").withStyle(ChatFormatting.GREEN), false);
        } catch (IOException e) {
            context.getSource().sendFailure(Component.literal("Error writing file '" + fileName + "'!").withStyle(ChatFormatting.RED));
        }

        return 0;
    }
}
