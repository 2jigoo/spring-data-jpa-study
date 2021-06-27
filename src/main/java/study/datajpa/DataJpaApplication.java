package study.datajpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
// @EnableJpaRepositories(basePackages = "study.datajpa.repository") // boot가 자동 설정: 이 패키지 포함 하위 패키지들
@EnableJpaAuditing // (modifyOnCreate = false) : 생성시 modify 컬럼은 null로
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

	@Bean
	public AuditorAware<String> auditorProvider() {
		// 스프링 시큐리티를 쓸 때는 컨텍스트나 홀더에서 세션 정보를 가져와 ID를 꺼내 넣어준다.
		
		/* return new AuditorAware<String>() {
			@Override
			public Optional<String> getCurrentAuditor() {
				return Optional.of(UUID.randomUUID().toString());
			}
		}; */

		return () -> Optional.of(UUID.randomUUID().toString());
	}

}
