package eu.octanne.edora;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.octanne.edora.client.screen.BackpackScreen;
import eu.octanne.edora.screenHandler.BackpackScreenHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class EdoraMain implements ModInitializer{

    public static Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "edora";
    public static final String MOD_NAME = "EdoraWorld";
    public static final Identifier MOD_FULL_ID = new Identifier(EdoraMain.MOD_ID, EdoraMain.MOD_ID);

    public static final ScreenHandlerType<BackpackScreenHandler> BACKPACK_SCREEN_HANDLER;

    static {
        BACKPACK_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier(MOD_ID, "backpack"), BackpackScreenHandler::new);
    }

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }

    @Override
    public void onInitialize() {
        
    }

}