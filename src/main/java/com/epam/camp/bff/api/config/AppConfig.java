package com.epam.camp.bff.api.config;

import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        var sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, TrustAllStrategy.INSTANCE)
                .build();

        var cm = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext))
                .build();

        var httpClient = HttpClients
                .custom()
                .setConnectionManager(cm)
                .build();

        var requestFactory = new HttpComponentsClientHttpRequestFactory();

        requestFactory.setHttpClient(httpClient);
        return new RestTemplate(requestFactory);
    }
}
