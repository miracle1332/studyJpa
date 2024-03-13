package com.studyolle.modules.event.validator;

import com.studyolle.modules.event.Event;
import com.studyolle.modules.event.form.EventForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;

@Component
public class EventValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return EventForm.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EventForm eventForm = (EventForm) target;

        if(isNotValidEndEnrollmentDateTime(eventForm)) {
            errors.rejectValue("endEnrollmentDateTime", "wrong.datetime", "모임 종료 일시를 정확히 입력하세요.");
        }
        if (isNotValidEndDateTime(eventForm)) {
            errors.rejectValue("endDateTime", "wrong.datetime", "모임 종료 일시를 정확히 입력하세요.");
        }

        if (isNotValidStartDateTime(eventForm)) {
            errors.rejectValue("startDateTime", "wrong.datetime", "모임 시작 일시를 정확히 입력하세요.");
        }
    }
    private boolean isNotValidEndEnrollmentDateTime(EventForm eventForm) { //유효하지 않은 모임 접수 종료일시
        return eventForm.getStartDateTime().isBefore(eventForm.getEndEnrollmentDateTime());
        //시작일이 모입 접수 종료일보다 이전이라면, isBefore() 메소드는 true를 반환하고, 그렇지 않으면 false를 반환
    }

    private boolean isNotValidEndDateTime(EventForm eventForm) { //유효하지않은 모임 종료일시
        LocalDateTime endDateTime = eventForm.getEndDateTime();
        boolean returnFalse = endDateTime.isBefore(eventForm.getStartDateTime()) || endDateTime.isBefore(eventForm.getEndEnrollmentDateTime());
        return returnFalse;
    }

    private boolean isNotValidStartDateTime(EventForm eventForm) { //유효하지 않은 모임 시작일시
        return eventForm.getStartDateTime().isBefore(eventForm.getEndEnrollmentDateTime());
    }

    public void validateUpdateForm(EventForm eventForm, Event event, Errors errors) {
        if(eventForm.getLimitOfEnrollments() < event.getNumberOfAcceptedEnrollments()) {
            errors.rejectValue("limitOfEnrollments", "wrong.value", "참가 신청보다 모집인원 수가 커야 합니다!!");
        }

    }

}
