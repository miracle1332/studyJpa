package com.studyolle.modules.study.form;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

public class StudyDescriptionForm { //스터디 소개

    @NotBlank
    @Length(max = 100)
    private String shortDescription;

    @NotBlank
    private String fullDescription;

}
