package xin.vanilla.initializr.controller;

import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.web.controller.ProjectGenerationController;
import io.spring.initializr.web.project.ProjectGenerationInvoker;
import io.spring.initializr.web.project.ProjectRequest;
import io.spring.initializr.web.project.WebProjectRequest;

import java.util.Map;

/**
 * @author Tsuki
 * @since 2023-11-22
 */
// @RestController
public class VaProjectGenerationController extends ProjectGenerationController<ProjectRequest> {

    public VaProjectGenerationController(InitializrMetadataProvider metadataProvider,
                                         ProjectGenerationInvoker<ProjectRequest> projectGenerationInvoker) {
        super(metadataProvider, projectGenerationInvoker);
    }

    @Override
    public ProjectRequest projectRequest(Map<String, String> headers) {
        WebProjectRequest request = new WebProjectRequest();
        request.getParameters().putAll(headers);
        request.initialize(getMetadata());
        return request;
    }
}
