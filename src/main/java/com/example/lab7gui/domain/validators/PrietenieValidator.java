package com.example.lab7gui.domain.validators;

import com.example.lab7gui.domain.Prietenie;

public class PrietenieValidator implements Validator<Prietenie> {

    /**
     *
     * @param entity - the friendship that needs to be validated
     * @throws ValidationException - if the friendship has the same id's for both users
     */
    @Override
    public void validate(Prietenie entity) throws ValidationException {
        var ids = entity.getId();
        if(ids.getLeft().equals(ids.getRight()))
            throw new ValidationException("Prietenia se face intre doi utilizatori diferiti!");
    }
}
