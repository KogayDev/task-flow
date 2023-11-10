package com.kogay.taskflow.validation;

import com.kogay.taskflow.dto.RegisterDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, RegisterDto> {

    @Override
    public boolean isValid(RegisterDto registerDto, ConstraintValidatorContext context) {
        String password = registerDto.getPassword();
        String confirmPassword = registerDto.getConfirmPassword();

        if (!StringUtils.hasText(password) || !StringUtils.hasText(confirmPassword)) {
            return false;
        }

        return password.equals(confirmPassword);
    }
}
