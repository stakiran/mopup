package com.stakiran.mopup;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class MopupCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            CommandManager.literal("mopup")
                .then(CommandManager.literal("item")
                    .executes(MopupCommand::executeItem))
                .then(CommandManager.literal("setup")
                    .executes(MopupCommand::executeSetup))
        );
    }

    private static int executeItem(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            source.sendFeedback(() -> Text.literal("§c[Mopup] プレイヤーがコマンドを実行してください。"), false);
            return 0;
        }

        KitManager.giveKit(player);
        source.sendFeedback(() -> Text.literal("§a[Mopup] 持ち物をクリアして初期アイテムを付与しました！"), false);
        return 1;
    }

    // setup はゲーム中・勝利後でも呼べる（リセットを兼ねる）
    private static int executeSetup(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (source.getPlayer() == null) {
            source.sendFeedback(() -> Text.literal("§c[Mopup] プレイヤーがコマンドを実行してください。"), false);
            return 0;
        }

        GameManager.setup(source);
        return 1;
    }
}
