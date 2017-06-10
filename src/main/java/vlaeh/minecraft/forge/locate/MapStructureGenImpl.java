package vlaeh.minecraft.forge.locate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructure;

public class MapStructureGenImpl {

    final static boolean isChunkGeneratedAt(final World world, final int x, final int z) {
        if (world.isBlockLoaded(new BlockPos(x << 4, 60, z << 4), false))
            return true;
        if (world.getChunkProvider().getLoadedChunk(x, z) != null)
            return true;
        return false;
    }

    final static void setupChunkSeed(long p_191068_0_, Random p_191068_2_, int p_191068_3_, int p_191068_4_) {
        p_191068_2_.setSeed(p_191068_0_);
        long i = p_191068_2_.nextLong();
        long j = p_191068_2_.nextLong();
        long k = (long) p_191068_3_ * i;
        long l = (long) p_191068_4_ * j;
        p_191068_2_.setSeed(k ^ l ^ p_191068_0_);
    }

    static final BlockPos findNearestStructurePosBySpacing(final MapGenStructure gen, World worldIn, BlockPos p_191069_2_, int p_191069_3_,
            int p_191069_4_, int p_191069_5_, boolean p_191069_6_, int p_191069_7_, boolean findUnexplored) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        int i = p_191069_2_.getX() >> 4;
        int j = p_191069_2_.getZ() >> 4;
        int k = 0;
        Method canSpawnStructureMethod = null;

        for (Random random = new Random(); k <= p_191069_7_; ++k) {
            for (int l = -k; l <= k; ++l) {
                boolean flag = l == -k || l == k;

                for (int i1 = -k; i1 <= k; ++i1) {
                    boolean flag1 = i1 == -k || i1 == k;

                    if (flag || flag1) {
                        int j1 = i + p_191069_3_ * l;
                        int k1 = j + p_191069_3_ * i1;

                        if (j1 < 0) {
                            j1 -= p_191069_3_ - 1;
                        }

                        if (k1 < 0) {
                            k1 -= p_191069_3_ - 1;
                        }

                        int l1 = j1 / p_191069_3_;
                        int i2 = k1 / p_191069_3_;
                        Random random1 = worldIn.setRandomSeed(l1, i2, p_191069_5_);
                        l1 = l1 * p_191069_3_;
                        i2 = i2 * p_191069_3_;

                        if (p_191069_6_) {
                            l1 = l1 + (random1.nextInt(p_191069_3_ - p_191069_4_)
                                    + random1.nextInt(p_191069_3_ - p_191069_4_)) / 2;
                            i2 = i2 + (random1.nextInt(p_191069_3_ - p_191069_4_)
                                    + random1.nextInt(p_191069_3_ - p_191069_4_)) / 2;
                        } else {
                            l1 = l1 + random1.nextInt(p_191069_3_ - p_191069_4_);
                            i2 = i2 + random1.nextInt(p_191069_3_ - p_191069_4_);
                        }

                        setupChunkSeed(worldIn.getSeed(), random, l1, i2);
                        random.nextInt();

                        if (canSpawnStructureMethod == null) {
                            canSpawnStructureMethod = gen.getClass().getDeclaredMethod("func_75047_a", int.class, int.class);
                            canSpawnStructureMethod.setAccessible(true);
                        }
                        if ((Boolean) canSpawnStructureMethod.invoke(gen, l1, i2)) {
                            if (!findUnexplored || !isChunkGeneratedAt(worldIn, l1, i2)) {
                                return new BlockPos((l1 << 4) + 8, 64, (i2 << 4) + 8);
                            }
                        } else if (k == 0) {
                            break;
                        }
                    }
                }

                if (k == 0) {
                    break;
                }
            }
        }
        return null;
    }

}
