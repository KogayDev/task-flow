package com.kogay.taskflow.config;

import com.kogay.taskflow.TaskFlowApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.envers.repository.config.EnableEnversRepositories;

@Configuration
@EnableEnversRepositories(basePackageClasses = TaskFlowApplication.class)
public class AuditConfiguration {
}
