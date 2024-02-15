package com.studyolle.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
//@Configuration : 해당 클래스가 1개 이상의 Bean을 제공한다면 @Configuration을 사용합니다.
//@Bean : 사용자가 직접 정의한 것이 아닌 외부 라이브러리 또는 설정을 위한 클래스를 Bean으로 등록하기 위해 사용합니다.
@Configuration
public class AppConfig {

    @Bean //회원가입할때 사용
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        //createDelegatingPasswordEncoder를 활용하면, PasswordEncoder를 반환받을 수 있습니다.
    }

    //모델매퍼 매번 만들어서 사용할 필요가 없기에 빈으로 등록하여 사용할것임
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
