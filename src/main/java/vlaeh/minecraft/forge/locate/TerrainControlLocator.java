package vlaeh.minecraft.forge.locate;

import java.lang.reflect.Method;
import java.util.Random;

import org.apache.commons.lang3.reflect.FieldUtils;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.gen.structure.MapGenStructure;

public final class TerrainControlLocator extends MapStructureGenImpl {

    public final static BlockPos locate(final IChunkGenerator chunkGenerator, final String structure,
            final WorldServer serverWorld, final BlockPos position) throws Exception {
        final Object world = FieldUtils.readField(chunkGenerator, "world", true);
        final MapGenStructure gen;
        if (structure.equalsIgnoreCase("Village")) {
            gen = (MapGenStructure) FieldUtils.readField(world, "villageGen", true);
        } else if (structure.equalsIgnoreCase("Monument")) {
            gen = (MapGenStructure) FieldUtils.readField(world, "oceanMonumentGen", true);
        } else if (structure.equalsIgnoreCase("Temple")) {
            gen = (MapGenStructure) FieldUtils.readField(world, "rareBuildingGen", true);
        } else if (structure.equalsIgnoreCase("Mineshaft")) {
            gen = (MapGenStructure) FieldUtils.readField(world, "mineshaftGen", true);
        } else if (structure.equalsIgnoreCase("Fortress")) {
            gen = (MapGenStructure) FieldUtils.readField(world, "netherFortressGen", true);
        } else
            return null;

        if (gen == null)
            return null;

        try {
            final BlockPos blockpos = gen.getClosestStrongholdPos(serverWorld, position);
            if (blockpos != null)
                return blockpos;
        } catch (Exception e) {
        }

        Method getClosestStrongholdPosMethod = null;
        try {
            getClosestStrongholdPosMethod = gen.getClass().getMethod("func_180706_b", World.class, BlockPos.class);
        } catch (Exception e1) {
        }
        if ((getClosestStrongholdPosMethod != null)
                && (getClosestStrongholdPosMethod.getDeclaringClass() != MapGenStructure.class))
            return null; // default method was overridden
        if (structure.equalsIgnoreCase("Village")) {
            return LocateVillage(gen, serverWorld, position);
        } else if (structure.equalsIgnoreCase("Monument")) {
            return LocateMonument(gen, serverWorld, position);
        } else if (structure.equalsIgnoreCase("Temple")) {
            return LocateRareBuilding(gen, serverWorld, position);
        } else if (structure.equalsIgnoreCase("Mineshaft")) {
            return LocateMineshaft(gen, serverWorld, position);
        } else if (structure.equalsIgnoreCase("Fortress")) {
            return LocateFortress(gen, serverWorld, position);
        }
        return null;
    }

    private final static BlockPos LocateVillage(final MapGenStructure gen, final WorldServer serverWorld,
            final BlockPos pos) throws Exception {
        final int distance = (Integer) FieldUtils.readField(gen, "distance", true);
        return findNearestStructurePosBySpacing(gen, serverWorld, pos, distance, 8, 10387312, false, 100, false);
    }

    private final static BlockPos LocateMonument(final MapGenStructure gen, final WorldServer serverWorld,
            final BlockPos pos) throws Exception {
        final int gridSize = (Integer) FieldUtils.readField(gen, "gridSize", true);
        final int randomOffset = (Integer) FieldUtils.readField(gen, "randomOffset", true);
        return findNearestStructurePosBySpacing(gen, serverWorld, pos, gridSize, gridSize - randomOffset - 1, 10387313,
                true, 100, false);
    }

    private final static BlockPos LocateRareBuilding(final MapGenStructure gen, final WorldServer serverWorld,
            final BlockPos pos) throws Exception {
        final int maxDistanceBetweenScatteredFeatures = (Integer) FieldUtils.readField(gen,
                "maxDistanceBetweenScatteredFeatures", true);
        return findNearestStructurePosBySpacing(gen, serverWorld, pos, maxDistanceBetweenScatteredFeatures, 8, 14357617,
                false, 100, false);
    }

    private final static BlockPos LocateMineshaft(final MapGenStructure gen, final WorldServer serverWorld,
            final BlockPos pos) throws Exception {
        final Random rand = (Random) FieldUtils.readField(gen, "rand", true);
        Method canSpawnStructureMethod = null;
        // int i = 1000;
        int j = pos.getX() >> 4;
        int k = pos.getZ() >> 4;

        for (int l = 0; l <= 1000; ++l) {
            for (int i1 = -l; i1 <= l; ++i1) {
                boolean flag = i1 == -l || i1 == l;

                for (int j1 = -l; j1 <= l; ++j1) {
                    boolean flag1 = j1 == -l || j1 == l;

                    if (flag || flag1) {
                        int k1 = j + i1;
                        int l1 = k + j1;
                        rand.setSeed((long) (k1 ^ l1) ^ serverWorld.getSeed());
                        rand.nextInt();

                        if (canSpawnStructureMethod == null) {
                            canSpawnStructureMethod = gen.getClass().getDeclaredMethod("func_75047_a", int.class,
                                    int.class);
                            canSpawnStructureMethod.setAccessible(true);
                        }
                        if (((Boolean) canSpawnStructureMethod.invoke(gen, k1, l1))
                                && !isChunkGeneratedAt(serverWorld, k1, l1)) {
                            return new BlockPos((k1 << 4) + 8, 64, (l1 << 4) + 8);
                        }
                    }
                }
            }
        }

        return null;
    }

    private final static BlockPos LocateFortress(final MapGenStructure gen, final WorldServer serverWorld,
            final BlockPos pos) throws Exception {
        Method canSpawnStructureMethod = null;

        // int i = 1000;
        int j = pos.getX() >> 4;
        int k = pos.getZ() >> 4;

        for (int l = 0; l <= 1000; ++l) {
            for (int i1 = -l; i1 <= l; ++i1) {
                boolean flag = i1 == -l || i1 == l;

                for (int j1 = -l; j1 <= l; ++j1) {
                    boolean flag1 = j1 == -l || j1 == l;

                    if (flag || flag1) {
                        int k1 = j + i1;
                        int l1 = k + j1;

                        if (canSpawnStructureMethod == null) {
                            canSpawnStructureMethod = gen.getClass().getDeclaredMethod("func_75047_a", int.class,
                                    int.class);
                            canSpawnStructureMethod.setAccessible(true);
                        }
                        if (((Boolean) canSpawnStructureMethod.invoke(gen, k1, l1))
                                && !isChunkGeneratedAt(serverWorld, k1, l1)) {
                            return new BlockPos((k1 << 4) + 8, 64, (l1 << 4) + 8);
                        }
                    }
                }
            }
        }
        return null;
    }

}
