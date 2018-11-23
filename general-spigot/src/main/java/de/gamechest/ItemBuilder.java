package de.gamechest;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by nemmerich on 23.11.2018.
 * <p>
 * Copyright by nemmerich - https://bytelist.de/
 */
public class ItemBuilder {

    private ItemStack itemStack;
    private ItemMeta itemMeta;

    private ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder displayname(String display) {
        this.itemMeta.setDisplayName(display);
        return this;
    }

    public ItemBuilder lore(String... lore) {
        this.itemMeta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder enchantments(HashMap<Enchantment, Integer> map) {
        this.itemStack.addEnchantments(map);
        return this;
    }

    public ItemBuilder enchantments(EnchantmentBuilder enchantmentBuilder) {
        this.enchantments(enchantmentBuilder.get());
        return this;
    }

    public ItemBuilder amount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder durability(short durability) {
        this.itemStack.setDurability(durability);
        return this;
    }

    public ItemStack get() {
        this.itemStack.setItemMeta(this.itemMeta);
        return this.itemStack;
    }

    public static ItemBuilder newBuilder(ItemStack itemStack) {
        return new ItemBuilder(itemStack);
    }

    public static ItemBuilder newBuilder(Material material, byte data) {
        return new ItemBuilder(new ItemStack(material, 1, data));
    }

    public static ItemBuilder newBuilder(Material material) {
        return newBuilder(material, (byte)0);
    }

}
