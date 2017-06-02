package vlaeh.minecraft.forge.locate;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = Locate.MODID, //
        version = Locate.VERSION, //
        name = Locate.NAME, //
        acceptableRemoteVersions = "*", //
        acceptedMinecraftVersions = "[1.9,1.12)" //
)
public class Locate {
    public static final String MODID = "locate";
    public static final String VERSION = "1.0";
    public static final String NAME = "Locate for 1.10";

    public static Configuration config;
    public static boolean skipEnabled = true;
    public static boolean messageEnabled = true;
    public static boolean statusEnabled = false;
    public static boolean clearWeather = true;
    public static boolean setSpawnDuringDayEnabled = false;
    public static int ratio = 50;

    @Mod.Instance
    public static Locate instance;

    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new LocateCommand());
    }

}
