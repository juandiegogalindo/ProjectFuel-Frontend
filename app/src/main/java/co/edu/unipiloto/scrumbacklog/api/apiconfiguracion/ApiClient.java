package co.edu.unipiloto.scrumbacklog.api.apiconfiguracion;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // TU IP
    private static final String BASE_URL = "http://172.16.129.94:8080/";

    private static Retrofit retrofit;

    public static Retrofit getClient() {

        if (retrofit == null) {

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}