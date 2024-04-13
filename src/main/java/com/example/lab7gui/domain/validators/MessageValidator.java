package com.example.lab7gui.domain.validators;

import com.example.lab7gui.domain.Message;

public class MessageValidator implements Validator<Message> {
    @Override
    public void validate(Message entity) throws ValidationException {
        if(entity.getMesaj().contains(";")){
            throw new ValidationException("Mesajul nu poate contine caracterul ';' ");
        }

    }
}
