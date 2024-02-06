package com.studyolle.config;

import com.studyolle.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity //내가 직접 시큐리티 설정을 직접 하겠다는 뜻
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    private final AccountService accountService;
    private final DataSource dataSource; //jpa라 데이터소스가 자동으로 등록되있다. 가져다 쓰기만 하면 된다.
   @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/", "/login", "sign-up", "check-email",
                "check-email-token", "/email-login", "/login-link").permitAll()
                .mvcMatchers(HttpMethod.GET, "/profile/*").permitAll() //프로필 요청은 get만 허용
                .anyRequest().authenticated();//나머지 요청은 로그인 해야만 쓸 수 있다

       http.formLogin() //http.formLogin() 만 써도 시큐리티가 기본로그인페이지를 제공하는데.loginPage("/")를 쓰면 커스텀한 로그인페이지를 쓸 수 있따
               .loginPage("/login").permitAll();

       http.logout()
               .logoutSuccessUrl("/"); //로그아웃 했을떄 어디로 갈지만 정함.fdewsfghj

       http.rememberMe()
               .userDetailsService(accountService)
               .tokenRepository(tokenRepository()); //username,토큰,시리즈 세가지정보를 조합해서 토큰정보를 db에 저장해야함.사용자쿠기와 리멤버미 쿠키비교해야함.

    }

    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
       web.ignoring()
               .mvcMatchers("/node_modules/**")
               .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
       //static한 리소스들은 시큐리티 필터를 적용하지 말라고 설정한것.static폴더 이미지 안보이는 이유
    }
}
