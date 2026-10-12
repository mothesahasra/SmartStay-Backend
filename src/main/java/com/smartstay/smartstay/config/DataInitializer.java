package com.smartstay.smartstay.config;

import com.smartstay.smartstay.entity.*;
import com.smartstay.smartstay.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AmenityRepository amenityRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already initialized, skipping seed data.");
            return;
        }

        log.info("Seeding initial SmartStay demo data...");

        // 1. Users
        User admin = User.builder()
                .name("SmartStay Administrator")
                .email("admin@smartstay.com")
                .password(passwordEncoder.encode("admin12345"))
                .phone("+1 800 555 0199")
                .role(UserRole.ADMIN)
                .isActive(true)
                .build();

        User manager = User.builder()
                .name("Alex Morgan")
                .email("manager@smartstay.com")
                .password(passwordEncoder.encode("manager12345"))
                .phone("+1 305 555 0142")
                .role(UserRole.MANAGER)
                .isActive(true)
                .build();

        User guest = User.builder()
                .name("Sarah Jenkins")
                .email("guest@smartstay.com")
                .password(passwordEncoder.encode("guest12345"))
                .phone("+1 212 555 0188")
                .role(UserRole.GUEST)
                .isActive(true)
                .build();

        userRepository.saveAll(List.of(admin, manager, guest));

        // 2. Amenities
        Amenity wifi = Amenity.builder().name("Free High-Speed Wi-Fi").icon("📶").description("Ultra-fast 500Mbps wireless internet").build();
        Amenity pool = Amenity.builder().name("Swimming Pool").icon("🏊").description("Heated infinity pool with lounge bar").build();
        Amenity spa = Amenity.builder().name("Luxury Spa").icon("🧖").description("Full wellness center, sauna & massage therapy").build();
        Amenity gym = Amenity.builder().name("Fitness Center").icon("🏋️").description("24/7 equipped gym with trainers").build();
        Amenity restaurant = Amenity.builder().name("Gourmet Restaurant").icon("🍽️").description("Fine dining with Michelin-starred chefs").build();
        Amenity parking = Amenity.builder().name("Free Valet Parking").icon("🚗").description("Complimentary secure parking").build();
        Amenity ac = Amenity.builder().name("Climate Control").icon("❄️").description("Individual quiet air conditioning").build();
        Amenity bar = Amenity.builder().name("Rooftop Lounge & Bar").icon("🍸").description("Panoramic skyline view cocktails").build();
        Amenity service = Amenity.builder().name("24/7 Room Service").icon("🛎️").description("Round the clock in-room dining").build();
        Amenity pets = Amenity.builder().name("Pet Friendly").icon("🐾").description("Dedicated amenities for your four-legged friends").build();

        List<Amenity> allAmenities = amenityRepository.saveAll(List.of(wifi, pool, spa, gym, restaurant, parking, ac, bar, service, pets));
        Set<Amenity> resortAmenities = new HashSet<>(allAmenities.subList(0, 8));
        Set<Amenity> cityAmenities = new HashSet<>(List.of(wifi, gym, restaurant, ac, bar, service));

        // 3. Hotels
        Address miamiAddress = Address.builder()
                .street("100 Ocean Drive")
                .city("Miami")
                .state("FL")
                .country("United States")
                .zipCode("33139")
                .latitude(25.7781)
                .longitude(-80.1313)
                .build();

        Hotel hotel1 = Hotel.builder()
                .name("Grand Azure Ocean Resort")
                .description("Perched directly on South Beach, Grand Azure offers world-class ocean views, Michelin-starred seaside dining, and unparalleled luxury hospitality.")
                .starRating(5)
                .contactEmail("reservations@grandazure.com")
                .contactPhone("+1 305 555 4000")
                .manager(manager)
                .address(miamiAddress)
                .averageRating(4.9)
                .totalReviews(128)
                .imageUrls(List.of(
                        "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=1200&q=80",
                        "https://images.unsplash.com/photo-1582719508461-905c673771fd?auto=format&fit=crop&w=1200&q=80"
                ))
                .amenities(resortAmenities)
                .build();

        Address nyAddress = Address.builder()
                .street("742 5th Avenue")
                .city("New York")
                .state("NY")
                .country("United States")
                .zipCode("10019")
                .latitude(40.7638)
                .longitude(-73.9730)
                .build();

        Hotel hotel2 = Hotel.builder()
                .name("The Manhattan Skyline Hotel")
                .description("Situated steps from Central Park and Fifth Avenue, this architectural masterpiece blends modern sophistication with serene skyline comfort.")
                .starRating(5)
                .contactEmail("contact@manhattanskyline.com")
                .contactPhone("+1 212 555 9000")
                .manager(manager)
                .address(nyAddress)
                .averageRating(4.8)
                .totalReviews(94)
                .imageUrls(List.of(
                        "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=1200&q=80",
                        "https://images.unsplash.com/photo-1571896349842-33c89424de2d?auto=format&fit=crop&w=1200&q=80"
                ))
                .amenities(cityAmenities)
                .build();

        hotelRepository.saveAll(List.of(hotel1, hotel2));

        // 4. Rooms for Grand Azure
        Room r101 = Room.builder()
                .hotel(hotel1)
                .roomNumber("101")
                .roomType(RoomType.DELUXE)
                .description("Spacious oceanfront deluxe king room featuring private balcony, marble bathroom, and panoramic Atlantic views.")
                .capacity(2)
                .pricePerNight(new BigDecimal("280.00"))
                .isAvailable(true)
                .imageUrls(List.of("https://images.unsplash.com/photo-1618773928121-c32242e63f39?auto=format&fit=crop&w=1000&q=80"))
                .amenities(new HashSet<>(List.of(wifi, ac, service)))
                .build();

        Room r201 = Room.builder()
                .hotel(hotel1)
                .roomNumber("201")
                .roomType(RoomType.SUITE)
                .description("Presidential ocean suite with separate living parlor, soaking jacuzzi tub, and sunset terrace.")
                .capacity(4)
                .pricePerNight(new BigDecimal("520.00"))
                .isAvailable(true)
                .imageUrls(List.of("https://images.unsplash.com/photo-1591088398332-8a7791972843?auto=format&fit=crop&w=1000&q=80"))
                .amenities(new HashSet<>(List.of(wifi, ac, service, bar)))
                .build();

        // Rooms for The Manhattan Skyline
        Room r301 = Room.builder()
                .hotel(hotel2)
                .roomNumber("301")
                .roomType(RoomType.DOUBLE)
                .description("Sophisticated urban double room with floor-to-ceiling soundproof glass overlooking Central Park.")
                .capacity(2)
                .pricePerNight(new BigDecimal("340.00"))
                .isAvailable(true)
                .imageUrls(List.of("https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=1000&q=80"))
                .amenities(new HashSet<>(List.of(wifi, ac, service)))
                .build();

        Room r401 = Room.builder()
                .hotel(hotel2)
                .roomNumber("401")
                .roomType(RoomType.PENTHOUSE)
                .description("Exclusive penthouse suite with private wrap-around terrace, bespoke artwork, and 360-degree city views.")
                .capacity(4)
                .pricePerNight(new BigDecimal("790.00"))
                .isAvailable(true)
                .imageUrls(List.of("https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=1000&q=80"))
                .amenities(new HashSet<>(List.of(wifi, ac, service, bar, spa)))
                .build();

        roomRepository.saveAll(List.of(r101, r201, r301, r401));

        log.info("SmartStay demo data seeded successfully!");
    }
}
