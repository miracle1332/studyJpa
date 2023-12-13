package com.studyolle.account;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : account")
//만약 로그인하지 않고 접근ㄴ한 사용자라고 하면 SecurityConfig에 모든걸 다 허용하는 페이지들을 접근할대는 익명사용자로 접근이 된다.
//그럴때는 authetication principal이 'anonymousUser'이라는 문자열이다.
//문자열인 경우에는 사실 인증된 정보가 없으므로 우리는 null로 간주하면 된다.
public @interface CurrentAccount { //사용자 정의 어노테이션
}
