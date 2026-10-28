package com.example.demo.repository;

import com.example.demo.model.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findAllByOrderByFullNameAsc();
}
