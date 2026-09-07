package com.minedest.addon;

import baritone.api.BaritoneAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.monster.IMob;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("minedestaddon")
public class MineDestAddon {
    private final Minecraft mc = Minecraft.getInstance();

    public MineDestAddon() {
        MinecraftForge.EVENT_BUS.register(this);
        
        // Настройка Baritone для корректной работы с водой
        BaritoneAPI.getSettings().assumeWalkOnWater.value = false;
        BaritoneAPI.getSettings().disconnectOnArrival.value = false;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || mc.player == null || mc.level == null) return;

        handleAutoEat();
        handleWater();
        handleMobDefense();
    }

    // 1. Логика авто-поедания
    private void handleAutoEat() {
        if (mc.player.getFoodData().getFoodLevel() < 14 && mc.screen == null) {
            for (int i = 0; i < 9; i++) {
                ItemStack stack = mc.player.inventory.getItem(i);
                if (stack.getItem().isEdible()) {
                    mc.player.inventory.selected = i;
                    mc.options.keyUse.setDown(true);
                    return;
                }
            }
        } else if (mc.options.keyUse.isDown() && mc.player.getFoodData().getFoodLevel() >= 20) {
            mc.options.keyUse.setDown(false);
        }
    }

    // 2. Авто-выплывание из воды
    private void handleWater() {
        if (mc.player.isInWater() && mc.screen == null) {
            mc.options.keyJump.setDown(true);
        }
    }

    // 3. Авто-атака мобов (KillAura)
    private void handleMobDefense() {
        if (mc.screen != null) return;
        
        // Атакуем только при 100% готовности КД оружия
        if (mc.player.getAttackStrengthScale(0.0f) < 1.0f) return;

        mc.level.entitiesForRendering().forEach(entity -> {
            if (entity instanceof IMob && mc.player.distanceToSqr(entity) <= 12.25) { // Дистанция ~3.5 блоков
                mc.gameMode.attack(mc.player, entity);
                mc.player.swing(Hand.MAIN_HAND);
            }
        });
    }
}
