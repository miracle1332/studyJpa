package com.studyolle.modules.event;

import com.studyolle.modules.account.CurrentAccount;
import com.studyolle.modules.account.Account;
import com.studyolle.modules.study.Study;
import com.studyolle.modules.event.form.EventForm;
import com.studyolle.modules.event.validator.EventValidator;
import com.studyolle.modules.study.StudyRepository;
import com.studyolle.modules.study.StudyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/study/{path}")
@RequiredArgsConstructor
public class EventController {
    private final StudyService studyService;
    private final EventService eventService;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;
    private final EventRepository eventRepository;
    private final StudyRepository studyRepository;
    private final EnrollmentRepository enrollmentRepository;

    @InitBinder("eventForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventValidator);
    }

    @GetMapping("/new-event") //새 알림 발송하는 뷰 보여주기
    public String newEventForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdateStatus(account, path);
        model.addAttribute(study);
        model.addAttribute(account);
        model.addAttribute(new EventForm());
        return "event/form";
    }

    @PostMapping("/new-event") //새알림 발송 로직
    public String newEventSubmit(@CurrentAccount Account account, @PathVariable String path, @Valid EventForm eventForm,
                                 Errors errors, Model model) {
        Study study = studyService.getStudyToUpdateStatus(account, path);
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);
            return "event/form";
        }

        Event event = eventService.createEvent(modelMapper.map(eventForm, Event.class), study, account);
        return "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{id}") //사용자가 발행한 알림 페이지 보여주기?
    public String getEvent(@CurrentAccount Account account, @PathVariable String path, @PathVariable("id") Event event, Model model) {
        model.addAttribute(account);                                                //받아온 id 값을 사용하여 해당 이벤트 객체를 가져오고자 하는 것
        model.addAttribute(event);
        model.addAttribute(studyRepository.findStudyWithManagersByPath(path));
        return "event/view";
    }

    @GetMapping("/events") //스터디의 알림 보여주기 ?
    public String viewStudyEvents(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdateStatus(account, path);
        model.addAttribute(account);
        model.addAttribute(study);

        List<Event> events = eventRepository.findByStudyOrderByStartDateTime(study);
        List<Event> newEvents = new ArrayList<>();
        List<Event> oldEvents = new ArrayList<>();

        events.forEach(event -> {
            if (event.getEndDateTime().isBefore(LocalDateTime.now())) {
                oldEvents.add(event);
            } else {
                newEvents.add(event);
            }
        });

        model.addAttribute("newEvents",newEvents);
        model.addAttribute("oldEvents",oldEvents);

        return "study/events";
    }
    @GetMapping("/events/{id}/edit") //알림 수정화면 보여주기
    public String updateEventForm(@CurrentAccount Account account,
                                  @PathVariable String path, @PathVariable("id") Event event, Model model) {
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(study);
        model.addAttribute(account);
        model.addAttribute(event);
        model.addAttribute(modelMapper.map(event, EventForm.class));
        return "event/update-form";
    }

    @PostMapping("/events/{id}/edit")
    public String updateEventSubmit(@CurrentAccount Account account, @PathVariable String path,
                                    @PathVariable("id") Event event, @Valid EventForm eventForm, Errors errors, Model model) {
        Study study = studyService.getStudyToUpdate(account, path);
        eventForm.setEventType(event.getEventType());
        eventValidator.validateUpdateForm(eventForm, event ,errors);

        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);
            model.addAttribute(event);
            return "event/update-form";
        }

        eventService.updateEvent(event, eventForm);
        return "redirect:/study/" + study.getEncodePath() +  "/events/" + event.getId();
    }

    @DeleteMapping("/events/{id}") //모임 삭제
    public String cancelEvent(@CurrentAccount Account account, @PathVariable String path, @PathVariable("id") Event event) {
        Study study = studyService.getStudyToUpdateStatus(account,path);
        eventService.deleteEvent(event);
        return "redirect:/study" + study.getEncodePath() + "/events/" + event.getId();
    }

    @PostMapping("/events/{id}/enroll") //모임 등록
    public String newEnrollment(@CurrentAccount Account account, @PathVariable String path, @PathVariable("id") Event event) {
        Study study = studyService.getStudyToEnroll(path); //왜 이 메소드에는 account객체가 필요 없는가?.....
        eventService.newEnrollment(event, account);
        return "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();
    }

    @PostMapping("/events/{id}/disenroll")
    public String cancelEnrollments(@CurrentAccount Account account, @PathVariable String path, @PathVariable("id") Event event) {
        Study study = studyService.getStudyToEnroll(path);
        eventService.cancelEnrollment(event, account);
        return "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();
    }

    @GetMapping("events/{eventId}/enrollments/{enrollmentId}/accept") //모입 접수 승인하기
    public String acceptEnrollment(@CurrentAccount Account account, @PathVariable String path, @PathVariable("eventId") Event event,
                                   @PathVariable("enrollmentId") Enrollment enrollment){
        Study study = studyService.getStudyToUpdate(account, path);
        eventService.acceptEnrollment(event, enrollment);
        return "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();
    }

    @GetMapping("events/{eventId}/enrollments/{enrollmentId}/reject") //모임 접수 거절하기
    public String rejectEnrollment(@CurrentAccount Account account, @PathVariable String path, @PathVariable("eventId") Event event,
                                   @PathVariable("enrollmentId") Enrollment enrollment) {
        Study study = studyService.getStudyToUpdate(account, path);
        eventService.rejectEnrollment(event, enrollment); //어떤 모임인지, 어떤 접수건인지
        return "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/checkin")
    public String checkInEnrollment(@CurrentAccount Account account, @PathVariable String path,
                                    @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
        Study study = studyService.getStudyToUpdate(account, path);
        eventService.checkInEnrollment(enrollment);
        return "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/cancel-checkin")
    public String cancelCheckInEnrollment(@CurrentAccount Account account, @PathVariable String path,
                                          @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
        Study study = studyService.getStudyToUpdate(account, path);
        eventService.cancelCheckInEnrollment(enrollment);
        return "redirect:/study/" + study.getEncodePath()+ "/events/" + event.getId();
    }

}



