package com.product.controller;

import com.product.dto.tag.TagReadInternalResponseDTO;
import com.product.entity.Tag;
import com.product.service.tag.TagService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping("/tags")
    public List<TagReadInternalResponseDTO> fetchTags(@RequestParam List<Integer> id) {
        return tagService.fetchTags(id);
    }
}
