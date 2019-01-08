package com.mfvanek.perfect.blog.utils;

import java.util.ArrayList;

public final class TagUtils {

    private TagUtils() {}

    // tags the tags string and put it into an array
    public static ArrayList<String> extractTags(String tags) {
        tags = tags.replaceAll("\\s", "");
        String[] tagArray = tags.split(",");

        // let's clean it up, removing the empty string and removing dups
        ArrayList<String> cleaned = new ArrayList<>();
        for (String tag : tagArray) {
            if (!tag.equals("") && !cleaned.contains(tag)) {
                cleaned.add(tag);
            }
        }

        return cleaned;
    }
}
