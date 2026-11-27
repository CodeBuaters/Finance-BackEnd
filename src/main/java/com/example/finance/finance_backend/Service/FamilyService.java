package com.example.finance.finance_backend.Service;

import java.security.SecureRandom;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.finance.finance_backend.Dto.FamilyResponse;
import com.example.finance.finance_backend.Model.Family;
import com.example.finance.finance_backend.Model.User;
import com.example.finance.finance_backend.Repository.FamilyRepository;
import com.example.finance.finance_backend.Repository.UserRepository;

@Service
public class FamilyService {

    // No look-alike characters (0/O, 1/I) so the code is easy to type
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private final SecureRandom random = new SecureRandom();

    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;

    public FamilyService(FamilyRepository familyRepository, UserRepository userRepository) {
        this.familyRepository = familyRepository;
        this.userRepository = userRepository;
    }

    // Null when the user is not in a family
    public FamilyResponse getFamily(String email) {
        Family family = getUser(email).getFamily();
        return family == null ? null : toResponse(family);
    }

    public FamilyResponse createFamily(String email, String name) {
        User user = getUserWithoutFamily(email);
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Family name is required");
        }
        return addMember(user, familyRepository.save(new Family(name.trim(), newCode())));
    }

    public FamilyResponse joinFamily(String email, String code) {
        User user = getUserWithoutFamily(email);
        Family family = familyRepository.findByCode(code == null ? "" : code.trim().toUpperCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid family code"));
        return addMember(user, family);
    }

    // The last member to leave deletes the family
    public void leaveFamily(String email) {
        User user = getUser(email);
        Family family = user.getFamily();
        if (family == null) {
            return;
        }
        user.setFamily(null);
        userRepository.save(user);
        if (userRepository.findByFamilyId(family.getId()).isEmpty()) {
            familyRepository.delete(family);
        }
    }

    // Whose data the dashboard shows: "family" = every member, anything else = only the user
    public List<Long> getUserIds(String email, String scope) {
        User user = getUser(email);
        if (!"family".equals(scope) || user.getFamily() == null) {
            return List.of(user.getId());
        }
        return userRepository.findByFamilyId(user.getFamily().getId()).stream().map(User::getId).toList();
    }

    private FamilyResponse addMember(User user, Family family) {
        user.setFamily(family);
        userRepository.save(user);
        return toResponse(family);
    }

    private FamilyResponse toResponse(Family family) {
        List<FamilyResponse.Member> members = userRepository.findByFamilyId(family.getId()).stream()
                .map(member -> new FamilyResponse.Member(member.getId(), member.getFirstName(), member.getLastName()))
                .toList();
        return new FamilyResponse(family.getId(), family.getName(), family.getCode(), members);
    }

    private String newCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            code.append(CODE_CHARS.charAt(random.nextInt(CODE_CHARS.length())));
        }
        return code.toString();
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private User getUserWithoutFamily(String email) {
        User user = getUser(email);
        if (user.getFamily() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You are already in a family");
        }
        return user;
    }
}
