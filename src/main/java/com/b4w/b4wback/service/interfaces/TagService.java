package com.b4w.b4wback.service.interfaces;


import com.b4w.b4wback.model.Tag;

import java.util.List;

public interface TagService {
    List<Tag> getOrCreateTagsFromStringList(List<String> tagsString);

    List<Tag> getAllTags();
}
