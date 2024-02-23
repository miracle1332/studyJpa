package com.studyolle.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

//EmailService구현체, 실제 이메일을 보내주는,,
//로컬환경에서 즉, 콘솔로 출력하는 거니까 로컬에서 개발할떄, 로컬이라는 프로파일로 실행할떄만 쓸것임
//아무런 빈 주입받을 필요없고 로깅만 하면 됌
@Slf4j
@Profile("local")
@Component
public class ConsoleEmailService implements  EmailService{
    @Override
    public void sendEmail(EmailMessage emailMessage) {
        log.info("sent email:{}", emailMessage.getMessage());//인포레벨로 로깅만
    }
}
