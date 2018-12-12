package io.redistrict.auth.validator;

import io.redistrict.auth.model.User;
import io.redistrict.auth.repository.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {

    @Autowired
    private UserDao userDao;

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        User user = (User) o;

        if (userDao.findByUsername(user.getUsername()) != null) {
            errors.rejectValue("username", "userTaken");
        }

        if (user.getPassword().length() < 8 || user.getPassword().length() > 32) {
            errors.rejectValue("password", "shortPw");
        }

        if (!user.getPasswordConfirm().equals(user.getPassword())) {
            errors.rejectValue("password", "pwNoMatch");
        }
    }
}
