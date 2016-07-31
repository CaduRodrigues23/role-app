package br.com.mytho.role.facade;

import android.content.Context;

import java.net.UnknownHostException;
import java.util.List;

import br.com.mytho.role.activity.delegate.EventDelegate;
import br.com.mytho.role.domain.service.EventService;
import br.com.mytho.role.model.Event;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import utils.DialogUtils;

/**
 * Created by leonardocordeiro on 22/07/16.
 */

public class EventFacade {

    private EventDelegate eventDelegate;
    private DialogUtils dialogUtils;

    public EventFacade(EventDelegate delegate) {
        this.eventDelegate = delegate;
        this.dialogUtils = new DialogUtils((Context) eventDelegate);
    }

    public void getEvents() {
        EventService service = new EventService.Builder().context((Context) eventDelegate).build();
        service.list()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Event>>() {
                    @Override
                    public void call(List<Event> events) {
                        EventFacade.this.eventDelegate.onEvents(events);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                    if (throwable instanceof UnknownHostException) {
                        dialogUtils.showConnectionError(new DialogUtils.OnRetryListener() {
                            @Override
                            public void onRetry() {
                                getEvents();
                            }
                        });
                    }
                    }
                });
    }
}
