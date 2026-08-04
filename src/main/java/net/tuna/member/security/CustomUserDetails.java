package net.tuna.member.security;

import lombok.Getter;
import net.tuna.member.dto.MemberDto;
import net.tuna.member.dto.Role;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class CustomUserDetails implements UserDetails, CredentialsContainer {

    private final Long memberId;
    private final String name;
    private final String email;
    private String password;
    private Role role;
    private Map<String, Object> attributes;

    /* 일반 로그인 생성자 */
    public CustomUserDetails(MemberDto member) {
        this.memberId = member.getId();
        this.name = member.getName();
        this.email = member.getEmail();
        this.role = member.getRole();
        this.password = member.getPassword();
    }

    private GrantedAuthority getAuthority(Role role) {
        return new SimpleGrantedAuthority("ROLE_" + role.name());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorityList = new ArrayList<>();

        authorityList.add(getAuthority(this.role));

        return authorityList;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
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

    @Override
    public void eraseCredentials() {
        this.password = null;
    }
}