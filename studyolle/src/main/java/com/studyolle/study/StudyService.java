package com.studyolle.study;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyService {
    private final studyRepository studyRepository;

}
