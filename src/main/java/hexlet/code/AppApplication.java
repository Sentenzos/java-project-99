package hexlet.code;

import io.sentry.spring.jakarta.EnableSentry;
import net.datafaker.Faker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableSentry(dsn = "https://83f49941acca138380ae24b8c94185a6@o4507578897530880.ingest.de.sentry.io/4507578905854032")
public class AppApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

    @Bean
    public Faker getFaker() {
        return new Faker();
    }
}
