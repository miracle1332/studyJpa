package com.studyolle.main;

import com.studyolle.account.AccountRepository;
import com.studyolle.account.AccountService;
import com.studyolle.account.SignUpForm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountService accountService;
    @Autowired AccountRepository accountRepository;

    @BeforeEach
    void beforeEach() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("hyerin");
        signUpForm.setEmail("hyerin@email.com");
        signUpForm.setPassword("12345678");
        accountService.processNewAccount(signUpForm);

    }
    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }
    @DisplayName("이메일로 로그인 성공")
    @Test
    void login_with_email() throws Exception{

        mockMvc.perform(post("/login")//포스트로 로그인이라는 곳으로 요청을 보내면-스프링시큐리티가 처리
                        .param("username","hyerin@email.com")
                        .param("password","12345678")
                        .with(csrf()))  //csrf토큰이 폼을 전달할때 같이 전송될 수 있도록
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("hyerin"));
        //UserAccount클래스에서 super()에 account.getNicknabe() -유저네임 부분을 닉네임으로 리턴했기 때문에 혜린으로 로그인잉 된 것처럼 될것임.
    }
    @DisplayName("닉네임으로 로그인 성공")
    @Test
    void login_with_nickname() throws Exception{

        mockMvc.perform(post("/login")//포스트로 로그인이라는 곳으로 요청을 보내면-스프링시큐리티가 처리
                        .param("username","hyerin")
                        .param("password","12345678")
                        .with(csrf()))  //csrf토큰이 폼을 전달할때 같이 전송될 수 있도록
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("hyerin"));
        //UserAccount클래스에서 super()에 account.getNicknabe() -유저네임 부분을 닉네임으로 리턴했기 때문에 혜린으로 로그인잉 된 것처럼 될것임.
    }

    @DisplayName("로그인 실패")
    @Test
    void login_fail() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "111111")
                        .param("password", "000000000")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @WithMockUser //가짜 유저 등록
    @DisplayName("로그아웃")
    @Test
    void logout() throws Exception {
        mockMvc.perform(post("/logout")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated());
    }

}