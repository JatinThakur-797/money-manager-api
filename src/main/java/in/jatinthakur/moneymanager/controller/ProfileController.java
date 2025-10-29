package in.jatinthakur.moneymanager.controller;

import in.jatinthakur.moneymanager.dto.AuthDTO;
import in.jatinthakur.moneymanager.dto.ProfileDto;
import in.jatinthakur.moneymanager.entity.ProfileEntity;
import in.jatinthakur.moneymanager.service.AppUserDetailsService;
import in.jatinthakur.moneymanager.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    @PostMapping("/register")
    public ResponseEntity<?> registerProfile(@RequestBody ProfileDto profileDto) {

        System.out.println(profileDto);
        if(profileService.isUserAlreadyExists(profileDto.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "message", "User with email " + profileDto.getEmail() + " already exists"
            ));
        }
        ProfileDto registeredProfile = profileService.registerProfile(profileDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredProfile);
    }
    @GetMapping("/activate/{token}")
    public ResponseEntity<?> activateProfile(@RequestParam String token) {
        boolean result = profileService.activateProfile(token);
        if(result) {
            return ResponseEntity.ok("Profile Activated Successfully");
        }else{
            return ResponseEntity.badRequest().body("Activation Token is not Found or already uses");
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthDTO authDTO) {
        try{
            if(!profileService.isAccountActivate(authDTO.getEmail())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "message", "Account is Not active. Please activate you account first"
                ));
            }
              Map<String, Object> response =  profileService.authenticateAndGenerateToken(authDTO);
            return ResponseEntity.ok(response);

        }catch(Exception e){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }
}
