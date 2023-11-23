package com.bwl.apiusers.repositories;

import com.bwl.apiusers.models.User;
import com.bwl.apiusers.models.UserApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface UserComposedRepository<T> extends JpaRepository<T, Integer> {
    List<T> findAllByIdUser(User idUser);
    Optional<T> findOneByIdUser(User user);
}
