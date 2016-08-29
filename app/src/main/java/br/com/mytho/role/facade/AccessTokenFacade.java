package br.com.mytho.role.facade;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import br.com.mytho.role.activity.delegate.AccessTokenDelegate;
import br.com.mytho.role.infra.exception.ConnectionErrorException;
import br.com.mytho.role.security.OAuthAccessTokenService;
import br.com.mytho.role.security.model.AccessToken;
import br.com.mytho.role.security.model.repository.AccessTokenRepository;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import utils.DialogUtils;

/**
 * Created by leonardocordeiro on 21/07/16.
 */

public class AccessTokenFacade {

    private Context context;
    private AccessTokenRepository tokenRepository;
    private DialogUtils dialogUtils;

    public AccessTokenFacade(Context context) {
        this.context = context;

        this.tokenRepository = new AccessTokenRepository(context);
        this.dialogUtils = new DialogUtils(context);
    }

    public void getAccessToken() {
        if (tokenRepository.empty()) {
            final OAuthAccessTokenService oAuthAccessTokenService = new OAuthAccessTokenService.Builder().build();

            oAuthAccessTokenService.getAccessToken(OAuthAccessTokenService.PUBLIC_SCOPE)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<AccessToken>() {
                                        @Override
                                        public void call(AccessToken accessToken) {
                                            tokenRepository.save(accessToken);
                                            EventBus.getDefault().post(accessToken);
                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            if (throwable instanceof UnknownHostException) {
                                                EventBus.getDefault().post(new ConnectionErrorException());
                                            } else {
                                                getAccessToken();
                                            }
                                        }
                                    });
        } else {
            EventBus.getDefault().post(new AccessToken());
        }
    }

    public void retry() {
        tokenRepository.clear();
        getAccessToken();
    }

}
