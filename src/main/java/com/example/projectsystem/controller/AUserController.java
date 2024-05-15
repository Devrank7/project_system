package com.example.projectsystem.controller;

import com.example.projectsystem.jwt.CookieUtils;
import com.example.projectsystem.jwt.JwtUtils;
import com.example.projectsystem.model.FUser;
import com.example.projectsystem.model.role.Role;
import com.example.projectsystem.repository.Repo_User;
import com.example.projectsystem.service.ServiceCacheProxyUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
@Slf4j
public class AUserController {

    private Repo_User repoUser;
    private PasswordEncoder passwordEncoder;
    private AuthenticationProvider provider;

    private JwtUtils jwtUtils;

    private ServiceCacheProxyUser serverCacheUser;

    @Autowired
    public AUserController(Repo_User repoUser, PasswordEncoder passwordEncoder, AuthenticationProvider provider, JwtUtils jwtUtils, ServiceCacheProxyUser serverCacheUser) {
        this.repoUser = repoUser;
        this.passwordEncoder = passwordEncoder;
        this.provider = provider;
        this.jwtUtils = jwtUtils;
        this.serverCacheUser = serverCacheUser;
    }

    @GetMapping("/admin")
    @ResponseBody
    public String test() {
        return "Hello Admin!";
    }

    @GetMapping("/a/hello")
    public String hello() {
        return "hi";
    }

    @GetMapping("/a/auth")
    public String auth(Model model) {
        model.addAttribute("usr", new FUser());
        return "auth";
    }

    @PostMapping("/a/auth")
    public String auth(@ModelAttribute("usr") FUser fUser) {
        fUser.setPassword(passwordEncoder.encode(fUser.getPassword()));
        fUser.setRole(Role.USER);
        repoUser.save(fUser);
        return "redirect:/user/hello";
    }

    @GetMapping("/profile")
    public String getProfile(Model model, HttpServletRequest request) {
        FUser fUser = serverCacheUser.callCache(jwtUtils.getUsername(CookieUtils.getCookie(request)));
        model.addAttribute("usr", fUser);
        return "profile";
    }

    @GetMapping("/all")
    public String getAllUserOnlyViewAdmin(Model model) {
        model.addAttribute("all", repoUser.findAll());
        return "all";
    }

    @GetMapping("/log")
    public String log() {
        return "log";
    }

    @PostMapping("/log")
    public String log(@RequestParam("username") String name, @RequestParam("password") String password, HttpServletResponse response) {
        provider.authenticate(new UsernamePasswordAuthenticationToken(name, password));
        String token = jwtUtils.generateToken(serverCacheUser.callCache(name));
        CookieUtils.setCookie(response, token);
        return "redirect:/user/profile";
    }

    @GetMapping("/profile/update")
    public String update(Model model, HttpServletRequest request) {
        FUser fUser = serverCacheUser.callCache(jwtUtils.getUsername(CookieUtils.getCookie(request)));
        model.addAttribute("usr", fUser);
        return "update";
    }

    @PostMapping("/profile/update")
    public String update(@ModelAttribute("usr") FUser fUser, HttpServletRequest request) {
        FUser fUser1 = serverCacheUser.callCache(jwtUtils.getUsername(CookieUtils.getCookie(request)));
        String name = fUser1.getName();
        fUser.setPassword(fUser1.getPassword());
        log.info("password = " + fUser.getPassword());
        fUser.setId(fUser1.getId());
        fUser.setRole(Role.USER);
        if (serverCacheUser.isCache(name)) {
            fUser.getProjects().addAll(fUser1.getProjects());
            serverCacheUser.putCache(name, fUser);
        }
        repoUser.save(fUser);
        return "redirect:/user/profile";
    }

    @PostMapping("/profile/delete")
    public String delete(HttpServletRequest request) {
        FUser fUser1 = serverCacheUser.callCache(jwtUtils.getUsername(CookieUtils.getCookie(request)));
        CookieUtils.deleteCookie(request);
        if (serverCacheUser.isCache(fUser1.getName())) {
            serverCacheUser.evict(fUser1.getName());
        }
        repoUser.delete(fUser1);
        return "redirect:/user/a/hello";
    }

    @GetMapping("/get/admin")
    public String getAdminka(Model model, HttpServletRequest request) {
        model.addAttribute("usr", serverCacheUser.callCache(jwtUtils.getUsername(CookieUtils.getCookie(request))));
        return "adminka";
    }

    @PostMapping("/get/admin")
    public String getAdmi(@RequestParam(value = "ch", defaultValue = "false") boolean check, HttpServletRequest request) {
        FUser fUser = serverCacheUser.callCache(jwtUtils.getUsername(CookieUtils.getCookie(request)));
        fUser.setRole(Role.ADMIN);
        if (serverCacheUser.isCache(fUser.getName())) {
            serverCacheUser.putCache(fUser.getName(), fUser);
        }
        repoUser.save(fUser);
        return "redirect:/user/profile";
    }


}
