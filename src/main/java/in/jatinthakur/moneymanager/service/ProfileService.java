package in.jatinthakur.moneymanager.service;

import in.jatinthakur.moneymanager.dto.AuthDTO;
import in.jatinthakur.moneymanager.dto.ProfileDto;
import in.jatinthakur.moneymanager.entity.ProfileEntity;
import in.jatinthakur.moneymanager.repository.ProfileRepository;
import in.jatinthakur.moneymanager.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;



@Service
@RequiredArgsConstructor
public class ProfileService {
    @Value("${money.manager.backend.url}")
    private String activationUrl;

    private final PasswordEncoder passwordEncoder;
    private final ProfileRepository profileRepo;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final TemplateEngine templateEngine;

    public ProfileDto registerProfile(ProfileDto profileDto) {

        System.out.println(profileDto);
        ProfileEntity profileEntity = toEntiy(profileDto);
        profileEntity.setActivationToken(UUID.randomUUID().toString());
        System.out.println(profileEntity);

        String activationLink = activationUrl + "/api/v1.0/activate?token=" + profileEntity.getActivationToken();
        String subject = "Activate your Money Manager account";

        // 2. Create the HTML body right here using Thymeleaf.
        Context context = new Context();
        context.setVariable("userName", profileDto.getFullName());
        context.setVariable("activationLink", activationLink);
        String htmlBody = templateEngine.process("email/accountActivation", context);

        // 3. Call the simple 3-parameter sendEmail method.
        emailService.sendEmail(
                profileEntity.getEmail(),
                subject,
                htmlBody  // <-- This is the final HTML string.
        );

        // --- END: MODIFIED EMAIL LOGIC ---

        return toDto(profileRepo.save(profileEntity));
    }

    public ProfileEntity toEntiy(ProfileDto profileDto) {
        return ProfileEntity.builder()
                .id(profileDto.getId())
                .fullName(profileDto.getFullName())
                .email(profileDto.getEmail())
                .password(passwordEncoder.encode(profileDto.getPassword()))
                .profileImageUrl(profileDto.getProfileImageUrl())
                 .createdAt(profileDto.getCreatedAt())
                .updatedAt(profileDto.getUpdatedAt())
                .build();
    }
    public ProfileDto toDto(ProfileEntity profileEntity) {
        return ProfileDto.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }
    public boolean activateProfile(String activationToken) {
        return profileRepo.findByActivationToken(activationToken)
                .map(profile -> {
                    profile.setIsActive(true);
                    profileRepo.save(profile);
                    return true;
                }).orElse(false);
    }

    public boolean isAccountActivate(String email){
        return profileRepo.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }


    public ProfileEntity getCurrentProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      return profileRepo.findByEmail(authentication.getName()).orElseThrow(()-> new RuntimeException("Profile not found" + authentication.getName()));
    }

    public ProfileDto getPublicProfile(String email){
        ProfileEntity profileEntity = null;
        if(email == null){
            profileEntity = getCurrentProfile();
        }else{
            profileEntity = profileRepo.findByEmail(email).orElseThrow(()-> new RuntimeException("Profile not found" + email));
        }
        return toDto(profileEntity);

    }


    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));
            //Generate JWT Token
            String token = jwtUtil.generateToken(authDTO.getEmail());
            return Map.of(
                "token", token,
                "user" , getPublicProfile(authDTO.getEmail())
            );
        }catch (Exception e){
            throw new BadCredentialsException("Invalid username and password");
        }

    }
}
