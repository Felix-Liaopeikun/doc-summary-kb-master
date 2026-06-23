package com.example.dockb.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * DeepSeek API 调用配置。
 *
 * <p>key 读取顺序：环境变量 {@code DEEPSEEK_API_KEY} → {@code deepseek.api-key}。
 */
@Data
@Component
@ConfigurationProperties(prefix = "deepseek")
public class DeepSeekProperties {

    /** API Key；启动时会先尝试环境变量覆盖。 */
    private String apiKey = "REPLACE_WITH_YOUR_KEY";

    private String baseUrl = "https://api.deepseek.com/v1";
    private String model = "deepseek-chat";

    /**
     * 解析后的最终 key（环境变量优先）。
     */
    public String resolveApiKey() {
        String env = System.getenv("DEEPSEEK_API_KEY");
        if (env != null && !env.isBlank() && !"REPLACE_WITH_YOUR_KEY".equals(env)) {
            return env;
        }
        return apiKey;
    }

    public boolean isKeyConfigured() {
        String k = resolveApiKey();
        return k != null && !k.isBlank() && !"REPLACE_WITH_YOUR_KEY".equals(k);
    }
}
