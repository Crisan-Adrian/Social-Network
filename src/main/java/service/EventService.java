package service;

import domain.User;
import domain.UserEvent;
import exceptions.ServiceException;
import repository.PaginationInfo;
import repository.PaginationInfoBuilder;
import repository.event.IEventRepository;

import java.util.List;

public class EventService implements IEventService {

    private final IEventRepository eventRepository;
    private final PaginationInfo paginationInfo;

    public EventService(IEventRepository eventRepository) {
        this.eventRepository = eventRepository;
        paginationInfo = new PaginationInfoBuilder().create().setPageSize(10).setPageNumber(0).build();
    }

    @Override
    public List<UserEvent> getFirstPage() {
        paginationInfo.setPageNumber(0);
        return eventRepository.getPage(paginationInfo);
    }

    @Override
    public List<UserEvent> getPage(int pageNumber) {
        paginationInfo.setPageNumber(pageNumber);
        return eventRepository.getPage(paginationInfo);
    }

    @Override
    public int getPageCount() {
        return eventRepository.getPageCount(paginationInfo);
    }

    @Override
    public boolean hasPrevPage() {
        return paginationInfo.getPageNumber() > 0;
    }

    @Override
    public boolean hasNextPage() {
        return paginationInfo.getPageNumber() < (eventRepository.getPageCount(paginationInfo) - 1);
    }

    @Override
    public int getPageNumber() {
        return paginationInfo.getPageNumber();
    }

    @Override
    public List<UserEvent> getNextPage() {
        if (hasNextPage()) {
            paginationInfo.incrementPageNumber();
            return eventRepository.getPage(paginationInfo);
        }
        throw new ServiceException("There is no next page");
    }

    @Override
    public List<UserEvent> getPrevPage() {
        if (hasPrevPage()) {
            paginationInfo.decrementPageNumber();
            return eventRepository.getPage(paginationInfo);
        }
        throw new ServiceException("There is no previous page");
    }

    @Override
    public void save(UserEvent newEvent) {
        eventRepository.save(newEvent);
    }

    @Override
    public UserEvent changeNotificationState(User user, UserEvent event, boolean isNotified) {
        if(isNotified){
            event.subscribe(user.getID());
        } else {
            event.unsubscribe(user.getID());
        }
        eventRepository.setSubscription(event, user.getID(), isNotified);
        return event;
    }

    @Override
    public UserEvent changeAttendance(User user, UserEvent event, boolean isAttending) {
        if(isAttending){
            event.addAttending(user.getID());
        } else {
            event.removeAttending(user.getID());
        }
        if(eventRepository.update(event) != null) {
            return event;
        }
        else {
            throw new ServiceException("Error updating event");
        }
    }
}
