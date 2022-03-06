package autumn.config.util;

import autumn.config.core.ApolloClientSystemConsts;
import autumn.config.core.ConfigConsts;
import org.springframework.util.StringUtils;

import java.io.File;

public class ConfigUtil {
    private boolean propertiesOrdered = false;
    private boolean propertyFileCacheEnabled = true;
    public boolean isPropertiesOrderEnabled() {
        return propertiesOrdered;
    }

    public boolean isPropertyFileCacheEnabled() {
        return propertyFileCacheEnabled;
    }

    public String getDefaultLocalCacheDir() {
        String cacheRoot = getCustomizedCacheRoot();

        if (!StringUtils.hasLength(cacheRoot)) {
            return cacheRoot + File.separator + getAppId();
        }

        cacheRoot = isOSWindows() ? "C:\\opt\\data\\%s" : "/opt/data/%s";
        return String.format(cacheRoot, getAppId());
    }

    /**
     * Get the app id for the current application.
     *
     * @return the app id or ConfigConsts.NO_APPID_PLACEHOLDER if app id is not available
     */
    public String getAppId() {
        String appId = "";
        if (!StringUtils.hasLength(appId)) {
            appId = ConfigConsts.NO_APPID_PLACEHOLDER;
        }
        return appId;
    }


    private String getCustomizedCacheRoot() {
        // 1. Get from System Property
        String cacheRoot = System.getProperty(ApolloClientSystemConsts.APOLLO_CACHE_DIR);
        if (StringUtils.hasLength(cacheRoot)) {
            // 2. Get from OS environment variable
            cacheRoot = System.getenv(ApolloClientSystemConsts.APOLLO_CACHE_DIR_ENVIRONMENT_VARIABLES);
        }

        return cacheRoot;
    }

    public boolean isOSWindows() {
        String osName = System.getProperty("os.name");
        if (!StringUtils.hasLength(osName)) {
            return false;
        }
        return osName.startsWith("Windows");
    }

}
