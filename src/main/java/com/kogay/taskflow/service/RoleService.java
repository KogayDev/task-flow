package com.kogay.taskflow.service;

import com.kogay.taskflow.entity.Role;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    public Role getDefaultRole() {
        return Role.USER;
    }
}
