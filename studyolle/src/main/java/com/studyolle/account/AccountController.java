package com.studyolle.account;

import com.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

        Account account = accountService.processNewAccount(signUpForm);//컨트롤러가 이 메소드들을 가질 필요없고 서비스가 가지고 있기 떄문에
//processNewAccount메소드로 두 뭉퉁이를 숨기도록함
        accountService.login(account);
        return "redirect:/"; //에러가 없으면 첫화면으로..
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model) {
        //이메일에 해당하는 유저가 있는지 확인 //리파지토리가 제공하는 모든 메소드는 트랜잭션 처리가 된다.
        Account account = accountRepository.findByEmail(email); //리파지토리를 통해서 accountService.completeSignUp(account) 가기전에 영속성컨텍스트 만들어진 상태에서 트랜잭션통해서 데이터가져옴
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
        //트랙잭션 범위 안에서 일어난 변경사항을 관리한다 -> 데이터변경사항할일, 트랜잭션은 서비스에다 위임하는 것으로 함
        accountService.completeSignUp(account);

        model.addAttribute("numberOfUser", accountRepository.count()); //jpa레파지토리에 기본으로 count()메소드 있음
        model.addAttribute("nickname", account.getNickname());
        return view;
    }

    @GetMapping("/checked-email")
    public String checkEmail(@CurrentAccount Account account, Model model) {
        model.addAttribute("email", account.getEmail());
        return "account/check-email";
    }



    @GetMapping("resend-confirm-email")
    public String resendConfirmEmail(@CurrentAccount Account account, Model model) {
        if(!account.canSendConfirmEmail()) {
            model.addAttribute("error","인증 이ㅣ메일은 한시간에 한번만 전송 가능합니다.");
            model.addAttribute("email",account.getEmail());
            return "account/check-email";
        }

        accountService.sendSignUpConfirmEmail(account);
        return "redirect:/";
    }

    @GetMapping("/profile/{nickname}")
    public String viewProfile(@PathVariable String nickname, Model model, @CurrentAccount Account account) {
        Account byNickName = accountRepository.findByNickname(nickname);
        if(nickname == null) {
            throw new IllegalArgumentException(nickname + "에 해당하는 사용자가 없습니다.");
        }
        model.addAttribute(byNickName);
        model.addAttribute("isOwner",byNickName.equals(account));
        return "settings/profile";
    }


}
