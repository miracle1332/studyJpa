package com.studyolle.account;

import com.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;


    // Validator를 사용 시 @Valid 어노테이션으로 검증이 필요한 객체를 가져오기 전에 수행할 method를 지정해주는 어노테이션
    @InitBinder("signUpForm")//이닛바인더로 signUpForm데이터를 받을때 바인더를 설정할 수 있음
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
      //  model.addAttribute("signUpForm", new SignUpFrom());
        model.addAttribute(new SignUpForm());
        //이렇게만 써도 카멜표기법으로 해석하여 자동으로 signUpForm에 attribute해준다.
        return "account/sign-up"; //html파일
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid @ModelAttribute SignUpForm signUpForm, Errors errors) {
        //여러 객체를 (signUpForm에 있는 여러 필드들)을 받아올때는 ModelAttribute애노테이션 사용
        //Errors -> 에러를 받아주는 객체
        if (errors.hasErrors()) {
            return "account/sign-up"; //에러가 있으면 다시 폼을 보여줌
        }

        Account account = accountService.processsNewAccount(signUpForm);//컨트롤러가 이 메소드들을 가질 필요없고 서비스가 가지고 있기 떄문에
//processNewAccount메소드로 두 뭉퉁이를 숨기도록함
        accountService.login(account);
        return "redirect:/"; //에러가 없으면 첫화면으로..
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model) {
        //이메일에 해당하는 유저가 있는지 확인
        Account account = accountRepository.findByEmail(email);
        String view = "account/checked-email";
        if (account == null) { //이메일이 없을때
            model.addAttribute("error", "worng.email");
            return view;
        }
        //이메일이 있다면 어카운트에 이메일토큰이랑 내가 받아온 토큰이랑 비교를 해봐야겠지
        if (!account.isValidToken(token)) { //근데 토큰값이 일치하지않다면
            model.addAttribute("error", "worng.token"); //화면에서는 이유 상관없이 이메일이 정확하지 않다고만 보여줄것이다
            return view;
        }
        //위에 두if문 통과했으면 사실상 signup을 한것
        account.completeSignUp();
        // 기존 - 컨트롤러에 있던 코드
        // account.setEmailVerified(true);
        // account.setJoinedAt(LocalDateTime.now());
        accountService.login(account);
        model.addAttribute("numberOfUser", accountRepository.count()); //jpa레파지토리에 기본으로 count()메소드 있음
        model.addAttribute("nickname", account.getNickname());
        return view;
    }

}
