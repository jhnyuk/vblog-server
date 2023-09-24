package com.example.vblogserver.domain.click.repository;

import java.util.List;

import com.example.vblogserver.domain.click.entity.Click;
import com.example.vblogserver.domain.user.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClickRepository  extends JpaRepository<Click, Long> {
	List<Click> findByUser(User user);
	Page<Click> findByUser(User user, Pageable pageable);
}
