package com.example.lab7gui.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Utilizator extends Entity<Long> {
    private String firstName;
    private String lastName;
    private ArrayList<Utilizator> friends;

    private ArrayList<Utilizator> pendingFriends;

    public Utilizator(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.friends = new ArrayList<>() {};
        this.pendingFriends = new ArrayList<>();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ArrayList<Utilizator> getFriends() {
        return friends;
    }
    public ArrayList<Utilizator> getPendingFriends() {
        return pendingFriends;
    }

    public void setFriends(ArrayList<Utilizator> friends) {
        this.friends = friends;
    }

    @Override
    public String toString() {
        StringBuilder prieteni = new StringBuilder();
        for(Utilizator u: friends) {
            prieteni.append(u.getFirstName()).append(" ").append(u.getLastName()).append(", ");
        }
        return "Utilizator{ID: " + this.getId().toString() +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", friends=" + prieteni +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilizator)) return false;
        Utilizator that = (Utilizator) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getFriends().equals(that.getFriends());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName());
    }

    public void addFriend(Utilizator u, PrietenieStatus status){
        if(status == PrietenieStatus.PENDING){
            this.pendingFriends.add(u);
        }
        if(status == PrietenieStatus.ACCEPTED){
            this.friends.add(u);
        }

    }

    public void delFriend(Utilizator u){
        for(Utilizator user: friends){
            if(user.equals(u)){
                friends.remove(user);
                break;
            }
        }
    }

    public void delFriendPending(Utilizator u){
        for(Utilizator user: pendingFriends){
            if(user.equals(u)){
                pendingFriends.remove(user);
                break;
            }
        }
    }
}