package com.studyolle.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter @RequiredArgsConstructor
public class EnrollmentEvent {

    protected final Enrollment enrollment;

    protected final String message;

}
