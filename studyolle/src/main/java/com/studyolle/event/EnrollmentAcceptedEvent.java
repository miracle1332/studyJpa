package com.studyolle.event;



public class EnrollmentAcceptedEvent extends EnrollmentEvent{
    public EnrollmentAcceptedEvent(Enrollment enrollment) {
        super(enrollment, "모임참가 신청을 확인했습니다. 모임에 참석해주세요.");
    }
}
