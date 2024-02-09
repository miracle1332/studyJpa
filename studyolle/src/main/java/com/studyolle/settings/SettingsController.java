package com.studyolle.settings;

import com.studyolle.account.AccountService;
import com.studyolle.account.CurrentAccount;
import com.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
@Controller
@RequiredArgsConstructor
public class SettingsController {

    private static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
    private static final String SETTINGS_PROFILE_URL = "/settings/profile";

    private final AccountService accountService; //데이터변경사항은 트랜잭션내에서 처리하고 서비스쪽에 위임했음.

    //어떤 유저의 프로필을 보여주는지 굳이 url이 필요 없는게 수정할 수 있는 것은 오로지 자기 자신의 프로필뿐임.
    @GetMapping(SETTINGS_PROFILE_URL)
    public String profileUpdateForm(@CurrentAccount Account account, Model model) {
        //뷰를 보여줄때 사용할 모델객체들이 필요하니까 모델 정보를
        model.addAttribute(account); //모델에 어카운트 정보를 넣어줌
        model.addAttribute(new Profile(account));
        return SETTINGS_PROFILE_VIEW_NAME; //사실 이코드는 줄일 수 있음 뷰네임 트랜슬레이터가 알아서 추측함.
    }

    @PostMapping("/settings/profile")
    public String updateProfile(@CurrentAccount Account account, @Valid @ModelAttribute Profile profile, Errors errors,
                                Model model, RedirectAttributes attributes) {
        //위의 Account정보는 persist상태의 정보가 아닌 세션에 넣어놓은 authentication안에 들어있는 principal 객체의 정보이다
        //errors는 바인딩 에러를 받아주는 모델에트리뷰트로 받는객체의 오른쪽에 두어야함 , @ModelAttribute는 생략가능
        if (errors.hasErrors()) { //폼에 채웟던 정보뿐만 아니라 에러에 대한 정보도 모델에 자동으로 들어간다.
            model.addAttribute(account);
            return SETTINGS_PROFILE_VIEW_NAME;
        }
        accountService.updateProfile(account, profile); //account를 profile로 변경해달라
        attributes.addFlashAttribute("message","프로필을 수정했습니다..");
        return "redirect:"  + SETTINGS_PROFILE_URL;//변경하고 난뒤 get post redirect패턴 - 사용자가 화면을 새로고침해도 폼 서브밋이 다시 일어나지 않도록!
    }
}
