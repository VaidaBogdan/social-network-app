package com.example.lab7gui.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Message extends Entity<Long>{
    private Utilizator from;
    private List<Utilizator> to;
    private String mesaj;
    private LocalDateTime data;
    private Message original;


    public Message(Utilizator from, List<Utilizator> to, String mesaj) {
        this.from = from;
        this.to = to;
        this.mesaj = mesaj;
        this.data = LocalDateTime.now();
        this.original = null;
    }

    public Message(Utilizator from, List<Utilizator> to, String mesaj, LocalDateTime data) {
        this.from = from;
        this.to = to;
        this.mesaj = mesaj;
        this.data = data;
        this.original = null;
    }

    public Message(Utilizator from, List<Utilizator> to, String mesaj, LocalDateTime data, Message original) {
        this.from = from;
        this.to = to;
        this.mesaj = mesaj;
        this.data = data;
        this.original = original;
    }

    public Message(Utilizator from, List<Utilizator> to, String mesaj, Message original) {
        this.from = from;
        this.to = to;
        this.mesaj = mesaj;
        this.data = LocalDateTime.now();
        this.original = original;
    }

    public Utilizator getFrom() {
        return from;
    }

    public void setFrom(Utilizator from) {
        this.from = from;
    }

    public List<Utilizator> getTo() {
        return to;
    }

    public List<Long> getToIds(){
        return to.stream().map(Entity::getId).toList();
    }

    public void setTo(List<Utilizator> to) {
        this.to = to;
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Message getOriginal() {
        return original;
    }

    public void setOriginal(Message original) {
        this.original = original;
    }


    @Override
    public String toString() {
        StringBuilder toS = new StringBuilder();
        for(Utilizator u: to) {
            toS.append(u.getFirstName()).append(" ").append(u.getLastName()).append(", ");
        }
        return "Message{" +
                "id=" +  this.getId().toString() +
                ", from=" + from.getLastName() + " " + from.getLastName() +
                ", to=" + toS +
                ", mesaj='" + mesaj + '\'' +
                ", data=" + data +
                ", original=" + original.getId().toString() +
                '}';
    }
}
