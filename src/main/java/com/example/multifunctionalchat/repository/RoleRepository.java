package com.example.multifunctionalchat.repository;

import com.example.multifunctionalchat.domain.Role;
import com.example.multifunctionalchat.domain.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(RoleName name);
}
