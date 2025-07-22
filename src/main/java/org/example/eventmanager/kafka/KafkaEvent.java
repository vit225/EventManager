package org.example.eventmanager.kafka;

import java.util.List;
import java.util.Map;

public class KafkaEvent {

    private Integer eventId;
    private Integer userIdWhoChanged;
    private Integer ownerId;
    private List<Integer> subscriberUserIds;
    private Map<String, ChangedField> changedFields;

    public KafkaEvent() {
    }

    public KafkaEvent(
            Integer eventId,
            Integer userIdWhoChanged,
            Integer ownerId,
            List<Integer> subscriberUserIds, Map<String, ChangedField> changedFields
    ) {
        this.eventId = eventId;
        this.userIdWhoChanged = userIdWhoChanged;
        this.ownerId = ownerId;
        this.subscriberUserIds = subscriberUserIds;
        this.changedFields = changedFields;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getUserIdWhoChanged() {
        return userIdWhoChanged;
    }

    public void setUserIdWhoChanged(Integer userIdWhoChanged) {
        this.userIdWhoChanged = userIdWhoChanged;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public List<Integer> getSubscriberUserIds() {
        return subscriberUserIds;
    }

    public void setSubscriberUserIds(List<Integer> subscriberUserIds) {
        this.subscriberUserIds = subscriberUserIds;
    }

    public Map<String, ChangedField> getChangedFields() {
        return changedFields;
    }

    public void setChangedFields(Map<String, ChangedField> changedFields) {
        this.changedFields = changedFields;
    }

    public static class ChangedField {
        private Object oldValue;
        private Object newValue;

        public ChangedField() {
        }

        public ChangedField(Object oldValue, Object newValue) {
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        public Object getOldValue() {
            return oldValue;
        }

        public void setOldValue(Object oldValue) {
            this.oldValue = oldValue;
        }

        public Object getNewValue() {
            return newValue;
        }

        public void setNewValue(Object newValue) {
            this.newValue = newValue;
        }
    }
}
