package com.intrasoft.csp.conf.server.repository;


import com.intrasoft.csp.conf.server.domain.entities.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

    public Module findByName(String name);


//
//    @Transactional
//    Module removeByName(String name);

}

