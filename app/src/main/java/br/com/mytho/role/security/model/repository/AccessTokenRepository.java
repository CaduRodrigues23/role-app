package br.com.mytho.role.security.model.repository;

import android.content.Context;
import android.content.SharedPreferences;

import br.com.mytho.role.security.model.AccessToken;

/**
 * Created by leonardocordeiro on 20/07/16.
 */

public class AccessTokenRepository {
    private static final String EMPTY_STRING = "";

    private static final String PREFERENCE_KEY = "Bearer Token";
    private static final String PREFERENCE_NAME = "role-app";

    private Context context;
    private SharedPreferences preferences;

    public AccessTokenRepository(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public void save(AccessToken token) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREFERENCE_KEY, token.getCode());

        editor.commit();
    }

    public AccessToken retrieve() {
        String code = preferences.getString(PREFERENCE_KEY, EMPTY_STRING);

        return new AccessToken(code);
    }

    public void clear() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();

        editor.commit();
    }

    public boolean empty() {
        return retrieve().getCode().isEmpty();
    }


}
