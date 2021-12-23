package iks.surveytool.utils.builder;

import iks.surveytool.entities.User;

public class UserBuilder {

    String firstName = "default";
    String lastName = "default";
    String email = "default@default.de";

    public User createUser(Long id, String name) {
        User newUser = new User();
        newUser.setId(id);
        newUser.setName(name);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        return newUser;
    }
}
