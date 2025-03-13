package org.example.expert.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.client.ImageClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.response.ImageResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.entity.UserImage;
import org.example.expert.domain.user.repository.UserImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserImageService {
    private final UserImageRepository userImageRepository;
    private final ImageClient imageClient;

    private final static String PROFILE_FOLDER = "user/profile/";

    @Transactional
    public ImageResponse save(AuthUser authUser, MultipartFile file) {

        ImageResponse newProfileImage = imageClient.uploadFile(PROFILE_FOLDER, file);

        UserImage image = userImageRepository.findByUserId(authUser.getId()).orElse(null);
        if (image != null) {
            imageClient.deleteFile(image.getFileName());
            image.update(newProfileImage.getUrl(), newProfileImage.getFilaName());
        } else {
            UserImage userImage = new UserImage(User.fromAuthUser(authUser), newProfileImage.getUrl(), newProfileImage.getFilaName());
            userImageRepository.save(userImage);
        }

        return new ImageResponse(newProfileImage.getUrl(), newProfileImage.getFilaName());
    }

    @Transactional
    public void delete(AuthUser authUser) {
        UserImage userImage = userImageRepository.findByUserId(authUser.getId()).orElse(null);
        if (userImage != null) {
            userImageRepository.delete(userImage);
            imageClient.deleteFile(userImage.getFileName());
        }
    }
}
