package br.com.mytho.role.security;

import android.util.Base64;

import java.io.IOException;

import br.com.mytho.role.security.model.AccessToken;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by leonardocordeiro on 11/07/16.
 */
public interface OAuthAccessTokenService {

    @FormUrlEncoded
    @POST("oauth/token")
    Call<AccessToken> getAccessToken(
            @Field("scope") String scope,
            @Field("grant_type") String grantType);

    class Builder {
        private static final String CLIENT_ID = "mobile-client";
        private static final String CLIENT_SECRET = "08282424-432a-11e6-beb8-9e71128cae77";
        private static final String CREDENTIALS = CLIENT_ID + ":" + CLIENT_SECRET;

        private OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        private Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://role-lema.rhcloud.com/rolebackend/")
                .addConverterFactory(JacksonConverterFactory.create());

        private String encodedCredentials = Base64.encodeToString(CREDENTIALS.getBytes(), Base64.NO_WRAP);

        public OAuthAccessTokenService build() {
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    Request.Builder builder = original.newBuilder()
                           .addHeader("Authorization", "Basic " + encodedCredentials)
                           .method(original.method(), original.body());

                    Request request = builder.build();
                    return chain.proceed(request);
                }
            });

            OkHttpClient okHttpClient = httpClient.build();
            Retrofit retrofit = builder.client(okHttpClient).build();

            return retrofit.create(OAuthAccessTokenService.class);

        }
    }
}
