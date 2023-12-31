package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.*;
import com.b4w.b4wback.dto.auth.SignInRequest;
import com.b4w.b4wback.service.interfaces.S3Service;
import com.b4w.b4wback.service.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.annotation.DirtiesContext;

import static com.b4w.b4wback.util.HttpEntityCreator.authenticateAndGetToken;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class S3ServiceTest {
    @Autowired
    private TestRestTemplate restTemplate;
    private CreateUserDTO userDTO;
    @Autowired
    private UserService userService;

    @Autowired
    private S3Service s3Service;

    private String token;

    @Value("${default-url-user}")
    private String userImageUrl;
    @BeforeEach
    public void setup() {
        userDTO = new CreateUserDTO("Nico", "Borja", "bejero7623@dusyum.com",
                "+5491154964341", "1Afjfslkjfl");
        SignInRequest signInRequest=new SignInRequest(userDTO.getEmail(), userDTO.getPassword());
        userService.createUser(userDTO);
        token = authenticateAndGetToken(signInRequest, restTemplate);
    }


    @Test
    void Test001_S3ServiceGetUploadURLWhenUserExistsAndIsAuthenticatedShouldReturnValidUrl(){
        String url= userService.createUrlForUploadingImage("Bearer "+token);
        assertTrue(url.startsWith(userImageUrl));
    }

    @Test
    void Test002_S3ServiceGetDownloadURLWhenUserExists(){
        String url= userService.createUrlForDownloadingImage(1L);
        assertTrue(url.startsWith(userImageUrl));
    }

    @Test
    void Test003_S3ServiceGetDownloadURLWhenUserNotExists(){
        String url= s3Service.generatePresignedDownloadImageUrl(userImageUrl+999999999,3600000);
        assertEquals(url,"default");
    }
}
