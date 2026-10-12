package com.smartstay.smartstay.repository;

import com.smartstay.smartstay.entity.Hotel;
import com.smartstay.smartstay.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    List<Hotel> findByManager(User manager);

    List<Hotel> findByManagerId(Long managerId);

    @Query("SELECT h FROM Hotel h WHERE LOWER(h.address.city) = LOWER(:city)")
    Page<Hotel> findByCity(@Param("city") String city, Pageable pageable);

    @Query("SELECT h FROM Hotel h WHERE " +
            "(:city IS NULL OR LOWER(h.address.city) = LOWER(:city)) AND " +
            "(:state IS NULL OR LOWER(h.address.state) = LOWER(:state)) AND " +
            "(:country IS NULL OR LOWER(h.address.country) = LOWER(:country)) AND " +
            "(:minRating IS NULL OR h.averageRating >= :minRating) AND " +
            "(:starRating IS NULL OR h.starRating = :starRating)")
    Page<Hotel> searchHotels(
            @Param("city") String city,
            @Param("state") String state,
            @Param("country") String country,
            @Param("minRating") Double minRating,
            @Param("starRating") Integer starRating,
            Pageable pageable
    );

    @Query("SELECT h FROM Hotel h WHERE LOWER(h.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(h.address.city) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(h.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Hotel> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT h FROM Hotel h JOIN h.amenities a WHERE a.id IN :amenityIds " +
            "GROUP BY h HAVING COUNT(DISTINCT a.id) = :amenityCount")
    Page<Hotel> findByAmenities(@Param("amenityIds") List<Long> amenityIds,
                                 @Param("amenityCount") long amenityCount,
                                 Pageable pageable);

    @Query("SELECT h FROM Hotel h JOIN h.rooms r WHERE r.pricePerNight BETWEEN :minPrice AND :maxPrice")
    Page<Hotel> findByPriceRange(@Param("minPrice") BigDecimal minPrice,
                                  @Param("maxPrice") BigDecimal maxPrice,
                                  Pageable pageable);
}
