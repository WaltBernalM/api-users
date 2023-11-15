package com.bwl.apiusers.exceptions;

import com.bwl.apiusers.models.Permission;

public class PermissionNotFoundException extends BaseNotFoundException {
    public PermissionNotFoundException(Integer id) {
        super(Permission.class, id);
    }
}
