package br.com.mytho.role.activity.delegate;

import java.util.List;

import br.com.mytho.role.model.Event;

/**
 * Created by leonardocordeiro on 22/07/16.
 */

public interface EventDelegate {

    void onEvents(List<Event> events);

}
