package com.example.lab7gui.repository.paging;

import com.example.lab7gui.domain.Entity;
import com.example.lab7gui.repository.Repository;

public interface PagingRepository<ID , E extends Entity<ID>> extends Repository<ID, E> {

    Page<E> findAll(Pageable pageable);
}
