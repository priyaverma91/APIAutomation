package env;


import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utilities.FileOperation;
import utilities.FilePathBuilder;
import utilities.ApplicationConfigConstants;

public class ApplicationConfigProperties {
    private static final ApplicationConfigProperties INSTANCE = new ApplicationConfigProperties();

    public static ApplicationConfigProperties getInstance() {
        return INSTANCE;
    }

    private final Logger logger = LoggerFactory.getLogger(ApplicationConfigProperties.class);

    private String baseURL;
    private String username;
    private String password;

    private Map<String, String> additionalProps = null;

    protected ApplicationConfigProperties() {
        additionalProps = new HashMap<>(5);
    }

    protected void loadProperties() {
        FilePathBuilder fpb = new FilePathBuilder(ApplicationConfigConstants.ENVIORNMENT_PROPS);
        fpb.setParentDirectory(ApplicationConfigConstants.PROPERTIES_DIRECTORY);

        String envProps = fpb.getFilePath();
        logger.debug("Environment Properties Path {}", envProps);

        FileOperation fileOps = new FileOperation(new File(envProps));
        Map<String, String> props = fileOps.getPropValuesInMap();

        if (props == null) {
            logger.error("Failed to read the properties for the application from resouce: " + envProps);
            return;
        }

        for (Entry<String, String> entry : props.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (ApplicationConfigConstants.APPLICATION_BASE_URL.equals(key))
                setBaseURL(value);
            else if (ApplicationConfigConstants.APPLICATION_USERNAME.equals(key))
                setUsername(value);
            else if (ApplicationConfigConstants.APPLICATION_PASSWORD.equals(key))
                setPassword(value);
            else
                setProperty(key, value);
        }
    }

    public String getBaseURL() {
        return baseURL;
    }


    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    private void setPassword(String passString) {
        this.password = passString;
    }

    private void setProperty(String key, String value) {
        additionalProps.put(key, value);
    }

    public String getPropertyValue(String key) {
        return additionalProps.get(key);
    }
}