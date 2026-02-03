package ru.practicum.main.event.model;

/**
 * Event lifecycle states and allowed transitions.
 * <ul>
 *     <li>PENDING → PUBLISHED: published by admin (PUBLISH_EVENT)</li>
 *     <li>PENDING → CANCELED: canceled by user (CANCEL_REVIEW) or rejected by admin (REJECT_EVENT)</li>
 *     <li>CANCELED → PENDING: resubmitted for moderation (SEND_TO_REVIEW)</li>
 *     <li>PUBLISHED: status changes are not allowed</li>
 * </ul>
 */
public enum EventState {

    /**
     * Pending moderation; event is not visible in the public API.
     */
    PENDING,

    /**
     * Published; event is available in the public API.
     */
    PUBLISHED,

    /**
     * Canceled; event is not available in the public API.
     */
    CANCELED
}
