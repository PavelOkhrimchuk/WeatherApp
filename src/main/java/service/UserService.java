package service;

import dto.user.UserLoginDto;
import dto.user.UserRegistrationDto;
import exception.InvalidCredentialsException;
import exception.UserAlreadyExistsException;
import exception.WeakPasswordException;
import model.User;
import org.mindrot.jbcrypt.BCrypt;
import repository.UserRepository;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(UserRegistrationDto userDto) throws UserAlreadyExistsException, WeakPasswordException {
        validateRegistration(userDto);

        String hashedPassword = BCrypt.hashpw(userDto.getPassword(), BCrypt.gensalt(12));
        User user = new User();
        user.setLogin(userDto.getLogin());
        user.setPassword(hashedPassword);

        return userRepository.save(user);
    }

    public User authenticateUser(UserLoginDto userDto) throws InvalidCredentialsException {
        User user = userRepository.findByLogin(userDto.getLogin())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid login or password."));

        if (!BCrypt.checkpw(userDto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid login or password.");
        }

        return user;
    }

    private void validateRegistration(UserRegistrationDto userDto) throws WeakPasswordException, UserAlreadyExistsException {
        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            throw new WeakPasswordException("Passwords do not match.");
        }

        if (userRepository.findByLogin(userDto.getLogin()).isPresent()) {
            throw new UserAlreadyExistsException("Login is already in use.");
        }

    }
}
