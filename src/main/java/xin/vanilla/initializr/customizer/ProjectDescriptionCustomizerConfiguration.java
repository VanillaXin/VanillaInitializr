package xin.vanilla.initializr.customizer;

import io.spring.initializr.generator.project.ProjectDescriptionCustomizer;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置自定义的 {@link ProjectDescriptionCustomizer}
 *
 * @author Tsuki
 * @since 2023-11-22
 */
@Configuration
public class ProjectDescriptionCustomizerConfiguration {

    @Bean
    public DefaultDependenciesProjectDescriptionCustomizer appendDependenciesProjectDescriptionCustomizer(InitializrMetadataProvider metadataProvider) {
        return new DefaultDependenciesProjectDescriptionCustomizer(metadataProvider);
    }
}
