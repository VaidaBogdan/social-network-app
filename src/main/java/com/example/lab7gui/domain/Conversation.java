package com.example.lab7gui.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Conversation extends Entity<Long> {
    private final List<Utilizator> participants;
    private List<Message> messages;

    public Conversation(List<Utilizator> participants, List<Message> messages) {
        this.participants = participants;
        this.messages = messages;
    }
    public Conversation(List<Utilizator> participants){
        this.participants = participants;
        this.messages = new ArrayList<>();
    }

    public List<Utilizator> getParticipants() {
        return participants;
    }

    public List<Long> getParticipantsIds() {
        return getParticipants().stream().map(Entity::getId).collect(Collectors.toList());
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void addMsg(Message m){
        this.messages.add(m);
    }

    public void delMsg(Message m){
        this.messages.remove(m);
    }

    public void setMessages(List<Message> messages){
        this.messages = messages;
    }
    public List<Long> getMessagesIds() {
        return getMessages().stream().map(Entity::getId).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conversation that = (Conversation) o;
        return (getParticipantsIds().equals(that.getParticipantsIds()) &&
                getMessagesIds().equals(that.getMessagesIds()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getParticipantsIds(), getMessagesIds());
    }
}
