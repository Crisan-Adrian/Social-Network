package service;

import repository.event.IEventRepository;

public class EventService implements IEventService {

    private IEventRepository eventRepository;

    public EventService(IEventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
}
