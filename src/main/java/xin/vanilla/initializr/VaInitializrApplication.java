package xin.vanilla.initializr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import xin.vanilla.initializr.configuration.CustomInitializrConfiguration;
import xin.vanilla.initializr.customizer.ProjectDescriptionCustomizerConfiguration;

@SpringBootConfiguration
@EnableAutoConfiguration
@Import({CustomInitializrConfiguration.class, ProjectDescriptionCustomizerConfiguration.class})
@ComponentScan(basePackages = {"xin.vanilla"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "xin.vanilla.initializr.contributor.*"))
public class VaInitializrApplication {
    public static void main(String[] args) {
        SpringApplication.run(VaInitializrApplication.class, args);
    }

}
