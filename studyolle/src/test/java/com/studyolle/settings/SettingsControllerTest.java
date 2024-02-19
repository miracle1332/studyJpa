package com.studyolle.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.WithAccount;
import com.studyolle.account.AccountRepository;
import com.studyolle.account.AccountService;
import com.studyolle.domain.Account;
import com.studyolle.domain.Tag;
import com.studyolle.domain.Zone;
import com.studyolle.settings.form.TagForm;
import com.studyolle.settings.form.ZoneForm;
import com.studyolle.tag.TagRepository;
import com.studyolle.zone.ZoneRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import static com.studyolle.settings.SettingsController.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired ObjectMapper objectMapper; //이미 빈으로 등록되어있음.
    @Autowired TagRepository tagRepository;
    @Autowired ZoneRepository zoneRepository;
    @Autowired AccountService accountService;

    private Zone testZone = Zone.builder().city("test").localNameOfCity("안양시").province("안양9주").build();
    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }


    @WithAccount("")
    @DisplayName("계정의 지역 정보 수정 폼")
    @Test
    void updateZonesForm() throws Exception {
        mockMvc.perform(get(ROOT + SETTINGS + ZONES))
                .andExpect(view().name(SETTINGS + ZONES))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("zones"));
    }

    @WithAccount("")
    @DisplayName("계정의 지역 정보 추가")
    @Test
    void addZone() throws Exception {
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());

        mockMvc.perform(post(ROOT + SETTINGS + ZONES + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        Account hyerin = accountRepository.findByNickname("keunes");
        Zone zone = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
        assertTrue(hyerin.getZones().contains(zone));
    }

    @WithAccount("hyerin")
    @DisplayName("계정의 지역 정보 추가")
    @Test
    void removeZone() throws Exception {
        Account hyerin = accountRepository.findByNickname("hyerin");
        Zone zone = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
        accountService.addZone(hyerin, zone);

        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());

        mockMvc.perform(post(ROOT + SETTINGS + ZONES + "/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        assertFalse(hyerin.getZones().contains(zone));
    }


    @WithAccount("hyerin") //요청url자체가 인증된 사용자만 접근할 수 있는 요청이라 이게 없으면 동작하지않음
    @DisplayName("프로필 수정하기 - 입력값 정상")
    @Test
    void updateProfile() throws Exception {
        String bio = "짧은 소개를 수정하는 경우.";
        mockMvc.perform(post(ROOT + SETTINGS + PROFILE)
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT + SETTINGS + PROFILE))
                .andExpect(flash().attributeExists("message"));

        Account hyerin  = accountRepository.findByNickname("hyerin");
        assertEquals(bio,hyerin.getBio());
    }

    @WithAccount("hyerin")
    @DisplayName("프로필 수정하기 - 입력값 에러")
    @Test
    void updateProfile_error() throws Exception {
        String bio = "길게 소개를 수정하는 경우.길게 소개를 수정하는 경우.길게 소개를 수정하는 경우.길게 소개를 수정하는 경우.길게 소개를 수정하는 경우.";
        mockMvc.perform(post(ROOT + SETTINGS + PROFILE)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name( SETTINGS + PROFILE))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account hyerin = accountRepository.findByNickname("hyerin");
        assertNull(bio, hyerin.getBio());
    }

    @WithAccount("hyerin")
    @DisplayName("패스워드 수정 - 입력값 정상")
    @Test
    void updaptePassword_success() throws Exception {
        mockMvc.perform(post(ROOT+SETTINGS+PASSWORD)
                .param("newPassword","12345678")
                .param("newPasswordConfirm","12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT+SETTINGS+PASSWORD))
                .andExpect(flash().attributeExists("message"));

        Account  hyerin = accountRepository.findByNickname("");
        assertTrue(passwordEncoder.matches("12345678", hyerin.getPassword()));
    }
    //컨틀롤러부터 하나의 기능단위로 보고 테스트코드 작성하는 스타일임 그래서 얽혀있는 서비스, 리파지토리 다 같이 테스트.
    @WithAccount("hyerin")
    @DisplayName("패스워드 수정 - 입력값 에러 - 패스워드 불일치")
    @Test
    void updatePassword_fail() throws Exception {
        mockMvc.perform(post(ROOT+SETTINGS+PASSWORD)
                        .param("newPassword","12345678")
                        .param("newPasswordConfirm","11111111")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS+PASSWORD))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));

        Account  hyerin = accountRepository.findByNickname("");
        assertTrue(passwordEncoder.matches("12345678", hyerin.getPassword()));
    }

    @WithAccount("hyerin")
    @DisplayName("계정의 태그 수정 폼")
    @Test
    void updateTagsForm() throws Exception {
        mockMvc.perform(get(ROOT+SETTINGS+TAGS))
                .andExpect(view().name(SETTINGS+TAGS))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"));
    }

    @WithAccount("hyerin")
    @DisplayName("계정에 태그 추가")
    @Test
    void addTag() throws Exception {
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTagTitle");

        mockMvc.perform(post(ROOT+SETTINGS+TAGS + "/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @WithAccount("")
    @DisplayName("계정에 태그 삭제")
    @Test
    void removeTag() throws Exception {
        Account  hyerin = accountRepository.findByNickname("");
        Tag newTag = tagRepository.save(Tag.builder().title("newTag").build());
        accountService.addTag(hyerin , newTag);

        assertTrue(hyerin.getTags().contains(newTag));

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(ROOT+SETTINGS+TAGS+ "/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        assertFalse(hyerin.getTags().contains(newTag));
        //은 detached상태 객체 -> 현재 클래스에 트랜잭셔널해줌. 그러면 persist되어 getTags가능
    }
}