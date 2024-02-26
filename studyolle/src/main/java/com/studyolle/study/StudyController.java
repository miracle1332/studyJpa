package com.studyolle.study;

import com.studyolle.account.CurrentAccount;
import com.studyolle.domain.Account;
import com.studyolle.study.form.studyForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StudyController {

    @GetMapping("/new-study")
    public String newStudyForm(@CurrentAccount Account account, Model model) { //model에 화면에 전달해줄 객체 넘겨주기
        model.addAttribute(account);
        model.addAttribute(new studyForm());
        return "study/form";
    }
}
