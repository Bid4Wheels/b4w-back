package com.b4w.b4wback.service;

import com.b4w.b4wback.model.Tag;
import com.b4w.b4wback.repository.TagRepository;
import com.b4w.b4wback.service.interfaces.TagService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class TagServiceImp implements TagService {

    private final TagRepository tagRepository;

    public TagServiceImp(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public List<Tag> getOrCreateTagsFromStringList(List<String> tagsString) {
        if (tagsString == null || tagsString.isEmpty()) return new ArrayList<>();

        tagsString = tagsString.stream().map(String::toLowerCase).collect(Collectors.toList());
        List<Tag> existentTags = tagRepository.findAllByTagNameIn(tagsString);

        if (tagsString.size() == existentTags.size()) return existentTags;
        removeExistentTagsInListFromStringList(existentTags, tagsString);
        existentTags.addAll(tagRepository.saveAll(createTagsFromStringList(tagsString)));

        return existentTags;
    }

    /**removes all tags that already exist**/
    private static void removeExistentTagsInListFromStringList(List<Tag> tags, List<String> tagsString){
        for (int i = tags.size() - 1; i >= 0; i--)
            for (int j = tagsString.size() - 1; j >= 0; j--)
                if (tags.get(i).getTagName().equals(tagsString.get(j)))
                    tagsString.remove(j);
    }

    private static List<Tag> createTagsFromStringList(List<String> tagsString){
        List<Tag> tags = new ArrayList<>();
        tagsString.forEach(ts->tags.add(new Tag(ts)));
        return tags;
    }
}
