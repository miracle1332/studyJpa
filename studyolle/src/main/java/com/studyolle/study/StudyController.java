package com.studyolle.study;

import com.studyolle.account.CurrentAccount;
import com.studyolle.domain.Account;
import com.studyolle.study.form.studyForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class StudyController {
    private final StudyService studyService;

    @GetMapping("/new-study")
    public String newStudyForm(@CurrentAccount Account account, Model model) { //model에 화면에 전달해줄 객체 넘겨주기
        model.addAttribute(account);
        model.addAttribute(new studyForm());
        return "study/form";
    }

    @PostMapping("/new-study")
    public String newStudySubmit(@CurrentAccount Account account, @Valid studyForm studyForm, Errors errors) {
        if(errors.hasErrors()) {
            return "study/form";
        }

        studyService
    }
}
