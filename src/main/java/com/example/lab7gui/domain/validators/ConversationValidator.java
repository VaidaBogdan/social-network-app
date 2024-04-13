package com.example.lab7gui.domain.validators;

import com.example.lab7gui.domain.Conversation;

public class ConversationValidator implements Validator<Conversation> {
    @Override
    public void validate(Conversation entity) throws ValidationException {
        if(entity.getParticipants().size() < 2){
            throw new ValidationException("o conversatie trebuie sa contina minim 2 participanti");
        }
    }
}
