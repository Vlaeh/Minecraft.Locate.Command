package vlaeh.minecraft.forge.locate;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.structure.MapGenStructure;

public class LocateCommand extends CommandBase {
    public static final String COMMAND = "locate";
    public static final String USAGE = "/" + COMMAND + " Stronghold Monument Village EndCity Fortress Temple Mineshaft";

    @Override
    public String getName() {
        return COMMAND;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return USAGE;
    }

    public void usage(final ICommandSender sender) {
        sender.sendMessage(
                new TextComponentString("Usage: " + USAGE).setStyle(new Style().setColor(TextFormatting.RED)));
    }

    @Override
    public void execute(final MinecraftServer server, final ICommandSender sender, final String[] args)
            throws CommandException {
        if (args.length != 1) {
            usage(sender);
            return;
        }
        final String structure = args[0];

        final World entityWorld = sender.getEntityWorld();
        if (!(entityWorld instanceof WorldServer)) {
            sender.sendMessage(new TextComponentString("\u00a74Invalid server type " + entityWorld.getClass()));
            return;
        }
        final BlockPos position = sender.getPosition();
        final WorldServer serverWorld = (WorldServer) entityWorld;
        final ChunkProviderServer chunkProvider = serverWorld.getChunkProvider();
        BlockPos blockpos = chunkProvider.getStrongholdGen(serverWorld, structure, position);
        if (blockpos == null) {
            MapGenStructure gen = null;
            final IChunkGenerator chunkGenerator = chunkProvider.chunkGenerator;
            final String provider = chunkGenerator.getClass().getName();
            try {
                if (provider.equals("net.minecraft.world.gen.ChunkProviderOverworld")) {
                    try {
                        if (structure.equalsIgnoreCase("Village")) {
                            gen = (MapGenStructure) FieldUtils.readField(chunkGenerator, "field_186005_x", true); // villageGenerator
                        } else if (structure.equalsIgnoreCase("Monument")) {
                            gen = (MapGenStructure) FieldUtils.readField(chunkGenerator, "field_185980_B", true); // oceanMonumentGenerator
                        } else if (structure.equalsIgnoreCase("Temple")) {
                            gen = (MapGenStructure) FieldUtils.readField(chunkGenerator, "field_186007_z", true); // MapGenScatteredFeature
                        } else if (structure.equalsIgnoreCase("Mineshaft")) {
                            gen = (MapGenStructure) FieldUtils.readField(chunkGenerator, "field_186006_y", true); // mineshaftGenerator
                        }
                    } catch (Exception e) {
                        System.err.println(
                                "Failed loading ChunkProviderOverworld field " + structure + ": " + e.toString());
                    }
                } else if (provider.equals("net.minecraft.world.gen.ChunkProviderHell")) {
                    try {
                        if (structure.equalsIgnoreCase("Fortress")) {
                            gen = (MapGenStructure) FieldUtils.readField(chunkGenerator, "field_73172_c", true); // genNetherBridge
                        }
                    } catch (Exception e) {
                        System.err.println(
                                "Failed loading ChunkProviderHell field " + structure + ": " + e.toString());
                    }
                } else if (provider.equals("net.minecraft.world.gen.ChunkProviderEnd")) {
                    try {
                        if (structure.equalsIgnoreCase("EndCity")) {
                            gen = (MapGenStructure) FieldUtils.readField(chunkGenerator, "field_185972_n", true); // endCityGen
                        }
                    } catch (Exception e) {
                        System.err.println("Failed loading ChunkProviderEnd field " + structure + ": " + e.toString());
                    }
                } else if (provider.equals("com.khorn.terraincontrol.forge.generator.TXChunkGenerator")
                        || provider.equals("com.khorn.terraincontrol.forge.generator.ChunkProvider")){
                    blockpos = TerrainControlLocator.locate(chunkGenerator, structure, serverWorld, position);
                } else
                    sender.sendMessage(new TextComponentString("\u00a74Unknown Chunk provider " + provider));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if ((gen != null) && (blockpos == null)) {
                try {
                    blockpos = gen.getClosestStrongholdPos(serverWorld, position);
                } catch (Exception e) {
                    sender.sendMessage(new TextComponentString("\u00a74Unable to locate any " + structure + " feature"));
                    return;
                }
            }
        }
        if (blockpos != null)
            sender.sendMessage(new TextComponentString("Located " + structure + " at " + blockpos.getX() + " (y?) " + blockpos.getZ()));
        else
            sender.sendMessage(new TextComponentString("Could not locate " + structure));
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "Village", "Stronghold", "Monument", "Temple", "Mineshaft",
                    "Fortress", "EndCity");
        }
        return Collections.emptyList();
    }
}
