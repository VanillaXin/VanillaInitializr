package xin.vanilla.initializr.configuration;

import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;

import java.nio.file.Path;
import java.util.List;

/**
 * 配置{@link ReplaceProjectContributorBeanPostProcessor}
 *
 * @author Tsuki
 * @since 2023-11-22
 */
@ProjectGenerationConfiguration
public class ReplaceProjectContributorConfiguration {

    @Bean
    public ReplaceProjectContributorBeanPostProcessor replaceProjectContributorBeanPostProcessor() {
        return new ReplaceProjectContributorBeanPostProcessor();
    }

    /**
     * 将一些ProjectContributor实例替换为NoOpContributor实例：
     * <ul>
     *     <li>替换WebFoldersContributor，避免生成src/main/resources/templates和src/main/resources/static目录</li>
     *     <li>替换HelpDocumentProjectContributor，避免生成HELP.md文件</li>
     * </ul>
     */
    static class ReplaceProjectContributorBeanPostProcessor implements BeanPostProcessor {

        private final List<String> replaceList;
        private final ProjectContributor emptyContributor;

        public ReplaceProjectContributorBeanPostProcessor() {
            replaceList = List.of("webFoldersContributor", "helpDocumentProjectContributor");
            emptyContributor = new EmptyProjectContributor();
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof ProjectContributor && replaceList.contains(beanName)) {
                return emptyContributor;
            } else {
                return bean;
            }
        }
    }

    /**
     * ProjectContributor的空实现，什么也不做
     *
     * @author Tsuki
     * @since 2023-11-22
     */
    static class EmptyProjectContributor implements ProjectContributor {

        @Override
        public void contribute(Path projectRoot) {
        }
    }
}
