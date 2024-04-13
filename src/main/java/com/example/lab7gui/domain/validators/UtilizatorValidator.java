package com.example.lab7gui.domain.validators;

import com.example.lab7gui.domain.Utilizator;

public class UtilizatorValidator implements Validator<Utilizator> {

    /**
     *
     * @param entity - the user that will be validated
     * @throws ValidationException - if any of the names are empty or don't contain letters
     */
    @Override
    public void validate(Utilizator entity) throws ValidationException {
        if(entity.getLastName().isEmpty())
            throw new ValidationException("Numele nu poate fi gol!");

        if(entity.getFirstName().isEmpty())
            throw new ValidationException("Prenumele nu poate fi gol!");

        for(int i=0;i<entity.getFirstName().length();i++){
            if(!Character.isLetter(entity.getFirstName().charAt(i)))
                throw new ValidationException("Prenumele contine doar litere!");
        }

        for(int i=0;i<entity.getLastName().length();i++){
            if(!Character.isLetter(entity.getLastName().charAt(i)))
                throw new ValidationException("Numele contine doar litere!");
        }
    }
}

