package com.project.commerce.dto;

import com.project.commerce.domain.Role;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class SaveAuthority {

    private List<String> roles;

    public SaveAuthority(List<Role> role) {
        this.roles = role.stream().map(Role::getRole_name)
                .collect(Collectors.toList());
    }
}
