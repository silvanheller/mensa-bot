package com.github.silvanheller.mensabot.util;

public class MarkdownUtil {

    public static final String escapeMarkdown(String md) {
        return md
                .replace("_", "\\_")
                .replace("[", "\\[")
                .replace("`", "\\`");
    }

}
