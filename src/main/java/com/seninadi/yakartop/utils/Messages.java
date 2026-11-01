package com.seninadi.yakartop.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class Messages {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    // Modern prefix: gradient efektli "YakarTop" ve ayraç
    public static final Component PREFIX = MM.deserialize(
            "<gradient:#FF6B6B:#FFD93D><bold>YakarTop</bold></gradient> <dark_gray>» <gray>"
    );

    public static final Component ERROR_PREFIX = MM.deserialize(
            "<gradient:#FF4C4C:#8B0000><bold>Hata</bold></gradient> <dark_gray>» <gray>"
    );

    public static final Component SUCCESS_PREFIX = MM.deserialize(
            "<gradient:#4CAF50:#00E676><bold>Başarılı</bold></gradient> <dark_gray>» <gray>"
    );

    private Messages() {}

    public static Component info(String msg) {
        return PREFIX.append(MM.deserialize("<gray>" + msg));
    }

    public static Component error(String msg) {
        return ERROR_PREFIX.append(MM.deserialize("<gray>" + msg));
    }

    public static Component success(String msg) {
        return SUCCESS_PREFIX.append(MM.deserialize("<gray>" + msg));
    }

    public static Component raw(String miniMessageString) {
        return MM.deserialize(miniMessageString);
    }

    public static Component title(String text) {
        return MM.deserialize("<gradient:#FF6B6B:#FFD93D><bold>" + text + "</bold></gradient>")
                .decoration(TextDecoration.ITALIC, false);
    }
}
