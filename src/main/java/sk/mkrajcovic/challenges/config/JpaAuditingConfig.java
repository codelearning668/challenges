package sk.mkrajcovic.challenges.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Configuration
@EnableJpaAuditing
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class JpaAuditingConfig {

}
