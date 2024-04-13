package com.example.lab7gui.domain.validators;

public class ValidationFactory implements Factory {
    private static ValidationFactory instance;

    private ValidationFactory() {

    }

    public static ValidationFactory getInstance() {
        if (instance == null) {
            instance = new ValidationFactory();
        }
        return instance;
    }

    public Validator createValidator(ValidationStrategy v){
        if(v == ValidationStrategy.UTILIZATOR)
            return new UtilizatorValidator();
        if(v == ValidationStrategy.PRIETENIE)
            return new PrietenieValidator();
        if(v == ValidationStrategy.MESAJ)
            return new MessageValidator();
        if(v == ValidationStrategy.CONVERSATIE)
            return new ConversationValidator();
        throw new IllegalArgumentException("Strategie incorecta!!");

    }
}
