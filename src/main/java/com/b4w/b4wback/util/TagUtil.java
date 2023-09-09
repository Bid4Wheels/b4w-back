package com.b4w.b4wback.util;

import com.b4w.b4wback.model.Tag;

import java.util.ArrayList;
import java.util.List;

public class TagUtil {
    /**removes all tags that already exist**/
    public static void removeExistentTagsInListFromStringList(List<Tag> tags, List<String> tagsString){
        for (int i = tags.size() - 1; i >= 0; i--)
            for (int j = tagsString.size() - 1; j >= 0; j--) {
                if (!tags.get(i).getTagName().equals(tagsString.get(j))) continue;
                tagsString.remove(j);
            }
    }

    public static List<Tag> createTagsFromStringList(List<String> tagsString){
        List<Tag> tags = new ArrayList<>();
        tagsString.forEach(ts->tags.add(new Tag(ts)));
        return tags;
    }
}
