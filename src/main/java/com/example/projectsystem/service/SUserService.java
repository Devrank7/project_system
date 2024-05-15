package com.example.projectsystem.service;

import com.example.projectsystem.repository.Repo_User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SUserService implements UserDetailsService {
    @Autowired
    private Repo_User repoUser;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repoUser.findByName(username);
    }
}
