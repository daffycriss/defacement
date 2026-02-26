package defacement.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import defacement.model.User;
import defacement.repository.RoleRepository;
import defacement.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(AdminUserServiceImpl.class);

    @Override
    public void createUser(User user, Long roleId, String rawPassword) {
        if (userRepository.findByUsername(user.getUsername()).isPresent())
            throw new IllegalArgumentException("Username already exists");
        if (!PasswordValidator.isValid(rawPassword))
            throw new IllegalArgumentException("Password does not meet complexity requirements");
        if (roleId == null)
            throw new IllegalArgumentException("Role must be selected");

        user.setPassword(passwordEncoder.encode(rawPassword));
        roleRepository.findById(roleId).ifPresent(user::addRole);
        userRepository.save(user);

        logger.info("User [{}] created with role ID [{}]", user.getUsername(), roleId);
    }

    @Override
    public void softDeleteUser(Long targetId, String requestingUsername) {
        User current = userRepository.findByUsername(requestingUsername).orElseThrow();
        User target = userRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (current.getId().equals(target.getId()))
            throw new IllegalArgumentException("You cannot delete your own account");
        if (target.isSystemUser())
            throw new IllegalArgumentException("System users cannot be deleted");

        target.softDelete(requestingUsername);
        userRepository.save(target);

        logger.info("User [{}] soft-deleted by [{}]", target.getUsername(), requestingUsername);
    }

    @Override
    public void updateUser(Long id, User formUser, Long roleId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.isSystemUser())
            throw new IllegalArgumentException("System users cannot be edited");
        if (!user.isEnabled())
            throw new IllegalArgumentException("Disabled users cannot be edited");

        user.setFirstName(formUser.getFirstName());
        user.setLastName(formUser.getLastName());
        user.setDateOfBirth(formUser.getDateOfBirth());
        user.setSex(formUser.getSex());
        user.setIdNumber(formUser.getIdNumber());

        if (roleId != null) {
            user.getRoles().clear();
            roleRepository.findById(roleId).ifPresent(user::addRole);
        }

        userRepository.save(user);
        logger.info("User [{}] updated", user.getUsername());
    }

    @Override
    public void changePassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.isSystemUser())
            throw new IllegalArgumentException("Cannot change password for system users");

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        logger.info("Password changed for user [{}]", user.getUsername());
    }

    @Override
    public void restoreUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.isSystemUser())
            throw new IllegalStateException("System users cannot be restored");

        user.restore();
        userRepository.save(user);

        logger.info("User [{}] restored", user.getUsername());
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Override
    public List<User> findAllActiveUsers() {
        return userRepository.findAllByEnabledTrue();
    }

    @Override
    public List<User> findAllDeletedUsers() {
        return userRepository.findAllByEnabledFalse();
    }
}