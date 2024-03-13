package com.studyolle.modules.study.validator;

import com.studyolle.modules.study.StudyRepository;
import com.studyolle.modules.study.form.StudyForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component @RequiredArgsConstructor
public class StudyFormValidator implements Validator {

    private final StudyRepository studyRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return StudyForm.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        StudyForm studyForm = (StudyForm)target;
        if(studyRepository.existsByPath(studyForm)) { //있으면 true, 업으면 false로 리턴
            errors.rejectValue("path","wrong.path","해당 스터디 경로값을 사용할 수 없습니다.");
        }

    }
}
