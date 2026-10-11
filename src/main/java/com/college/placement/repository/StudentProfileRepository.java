package com.college.placement.repository;

import com.college.placement.entity.StudentProfile;
import com.college.placement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    Optional<StudentProfile> findByUser(User user);
    Optional<StudentProfile> findByUserUsername(String username);
    Optional<StudentProfile> findByUsn(String usn);
    boolean existsByUsn(String usn);
    List<StudentProfile> findByBranch(String branch);
    List<StudentProfile> findByGraduationYear(Integer graduationYear);

    long countByPlacedTrue();

    @Query("SELECT s.branch, COUNT(s) FROM StudentProfile s GROUP BY s.branch")
    List<Object[]> countStudentsByDepartment();

    @Query("SELECT s.branch, COUNT(s) FROM StudentProfile s WHERE s.placed = true GROUP BY s.branch")
    List<Object[]> countPlacedStudentsByDepartment();
}
