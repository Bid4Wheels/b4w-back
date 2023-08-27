package com.b4w.b4wback.controller;

import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.dto.auth.JwtResponse;
import com.b4w.b4wback.dto.auth.SignInRequest;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import com.b4w.b4wback.service.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class AuctionControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    private final String baseUrl = "/auction";

    @Autowired
    private UserService userService;

    private String token;
    @BeforeEach
    public void setup() {
        CreateUserDTO userDTO = new CreateUserDTO("Nico", "Borja", "bejero7623@dusyum.com",
                8493123, "1Afjfslkjfl");
        SignInRequest signInRequest=new SignInRequest(userDTO.getEmail(), userDTO.getPassword());
        userService.createUser(userDTO);
        token=authenticateAndGetToken(signInRequest);
    }

    private String authenticateAndGetToken(SignInRequest signInRequest) {
        String loginURL = "/auth/login";
        ResponseEntity<JwtResponse> response = restTemplate.exchange(loginURL, HttpMethod.POST,
                new HttpEntity<>(signInRequest), JwtResponse.class);
        return Objects.requireNonNull(response.getBody()).getToken();
    }


    @Test
    void Test001_AuctionControllerWhenReceivesValidAuctionShouldReturnStatusCREATED(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        CreateAuctionDTO auctionDTO=new CreateAuctionDTO(1L,"Subasta de automovil",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Toyota",
                "Corolla",150000,30000, GasType.GASOLINE,2022,"Silver",4, GearShiftType.AUTOMATIC);
        ResponseEntity<CreateAuctionDTO> createAuctionDTOResponseEntity= restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(auctionDTO,headers),CreateAuctionDTO.class);
        assert createAuctionDTOResponseEntity.getStatusCode().equals(HttpStatus.CREATED);
    }
    @Test
    void Test002_AuctionControllerWhenReceivesAuctionWithEmptyDescriptionShouldReturnStatusBAD_REQUEST(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        CreateAuctionDTO auctionDTO=new CreateAuctionDTO(1L,"",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Toyota",
                "Corolla",150000,30000, GasType.GASOLINE,2022,"Silver",4, GearShiftType.AUTOMATIC);
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(auctionDTO,headers),String.class);
        assert createAuctionDTOResponseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST);
    }
    @Test
    void Test003_AuctionControllerWhenReceivesAuctionWithEmptyBrandShouldReturnStatusBAD_REQUEST(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        CreateAuctionDTO auctionDTO=new CreateAuctionDTO(1L,"Subasta de automovil",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"",
                "Corolla",150000,30000, GasType.GASOLINE,2022,"Silver",4, GearShiftType.AUTOMATIC);
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(auctionDTO,headers),String.class);
        assert createAuctionDTOResponseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST);
    }

    @Test
    void Test004_AuctionControllerWhenReceivesAuctionWithEmptyModelShouldReturnStatusBAD_REQUEST(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        CreateAuctionDTO auctionDTO=new CreateAuctionDTO(1L,"Subasta de automovil",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Toyota",
                "",150000,30000, GasType.GASOLINE,2022,"Silver",4, GearShiftType.AUTOMATIC);
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(auctionDTO,headers),String.class);
        assert createAuctionDTOResponseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST);
    }

    @Test
    void Test005_AuctionControllerWhenReceivesAuctionWithEmptyBasePriceShouldReturnStatusBAD_REQUEST(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        CreateAuctionDTO auctionDTO=new CreateAuctionDTO(1L,"Subasta de automovil",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Toyota",
                "Corolla",null,30000, GasType.GASOLINE,2022,"Silver",4, GearShiftType.AUTOMATIC);
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(auctionDTO,headers),String.class);
        assert createAuctionDTOResponseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST);
    }

    @Test
    void Test005_AuctionControllerWhenReceivesAuctionWithLessThan24HoursBAD_REQUEST(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        LocalDateTime auctionDateTime = LocalDateTime.now().minus(23, ChronoUnit.HOURS);
        CreateAuctionDTO auctionDTO=new CreateAuctionDTO(1L,"Subasta de automovil",
                auctionDateTime,"Toyota",
                "Corolla",null,30000, GasType.GASOLINE,2022,"Silver",4, GearShiftType.AUTOMATIC);
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(auctionDTO,headers),String.class);
        assert createAuctionDTOResponseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST);
    }

    @Test
    void Test006_AuctionControllerWhenReceivesAuctionWithEmptyMilageShouldReturnStatusBAD_REQUEST(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        CreateAuctionDTO auctionDTO=new CreateAuctionDTO(1L,"Subasta de automovil",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Toyota",
                "Corolla",150000,null, GasType.GASOLINE,2022,"Silver",4, GearShiftType.AUTOMATIC);
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(auctionDTO,headers),String.class);
        assert createAuctionDTOResponseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST);
    }

    @Test
    void Test007_AuctionControllerWhenReceivesAuctionWithEmptyGasTypeShouldReturnStatusBAD_REQUEST(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        CreateAuctionDTO auctionDTO=new CreateAuctionDTO(1L,"Subasta de automovil",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Toyota",
                "Corolla",150000,30000, null,2022,"Silver",4, GearShiftType.AUTOMATIC);
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(auctionDTO,headers),String.class);
        assert createAuctionDTOResponseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST);
    }

    @Test
    void Test008_AuctionControllerWhenReceivesAuctionWithEmptyModelYearShouldReturnStatusBAD_REQUEST(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        CreateAuctionDTO auctionDTO=new CreateAuctionDTO(1L,"Subasta de automovil",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Toyota",
                "Corolla",150000,30000, GasType.GASOLINE,null,"Silver",4, GearShiftType.AUTOMATIC);
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(auctionDTO,headers),String.class);
        assert createAuctionDTOResponseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST);
    }

    @Test
    void Test009_AuctionControllerWhenReceivesAuctionWithEmptyColorShouldReturnStatusBAD_REQUEST(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        CreateAuctionDTO auctionDTO=new CreateAuctionDTO(1L,"Subasta de automovil",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Toyota",
                "Corolla",150000,30000, GasType.GASOLINE,2022,"",4, GearShiftType.AUTOMATIC);
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(auctionDTO,headers),String.class);
        assert createAuctionDTOResponseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST);
    }

    @Test
    void Test010_AuctionControllerWhenReceivesAuctionWithEmptyDoorsAmountShouldReturnStatusBAD_REQUEST(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        CreateAuctionDTO auctionDTO=new CreateAuctionDTO(1L,"Subasta de automovil",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Toyota",
                "Corolla",150000,30000, GasType.GASOLINE,2022,"Silver",null, GearShiftType.AUTOMATIC);
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(auctionDTO,headers),String.class);
        assert createAuctionDTOResponseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST);
    }
    @Test
    void Test011_AuctionControllerWhenReceivesAuctionWithEmptyGearShiftTypeShouldReturnStatusBAD_REQUEST(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        CreateAuctionDTO auctionDTO=new CreateAuctionDTO(1L,
                "Subasta de automovil",LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Toyota",
                "Corolla",150000,30000, GasType.GASOLINE,2022,"Silver",4, null);
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(auctionDTO,headers),String.class);
        assert createAuctionDTOResponseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST);
    }
    @Test
    void Test012AuctionControllerWhenReceivesAuctionWithMoreThan500CharactersInDescriptionShouldReturnStatusBAD_REQUEST(){
        String text="Este elegante automóvil deportivo combina un diseño aerodinámico con tecnología de vanguardia para brindarte una experiencia de conducción única. " +
                "Su motor potente proporciona un rendimiento excepcional en carretera, mientras que su interior espacioso y cómodo te permite disfrutar de viajes largos con estilo. " +
                "Además, sus características de seguridad avanzadas te brindan tranquilidad en cada trayecto. " +
                "Experimenta la emoción de conducir con este auto que combina elegancia, rendimiento y comodidad en un solo paquete.";
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        CreateAuctionDTO auctionDTO=new CreateAuctionDTO(1L, text,LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Toyota",
                "Corolla",150000,30000, GasType.GASOLINE,2022,"Silver",4, null);
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(auctionDTO,headers),String.class);
        assert createAuctionDTOResponseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST);
    }
}