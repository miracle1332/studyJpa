package com.studyolle.account;

import com.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    @Transactional //트랜잭셔널 애노테이션을 붙여놔야 퍼시스트 상태가 유지됌
    public Account processsNewAccount(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm); //사인업폼 가지고 새 어카운트를 저장을 하고
        //저장이 된거고, 이제 이메일보내기
        newAccount.generateEmailCheckToken(); //토큰생성
        sendSignUpConfirmEmail(newAccount); //확인이메일을 보냈구나.
        return newAccount;
    }

    private Account saveNewAccount(@Valid SignUpForm signUpForm) {
        //실제로 어카운트 객체으로 만들어서 검증해보아야함
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .studyUpdateByWeb(true) //스터디 알림은 웹 관련만 켜두기로 - 나머지들은 기본값 false이다.
                .studyCreatedByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .build();
        Account newAccount =  accountRepository.save(account); //어카운트 레파지토리로 저장을 한다.
        return newAccount;
    }

    private void sendSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail()); //이메일 받는사람
        mailMessage.setSubject("스터디올래, 회원가입 인증"); //메일 제목
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());
        javaMailSender.send(mailMessage); //메일을 보냄
    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
               new UserAccount(account), //이 객체가 principal객체가 된다. 만약 로그인을 하면 이게 로그인을 한 user객체로 간주가 됌.
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);

    }
}
