package defacement.service;

import defacement.model.User;

public interface UserService {
    User getCurrentUser(); // fetch logged-in user
    void changePassword(String oldPassword, String newPassword);
}