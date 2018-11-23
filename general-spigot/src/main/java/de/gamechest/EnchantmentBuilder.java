package de.gamechest;

import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;

/**
 * Created by nemmerich on 23.11.2018.
 * <p>
 * Copyright by nemmerich - https://bytelist.de/
 */
public class EnchantmentBuilder {

    private final HashMap<Enchantment, Integer> map;

    private EnchantmentBuilder() {
        this.map = new HashMap<>();
    }

    public EnchantmentBuilder append(Enchantment enchantment, Integer level) {
        this.map.put(enchantment, level);
        return this;
    }

    public EnchantmentBuilder append(Enchantment enchantment) {
        this.append(enchantment, 1);
        return this;
    }

    public HashMap<Enchantment, Integer> get() {
        return new HashMap<>(this.map);
    }


    public static EnchantmentBuilder newBuilder() {
        return new EnchantmentBuilder();
    }
}
