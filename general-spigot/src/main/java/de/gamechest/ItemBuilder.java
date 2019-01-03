package de.gamechest;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
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

    public ItemBuilder lore(ArrayList<String> lore) {
        this.itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder enchantments(HashMap<Enchantment, Integer> map, boolean unsave) {
        if(unsave) map.forEach((enchantment, integer) -> this.itemMeta.addEnchant(enchantment, integer, true));
        else this.itemStack.addEnchantments(map);

        return this;
    }

    public ItemBuilder enchantments(HashMap<Enchantment, Integer> map) {
        return this.enchantments(map, false);
    }

    public ItemBuilder enchantments(EnchantmentBuilder enchantmentBuilder, boolean unsave) {
        return this.enchantments(enchantmentBuilder.get(), unsave);
    }

    public ItemBuilder enchantments(EnchantmentBuilder enchantmentBuilder) {
        return this.enchantments(enchantmentBuilder, false);
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

    public static ItemBuilder newBuilder(Material material, int amount, byte data) {
        return newBuilder(new ItemStack(material, amount, data));
    }

    public static ItemBuilder newBuilder(Material material, byte data) {
        return newBuilder(material, 1, data);
    }

    public static ItemBuilder newBuilder(Material material) {
        return newBuilder(material, (byte)0);
    }

    public static ItemStack getPlaceholder() {
        return newBuilder(Material.STAINED_GLASS_PANE, (byte)15).displayname("§r").get();
    }

}
