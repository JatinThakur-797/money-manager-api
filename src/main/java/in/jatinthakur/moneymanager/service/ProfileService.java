package in.jatinthakur.moneymanager.service;

import in.jatinthakur.moneymanager.dto.AuthDTO;
import in.jatinthakur.moneymanager.dto.ProfileDto;
import in.jatinthakur.moneymanager.entity.ProfileEntity;
import in.jatinthakur.moneymanager.repository.ProfileRepository;
import in.jatinthakur.moneymanager.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final PasswordEncoder passwordEncoder;
    private final ProfileRepository profileRepo;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public ProfileDto registerProfile(ProfileDto profileDto) {
        System.out.println(profileDto);
        ProfileEntity profileEntity = toEntiy(profileDto);
        profileEntity.setActivationToken(UUID.randomUUID().toString());
        System.out.println(profileEntity);

        //Send Acctivation Email
        String activationLink = "http://localhost:8080/api/v1.0/activate?token=" + profileEntity.getActivationToken();
        String subject = "Activate you Money Manager account";
        String body = "Click on the following link to activate your Money Manager account." + activationLink;
        emailService.sendEmail(profileEntity.getEmail(), subject, body);
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
