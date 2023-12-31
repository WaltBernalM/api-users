package com.bwl.apiusers.security;

import com.bwl.apiusers.models.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Data
public class UserDetailsImpl implements UserDetails {

    private final User user;
    private Integer idApplication;
    private List<String> profileKeycodes;
    private Map<String, Object> profilePermissions;
    private  List<GrantedAuthority> authorities;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities; // Useful if user has authorities or roles
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Integer getId() {
        return user.getId();
    }

    public String getName() {
        return user.getName();
    }

    public Boolean getEnabled() {
        return user.getEnabled();
    }
}
