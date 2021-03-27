package ru.kvanttelecom.tv.streammonitoring.monitor;

import com.cosium.spring.data.jpa.entity.graph.repository.support.EntityGraphJpaRepositoryFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.kvanttelecom.tv.streammonitoring.core.repositories._base.RepositoryWithEntityManager;

@SpringBootApplication(scanBasePackages = "ru.kvanttelecom.tv.streammonitoring")
@EnableJpaRepositories(basePackages = "ru.kvanttelecom.tv.streammonitoring",
                       repositoryBaseClass = RepositoryWithEntityManager.class,
                       repositoryFactoryBeanClass = EntityGraphJpaRepositoryFactoryBean.class)
@EntityScan(basePackages = {"ru.kvanttelecom.tv.streammonitoring"})
public class MonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(MonitorApplication.class, args);
	}

}
