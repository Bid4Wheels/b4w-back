package com.b4w.b4wback.service;

import com.b4w.b4wback.model.Tag;
import com.b4w.b4wback.repository.TagRepository;
import com.b4w.b4wback.service.interfaces.TagService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class TagServiceTest {
    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagService tagService;

    @Test
    void Test001_TagServiceWhenGetTagsShouldReturnAllTags(){
        List<String> tags = List.of("Tag1", "Tag2", "Tag3", "Tag4", "Tag5");
        tagRepository.saveAll(tags.stream().map(Tag::new).toList());
        assertEquals(tagService.getAllTags().size(),tags.size());

    }
}
