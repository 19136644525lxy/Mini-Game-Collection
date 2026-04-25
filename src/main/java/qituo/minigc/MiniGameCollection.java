package qituo.minigc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qituo.minigc.commands.GameCommand;
import qituo.minigc.config.GameConfig;
import qituo.minigc.games.GameModeTaskManager;

public class MiniGameCollection implements ModInitializer {
    public static final String MOD_ID = "minigc";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {

        LOGGER.info("Hello Fabric world!");

        GameConfig.loadConfig();

        GameModeTaskManager.register();
        LOGGER.info("Game mode switcher registered. Will switch game modes every {} seconds.", GameConfig.switchIntervalSeconds);

        CommandRegistrationCallback.EVENT.register(GameCommand::register);
        LOGGER.info("Commands registered.");
    }
}