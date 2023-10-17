package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.*;
import com.b4w.b4wback.enums.AuctionStatus;
import com.b4w.b4wback.exception.BadRequestParametersException;
import com.b4w.b4wback.exception.EntityNotFoundException;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.Bid;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.repository.BidRepository;
import com.b4w.b4wback.repository.UserRepository;
import com.b4w.b4wback.service.interfaces.JwtService;
import com.b4w.b4wback.service.interfaces.MailService;
import com.b4w.b4wback.service.interfaces.S3Service;
import com.b4w.b4wback.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;
import java.util.Random;


@Service
@Validated
public class UserServiceImp implements UserService {
    private final JwtService jwtService;
    @Value("${sendMail.Boolean.Value}")
    private boolean sendMail;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final Random Random = new Random();

    @Value("${aws.users.objectKey}")
    private String usersObjectKey;

    @Value("${expiration.time.image.url}")
    private Integer expirationTimeImageUrl;
    private final S3Service s3Service;
    public UserServiceImp(UserRepository userRepository, MailService mailService, S3Service s3Service, JwtService jwtService, AuctionRepository auctionRepository, BidRepository bidRepository) {
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.s3Service=s3Service;
        this.jwtService=jwtService;
        this.auctionRepository=auctionRepository;
        this.bidRepository = bidRepository;
    }
    @Override
    public User createUser(CreateUserDTO createUserDTO) {
        //Added encode method for the password.
        if (createUserDTO.getPassword() == null) {
            throw new DataIntegrityViolationException("Password must not be null");
        }
        createUserDTO.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));
        String email = createUserDTO.getEmail();
        User newUser = userRepository.save(new User(createUserDTO));
        if (sendMail) {
            mailService.sendMail(email, "Welcome to B4W", "Welcome to B4W");
            email = "";
        }
        return newUser;
    }
    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User with Id "+id+" not found"));
        if(Objects.equals(user.getName(), "Deleted")){
            throw new EntityNotFoundException("User with "+id+" not found");
        }
        return UserDTO.builder().name(user.getName()).lastName(user.getLastName()).email(user.getEmail()).
                phoneNumber(user.getPhoneNumber()).imgURL(createUrlForDownloadingImage(id)).build();
    }

    @Override
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            if (user.isDeleted()) {
                throw new UsernameNotFoundException("User not found");
            }
            return user;
        };
    }

    @Override
    public void modifyUser(long id, ModifyUserDTO modifyUserDTO){
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User with Id "+id+" not found"));
        user.modifyUser(modifyUserDTO);
        userRepository.save(user);
    }

    @Override
    public Integer createPasswordCodeForId(PasswordChangerDTO userDTO) {
        User user = userRepository.findByEmail(userDTO.getEmail()).orElseThrow(() -> new EntityNotFoundException("User with email "+userDTO.getEmail()+" not found"));
        Integer passwordCode = Random.nextInt(100000, 999999);
        user.setPasswordCode(passwordCode);
        userRepository.save(user);
        mailService.sendMail(userDTO.getEmail(), "Password change code", "Your password change code is: " + passwordCode);
        return passwordCode;
    }

    @Override
    public void checkPasswordCode(GetPasswordCodeDTO passwordCodeDTO) {
        User user = userRepository.findByEmail(passwordCodeDTO.getEmail()).orElseThrow(() -> new EntityNotFoundException("User with email "+passwordCodeDTO.getEmail()+" not found"));
        boolean check = Objects.equals(user.getPasswordCode(), passwordCodeDTO.getPasswordCode());
        if (!check) {
            throw new BadRequestParametersException("Password code does not match");
        }
        user.setPasswordCode(null);
        userRepository.save(user);
    }


    @Override
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        User user = userRepository.findByEmail(changePasswordDTO.getEmail()).orElseThrow(() -> new EntityNotFoundException("User with email "+changePasswordDTO.getEmail() +" not found"));
        user.setPassword(passwordEncoder.encode(changePasswordDTO.getPassword()));
        userRepository.save(user);
    }

    @Override
    public String createUrlForUploadingImage(String token){
        String email= jwtService.extractUsername(token.substring(7));
        long userId= userRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("User with email "+ email +" not found")).getId();
        return s3Service.generatePresignedUploadImageUrl(usersObjectKey+userId,expirationTimeImageUrl);
    }
    @Override
    public String createUrlForDownloadingImage(Long userID) {
        String userUrl=usersObjectKey + userID;
        return s3Service.generatePresignedDownloadImageUrl(userUrl,expirationTimeImageUrl);
    }

    @Override
    public void deleteUser(String token) {
        long id= jwtService.extractId(token.substring(7));
        User user= userRepository.findById(id).orElseThrow(()->new EntityNotFoundException("User with Id "+ id +" not found"));
        user.setName("Deleted");
        user.setLastName("Deleted");
        user.setPhoneNumber("Deleted");
        user.setDeleted(true);
        userRepository.save(user);
        List<Auction> auctionsList = auctionRepository.findByUser(user);
        auctionRepository.deleteAll(auctionsList);
        List<Bid> bidsList = bidRepository.findAllByUserAndStatusOpen(id, AuctionStatus.OPEN);
        bidRepository.deleteAll(bidsList);
    }
}
