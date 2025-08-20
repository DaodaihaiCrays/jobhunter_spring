package vn.hoidanit.jobhunter.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import vn.hoidanit.jobhunter.util.SecurityUtil;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Collection;


// Ghi đè lại cấu hình mặc định thì dùng @Configuration
@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    @Value("${config.jwt.base64-secret}")
    private String keyJwt;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(keyJwt).decode();
        return new SecretKeySpec(keyBytes, 0,
                keyBytes.length,
                SecurityUtil.JWT_ALGORITHM.getName());
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }


    // check token hợp lệ, Được gọi bởi nội bộ Spring trong filter
    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                getSecretKey()).macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();
        return token -> {
            try {
                return jwtDecoder.decode(token);
            } catch (Exception e) {
                System.out.println(">>> JWT error: " + e.getMessage());
                throw e;
            }
        };
    }

    // JwtAuthenticationConverter implement interface Authentication (Spring Context lưu Authentication)
    // Hàm này nhận vào một Jwt (đã được JwtDecoder verify chữ ký, hạn sử dụng, …),
    // rồi chuyển thành một Authentication (cụ thể là JwtAuthenticationToken)
    // và BearerTokenAuthenticationFilter sẽ đưa authentication vào context
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        grantedAuthoritiesConverter.setAuthorityPrefix("");

        // Khi bạn chỉ định claim name, converter sẽ tìm trong payload JWT.

        //Nếu claim đó là một collection/array các chuỗi or string → nó sẽ map từng chuỗi thành SimpleGrantedAuthority.

        //Nếu claim đó là kiểu khác (object, số, chuỗi đơn lẻ) →
        // nó bỏ qua, không tạo authority nào -> return [] của trường authorities ơ interface Authentication
        // khi lưu vào context
        grantedAuthoritiesConverter.setAuthoritiesClaimName("data");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

        Converter<Jwt, Collection<GrantedAuthority>> grantedAuthoritiesConv;

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
       CustomAuthenticationEntryPoint customAuthenticationEntryPoint) throws Exception {

        http
                .csrf(c -> c.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(
                        authz -> authz
                                .requestMatchers("/", "api/v1/auth/login",
                                        "api/v1/auth/refresh", "/storage/**").permitAll()
                                .anyRequest().authenticated()
                )
                // Chính dòng .oauth2ResourceServer().jwt() này sẽ kích hoạt:
                // -> BearerTokenAuthenticationFilter(cũng giúp đưa Authentication vào context nếu token ok) → filter này tự động lấy token từ header
                // -> JwtDecoder, cụ thể là bean jwtDecoder() là nơi validate token mỗi khi có request gọi API có Bearer token
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults())
                    .authenticationEntryPoint(customAuthenticationEntryPoint)
                )
                .exceptionHandling(
                        exceptions -> exceptions
                                .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint()) //401
                                .accessDeniedHandler(new BearerTokenAccessDeniedHandler())) // 403
                .formLogin(f -> f.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

}
