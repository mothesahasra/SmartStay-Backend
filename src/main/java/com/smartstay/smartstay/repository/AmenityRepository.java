package com.smartstay.smartstay.repository;

import com.smartstay.smartstay.entity.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long> {

    Optional<Amenity> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    List<Amenity> findByNameContainingIgnoreCase(String name);

    List<Amenity> findByIdIn(List<Long> ids);
}
