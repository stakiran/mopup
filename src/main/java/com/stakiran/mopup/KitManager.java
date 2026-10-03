package com.stakiran.mopup;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// lightgame100 のキットからエリトラとロケット花火を除いたもの
public class KitManager {

    public static void giveKit(ServerPlayerEntity player) {
        Registry<Enchantment> enchantmentRegistry = player.getEntityWorld()
            .getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);

        List<ItemStack> contents = new ArrayList<>(Collections.nCopies(27, ItemStack.EMPTY));

        // Row 1: ネザライトの剣, 松明, ネザライトのツルハシ, 丸石, ネザライトの斧, ネザライトのショベル, マグマバケツ, 水バケツ, 牛肉
        contents.set(0, new ItemStack(Items.NETHERITE_SWORD));
        contents.set(1, new ItemStack(Items.TORCH, 64));
        contents.set(2, createPickaxe(enchantmentRegistry));
        contents.set(3, new ItemStack(Items.COBBLESTONE, 64));
        contents.set(4, createAxe(enchantmentRegistry));
        contents.set(5, createShovel(enchantmentRegistry));
        contents.set(6, new ItemStack(Items.LAVA_BUCKET));
        contents.set(7, new ItemStack(Items.WATER_BUCKET));
        contents.set(8, new ItemStack(Items.COOKED_BEEF, 64));

        // Row 2: 盾, 鉄レギンス, 鉄ブーツ, 鉄ヘルメット, 鉄チェストプレート, 盾, (空), (空), 松明
        contents.set(9, new ItemStack(Items.SHIELD));
        contents.set(10, new ItemStack(Items.IRON_LEGGINGS));
        contents.set(11, new ItemStack(Items.IRON_BOOTS));
        contents.set(12, new ItemStack(Items.IRON_HELMET));
        contents.set(13, new ItemStack(Items.IRON_CHESTPLATE));
        contents.set(14, new ItemStack(Items.SHIELD));
        contents.set(17, new ItemStack(Items.TORCH, 64));

        // Row 3: 松明 x5スタック
        for (int i = 18; i <= 22; i++) {
            contents.set(i, new ItemStack(Items.TORCH, 64));
        }

        ItemStack shulkerBox = new ItemStack(Items.SHULKER_BOX);
        shulkerBox.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(contents));

        player.giveItemStack(shulkerBox);
    }

    private static ItemStack createPickaxe(Registry<Enchantment> registry) {
        ItemStack stack = new ItemStack(Items.NETHERITE_PICKAXE);
        ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
        addEnchantment(builder, registry, Enchantments.EFFICIENCY, 5);
        addEnchantment(builder, registry, Enchantments.AQUA_AFFINITY, 1);
        addEnchantment(builder, registry, Enchantments.UNBREAKING, 3);
        addEnchantment(builder, registry, Enchantments.MENDING, 1);
        stack.set(DataComponentTypes.ENCHANTMENTS, builder.build());
        return stack;
    }

    private static ItemStack createAxe(Registry<Enchantment> registry) {
        ItemStack stack = new ItemStack(Items.NETHERITE_AXE);
        ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
        addEnchantment(builder, registry, Enchantments.EFFICIENCY, 5);
        addEnchantment(builder, registry, Enchantments.UNBREAKING, 3);
        addEnchantment(builder, registry, Enchantments.MENDING, 1);
        stack.set(DataComponentTypes.ENCHANTMENTS, builder.build());
        return stack;
    }

    private static ItemStack createShovel(Registry<Enchantment> registry) {
        ItemStack stack = new ItemStack(Items.NETHERITE_SHOVEL);
        ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
        addEnchantment(builder, registry, Enchantments.EFFICIENCY, 5);
        addEnchantment(builder, registry, Enchantments.UNBREAKING, 3);
        addEnchantment(builder, registry, Enchantments.MENDING, 1);
        stack.set(DataComponentTypes.ENCHANTMENTS, builder.build());
        return stack;
    }

    private static void addEnchantment(ItemEnchantmentsComponent.Builder builder,
            Registry<Enchantment> registry, RegistryKey<Enchantment> key, int level) {
        registry.getEntry(key.getValue()).ifPresent(entry -> builder.add(entry, level));
    }
}
