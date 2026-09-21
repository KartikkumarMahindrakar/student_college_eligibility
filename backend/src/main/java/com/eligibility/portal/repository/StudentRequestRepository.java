package com.eligibility.portal.repository;

import com.eligibility.portal.entity.StudentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRequestRepository extends JpaRepository<StudentRequest, Long>, JpaSpecificationExecutor<StudentRequest> {
}
