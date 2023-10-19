package com.b4w.b4wback.controller;

import com.b4w.b4wback.dto.*;
import com.b4w.b4wback.dto.auth.SignInRequest;
import com.b4w.b4wback.enums.AuctionStatus;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.repository.UserRepository;
import com.b4w.b4wback.service.interfaces.AuctionService;
import com.b4w.b4wback.service.interfaces.TagService;
import com.b4w.b4wback.service.interfaces.UserService;
import com.b4w.b4wback.util.AuctionGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.b4w.b4wback.util.HttpEntityCreator.authenticateAndGetToken;
import static com.b4w.b4wback.util.HttpEntityCreator.createHeaderWithToken;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class AuctionControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    private final String baseUrl = "/auction";

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private TagService tagService;
    @Autowired
    private AuctionService auctionService;
    private String token;
    private CreateAuctionDTO auctionDTO;

    private List<CreateUserDTO> userDTOs;
    @BeforeEach
    public void setup() {
        CreateUserDTO userDTO = new CreateUserDTO("Nico", "Borja", "bejero7623@dusyum.com",
                "+5491112345678", "1Afjfslkjfl");
        CreateUserDTO userDTO2 = new CreateUserDTO("Mico", "Vorja", "bejero@dusyum.com",
                "+5491112345678", "1Afjfslkjfl");
        SignInRequest signInRequest=new SignInRequest(userDTO.getEmail(), userDTO.getPassword());

        String psw = userDTO.getPassword();
        userService.createUser(userDTO);
        userDTO.setPassword(psw);

        psw = userDTO2.getPassword();
        userService.createUser(userDTO2);
        userDTO2.setPassword(psw);

        token = authenticateAndGetToken(signInRequest, restTemplate);

        auctionDTO= new CreateAuctionDTO(1L,"Subasta de automovil", "text",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Toyota",
                "Corolla",150000,30000, GasType.GASOLINE,2022,"Silver",
                4, GearShiftType.AUTOMATIC, null);

        userDTOs = new ArrayList<>();
        userDTOs.add(userDTO);
        userDTOs.add(userDTO2);
    }

    private HttpEntity<CreateBidDTO> createHttpEntity(CreateBidDTO bidDTO, CreateUserDTO userDTO){
        HttpHeaders headers = createHeaderWithToken(userDTO, restTemplate);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(bidDTO, headers);
    }

    @Test
    void Test001_AuctionControllerWhenReceivesValidAuctionShouldReturnStatusCREATED(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        ResponseEntity<CreateAuctionDTO> createAuctionDTOResponseEntity= restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(auctionDTO,headers),CreateAuctionDTO.class);
        assert createAuctionDTOResponseEntity.getStatusCode().equals(HttpStatus.CREATED);
    }
    @Test
    void Test002_AuctionControllerWhenReceivesAuctionWithEmptyTitleShouldReturnStatusBAD_REQUEST(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        auctionDTO.setTitle("");
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(auctionDTO,headers),String.class);
        assert createAuctionDTOResponseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST);
    }
    @Test
    void Test003_AuctionControllerWhenReceivesAuctionWithEmptyDescriptionShouldReturnStatusBAD_REQUEST(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        auctionDTO.setDescription("");
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(auctionDTO,headers),String.class);
        assert createAuctionDTOResponseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST);
    }
    @Test
    void Test004_AuctionControllerWhenReceivesAuctionWithEmptyBrandShouldReturnStatusBAD_REQUEST(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        auctionDTO.setBrand("");
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(auctionDTO,headers),String.class);
        assert createAuctionDTOResponseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST);
    }

    @Test
    void Test005_AuctionControllerWhenReceivesAuctionWithEmptyModelShouldReturnStatusBAD_REQUEST(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        auctionDTO.setModel(null);
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(auctionDTO,headers),String.class);
        assert createAuctionDTOResponseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST);
    }

    @Test
    void Test006_AuctionControllerWhenReceivesAuctionWithEmptyBasePriceShouldReturnStatusBAD_REQUEST(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        auctionDTO.setBasePrice(null);
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(auctionDTO,headers),String.class);
        assert createAuctionDTOResponseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST);
    }

    @Test
    void Test007_AuctionControllerWhenReceivesAuctionWithLessThan24HoursBAD_REQUEST(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        LocalDateTime auctionDateTime = LocalDateTime.now().minus(23, ChronoUnit.HOURS);
        auctionDTO.setDeadline(auctionDateTime);
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(auctionDTO,headers),String.class);
        assertEquals(HttpStatus.BAD_REQUEST, createAuctionDTOResponseEntity.getStatusCode());
    }

    @Test
    void Test008_AuctionControllerWhenReceivesAuctionWithEmptyMilageShouldReturnStatusBAD_REQUEST(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        auctionDTO.setMilage(null);
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(auctionDTO,headers),String.class);
        assert createAuctionDTOResponseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST);
    }

    @Test
    void Test009_AuctionControllerWhenReceivesAuctionWithEmptyGasTypeShouldReturnStatusBAD_REQUEST(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        auctionDTO.setGasType(null);
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(auctionDTO,headers),String.class);
        assert createAuctionDTOResponseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST);
    }

    @Test
    void Test010_AuctionControllerWhenReceivesAuctionWithEmptyModelYearShouldReturnStatusBAD_REQUEST(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        auctionDTO.setModelYear(null);
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(auctionDTO,headers),String.class);
        assert createAuctionDTOResponseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST);
    }

    @Test
    void Test011_AuctionControllerWhenReceivesAuctionWithEmptyColorShouldReturnStatusBAD_REQUEST(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        auctionDTO.setColor("");
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(auctionDTO,headers),String.class);
        assert createAuctionDTOResponseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST);
    }

    @Test
    void Test012_AuctionControllerWhenReceivesAuctionWithEmptyDoorsAmountShouldReturnStatusBAD_REQUEST(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        auctionDTO.setDoorsAmount(null);
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(auctionDTO,headers),String.class);
        assert createAuctionDTOResponseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST);
    }
    @Test
    void Test013_AuctionControllerWhenReceivesAuctionWithEmptyGearShiftTypeShouldReturnStatusBAD_REQUEST(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        auctionDTO.setGearShiftType(null);
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(auctionDTO,headers),String.class);
        assert createAuctionDTOResponseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST);
    }
    @Test
    void Test014_AuctionControllerWhenReceivesAuctionWithMoreThan500CharactersInDescriptionShouldReturnStatusBAD_REQUEST(){
        String text="Este elegante automóvil deportivo combina un diseño aerodinámico con tecnología de vanguardia para brindarte una experiencia de conducción única. " +
                "Su motor potente proporciona un rendimiento excepcional en carretera, mientras que su interior espacioso y cómodo te permite disfrutar de viajes largos con estilo. " +
                "Además, sus características de seguridad avanzadas te brindan tranquilidad en cada trayecto. " +
                "Experimenta la emoción de conducir con este auto que combina elegancia, rendimiento y comodidad en un solo paquete.";
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        auctionDTO.setDescription(text);
        ResponseEntity<String> createAuctionDTOResponseEntity= restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(auctionDTO,headers),String.class);
        assert createAuctionDTOResponseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST);
    }

    @Test
    void Test015_AuctionControllerWhenGetAuctionByIdShouldReturnStatusOK(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);

        restTemplate.exchange(baseUrl, HttpMethod.POST,new HttpEntity<>(auctionDTO,headers),CreateAuctionDTO.class);

        restTemplate.exchange("/bid", HttpMethod.POST, new HttpEntity<>(new CreateBidDTO(150000,2L,1L),headers),String.class);

        ResponseEntity<String> getAuctionDTOResponseEntity= restTemplate.exchange(baseUrl+"/1", HttpMethod.GET,
                new HttpEntity<>(headers),String.class);

        assertEquals(HttpStatus.OK,getAuctionDTOResponseEntity.getStatusCode());
    }

    @Test
    void Test016_AuctionControllerWhenGetAuctionByIdShouldReturnStatusNOT_FOUND(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        ResponseEntity<String> getAuctionDTOResponseEntity= restTemplate.exchange(baseUrl+"/1", HttpMethod.GET,
                new HttpEntity<>(headers),String.class);
        assertEquals(HttpStatus.NOT_FOUND,getAuctionDTOResponseEntity.getStatusCode());
    }
    @Test
    void Test017_UserControllerWhenGetAuctionsUserIDShouldReturnOK() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        restTemplate.exchange(baseUrl, HttpMethod.POST,new HttpEntity<>(auctionDTO, headers), String.class);

        ResponseEntity<String> getAuctionsResponse = restTemplate.exchange(baseUrl + "/user/1", HttpMethod.GET,
                new HttpEntity<>(headers), String.class);

        assertEquals(HttpStatus.OK, getAuctionsResponse.getStatusCode());
    }

    @Test
    void Test018_UserControllerWhenGetAuctionsUserIDShouldReturnNotFound() {
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);

        ResponseEntity<String> getAuctionsResponse = restTemplate.exchange(baseUrl + "/user/3", HttpMethod.GET,
                new HttpEntity<>(headers), String.class);

        assertEquals(HttpStatus.NOT_FOUND, getAuctionsResponse.getStatusCode());
    }

    @Test
    void Test019_AuctionControllerWhenAuctionFilterWithAllOkShouldReturnOK() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        //new AuctionGenerator(userRepository, tagService).generateAndSaveListOfAuctions(100, auctionRepository);

        ResponseEntity<String> getAuctionsResponse = restTemplate.exchange(baseUrl + "/filter", HttpMethod.POST,
                new HttpEntity<>(FilterAuctionDTO.builder().build(), headers), String.class);

        assertEquals(HttpStatus.OK, getAuctionsResponse.getStatusCode());
    }

    @Test

    void Test020_AuctionControllerWhenGenerateAuctionImageUrlWithAllOkShouldReturnOk() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        new AuctionGenerator(userRepository,tagService).generateAndSaveListOfAuctions(5, auctionRepository);
        ResponseEntity<List<String>> getAuctionsResponse = restTemplate.exchange(baseUrl + "/image-url/1", HttpMethod.POST,
                new HttpEntity<>(headers), new ParameterizedTypeReference<>() {
                });
        assertEquals(HttpStatus.OK, getAuctionsResponse.getStatusCode());
    }

    @Test
    void Test021_AuctionControllerWhenGenerateAuctionImageUrlAndAlreadySentAnUploadRequestShouldReturnBadRequest() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        new AuctionGenerator(userRepository,tagService).generateAndSaveListOfAuctions(5, auctionRepository);
        restTemplate.exchange(baseUrl + "/image-url/1", HttpMethod.POST, new HttpEntity<>(headers), new ParameterizedTypeReference<>() {});
        ResponseEntity<String> getAuctionsResponse2 = restTemplate.exchange(baseUrl + "/image-url/1", HttpMethod.POST,
                new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatus.BAD_REQUEST, getAuctionsResponse2.getStatusCode());}

    @Test
    void Test022_AuctionControllerWhenCreateAuctionWithTagsThenGetAuctionWithTags(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        List<String> tags = new ArrayList<>(List.of("tag1", "tag2", "tag3"));
        auctionDTO.setTags(tags);
        ResponseEntity<CreateAuctionDTO> createAuctionDTOResponseEntity= restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(auctionDTO,headers),CreateAuctionDTO.class);
        assertEquals(HttpStatus.CREATED, createAuctionDTOResponseEntity.getStatusCode());
        assertTrue(createAuctionDTOResponseEntity.getBody().getTags().containsAll(tags));
    }
    @Test
    void Test021_AuctionControllerWhenGetAuctionsEndingWithAllOkShouldReturnOK() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        new AuctionGenerator(userRepository, tagService).generateAndSaveListOfAuctions(100, auctionRepository);

        ResponseEntity<String> getAuctionsResponse = restTemplate.exchange(baseUrl + "/ending", HttpMethod.GET,
                new HttpEntity<>(headers), String.class);

        assertEquals(HttpStatus.OK, getAuctionsResponse.getStatusCode());
    }

    @Test
    void Test022_AuctionControllerWhenGetAuctionsNewWithAllOkShouldReturnOK() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        new AuctionGenerator(userRepository, tagService).generateAndSaveListOfAuctions(100, auctionRepository);

        ResponseEntity<String> getAuctionsResponse = restTemplate.exchange(baseUrl + "/new", HttpMethod.GET,
                new HttpEntity<>(headers), String.class);

        assertEquals(HttpStatus.OK, getAuctionsResponse.getStatusCode());
    }
    @Test
    void Test023_AuctionControllerWhenGetAuctionsBiddedByUserAndUserNotFoundShouldReturnNOTFOUND(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);

        ResponseEntity<String> getAuctionsResponse = restTemplate.exchange(baseUrl + "/bidder/3", HttpMethod.GET,
                new HttpEntity<>(headers), String.class);

        assertEquals(HttpStatus.NOT_FOUND, getAuctionsResponse.getStatusCode());
    }

    @Test
    void Test024_AuctionControllerWhenGetAuctionsBiddedByUserAndUserFoundShouldReturnOK(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);

        ResponseEntity<String> getAuctionsResponse = restTemplate.exchange(baseUrl + "/bidder/1", HttpMethod.GET,
                new HttpEntity<>(headers), String.class);

        assertEquals(HttpStatus.OK, getAuctionsResponse.getStatusCode());
    }

    @Test
    void Test025_AuctionControllerWhenDeleteAuctionWithAllOKShouldReturnOK(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        restTemplate.exchange(baseUrl, HttpMethod.POST, new HttpEntity<>(auctionDTO,headers),CreateAuctionDTO.class);
        ResponseEntity<String> deleteAuctionDTOResponseEntity= restTemplate.exchange(baseUrl+"/1", HttpMethod.DELETE,
                new HttpEntity<>(headers),String.class);
        assertEquals(HttpStatus.OK, deleteAuctionDTOResponseEntity.getStatusCode());
    }

    @Test
    void Test026_AuctionControllerWhenDeleteAuctionWithInvalidIdShouldReturnNotFound(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        ResponseEntity<String> deleteAuctionDTOResponseEntity= restTemplate.exchange(baseUrl+"/5", HttpMethod.DELETE,
                new HttpEntity<>(headers),String.class);
        assertEquals(HttpStatus.NOT_FOUND, deleteAuctionDTOResponseEntity.getStatusCode());
    }

    @Test
    void Test027_AuctionControllerWhenDeleteAuctionWithAuctionExpiredShouldReturnBadRequest(){
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        auctionDTO.setDeadline(LocalDateTime.now().minus(1, ChronoUnit.HOURS));
        //I have to instance the auction service because I can't create an auction with invalid deadline in the auction controller.
        auctionService.createAuction(auctionDTO);
        ResponseEntity<String> deleteAuctionDTOResponseEntity= restTemplate.exchange(baseUrl+"/1", HttpMethod.DELETE,
                new HttpEntity<>(headers),String.class);
        assertEquals(HttpStatus.BAD_REQUEST, deleteAuctionDTOResponseEntity.getStatusCode());
    }

    @Test
    void Test028_AuctionControllerWhenUserGetAuctionByIdGetHisHighestBidReturnGetAuctionSTOWithHighestBid(){
        int userNumber = 1;
        Long auctionId = auctionService.createAuction(auctionDTO).getAuctionId();
        Optional<User> user = userRepository.findByEmail(userDTOs.get(userNumber).getEmail());
        restTemplate.exchange("/bid", HttpMethod.POST, createHttpEntity(new CreateBidDTO(100000000, user.get().getId(),
                auctionId), userDTOs.get(userNumber)), String.class);

        HttpHeaders header = createHeaderWithToken(userDTOs.get(userNumber), restTemplate);
        header.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<?> getAuction = restTemplate.exchange(baseUrl+"/"+auctionId,
                HttpMethod.GET, new HttpEntity<>(header), String.class);

        assertEquals(HttpStatus.OK, getAuction.getStatusCode());
        //assertEquals(10, getAuction.getBody().getMyHighestBid());
    }

    @Test
    void Test029_AuctionServiceWhenFinishAuctionByTheWinnerThenHTTPOK(){
        int userNumber = 1;
        Long auctionId = auctionService.createAuction(auctionDTO).getAuctionId();
        Optional<User> user = userRepository.findByEmail(userDTOs.get(userNumber).getEmail());
        restTemplate.exchange("/bid", HttpMethod.POST, createHttpEntity(new CreateBidDTO(100000000, user.get().getId(),
                auctionId), userDTOs.get(userNumber)), String.class);

        Optional<Auction> auctionHelper = auctionRepository.findById(auctionId);
        if (auctionHelper.isEmpty()) throw new RuntimeException("Expected to found auction");
        auctionHelper.get().setStatus(AuctionStatus.AWATINGDELIVERY);
        auctionRepository.save(auctionHelper.get());

        HttpHeaders header = createHeaderWithToken(userDTOs.get(userNumber), restTemplate);
        header.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<?> modStatus = restTemplate.exchange(baseUrl+"/finish/"+auctionId,
                HttpMethod.PATCH, new HttpEntity<>(header), String.class);

        assertEquals(HttpStatus.OK, modStatus.getStatusCode());
    }
}
