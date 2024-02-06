package com.studyolle.settings;

import com.studyolle.account.CurrentAccount;
import com.studyolle.domain.Account;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

public class SettingsController {

    //어떤 유저의 프로필을 보여주는지 굳이 url이 필요 없는게 수정할 수 있는 것은 오로지 자기 자신의 프로필뿐임.
    @GetMapping("/settings/profile")
    public String profileUpdateForm(@CurrentAccount Account account, Model model) {
                                                                    //뷰를 보여줄때 사용할 모델객체들이 필요하니까 모델 정보를
        model.addAttribute(account); //모델에 어카운트 정보를 넣어줌
        model.addAttribute(new Profile(account));
        return "settings/profile"; //사실 이코드는 줄일 수 있음 뷰네임 트랜슬레이터가 알아서 추측함.
    }
}
