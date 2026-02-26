package defacement.service;

import defacement.model.User;
import java.util.List;

public interface AdminUserService {
    List<User> findAllActiveUsers();
    List<User> findAllDeletedUsers();
    User findUserById(Long id);

    void createUser(User user, Long roleId, String rawPassword);
    void updateUser(Long id, User formUser, Long roleId);
    void softDeleteUser(Long targetId, String requestingUsername);
    void restoreUser(Long id);
    void changePassword(Long id, String newPassword);
}

