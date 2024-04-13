package com.example.lab7gui.repository;

import com.example.lab7gui.domain.*;
import com.example.lab7gui.domain.validators.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {
    private final Validator<E> validator;
    Map<ID,E> entities;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities=new HashMap<ID,E>();
    }

    @Override
    public Optional<E> findOne(ID id){
        if (id==null)
            throw new IllegalArgumentException("id must be not null");
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public Optional<E> save(E entity) {
        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);
        if(entities.get(entity.getId()) != null) {
            return Optional.of(entity);
        }
        else entities.put(entity.getId(),entity);
        return Optional.empty();
    }

    @Override
    public Optional<E> delete(ID id) {
        if(id == null){
            throw new IllegalArgumentException("entity id must be not null!");
        }
        E e = entities.get(id);
        if(e == null)
            return Optional.empty();
        return Optional.of(entities.remove(e.getId()));
    }

    @Override
    public Optional<E> update(E entity) {

        if(entity == null)
            throw new IllegalArgumentException("entity must be not null!");
        validator.validate(entity);

        if(entities.get(entity.getId()) != null) {
            var u = entities.get(entity.getId());
            if(u instanceof Utilizator){
                ((Utilizator) u).setFirstName(((Utilizator) entity).getFirstName());
                ((Utilizator) u).setLastName(((Utilizator) entity).getLastName());
            }
            if(u instanceof Prietenie){
                ((Prietenie) u).setStatus(PrietenieStatus.ACCEPTED);
            }
            if(u instanceof Message){
                ((Message) u).setOriginal(null);
            }
            if(u instanceof Conversation){
                ((Conversation) u).setMessages(((Conversation) entity).getMessages());
            }
            return Optional.empty();
        }
        return Optional.of(entity);

    }

    @Override
    public Optional<E> savePasswd(E entity, String passwd) {
        return Optional.empty();
    }

    @Override
    public Optional<E> findOnePasswd(ID id, String passwd) {
        return Optional.empty();
    }
}
