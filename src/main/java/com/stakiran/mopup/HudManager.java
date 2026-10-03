package com.stakiran.mopup;

import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

// ボスバー: 地上のターゲットモブ数 / 勝利までのカウントダウン
// アクションバー: Y座標と発光タイミング
public class HudManager {

    private static final int TICKS_PER_SECOND = 20;

    private static final ServerBossBar bossBar = new ServerBossBar(
        Text.literal(""), BossBar.Color.RED, BossBar.Style.PROGRESS);

    public static void show(MinecraftServer server) {
        bossBar.clearPlayers();
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            bossBar.addPlayer(player);
        }
        bossBar.setVisible(true);
    }

    public static void hide() {
        bossBar.clearPlayers();
        bossBar.setVisible(false);
    }

    public static void update(MinecraftServer server, int targetCount, int clearTicksRemaining, int clearTicks,
            boolean glowing, int glowSecondsLeft) {
        if (clearTicksRemaining >= 0) {
            int secondsLeft = (clearTicksRemaining + TICKS_PER_SECOND - 1) / TICKS_PER_SECOND;
            bossBar.setName(Text.literal("§a地上の敵: 0 §f| 勝利まで " + secondsLeft + " 秒"));
            bossBar.setColor(BossBar.Color.GREEN);
            bossBar.setPercent((float) clearTicksRemaining / clearTicks);
        } else {
            bossBar.setName(Text.literal("§c地上の敵: " + targetCount));
            bossBar.setColor(BossBar.Color.RED);
            bossBar.setPercent(1.0f);
        }

        String glowText = glowing
            ? "§e発光中 残り " + glowSecondsLeft + " 秒"
            : "§7次の発光まで " + glowSecondsLeft + " 秒";
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            // Covers players who joined after setup (no-op if already added)
            bossBar.addPlayer(player);
            int y = player.getBlockPos().getY();
            player.sendMessage(Text.literal("§fY: §b" + y + " §8| " + glowText), true);
        }
    }

    public static void showTitle(MinecraftServer server, String title, String subtitle) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            player.networkHandler.sendPacket(new SubtitleS2CPacket(Text.literal(subtitle)));
            player.networkHandler.sendPacket(new TitleS2CPacket(Text.literal(title)));
        }
    }
}
