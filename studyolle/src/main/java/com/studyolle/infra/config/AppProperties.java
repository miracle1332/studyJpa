package com.studyolle.infra.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("app")
public class AppProperties { //프로퍼티즈파일에 app.host값 바인딩받아옴.
    private String host;
}
