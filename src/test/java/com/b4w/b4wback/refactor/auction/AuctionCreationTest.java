package com.b4w.b4wback.refactor.auction;

import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.dto.auth.SignInRequest;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static com.b4w.b4wback.util.HttpEntityCreator.authenticateAndGetToken;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class AuctionCreationTest {
    @Autowired
    private TestRestTemplate restTemplate;
    private CreateAuctionDTO auctionDTO;
    private CreateUserDTO userDTO;

    @BeforeEach
    public void setUp() {
        auctionDTO= new CreateAuctionDTO(1L,"titulo", "vendo auto",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Toyota",
                "Corolla",1000,100, GasType.GASOLINE,2019,"Blanco",
                4, GearShiftType.MANUAL,new ArrayList<>(Arrays.asList("azul", "nuevo")));
        userDTO = new CreateUserDTO("Nico", "Borja", "test@mail.com",
                "+5491154964341", "1Afjfslkjfl");
        restTemplate.exchange("/user", HttpMethod.POST, createHttpEntity(userDTO), Void.class);
    }
    private HttpEntity<CreateUserDTO> createHttpEntity(CreateUserDTO createUserDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(createUserDTO, headers);
    }
    @Test
    void Test11_WhenCreateAuctionWithNoBrandThenRespondBadRequest() {
        auctionDTO.setBrand(null);
        String jwtToken= authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +jwtToken);
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange("/auction", HttpMethod.POST,
                new HttpEntity<>(auctionDTO, headers), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, createAuctionDTOResponseEntity.getStatusCode());
    }

    @Test
    void Test12_WhenCreateAuctionWithNoColorThenRespondBadRequest() {
        auctionDTO.setColor(null);
        String jwtToken= authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +jwtToken);
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange("/auction", HttpMethod.POST,
                new HttpEntity<>(auctionDTO, headers), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, createAuctionDTOResponseEntity.getStatusCode());
    }

    @Test
    void Test13_WhenCreateAuctionWithNoGasTypeThenRespondBadRequest() {
        auctionDTO.setGasType(null);
        String jwtToken= authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +jwtToken);
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange("/auction", HttpMethod.POST,
                new HttpEntity<>(auctionDTO, headers), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, createAuctionDTOResponseEntity.getStatusCode());
    }

    @Test
    void Test14_WhenCreateAuctionWithNoModelThenRespondBadRequest() {
        auctionDTO.setModel(null);
        String jwtToken= authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +jwtToken);
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange("/auction", HttpMethod.POST,
                new HttpEntity<>(auctionDTO, headers), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, createAuctionDTOResponseEntity.getStatusCode());
    }

    @Test
    void Test15_WhenCreateAuctionWithNoPriceThenRespondBadRequest() {
        auctionDTO.setBasePrice(null);
        String jwtToken= authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +jwtToken);
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange("/auction", HttpMethod.POST,
                new HttpEntity<>(auctionDTO, headers), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, createAuctionDTOResponseEntity.getStatusCode());
    }

    @Test
    void Test16_WhenCreateAuctionWithNoModelYearThenRespondBadRequest() {
        auctionDTO.setModelYear(null);
        String jwtToken= authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +jwtToken);
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange("/auction", HttpMethod.POST,
                new HttpEntity<>(auctionDTO, headers), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, createAuctionDTOResponseEntity.getStatusCode());
    }

    @Test
    void Test17_WhenCreateAuctionWithNoMilageThenRespondBadRequest() {
        auctionDTO.setMilage(null);
        String jwtToken= authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +jwtToken);
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange("/auction", HttpMethod.POST,
                new HttpEntity<>(auctionDTO, headers), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, createAuctionDTOResponseEntity.getStatusCode());
    }

    @Test
    void Test18_WhenCreateAuctionWithNoDoorsThenRespondBadRequest() {
        auctionDTO.setDoorsAmount(null);
        String jwtToken= authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +jwtToken);
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange("/auction", HttpMethod.POST,
                new HttpEntity<>(auctionDTO, headers), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, createAuctionDTOResponseEntity.getStatusCode());
    }

    @Test
    void Test19_WhenCreateAuctionWithNoGearShiftTypeThenRespondBadRequest() {
        auctionDTO.setGearShiftType(null);
        String jwtToken= authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +jwtToken);

        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange("/auction", HttpMethod.POST,
                new HttpEntity<>(auctionDTO, headers), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, createAuctionDTOResponseEntity.getStatusCode());
    }

//    @Test
//    void Test110_WhenCreateAuctionWithNoTagsThenRespondBadRequest() {
//        auctionDTO.setTags(null);
//        String jwtToken= authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
//        HttpHeaders headers= new HttpHeaders();
//        headers.set("Authorization","Bearer "+jwtToken);
//
//        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange("/auction", HttpMethod.POST,
//                new HttpEntity<>(auctionDTO, headers), String.class);
//
//        assertEquals(HttpStatus.BAD_REQUEST, createAuctionDTOResponseEntity.getStatusCode());
//    }

    @Test
    void Test111_WhenCreateAuctionWithNoTitleThenRespondBadRequest() {
        auctionDTO.setTitle(null);
        String jwtToken= authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer "+jwtToken);

        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange("/auction", HttpMethod.POST,
                new HttpEntity<>(auctionDTO, headers), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, createAuctionDTOResponseEntity.getStatusCode());
    }

    @Test
    void Test112_WhenCreateAuctionWithNoDescriptionThenRespondBadRequest() {
        auctionDTO.setDescription(null);
        String jwtToken= authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer "+jwtToken);

        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange("/auction", HttpMethod.POST,
                new HttpEntity<>(auctionDTO, headers), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, createAuctionDTOResponseEntity.getStatusCode());
    }

    @Test
    void Test113_WhenCreateAuctionWithTooLargeDescriptionThenRespondBadRequest() {
        auctionDTO.setDescription("Este elegante automóvil deportivo combina un diseño aerodinámico con tecnología de vanguardia para brindarte una experiencia de conducción única. " +
                "Su motor potente proporciona un rendimiento excepcional en carretera, mientras que su interior espacioso y cómodo te permite disfrutar de viajes largos con estilo. " +
                "Además, sus características de seguridad avanzadas te brindan tranquilidad en cada trayecto. " +
                "Experimenta la emoción de conducir con este auto que combina elegancia, rendimiento y comodidad en un solo paquete.");
        String jwtToken= authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization", "Bearer "+jwtToken);

        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange("/auction", HttpMethod.POST,
                new HttpEntity<>(auctionDTO, headers), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, createAuctionDTOResponseEntity.getStatusCode());
    }

    @Test
    void Test114_WhenCreateAuctionWithNoEndDateThenRespondBadRequest() {
        auctionDTO.setDeadline(null);
        String jwtToken= authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization", "Bearer "+jwtToken);

        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange("/auction", HttpMethod.POST,
                new HttpEntity<>(auctionDTO, headers), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, createAuctionDTOResponseEntity.getStatusCode());
    }

    @Test
    void Test115_WhenCreateAuctionWithDeadlineTooSoonRespondBadRequest(){
        auctionDTO.setDeadline(LocalDateTime.now().plusMinutes(1));
        String jwtToken= authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization", "Bearer "+jwtToken);

        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange("/auction", HttpMethod.POST,
                new HttpEntity<>(auctionDTO, headers), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, createAuctionDTOResponseEntity.getStatusCode());
    }

    @Test
    void Test117_WhenCreateAuctionWithAllValidRespondOK(){
        String jwtToken= authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization", "Bearer "+jwtToken);

        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange("/auction", HttpMethod.POST,
                new HttpEntity<>(auctionDTO, headers), String.class);

        assertEquals(HttpStatus.CREATED, createAuctionDTOResponseEntity.getStatusCode());
    }

}
