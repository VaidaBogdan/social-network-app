package com.example.lab7gui.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Prietenie extends Entity<Tuple<Long,Long>> {

    private LocalDateTime date;
    private Utilizator u1;
    private Utilizator u2;

    private PrietenieStatus status;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public Prietenie(Utilizator u1, Utilizator u2, PrietenieStatus status) {
        this.u1 = u1;
        this.u2 = u2;
        date = LocalDateTime.now();
        this.setId(new Tuple<>(u1.getId(),u2.getId()));
        this.status = status;
        if(this.status == PrietenieStatus.PENDING){
            u2.addFriend(u1, this.status);
        }
        else {
            u1.addFriend(u2, PrietenieStatus.ACCEPTED);
            u2.addFriend(u1, PrietenieStatus.ACCEPTED);
        }
    }

    public void setData(LocalDateTime date){
        this.date = date;
    }

    public Utilizator getU1(){
        return this.u1;
    }

    public Utilizator getU2(){
        return this.u2;
    }

    public PrietenieStatus getStatus() {
        return status;
    }

    public void setStatus(PrietenieStatus status) {
        this.status = status;
    }

    /**
     *
     * @return the date when the friendship was created
     */
    public LocalDateTime getData() {
        return date;
    }
}
