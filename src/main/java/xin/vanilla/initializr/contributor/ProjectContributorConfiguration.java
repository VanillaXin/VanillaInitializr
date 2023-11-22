package xin.vanilla.initializr.contributor;

import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import org.springframework.context.annotation.Bean;

/**
 * 配置自定义的{@link ProjectContributor}
 *
 * @author Tsuki
 * @since 2023-11-22
 */
@ProjectGenerationConfiguration
public class ProjectContributorConfiguration {

    private final ProjectDescription description;

    public ProjectContributorConfiguration(ProjectDescription description) {
        this.description = description;
    }

    @Bean
    public SourceCodeTemplateProjectContributor fileSourceCodeProjectContributor() {
        return new SourceCodeTemplateProjectContributor(this.description);
    }

    @Bean
    public ConfigFileTemplateProjectContributor templateConfigFileProjectContributor() {
        return new ConfigFileTemplateProjectContributor(this.description);
    }
}
