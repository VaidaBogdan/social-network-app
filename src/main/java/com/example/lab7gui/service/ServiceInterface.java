package com.example.lab7gui.service;

import com.example.lab7gui.domain.Entity;
import com.example.lab7gui.domain.Tuple;
import com.example.lab7gui.domain.Utilizator;
import com.example.lab7gui.domain.validators.ValidationException;
import com.example.lab7gui.domain.Prietenie;

import java.util.List;

public interface ServiceInterface {

    /**
     *
     * @param user - the user that will be added
     * @return - true or false, if the user was added or not
     */
    Utilizator addUser(Utilizator user);

    /**
     *
     * @param id - the user's id
     * @return - the user that was removed, if they were removed
     */
    Utilizator removeUser(Long id);

    /**
     * removes an user and all the friendships that contain that user
     * @param id - the id of the friendship that will be added
     * @return - true or false, if the friendship was created and added or not
     */
    boolean createFriendship(Tuple<Long, Long> id);

    /**
     *
     * @param id - the id of the friendship that will be deleted
     * @return - the friendship that was deleted, if it was removed
     */
    Prietenie deleteFriendship(Tuple<Long, Long> id);

    /**
     *
     * @return - an Iterable of all the users
     */

    Iterable<Utilizator> getUtilizatori();

    /**
     *
     * @return - an Iterable of all the friendships
     */

    Iterable<Prietenie> getPrietenii();

    /**
     *
     * @param id - the id of the user we want to get
     * @return - the user, if they exist
     */

    Utilizator getUser(Long id);

    /**
     *
     * @return - the number of communities (conex components of the graph)
     */

    int nrComunitati();

    /**
     *
     * @return a list of Iterable objects of students that have the longest path (conex components with the longest patsh)
     */
    List<Iterable<Utilizator>> ceaMaiSociabilaComunitate();

    Utilizator updateUser(Utilizator u);



}
