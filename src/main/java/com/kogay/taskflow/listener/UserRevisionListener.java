package com.kogay.taskflow.listener;

import com.kogay.taskflow.entity.Revision;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ((Revision) revisionEntity).setUsername(username);
    }
}
