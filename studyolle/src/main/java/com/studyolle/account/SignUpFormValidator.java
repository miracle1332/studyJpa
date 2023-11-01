package com.studyolle.account;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/*Validator 인터페이스는 두가지 메서드를 정의하는데, supports메서드와 validate메서드이다.
        supports메서드는 검증하고자 하는 객체가 validator에서 지원하는지 아닌지 확인하는 메서드이고,
        validate메서드는 실제 로직을 구현하는 메서드이다.
 */
@Component //스프링은 빈만 주입받을 수 있기 때문에 명시적으로 넣어주어야함.
@RequiredArgsConstructor
//커스텀한 벨리데이터를 만듦
public class SignUpFormValidator implements Validator{

    private final AccountRepository accountRepository;
   /* public SignUpFormValidator(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    } ---> * @RequiredArgsConstructor가 대신 해줌, Autowried없이도 빈 주입*/
    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(SignUpForm.class); //signup타입의 인스턴스를 검사한다.
    }

    @Override //실제로직 구현 메서드
    public void validate(Object object, Errors errors) {
        //빈을 주입받았다는 가정하에
        SignUpForm signUpForm = (SignUpForm)object;
        if(accountRepository.existsByEmail(signUpForm.getEmail())) { //만약 사인업폼에 이메일에 해당하는것이 있다면
           //필드(객체의 프로퍼티)에 대한 에러정보 추가(에러코드 및 메시지, 메시지 인자 전달)
            errors.rejectValue("email","invalid.emaill", new Object[]{signUpForm.getEmail()}, "이미 사용중인 이메일입니다.");
        }

        if(accountRepository.existsByNickname(signUpForm.getNickname())) { //만약 사인업폼에 이메일에 해당하는것이 있다면
            //필드(객체의 프로퍼티)에 대한 에러정보 추가(에러코드 및 메시지, 메시지 인자 전달)
            errors.rejectValue("nickname","invalid.nickname", new Object[]{signUpForm.getNickname()}, "이미 사용중인 닉네임입니다.");
        }
    }
}
