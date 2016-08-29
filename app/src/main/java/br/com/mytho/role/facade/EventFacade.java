package br.com.mytho.role.facade;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import java.net.UnknownHostException;
import java.util.List;

import br.com.mytho.role.activity.MainActivity;
import br.com.mytho.role.domain.service.EventService;
import br.com.mytho.role.infra.exception.ConnectionErrorException;
import br.com.mytho.role.infra.exception.HTTPUnauthorizedException;
import br.com.mytho.role.infra.exception.UnavailableException;
import br.com.mytho.role.model.Event;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import utils.DialogUtils;

/**
 * Created by leonardocordeiro on 22/07/16.
 */

public class EventFacade {
    private Context context;

    public EventFacade(Context context) {
        this.context = context;
    }

    public void getEvents() {
        EventService service = new EventService.Builder().context(context).build();
        service.list()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Event>>() {
                    @Override
                    public void call(List<Event> events) {
                        EventBus.getDefault().post(events);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (throwable instanceof UnknownHostException) {
                            EventBus.getDefault().post(new ConnectionErrorException());
                        } else if(throwable.getMessage().contains("401")){
                            EventBus.getDefault().post(new HTTPUnauthorizedException());
                        }
                    }
                });
    }
}
