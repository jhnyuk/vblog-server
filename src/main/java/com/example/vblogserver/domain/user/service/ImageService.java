package com.example.vblogserver.domain.user.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImageService {
	private static final String USER_IMAGE_FOLDER = "userImage/";
	private final AmazonS3 s3Client;
	private final UserRepository userRepository;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	public String uploadFile(MultipartFile multipartFile) throws IOException {

		byte[] optimizedImage = optimizeImage(multipartFile);

		String fileName = UUID.randomUUID().toString() + "_" + multipartFile.getOriginalFilename();

		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(multipartFile.getContentType());
		objectMetadata.setContentLength(optimizedImage.length);

		InputStream optimizedImageInputStream = new ByteArrayInputStream(optimizedImage);

		s3Client.putObject(
				bucket,
				USER_IMAGE_FOLDER + fileName,
				optimizedImageInputStream,
				objectMetadata
		);

		return s3Client.getUrl(bucket, USER_IMAGE_FOLDER + fileName).toString();
	}

	private byte[] optimizeImage(MultipartFile multipartFile) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Thumbnails.of(multipartFile.getInputStream())
				.size(800, 800)
				.outputQuality(0.75)
				.toOutputStream(outputStream);
		return outputStream.toByteArray();
	}

	public String updateProfileImage(User user, MultipartFile file) throws IOException {
		if (user.getImageUrl() != null) {
			deleteProfileImage(user);
		}

		String imageUrl = uploadFile(file);
		user.setImageUrl(imageUrl);

		userRepository.save(user);

		return imageUrl;
	}

	public void deleteProfileImage(User user) {
		if (user.getImageUrl() != null) {
			URI url = URI.create(user.getImageUrl());
			String key = url.getPath().substring(1); // URL에서 버킷 이름 다음부터가 key

			s3Client.deleteObject(bucket, key);
			user.setImageUrl(null);
			userRepository.save(user);
		}
	}
}
