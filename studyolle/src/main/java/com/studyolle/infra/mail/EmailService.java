package com.studyolle.infra.mail;
//자바메일센더구현체인 콘솔메일센더에 createMimeMessage()가 널을 리턴하게 되어있고, 로컬환경과 개발환경을 맞추는게 어렵다 ->
//이메일서비스라는 인터페이스로 추상화.
public interface EmailService {

    void sendEmail(EmailMessage emailMessage); //EmailMessage를 받아서 전송할것임.
}
