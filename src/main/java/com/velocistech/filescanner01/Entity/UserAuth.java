package com.velocistech.filescanner01.Entity;

import com.sun.istack.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
public class UserAuth implements UserDetails {
    @SequenceGenerator(
            name = "userauth_sequence",
            sequenceName = "userauth_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "userauth_sequence"
    )
    private Long id;
    private String firstname;
    private String lastname;
    @Column(unique = true)
    private String email;
    private String password;
    private Boolean locked=false;
    private Boolean enabled=false;
    @Enumerated(EnumType.STRING)
    private UserRole UserRole;

    public UserAuth(String firstname, String lastname,
                    String email, String password,
                    com.velocistech.filescanner01.Entity.UserRole userRole) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.locked = locked;
        this.enabled = enabled;
        UserRole = userRole;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority=new SimpleGrantedAuthority(UserRole.name());
        return Collections.singletonList(authority);
    }

    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public String getUsername() {
        return email;
    }

    public String getFirstName(){
        return firstname;
    }
    public String getLastName(){
        return lastname;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

}
