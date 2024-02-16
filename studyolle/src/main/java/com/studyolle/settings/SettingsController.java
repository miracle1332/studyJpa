package com.studyolle.settings;

import com.studyolle.account.AccountService;
import com.studyolle.account.CurrentAccount;
import com.studyolle.domain.Account;
import com.studyolle.settings.form.NicknameForm;
import com.studyolle.settings.form.Notifications;
import com.studyolle.settings.form.PasswordForm;
import com.studyolle.settings.form.Profile;
import com.studyolle.settings.validator.PasswordFormValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
@Controller
@RequiredArgsConstructor
public class SettingsController { //현재 사용자에 대한 정보를 넣어주고 수정하는 기능 컨트롤러


    static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
    static final String SETTINGS_PROFILE_URL = "/settings/profile";
    static final String SETTINGS_PASSWORD_VIEW_NAME = "settings/password";
    static final String SETTINGS_PASSWORD_URL = "/settings/password";

    static final String SETTINGS_NOTIFICATIONS_VIEW_NAME = "settings/notifications";
    static final String SETTINGS_NOTIFICATIONS_URL = "/settings/notifications";
    static final String SETTINGS_ACCOUNT_URL = "settings/account";
    static final String SETTINGS_ACCOUNT_VIEW_NAME = "/settings/account";
    static final String SETTINGS_TAGS_URL = "settings/tags";
    static final String SETTINGS_TAGS_VIEW_NAME = "/settings/tags";

    private final AccountService accountService; //데이터변경사항은 트랜잭션내에서 처리하고 서비스쪽에 위임했음.
    private final ModelMapper modelMapper;
    @InitBinder("passwordForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new PasswordFormValidator());
    }


    //어떤 유저의 프로필을 보여주는지 굳이 url이 필요 없는게 수정할 수 있는 것은 오로지 자기 자신의 프로필뿐임.
    @GetMapping(SETTINGS_PROFILE_URL)
    public String updateProfileForm(@CurrentAccount Account account, Model model) {
        //뷰를 보여줄때 사용할 모델객체들이 필요하니까 모델 정보를
        model.addAttribute(account); //모델에 어카운트 정보를 넣어줌
        model.addAttribute(modelMapper.map(account, Profile.class));
        return SETTINGS_PROFILE_VIEW_NAME; //사실 이코드는 줄일 수 있음 뷰네임 트랜슬레이터가 알아서 추측함.
    }

    @PostMapping(SETTINGS_PROFILE_URL)
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
    @GetMapping(SETTINGS_PASSWORD_URL) //모델객체 - 폼을 채울 객체를 보여줘야 하니 모델객체 있어야하고
    public String UpdatePasswordForm(@CurrentAccount Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(new PasswordForm());
        return SETTINGS_PASSWORD_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PASSWORD_URL) //현재 접속중인 사용자의 패스워드 수정
    public String updatePassword(@CurrentAccount Account account, @Valid @ModelAttribute PasswordForm passwordForm, Errors errors,
                                 Model model, RedirectAttributes attributes) {
        if(errors.hasErrors()) { //@CurrentAccount Account account-> detached상태의 객체
            model.addAttribute(account);
            return SETTINGS_PASSWORD_VIEW_NAME;
        }
        accountService.updatePassword(account, passwordForm.getNewPassword());
        attributes.addFlashAttribute("message","패스워드를 변경했습니당");
        return "redirect:" + SETTINGS_PASSWORD_URL;
    }

    //알림설정 - 테스트코드 작성안함 - 난 해보기
    @GetMapping(SETTINGS_NOTIFICATIONS_URL)
    public String updateNotificationsForm(@CurrentAccount Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, Notifications.class));
        return SETTINGS_NOTIFICATIONS_VIEW_NAME;
    }

    @PostMapping(SETTINGS_NOTIFICATIONS_URL)
    public String updateNotifications(@CurrentAccount Account account, @Valid @ModelAttribute Notifications notifications, Errors errors,
                                      Model model, RedirectAttributes attributes) {
        if(errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_NOTIFICATIONS_VIEW_NAME;
        }
        accountService.updateNotifications(account, notifications);
        attributes.addFlashAttribute("message","알림설정을 변경했습니다~");
        return "redirect:" + SETTINGS_NOTIFICATIONS_URL;

    }

    @GetMapping(SETTINGS_TAGS_URL)
    public String updateTags(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        return SETTINGS_TAGS_VIEW_NAME;
    }

    //닉네임 변경
    @GetMapping(SETTINGS_ACCOUNT_URL)
    public String updateAccountForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, NicknameForm.class));
        return SETTINGS_ACCOUNT_VIEW_NAME;
    }

    @PostMapping(SETTINGS_ACCOUNT_URL)
    public String updateAccount(@CurrentAccount Account account, @Valid @ModelAttribute NicknameForm nicknameForm, Errors errors,
                                 Model model, RedirectAttributes attributes) {
        if(errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_ACCOUNT_VIEW_NAME;
        }
        accountService.updateNickname(account, nicknameForm.getNickname());
        attributes.addFlashAttribute("message","닉네임을 변경했습니다.");
        return "redirect:" + SETTINGS_ACCOUNT_URL;
    }


}

