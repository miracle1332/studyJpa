package com.studyolle.tag;

import com.studyolle.domain.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

@Service @RequiredArgsConstructor
@Transactional
public class TagService {

    private final TagRepository tagRepository;

    public Tag findOrCreateNew(String tagTitle) { //관심주제이름 만들거나 새로 생성하기
        Tag tag= tagRepository.findByTitle(tagTitle); //db에서 관심주제 가져와서
        if(tag == null) { //만약 관심주제가 없다면
            tag = tagRepository.save(Tag.builder().title(tagTitle).build()); //새 태그 저장하기
        }
        return tag;
    }
}
