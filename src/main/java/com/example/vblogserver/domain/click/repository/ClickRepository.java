package com.example.vblogserver.domain.click.repository;


import com.example.vblogserver.domain.click.entity.Click;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClickRepository  extends JpaRepository<Click, Long> {
}
