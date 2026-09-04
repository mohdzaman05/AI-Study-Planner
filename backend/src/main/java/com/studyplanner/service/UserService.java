package com.studyplanner.service;

import com.studyplanner.dto.UpdateProfileRequest;
import com.studyplanner.dto.UserProfileResponse;
import com.studyplanner.exception.ResourceNotFoundException;
import com.studyplanner.model.User;
import com.studyplanner.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return new UserProfileResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getDailyStudyHours(),
                user.getPreferredStudyTime(),
                user.getCreatedAt()
        );
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setFullName(request.getFullName().trim());
        user.setDailyStudyHours(request.getDailyStudyHours());
        user.setPreferredStudyTime(request.getPreferredStudyTime().trim());

        User updated = userRepository.save(user);

        return new UserProfileResponse(
                updated.getId(),
                updated.getFullName(),
                updated.getEmail(),
                updated.getDailyStudyHours(),
                updated.getPreferredStudyTime(),
                updated.getCreatedAt()
        );
    }
}
