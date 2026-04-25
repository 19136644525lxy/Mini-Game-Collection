package qituo.minigc.games;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import qituo.minigc.config.GameConfig;

public class GameModeTaskManager {
    private static final int TICKS_PER_SECOND = 20;
    private static int tickCounter = 0;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(GameModeTaskManager::onServerTick);
        ServerLifecycleEvents.SERVER_STOPPING.register(GameModeTaskManager::onServerStopping);
    }

    private static void onServerTick(MinecraftServer server) {
        if (!GameConfig.gameModeSwitchEnabled) {
            return;
        }

        // 确保游戏活跃状态为true
        if (!GameConfig.isGameActive()) {
            GameConfig.setGameActive(true);
        }

        tickCounter++;
        int switchIntervalTicks = GameConfig.switchIntervalSeconds * TICKS_PER_SECOND;

        if (tickCounter >= switchIntervalTicks) {
            tickCounter = 0;
            GameModeManager.switchGameMode(server);
        }
    }

    private static void onServerStopping(MinecraftServer server) {
        // 服务器停止时重置游戏活跃状态
        GameConfig.setGameActive(false);
    }
}