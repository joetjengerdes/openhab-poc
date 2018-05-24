package org.openhab.binding.a.eventbus;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.common.ThreadPoolManager;
import org.eclipse.smarthome.core.events.Event;
import org.eclipse.smarthome.core.events.EventFilter;
import org.eclipse.smarthome.core.events.EventPublisher;
import org.eclipse.smarthome.core.events.EventSubscriber;
import org.eclipse.smarthome.core.items.events.ItemStateEvent;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.types.State;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = { EventSubscriber.class })
public class Eventbus implements EventSubscriber {

    @Reference
    public EventPublisher publisher;

    @Override
    public @NonNull Set<@NonNull String> getSubscribedEventTypes() {
        return Collections.singleton(EventSubscriber.ALL_EVENT_TYPES);
    }

    @Override
    public @Nullable EventFilter getEventFilter() {
        return (event) -> {
            if (event instanceof ItemStateEvent) {
                ItemStateEvent castEvent = (ItemStateEvent) event;
                return castEvent.getItemState() instanceof OnOffType;
            } else {
                return false;
            }

        };
    }

    private final ScheduledExecutorService scheduler = ThreadPoolManager.getScheduledPool("eventbusfake");

    @Override
    public void receive(@NonNull Event event) {

        // We can safely cast here because we only get filtered events.
        ItemStateEvent castEvent = (ItemStateEvent) event;
        String itemname = castEvent.getItemName();
        OnOffType state = (OnOffType) castEvent.getItemState();

        // Flips the On/Off state.
        if (state == OnOffType.ON) {
            state = OnOffType.OFF;
        } else {
            state = OnOffType.ON;
        }

        Event e = new FakeEvent(event.getTopic(), event.getPayload(), itemname, state, event.getSource());

        // Flip switch after five seconds.
        scheduler.schedule(() -> {
            publisher.post(e);
        }, 5, TimeUnit.SECONDS);

    }

    // Overcomes constructor visibility restrictions of ItemStateEvent.
    class FakeEvent extends ItemStateEvent {

        protected FakeEvent(String topic, String payload, String itemName, State itemState, String source) {
            super(topic, payload, itemName, itemState, source);
        }

    }

}
