package com.college.placement.repository;

import com.college.placement.entity.Application;
import com.college.placement.entity.PlacementOffer;
import com.college.placement.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlacementOfferRepository extends JpaRepository<PlacementOffer, Long> {
    Optional<PlacementOffer> findByApplication(Application application);
    List<PlacementOffer> findByStudent(StudentProfile student);
    List<PlacementOffer> findByStudentUserUsername(String username);

    @Query("SELECT AVG(o.packageOffered) FROM PlacementOffer o")
    Double findAverageOfferedPackage();

    @Query("SELECT MAX(o.packageOffered) FROM PlacementOffer o")
    Double findHighestOfferedPackage();

    @Query("SELECT o FROM PlacementOffer o ORDER BY o.offerDate DESC")
    List<PlacementOffer> findAllOrderByOfferDateDesc();
}
