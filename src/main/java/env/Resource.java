package env;

import java.io.File;

public enum Resource {
    INSTANCE;
    private ApplicationConfigProperties appProps;
    private String resourcePath;

    private Resource() {

    }

    public ApplicationConfigProperties getApplicationProperties() {
        if (appProps == null) {
            appProps = new ApplicationConfigProperties();
            appProps.loadProperties();
        }

        return appProps;
    }

    public String getResourcePath() {
        if (resourcePath == null) {
            String userDir = System.getProperty("user.dir");
            String projectResourcesPath = userDir.concat("/src/test/resources");
            File f = new File(projectResourcesPath);
            if (f.exists()) {
                resourcePath = projectResourcesPath;
            } else {
                resourcePath = userDir;
            }
        }

        return resourcePath;
    }
}