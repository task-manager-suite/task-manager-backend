package org.montadhahri.taskmanager;

import org.montadhahri.taskmanager.config.CorsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@EnableConfigurationProperties(CorsProperties.class)
public class TaskManagerBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskManagerBackendApplication.class, args);
	}

}
