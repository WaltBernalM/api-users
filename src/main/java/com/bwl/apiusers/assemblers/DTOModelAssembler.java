package com.bwl.apiusers.assemblers;

import org.springframework.hateoas.EntityModel;

public abstract class DTOModelAssembler<D, T> extends BaseModelAssembler<T> {
    @Override
    public EntityModel<D> toModel(Object entity) {
        return (EntityModel<D>) EntityModel.of(entity);
    }
}
