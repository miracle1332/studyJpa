
package com.studyolle.account;

import com.studyolle.domain.Account;
import com.studyolle.mail.EmailMessage;
import com.studyolle.mail.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired //유저를 조회해봐야하기때문에 필드
    private AccountRepository accountRepository;

    @MockBean
    EmailService emailService;

    @DisplayName("인증메일 확인 - 입력값이 오류인 경우")
    @Test
    void checkEmailToken_with_wrong_input() throws Exception {
        mockMvc.perform(get("/check-email-token")
                        .param("token","ahsdjhkas")
                        .param("email","email@email.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(unauthenticated()); //입력값 오류인 경우라 인증이 안되어야함
    }

    @DisplayName("인증메일 확인 - 입력값이 정상인 경우")
    @Test
    void checkEmailToken() throws Exception {
        //입력값이 올바른지 볼려면 어카운트를 저장하는 데이터가 하나 있어야함.
        Account account = Account.builder()
                .email("test@email.com")
                .password("12345678")
                .nickname("rin")
                .build();
        Account newAccount = accountRepository.save(account);
        //저장된 어카운트에 이메일토큰을 만들면 이메일토큰이 생길거고 그러면 보낼때
        newAccount.generateEmailCheckToken();

        mockMvc.perform(get("/check-email-token")
                        .param("token",newAccount.getEmailCheckToken())
                        .param("email",newAccount.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(authenticated().withUsername("rin"));
    }
    @DisplayName("회원 가입 화면 보이는지 테스트")
    @Test
    void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"))
                .andExpect(unauthenticated());
        //모델에 signUpForm.html이 있는지 확인
    }
    @DisplayName("회원 가입 처리 - 입력값 오류")
    @Test
    void signUpSubmit_with_wrong_input() throws Exception{
        mockMvc.perform(post("/sign-up")
                        .param("nickname", "")
                        .param("email","emailafds")
                        .param("password","1234")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원 가입 처리 - 입력값 정상")
    @Test
    void signUpSubmit_with_correct_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("nickname","hyerin")
                        .param("email","rin@naver.com")
                        .param("password","rin12345")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated().withUsername("hyerin"));
        //authentication자체의 정보들도 같이 확인 가능

        Account account = accountRepository.findByEmail("rin@naver.com");
        assertNotNull(account); //어카운트가 널이 아닌지 확인해봄
        assertNotEquals(account.getPassword(), "rin12345"); //어카운트의 겟패스워드가 입력했던값123456이랑 다른지 확인
        //자동으로 salt적용 //이렇게 테스트코드 작성하고 완료되면 패스워드가 인코딩됐다고 가정할 수 있다.
        //토큰 널인지 확인
        assertNotNull(account.getEmailCheckToken());
        //어카운트레파지토리에 혜린의 이메일탓컴에 해당하는 계정이 존재하는지 확인
        assertTrue(accountRepository.existsByEmail("rin@naver.com"));
        //심플메일메세지타입의 아무런 타입가지고 send가 호줄이 됬는가만 확인하는것 =메일을 보냈다고 확인할 수 있는것
        then(emailService).should().sendEmail(any(EmailMessage.class));


    }

}