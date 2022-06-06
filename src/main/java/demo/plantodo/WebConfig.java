package demo.plantodo;

import demo.plantodo.interceptor.HomeRenderInterceptor;
import demo.plantodo.interceptor.LoginCheckInterceptor;
import demo.plantodo.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final HomeRenderInterceptor homeRenderInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/member/join", "/member/login");

        registry.addInterceptor(homeRenderInterceptor)
                .order(3)
                .addPathPatterns("/home", "/home/*", "/member/login", "/todo/register", "/plan/register/*");
    }
}
