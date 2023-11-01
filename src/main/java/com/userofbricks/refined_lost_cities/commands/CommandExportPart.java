package com.userofbricks.refined_lost_cities.commands;

import com.google.gson.*;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import mcjty.lostcities.setup.Registration;
import mcjty.lostcities.varia.ComponentFactory;
import mcjty.lostcities.varia.Tools;
import mcjty.lostcities.worldgen.IDimensionInfo;
import mcjty.lostcities.worldgen.lost.BuildingInfo;
import mcjty.lostcities.worldgen.lost.cityassets.CompiledPalette;
import mcjty.lostcities.worldgen.lost.cityassets.Palette;
import mcjty.lostcities.worldgen.lost.regassets.BuildingPartRE;
import mcjty.lostcities.worldgen.lost.regassets.PaletteRE;
import mcjty.lostcities.worldgen.lost.regassets.data.PaletteEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import javax.annotation.Nullable;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CommandExportPart implements Command<CommandSourceStack> {

    private static final CommandExportPart CMD = new CommandExportPart();
    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("exportpart")
                .requires(cs -> cs.hasPermission(1))
                .then(Commands
                        .argument("name", StringArgumentType.word())
                        .then(Commands.argument("from", BlockPosArgument.blockPos())
                                .then(Commands.argument("to", BlockPosArgument.blockPos())
                                        .executes(CMD))));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String filename = context.getArgument("name", String.class);
        ServerPlayer player = context.getSource().getPlayerOrException();
        BoundingBox area = BoundingBox.fromCorners(
                BlockPosArgument.getLoadedBlockPos(context, "from"),
                BlockPosArgument.getLoadedBlockPos(context, "to")
        );

        if (area.getXSpan() > 16) {
            context.getSource().sendFailure(Component.literal("X dimension is too large").withStyle(ChatFormatting.RED));
            return 0;
        }

        if (area.getZSpan() > 16) {
            context.getSource().sendFailure(Component.literal("Z dimension is too large").withStyle(ChatFormatting.RED));
            return 0;
        }

        ServerLevel level = (ServerLevel) player.level();
        IDimensionInfo dimInfo = Registration.LOSTCITY_FEATURE.get().getDimensionInfo(level);
        if (dimInfo == null) {
            context.getSource().sendFailure(ComponentFactory.literal("This dimension doesn't support Lost Cities!"));
            return 0;
        }

        BuildingInfo info = BuildingInfo.getBuildingInfo(area.getCenter().getX() >> 4, area.getCenter().getZ() >> 4, dimInfo);
        CompiledPalette palette = info.getCompiledPalette();
        Palette buildingPalette = info.getBuilding().getLocalPalette(level);
        if (buildingPalette != null) {
            palette = new CompiledPalette(palette, buildingPalette);
        }

        Map<BlockState, Character> unknowns = new HashMap<>();

        List<List<String>> slices = new ArrayList<>();
        Set<Character> usedCharacters = new HashSet<>(palette.getCharacters());
        String possibleChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()_+-=[]{}|;:'<>,.?/`~¡™£¢∞§¶•ªº–≠œ∑´®†¥øπ“‘«åß∂ƒ©˙∆˚¬…æΩ≈√∫∫≠µ≤≥";

        for (int y = 0 ; y < area.getYSpan() ; y++) {
            List<String> yslice = new ArrayList<>();
            for (int z = 0; z < area.getZSpan(); z++) {
                StringBuilder b = new StringBuilder();
                for (int x = 0; x < area.getXSpan(); x++) {
                    BlockPos pos = new BlockPos(area.minX()+x, area.minY()+y, area.minZ()+z);
                    BlockState state = level.getBlockState(pos);
                    Character c = palette.find(state);
                    if (c == null) {
                        c = unknowns.get(state);
                    }
                    if (c == null) {
                        // New state!
                        c = (char) possibleChars.chars().filter(value -> !usedCharacters.contains((char)value)).findFirst().getAsInt();
                        unknowns.put(state, c);
                        usedCharacters.add(c);
                    }
                    b.append(c);
                }
                yslice.add(b.toString());
            }
            slices.add(yslice);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        JsonObject root = new JsonObject();

        if (!unknowns.isEmpty()) {
            List<PaletteEntry> entries = new ArrayList<>();
            for (Map.Entry<BlockState, Character> entry : unknowns.entrySet()) {
                entries.add(new PaletteEntry(Character.toString(entry.getValue()), Optional.of(Tools.stateToString(entry.getKey())),
                        Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                        Optional.empty()));
            }
            PaletteRE paletteRE = new PaletteRE(entries);
            DataResult<JsonElement> result = PaletteRE.CODEC.encodeStart(JsonOps.INSTANCE, paletteRE);
            root.add("__comment__", new JsonPrimitive("'missingpalette' represents all blockstates that it couldn't find in the palette. These have to be put in a palette. " +
                    "'exportedpart' is the actual exported part"));
            root.add("missingpalette", result.result().get());
        } else {
            root.add("__comment__", new JsonPrimitive("'exportedpart' is the actual exported part"));
        }

        BuildingPartRE buildingPartRE = new BuildingPartRE(area.getXSpan(), area.getZSpan(), slices,
                Optional.empty(), Optional.empty(), Optional.empty());
        DataResult<JsonElement> result = BuildingPartRE.CODEC.encodeStart(JsonOps.INSTANCE, buildingPartRE);
        root.add("exportedpart", result.result().get());

        String json = gson.toJson(root);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write(json);
            writer.close();
            context.getSource().sendSuccess(() -> ComponentFactory.literal("Exported part to '" + filename + "'!").withStyle(ChatFormatting.GREEN), false);
        } catch (IOException e) {
            context.getSource().sendFailure(Component.literal("Error writing file '" + filename + "'!").withStyle(ChatFormatting.RED));
        }

        return 0;
    }

    @Nullable
    public static Character find(BlockState state, @Nullable CompoundTag tag, CompiledPalette compiledPalette) {
        for (Character character : compiledPalette.getCharacters()) {
            CompoundTag cTag = compiledPalette.getInfo(character).tag();
            if (tag != null && cTag != tag) continue;

            Set<BlockState> states = compiledPalette.getAll(character);
            for (BlockState randomBlock : states) {
                if (randomBlock == state) {
                    return character;
                }
            }
        }
        return null;
    }
}
