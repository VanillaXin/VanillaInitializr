package xin.vanilla.initializr.contributor;

import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.StringJoiner;

import static xin.vanilla.initializr.contributor.ConfigFileTemplateProjectContributor.SpringConfigFilePlaceHolder.*;

/**
 * 1. 添加以下配置文件至项目的resources目录下：
 * <ul>
 *     <li>application.yaml (copy from application.yam.tpl)</li>
 *     <li>application-dev.yaml (copy from application-dev.yam.tpl)</li>
 *     <li>application-test.yaml (copy from application-test.yam.tpl)</li>
 *     <li>application-prod.yaml (copy from application-prod.yam.tpl)</li>
 *     <li>mybatis-config.xml (copy from mybatis-config.xml.tpl)</li>
 * </ul>
 * <p>
 *     2.替换配置文件中的占位符：${controllerPackage}, ${artifactId}, ${version}, ${quartzSchedulerName}
 * </p>
 *
 * <p>
 *     3.删除已有的application.properties文件；
 * </p>
 *
 * @author Tsuki
 * @since 2023-11-22
 */
class ConfigFileTemplateProjectContributor implements ProjectContributor {

    private static final Logger log = LoggerFactory.getLogger(ConfigFileTemplateProjectContributor.class);

    // 配置文件所在目录
    private static final String rootResource = "classpath:template/configfile";
    // maven项目资源文件目录，src/main/resources
    private static final String resourcesDirPrefix = "src" + File.separatorChar + "main" + File.separatorChar + "resources";
    // spring initializr生成的配置文件名称
    private static final String defaultConfigFile = "application.properties";

    private final ProjectDescription description;
    private final PathMatchingResourcePatternResolver resolver;

    public ConfigFileTemplateProjectContributor(ProjectDescription description) {
        this.description = description;
        this.resolver = new PathMatchingResourcePatternResolver();
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void contribute(Path projectRoot) throws IOException {
        Resource root = this.resolver.getResource(rootResource);
        Resource[] resources = this.resolver.getResources(rootResource + "/**");
        for (Resource resource : resources) {
            if (resource.isReadable()) {
                String filename = extractFileName(root.getURI(), resource.getURI());
                Path output = projectRoot.resolve(addParentPath(filename));
                Files.createDirectories(output.getParent());
                Files.createFile(output);
                InputStream inputStream = processingConfigFile(resource.getInputStream(), filename);
                FileCopyUtils.copy(inputStream, Files.newOutputStream(output));
            }
        }

        // 删除application.properties文件
        File f = getDefaultConfigFile(projectRoot.toFile());
        if (f != null && f.delete()) {
            log.debug("delete file '{}' successfully", defaultConfigFile);
        }
    }

    private String extractFileName(URI root, URI resource) {
        String candidate = resource.toString().substring(root.toString().length());
        String filename = StringUtils.trimLeadingCharacter(candidate, '/');
        if (filename.endsWith(".tpl")) {
            filename = filename.substring(0, filename.length() - ".tpl".length());
        }
        return filename;
    }

    /**
     * 替换配置文件中的占位符
     */
    private InputStream processingConfigFile(InputStream inputStream, String filename) throws IOException {
        if (filename.endsWith(".yml")) {
            return processingSpringConfigFile(inputStream);
        } else if (filename.endsWith(".xml")) {
            return processingSpringConfigFile(inputStream);
        } else {
            return inputStream;
        }
    }

    /**
     * 替换spring配置文件中的占位符
     */
    private InputStream processingSpringConfigFile(InputStream inputStream) throws IOException {
        StringJoiner fileContent = new StringJoiner("\n");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            if (line.contains(applicationName)) {
                line = line.replace(applicationName, description.getApplicationName());
            } else if (line.contains(controllerPackage)) {
                line = line.replace(controllerPackage, description.getPackageName() + ".controller");
            } else if (line.contains(version)) {
                line = line.replace(version, description.getVersion());
            } else if (line.contains(artifactId)) {
                line = line.replace(artifactId, description.getArtifactId());
            } else if (line.contains(quartzSchedulerName)) {
                line = line.replace(quartzSchedulerName, toCamelCase(description.getArtifactId()) + "Scheduler");
            } else if (line.contains(groupId)) {
                line = line.replace(groupId, description.getGroupId());
            } else if (line.contains(groupIdPath)) {
                line = line.replace(groupIdPath, description.getGroupId().replace('.', '/'));
            }
            fileContent.add(line);
        }
        return new ByteArrayInputStream(fileContent.toString().getBytes(StandardCharsets.UTF_8));
    }

    private String toCamelCase(String artifactId) {
        if (!artifactId.contains("-")) {
            return artifactId;
        }
        StringBuilder sb = new StringBuilder();
        char[] chars = artifactId.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c != '-') {
                sb.append(c);
            } else {
                c = chars[++i];
                sb.append(Character.toUpperCase(c));
            }
        }
        return sb.toString();
    }

    /**
     * 添加父路径：src/main/resources
     */
    private String addParentPath(String fileName) {
        return resourcesDirPrefix + File.separatorChar + fileName;
    }

    public File getDefaultConfigFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    File target = getDefaultConfigFile(f);
                    if (target != null) {
                        return target;
                    }
                }
            }
        } else if (file.isFile() && Objects.equals(defaultConfigFile, file.getName())) {
            return file;
        }
        return null;
    }

    /**
     * mybatis配置文件占位符定义
     */
    static final class MybatisConfigFilePlaceHolder {

        /**
         * maven project root package path
         */
        static final String packageName = "${package}";
    }

    /**
     * spring配置文件中的占位符定义
     */
    static final class SpringConfigFilePlaceHolder {

        /**
         * 应用名称
         */
        static final String applicationName = "${applicationName}";
        /**
         * Controller包类路径
         */
        static final String controllerPackage = "${controllerPackage}";
        /**
         * Maven项目工件
         */
        static final String artifactId = "${artifactId}";
        /**
         * Maven项目版本
         */
        static final String version = "${version}";
        /**
         * Quartz实例名称
         */
        static final String quartzSchedulerName = "${quartzSchedulerName}";
        /**
         * 组ID
         */
        static final String groupId = "${groupId}";
        /**
         * 组ID路径
         */
        static final String groupIdPath = "${groupIdPath}";
    }
}
