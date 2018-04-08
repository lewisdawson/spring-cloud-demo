package com.example.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RefreshScope
@RestController
public class PropertiesRestController {

    private Environment injectedEnv;

    private WebClient webClient;

    @Value("${first.message}")
    private String message;

    @Value("${app.name}")
    private String appName;

    @Value("${env}")
    private String env;

    @Value("${spring.cloud.config.uri}")
    private String configServerBaseUri;

    @Autowired
    public PropertiesRestController(Environment injectedEnv, WebClient webClient) {
        this.injectedEnv = injectedEnv;
        this.webClient = webClient;
    }

    @GetMapping(value = "/properties", produces = {"application/json"})
    Map<String, Object> getProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("message", message);
        props.put("appName", appName);
        props.put("env", env);
        props.put("injectedEnv", injectedEnv.getActiveProfiles());

        return props;
    }

    @GetMapping(value = "/all-properties", produces = {"application/json"})
    Map<String, Object> getAllKnownProperties() {
        Map<String, Object> rtn = new HashMap<>();

        if (injectedEnv instanceof ConfigurableEnvironment) {
            for (PropertySource<?> propertySource : ((ConfigurableEnvironment) injectedEnv).getPropertySources()) {
                if (propertySource instanceof EnumerablePropertySource) {
                    for (String key : ((EnumerablePropertySource) propertySource).getPropertyNames()) {
                        rtn.put(key, propertySource.getProperty(key));
                    }
                }
            }
        }

        return rtn;
    }

    @SuppressWarnings("unchecked")
    @GetMapping(value = "/properties-file/{appName}/{env}/{gitBranch}/{filePath}", produces = {"application/json"})
    Mono<Map> getPropertiesFile(@PathVariable("appName") String appName,
                                @PathVariable("env") String env,
                                @PathVariable("gitBranch") String gitBranch,
                                @PathVariable("filePath") String filePath) {
        String endpoint = String.format("%s/%s/%s/%s/%s", configServerBaseUri, appName, env, gitBranch, filePath);

        log.info("Fetching properties file for path {}...", endpoint);

        return webClient
                .get()
                .uri(endpoint)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Map.class);
    }
}
