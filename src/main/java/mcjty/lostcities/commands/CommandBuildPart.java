package mcjty.lostcities.commands;

public class CommandBuildPart {} /* @todo 1.14 implements ICommand {

    @Override
    public String getName() {
        return "lc_buildpart";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return getName() + " <partname>";
    }

    @Override
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        try {
            String partname = args[0];
            BuildingPart part = AssetRegistries.PARTS.get(partname);
            if (part == null) {
                sender.sendMessage(new StringTextComponent("Cannot find part '" + partname + "'!"));
                return;
            }

            EntityPlayer player = (EntityPlayer) sender;
            BlockPos start = player.getPosition();

            LostCityChunkGenerator provider = WorldTypeTools.getChunkGenerator(sender.getEntityWorld().provider.getDimension());
            BuildingInfo info = BuildingInfo.getBuildingInfo(start.getX() >> 4, start.getZ() >> 4, provider);
            CompiledPalette palette = info.getCompiledPalette();
            for (int y = 0 ; y < part.getSliceCount() ; y++) {
                for (int x = 0; x < part.getXSize(); x++) {
                    for (int z = 0; z < part.getZSize(); z++) {
                        BlockPos pos = new BlockPos(info.chunkX*16+x, start.getY()+y, info.chunkZ*16+z);
                        Character character = part.getC(x, y, z);
                        BlockState state = palette.getStraight(character);
                        if (state != null && state.getBlock() != Blocks.COMMAND_BLOCK) {
                            try {
                                sender.getEntityWorld().setBlockState(pos, state, 3);
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return Collections.emptyList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return getName().compareTo(o.getName());
    }

    public static class Slice {
        String sequence[] = new String[256];
    }
}
*/