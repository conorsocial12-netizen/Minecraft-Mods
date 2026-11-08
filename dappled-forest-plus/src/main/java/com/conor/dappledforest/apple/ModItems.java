package com.conor.dappledforest.apple;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

public final class ModItems {
	private ModItems() {
	}

	/** 2 hunger points = 1 drumstick on the hunger bar. */
	public static final FoodProperties SOUR_CANDY_APPLE_FOOD = new FoodProperties.Builder()
			.nutrition(2)
			.saturationModifier(0.3f)
			.alwaysEdible() // like golden apples, you can eat it for the effect even when full
			.build();

	/** Swiftness I (amplifier 0) for 60 seconds (20 ticks per second). */
	public static final Consumable SOUR_CANDY_APPLE_CONSUMABLE = Consumables.defaultFood()
			.onConsume(new ApplyStatusEffectsConsumeEffect(
					new MobEffectInstance(MobEffects.SPEED, 60 * 20, 0), 1.0f))
			.build();

	public static final ResourceKey<Item> SOUR_CANDY_APPLE_KEY = ResourceKey.create(
			Registries.ITEM, Identifier.fromNamespaceAndPath(SourCandyApple.MOD_ID, "sour_candy_apple"));

	public static final Item SOUR_CANDY_APPLE = Registry.register(
			BuiltInRegistries.ITEM,
			SOUR_CANDY_APPLE_KEY,
			new Item(new Item.Properties()
					.setId(SOUR_CANDY_APPLE_KEY)
					.food(SOUR_CANDY_APPLE_FOOD, SOUR_CANDY_APPLE_CONSUMABLE)
					.rarity(Rarity.RARE)));

	public static void init() {
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
				.register(output -> output.accept(SOUR_CANDY_APPLE));
	}
}
