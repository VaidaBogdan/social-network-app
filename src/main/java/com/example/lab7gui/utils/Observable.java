package com.example.lab7gui.utils;

public interface Observable {
    void addObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObs();

}
