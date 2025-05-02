package org.turquase.pranalyser.infrastructure.github.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.turquase.pranalyser.infrastructure.config.ConfigLoader;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RetrofitClient {

    private final ConfigLoader configLoader;

    public RetrofitClient() {
        this.configLoader = new ConfigLoader();
    }

    public Retrofit getRetrofitClient(String accessToken) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        HttpLoggingInterceptor loggingInterceptor =
                new HttpLoggingInterceptor().setLevel(configLoader.getApiLoggingLevel());

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new AuthInterceptor(accessToken))
                .build();

        return new Retrofit.Builder()
                .baseUrl(configLoader.getGitHubApiBaseUrl())
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .client(client)
                .build();
    }
}
