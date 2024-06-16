package com.velocistech.filescanner01.Service;

import com.velocistech.filescanner01.Entity.UserAuth;
import com.velocistech.filescanner01.Registration.token.ConfirmationToken;
import com.velocistech.filescanner01.Registration.token.ConfirmationTokenService;
import com.velocistech.filescanner01.Repository.UserAuthRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service()
@AllArgsConstructor
public class UserAuthService implements UserDetailsService {
    @Autowired
    private final UserAuthRepository userAuthRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    private final static String USER_NOT_FOUND_MSG="User with email %s not Found";
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userAuthRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() ->new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email.toLowerCase())));
    }

    public String signUpUser(UserAuth userAuth) {
        boolean userExists = userAuthRepository
                .findByEmail(userAuth.getEmail())
                .isPresent();

        if (userExists) {
            // TODO check of attributes are the same and
            // TODO if email not confirmed send confirmation email.
            return ("Email already taken");
        }

        String encodedPassword = bCryptPasswordEncoder
                .encode(userAuth.getPassword());

        userAuth.setPassword(encodedPassword);

        userAuthRepository.save(userAuth);

        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(60),
                userAuth
        );

        confirmationTokenService.saveConfirmationToken(
                confirmationToken);

//        TODO: SEND EMAIL

        return token;
    }

    public int enableAppUser(String email) {
        return userAuthRepository.enableAppUser(email);
    }
}
