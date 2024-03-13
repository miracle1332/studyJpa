package com.studyolle.modules.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.modules.tag.Tag;
import com.studyolle.modules.zone.Zone;
import com.studyolle.modules.account.form.NicknameForm;
import com.studyolle.modules.account.form.Notifications;
import com.studyolle.modules.account.form.PasswordForm;
import com.studyolle.modules.account.form.Profile;
import com.studyolle.modules.tag.TagForm;
import com.studyolle.modules.zone.ZoneForm;
import com.studyolle.modules.account.validator.PasswordFormValidator;
import com.studyolle.modules.tag.TagRepository;
import com.studyolle.modules.zone.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingsController { //현재 사용자에 대한 정보를 넣어주고 수정하는 기능 컨트롤러S

    static final String ROOT = "/";
    static final String SETTINGS = "settings";
    static final String PROFILE = "/profile";
    static final String PASSWORD = "/password";
    static final String NOTIFICATIONS = "/notifications";
    static final String ACCOUNT = "/account";
    static final String TAGS = "/tags";
    static final String ZONES= "/zones";

    private final TagRepository tagRepository;
    private final ZoneRepository zoneRepository;
    private final AccountService accountService; //데이터변경사항은 트랜잭션내에서 처리하고 서비스쪽에 위임했음.
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    @InitBinder("passwordForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new PasswordFormValidator());
    }


    //어떤 유저의 프로필을 보여주는지 굳이 url이 필요 없는게 수정할 수 있는 것은 오로지 자기 자신의 프로필뿐임.
    @GetMapping(PROFILE)
    public String updateProfileForm(@CurrentAccount Account account, Model model) {
        //뷰를 보여줄때 사용할 모델객체들이 필요하니까 모델 정보를
        model.addAttribute(account); //모델에 어카운트 정보를 넣어줌
        model.addAttribute(modelMapper.map(account, Profile.class));
        return SETTINGS+PROFILE; //사실 이코드는 줄일 수 있음 뷰네임 트랜슬레이터가 알아서 추측함.
    }

    @PostMapping(PROFILE)
    public String updateProfile(@CurrentAccount Account account, @Valid @ModelAttribute Profile profile, Errors errors,
                                Model model, RedirectAttributes attributes) {
        //위의 Account정보는 persist상태의 정보가 아닌 세션에 넣어놓은 authentication안에 들어있는 principal 객체의 정보이다
        //errors는 바인딩 에러를 받아주는 모델에트리뷰트로 받는객체의 오른쪽에 두어야함 , @ModelAttribute는 생략가능
        if (errors.hasErrors()) { //폼에 채웟던 정보뿐만 아니라 에러에 대한 정보도 모델에 자동으로 들어간다.
            model.addAttribute(account);
            return SETTINGS+PROFILE;
        }
        accountService.updateProfile(account, profile); //account를 profile로 변경해달라
        attributes.addFlashAttribute("message","프로필을 수정했습니다..");
        return "redirect:"  + ROOT + SETTINGS + PROFILE;//변경하고 난뒤 get post redirect패턴 - 사용자가 화면을 새로고침해도 폼 서브밋이 다시 일어나지 않도록!
    }
    @GetMapping(PASSWORD) //모델객체 - 폼을 채울 객체를 보여줘야 하니 모델객체 있어야하고
    public String UpdatePasswordForm(@CurrentAccount Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(new PasswordForm());
        return SETTINGS+PASSWORD;
    }

    @PostMapping(PASSWORD) //현재 접속중인 사용자의 패스워드 수정
    public String updatePassword(@CurrentAccount Account account, @Valid @ModelAttribute PasswordForm passwordForm, Errors errors,
                                 Model model, RedirectAttributes attributes) {
        if(errors.hasErrors()) { //@CurrentAccount Account account-> detached상태의 객체 //account객체는 영속성 리파지토리를 통해 가져온 객체가 아니라 스프링시큐리티 컨텍스트에 저장해놓은 객체 인증될떄 세션에 남아있던 객체
            model.addAttribute(account);
            return SETTINGS+PASSWORD;
        }
        accountService.updatePassword(account, passwordForm.getNewPassword());
        attributes.addFlashAttribute("message","패스워드를 변경했습니당");
        return "redirect:" + ROOT+SETTINGS+PASSWORD;
    }

    //알림설정 - 테스트코드 작성안함 - 난 해보기
    @GetMapping(NOTIFICATIONS)
    public String updateNotificationsForm(@CurrentAccount Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, Notifications.class));
        return SETTINGS+NOTIFICATIONS;
    }

    @PostMapping(NOTIFICATIONS)
    public String updateNotifications(@CurrentAccount Account account, @Valid @ModelAttribute Notifications notifications, Errors errors,
                                      Model model, RedirectAttributes attributes) {
        if(errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS+NOTIFICATIONS;
        }
        accountService.updateNotifications(account, notifications);
        attributes.addFlashAttribute("message","알림설정을 변경했습니다~");
        return "redirect:" +ROOT+ SETTINGS+NOTIFICATIONS;

    }

    @GetMapping(TAGS)
    public String updateTags(@CurrentAccount Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);

        Set<Tag> tags = accountService.getTags(account);
        model.addAttribute("tags",tags.stream().map(Tag::getTitle).collect(Collectors.toList())); //tags엔티티자체를 보내준는게 아니라 tags의 title만 가져와서 콜렉트로 수집해서 리스트로 만들어 보내줌!
        //즉, 태그목록을 뷰에 전달함.

        //컨트롤러에서 뷰를 보여줄때 태그목록을 whiteList로 보여주어야함.
        List<String> allTags = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
        //자바객체의 list타입--> json문자열 변환하기 --> objectMapper사용 //이미 빈으로 등록되어있음.
        model.addAttribute("whiteList",objectMapper.writeValueAsString(allTags));
        return SETTINGS+TAGS;
    }

    @PostMapping(TAGS + "/add") //ajax요청
    @ResponseBody
    public ResponseEntity addTag(@CurrentAccount Account account, @RequestBody TagForm tagForm) {
        String title = tagForm.getTagTitle();
       //옵셔널 쓰는 경우 - 값없으면
        // Tag tag = tagRepository.findByTitle(title).orElseGet(() -> tagRepository.save(Tag.builder().title(tagForm.getTagTitle()).build()));

        //옵셔널 안쓰는 경우
        Tag tag = tagRepository.findByTitle(title);
        if(tag == null) {
            tag = tagRepository.save(Tag.builder().title(tagForm.getTagTitle()).build()); //기존 관심주제가 없으면 새로 저장
        }
        accountService.addTag(account, tag);
        return ResponseEntity.ok().build();
    }

    @PostMapping(TAGS + "/remove") //ajax요청
    @ResponseBody
    public ResponseEntity removeTag(@CurrentAccount Account account, @RequestBody TagForm tagForm) {
        String title = tagForm.getTagTitle();
        Tag tag = tagRepository.findByTitle(title);
        if(tag == null) {
            //기존 관심주제 없으면 badrequest로 응답
            return ResponseEntity.badRequest().build();
        }
        accountService.removeTag(account, tag); //정상적인 경우 태그 삭제
        return ResponseEntity.ok().build();
    }
    //지역정보
    @GetMapping(ZONES)
    public String updateZonesForm(@CurrentAccount Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);

        Set<Zone> zones = accountService.getZones(account);
        model.addAttribute("zones", zones.stream().map(Zone::toString).collect(Collectors.toList()));

        List<String> allZones = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allZones));
        return SETTINGS+ZONES;

    }

    @PostMapping(ZONES + "/add")
    @ResponseBody
    public ResponseEntity addZones(@CurrentAccount Account account, @RequestBody ZoneForm zoneForm) {
      Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(),zoneForm.getProvinceName());
      if(zone == null) {
          return ResponseEntity.badRequest().build();
      }
      accountService.addZone(account,zone);
      return ResponseEntity.ok().build();
    }

    @PostMapping(ZONES + "/remove")
    @ResponseBody
    public ResponseEntity removeZone(@CurrentAccount Account account, @RequestBody ZoneForm zoneForm) {
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }

        accountService.removeZone(account, zone);
        return ResponseEntity.ok().build();
    }

    //닉네임 변경
    @GetMapping(ACCOUNT)
    public String updateAccountForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, NicknameForm.class));
        return SETTINGS+ACCOUNT;
    }

    @PostMapping(ACCOUNT)
    public String updateAccount(@CurrentAccount Account account, @Valid @ModelAttribute NicknameForm nicknameForm, Errors errors,
                                 Model model, RedirectAttributes attributes) {
        if(errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS+ACCOUNT;
        }
        accountService.updateNickname(account, nicknameForm.getNickname());
        attributes.addFlashAttribute("message","닉네임을 변경했습니다.");
        return "redirect:" +ROOT+SETTINGS+ACCOUNT;
    }


}

