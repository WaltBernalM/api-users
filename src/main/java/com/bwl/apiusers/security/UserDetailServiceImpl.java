package com.bwl.apiusers.security;

import com.bwl.apiusers.exceptions.BaseNotFoundException;
import com.bwl.apiusers.models.User;
import com.bwl.apiusers.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        List<String> all = userRepository.findAll().stream().map(User::getUsername).toList();
        System.out.println(all + " " + userName);

        User user = userRepository
                .findOneByUsername(userName)
                .orElseThrow(() -> new BaseNotFoundException(User.class, "Username not found in database"));

        return new UserDetailsImpl(user);
    }
}