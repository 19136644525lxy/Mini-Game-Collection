package qituo.minigc.games;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GameModeManager {
    private static final Random random = new Random();
    private static final Map<ServerPlayerEntity, GameMode> lastGameModes = new HashMap<>();

    public static void switchGameMode(MinecraftServer server) {
        List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();

        if (players.isEmpty()) {
            return;
        }

        for (ServerPlayerEntity player : players) {
            GameMode[] gameModes = GameMode.values();
            GameMode lastGameMode = lastGameModes.get(player);
            GameMode newGameMode;

            // 确保不选择与上次相同的游戏模式
            do {
                newGameMode = gameModes[random.nextInt(gameModes.length)];
            } while (lastGameMode != null && newGameMode == lastGameMode);

            // 记录本次游戏模式
            lastGameModes.put(player, newGameMode);

            player.changeGameMode(newGameMode);

            Formatting color = getGameModeColor(newGameMode);
            String gameModeKey = getGameModeName(newGameMode);
            Text gameModeText = Text.translatable(gameModeKey).styled(style -> style.withColor(color));
            Text message = Text.translatable("minigc.game_mode.switch", player.getName(), gameModeText);
            Text prefix = Text.translatable("minigc.prefix").styled(style -> style.withColor(Formatting.GOLD));
            message = Text.empty().append(prefix).append(Text.literal(" ")).append(message);
            server.getPlayerManager().broadcast(message, false);
        }
    }

    private static String getGameModeName(GameMode gameMode) {
        switch (gameMode) {
            case SURVIVAL:
                return "minigc.game_mode.survival";
            case CREATIVE:
                return "minigc.game_mode.creative";
            case ADVENTURE:
                return "minigc.game_mode.adventure";
            case SPECTATOR:
                return "minigc.game_mode.spectator";
            default:
                return gameMode.getName();
        }
    }

    private static Formatting getGameModeColor(GameMode gameMode) {
        switch (gameMode) {
            case SURVIVAL:
                return Formatting.GREEN;
            case CREATIVE:
                return Formatting.AQUA;
            case ADVENTURE:
                return Formatting.YELLOW;
            case SPECTATOR:
                return Formatting.GRAY;
            default:
                return Formatting.WHITE;
        }
    }
}