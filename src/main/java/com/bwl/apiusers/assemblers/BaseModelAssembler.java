package com.bwl.apiusers.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class BaseModelAssembler<T> implements RepresentationModelAssembler<T, EntityModel<T>> {
    @Override
    public EntityModel<T> toModel(T entity) {
        return EntityModel.of(entity);
    }
}
