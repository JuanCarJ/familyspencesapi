package com.familyspencesapi.utils;

public interface MessageSender<T> {
    void execute(T message, String routingKey);
}
