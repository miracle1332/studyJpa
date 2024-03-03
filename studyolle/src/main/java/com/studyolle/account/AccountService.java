package com.studyolle.account;

import com.studyolle.config.AppProperties;
import com.studyolle.domain.Account;
import com.studyolle.domain.Tag;
import com.studyolle.domain.Zone;
import com.studyolle.mail.EmailMessage;
import com.studyolle.mail.EmailService;
import com.studyolle.settings.form.Notifications;
import com.studyolle.settings.form.Profile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.util.List;
import java.util.Optional;
import java.util.Set;
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional //트랜잭셔널 애노테이션을 붙여놔야 퍼시스트 상태가 유지됌
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final AppProperties appProperties;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;


    public Account processNewAccount(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm); //사인업폼 가지고 새 어카운트를 저장을 하고
        //저장이 된거고, 이제 이메일보내기
        sendSignUpConfirmEmail(newAccount); //확인이메일을 보냈구나.
        return newAccount;
    }

    private Account saveNewAccount(@Valid SignUpForm signUpForm) {
        signUpForm.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
        Account account = modelMapper.map(signUpForm, Account.class); // 생성자로 만들어서 study~->true세팅이 적용됌.
        account.generateEmailCheckToken(); //토큰생성

      /*  //실제로 어카운트 객체으로 만들어서 검증해보아야함
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .studyUpdateByWeb(true) //스터디 알림은 웹 관련만 켜두기로 - 나머지들은 기본값 false이다.
                .studyCreatedByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .build();*/
        Account newAccount = accountRepository.save(account); //어카운트 레파지토리로 저장을 한다.
        return newAccount;
    }

    void sendSignUpConfirmEmail(Account newAccount) { //html메세지
        Context context = new Context();
        context.setVariable("link", "/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());
        context.setVariable("nickname", newAccount.getNickname());
        context.setVariable("linkName", "이메일인증하기");
        context.setVariable("message", "스터디 올래 서비스를 사용하려면 링크를 클릭하세요");
        context.setVariable("host", appProperties.getHost());

        String message = templateEngine.process("mail/simple-link", context);//타임리프에 있는 컨텍스트

        EmailMessage emailMessage = EmailMessage.builder()
                .to(newAccount.getEmail())
                .subject("스터디올래, 회원가입 인증")
                .message(message)
                .build();

        emailService.sendEmail(emailMessage); //메일을 보냄
    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account), //이 객체가 principal객체가 된다. 만약 로그인을 하면 이게 로그인을 한 user객체로 간주가 됌.
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);

    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(emailOrNickname);
        if (account == null) {
            account = accountRepository.findByNickname(emailOrNickname);
        }
        //그랬는데도 못찾으믄
        if (account == null) {
            throw new UsernameNotFoundException(emailOrNickname);
        }
        return new UserAccount(account); //프린시펄에 해당하는 객체를 넘기면 된다.
        //UserAccount에 스프링시큐리티가 제공한 유저를 확장한 유저어카운트이다.
    }

    public void completeSignUp(Account account) {
        //위에 두if문 통과했으면 사실상 signup을 한것
        account.completeSignUp();
        // 기존 - 컨트롤러에 있던 코드
        // account.setEmailVerified(true);
        // account.setJoinedAt(LocalDateTime.now());
        login(account);
    }

    public void updateProfile(Account account, Profile profile) {
        modelMapper.map(profile, account);//프로필에 있는 데이터를 어카운트에세팅
        accountRepository.save(account);
        /*account.setUrl(profile.getUrl()); //여기서 업데이트 정보를 변경하다.
        account.setOccupation(profile.getOccupation());
        account.setLocation(profile.getLocation());
        account.setBio(profile.getBio());
        account.setProfileImage(profile.getProfileImage());
        accountRepository.save(account);
        //save구현체 안에서 아이디 값이 있는지 없는지 보고 아이디값이 있으면 merge시킴
        //==기존데이터에 업데이트시키는것.*/
    }

    public void updatePassword(Account account, String newPassword) {
        //account.setPassword(newPassword); //->이렇게하면 평문 저장
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account); //셋팅컨트롤러에서 account가 detached객체이므로 명시적으로 머지해주어야함.
    }

    public void updateNotifications(Account account, Notifications notifications) {
       /* account.setStudyCreatedByEmail(notifications.isStudyCreatedByEmail());
        account.setStudyCreatedByWeb(notifications.isStudyCreatedByWeb());
        account.setStudyUpdateByWeb(notifications.isStudyUpdatedByWeb());
        account.setStudyUpdateByEmail(notifications.isStudyUpdatedByEmail());
        account.setStudyEnrollmentResultByWeb(notifications.isStudyEnrollmentResultByWeb());
        account.setStudyEnrollmentResultByEmail(notifications.isStudyEnrollmentResultByEmail());*/
        modelMapper.getConfiguration()
                .setDestinationNameTokenizer(NameTokenizers.UNDERSCORE)
                .setSourceNameTokenizer(NameTokenizers.UNDERSCORE); //언더스코어가 아닌 이상 전부 하나의 프로퍼티로 간주.
        //노티피케이션의 변수가 길어서 매핑이 잘 안되는 경우가 발생했음.
        accountRepository.save(account);

    }

    public void updateNickname(Account account, String nickname) {
        account.setNickname(nickname);
        accountRepository.save(account);
        login(account);
    }

    public void sendLoginLink(Account account) {
        Context context = new Context();
        context.setVariable("link", "/login-by-email?token=" + account.getEmailCheckToken() +
                "&email=" + account.getEmail());
        context.setVariable("nickname", account.getNickname());
        context.setVariable("linkName", "로그인하기");
        context.setVariable("message", "로그인하려면 아래 링크를 클릭하세요");
        context.setVariable("host", appProperties.getHost());

        String message = templateEngine.process("mail/simple-link", context);//타임리프에 있는 컨텍스트

        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getEmail())
                .subject("스터디올래,로그인 링크")
                .message(message)
                .build();
        emailService.sendEmail(emailMessage);
    }

    //관심주제 추가
    public void addTag(Account account, Tag tag) {
        //어카운트를 먼저 로딩해줘야함. 어카운트가 detached객체임 지금. 그리고 account엔티티의 연관관계로 있는 Tag는 detached상태일 경우 값들이 모두 null임, 지연로딩도 persist상태에서만 가능!
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTags().add(tag));
    }

    //관심주제 가져오기
    public Set<Tag> getTags(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getTags(); //없으면 예외던지고 있으면 태그정보 리턴하도록

    }

    public void removeTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTags().remove(tag));
    }

    public Set<Zone> getZones(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getZones();
    }

    public void addZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getZones().add(zone));
    }

    public void removeZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getZones().remove(zone));
    }

    public Account getAccount(String nickname) {
        Account account = accountRepository.findByNickname(nickname);
        if (nickname == null) {
            throw new IllegalArgumentException(nickname + "에 해당하는 사용자가 없습니다.");
        }
        return account;
    }
}
