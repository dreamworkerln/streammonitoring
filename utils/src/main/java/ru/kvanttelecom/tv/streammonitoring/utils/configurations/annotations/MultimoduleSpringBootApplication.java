package ru.kvanttelecom.tv.streammonitoring.utils.configurations.annotations;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.annotation.*;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited

@SpringBootApplication(scanBasePackages = "ru.kvanttelecom.tv.streammonitoring")
//@EnableJpaRepositories(basePackages = "ru.kvanttelecom.tv.streammonitoring",
//                       repositoryBaseClass = RepositoryWithEntityManager.class,
//                       repositoryFactoryBeanClass = EntityGraphJpaRepositoryFactoryBean.class)
//@EntityScan(basePackages = {"ru.kvanttelecom.tv.streammonitoring"})
//@EnableGlobalMethodSecurity(
//    prePostEnabled = true,
//    securedEnabled = true,
//    jsr250Enabled = true)
public @interface MultimoduleSpringBootApplication {
}
