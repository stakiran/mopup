package com.stakiran.mopup;

import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// ボスバーに 地上のターゲットモブ数 / 勝利までのカウントダウン、Y座標、発光タイミング をまとめて表示する
// Y座標がプレイヤーごとに異なるため、ボスバーはプレイヤーごとに持つ
public class HudManager {

    private static final int TICKS_PER_SECOND = 20;

    private static final Map<UUID, ServerBossBar> bossBars = new HashMap<>();

    public static void hide() {
        for (ServerBossBar bar : bossBars.values()) {
            bar.clearPlayers();
        }
        bossBars.clear();
    }

    public static void update(MinecraftServer server, int targetCount, boolean targetSeen,
            int clearTicksRemaining, int clearTicks, boolean glowing, int glowSecondsLeft) {
        String mobText;
        BossBar.Color color;
        float percent;
        if (clearTicksRemaining >= 0) {
            int secondsLeft = (clearTicksRemaining + TICKS_PER_SECOND - 1) / TICKS_PER_SECOND;
            mobText = "§a地上の敵: 0 §f勝利まで " + secondsLeft + " 秒";
            color = BossBar.Color.GREEN;
            percent = (float) clearTicksRemaining / clearTicks;
        } else if (!targetSeen) {
            mobText = "§c地上の敵: 0 §7(出現待ち)";
            color = BossBar.Color.RED;
            percent = 1.0f;
        } else {
            mobText = "§c地上の敵: " + targetCount;
            color = BossBar.Color.RED;
            percent = 1.0f;
        }

        String glowText = glowing
            ? "§e発光中 残り " + glowSecondsLeft + " 秒"
            : "§7発光まで " + glowSecondsLeft + " 秒";

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ServerBossBar bar = bossBars.computeIfAbsent(player.getUuid(),
                uuid -> new ServerBossBar(Text.literal(""), color, BossBar.Style.PROGRESS));
            // Covers players who joined after setup or reconnected (new player object)
            if (!bar.getPlayers().contains(player)) {
                bar.clearPlayers();
                bar.addPlayer(player);
            }

            int y = player.getBlockPos().getY();
            // setName/setColor/setPercent only send packets when the value changes
            bar.setName(Text.literal(mobText + " §8| §fY: §b" + y + " §8| " + glowText));
            bar.setColor(color);
            bar.setPercent(percent);
        }
    }

    public static void showTitle(MinecraftServer server, String title, String subtitle) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            player.networkHandler.sendPacket(new SubtitleS2CPacket(Text.literal(subtitle)));
            player.networkHandler.sendPacket(new TitleS2CPacket(Text.literal(title)));
        }
    }
}
