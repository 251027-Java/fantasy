package dev.revature.fantasy;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // Fields
    private final JwtInterceptor jwtInterceptor;

    // Constructor
    public WebConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry reg) {
        // adding interceptors to the list of active/running interceptors
        // that are scanning requests as they come in
        reg.addInterceptor(jwtInterceptor).addPathPatterns("/api/**").excludePathPatterns("/api/auth/google");
    }
}
