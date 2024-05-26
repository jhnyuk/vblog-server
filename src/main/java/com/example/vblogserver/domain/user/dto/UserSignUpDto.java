package com.example.vblogserver.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 자체 로그인 회원 가입 API에 RequestBody로 사용
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserSignUpDto {
	@NotBlank(message = "Login ID cannot be empty")
	@Pattern(regexp="^[a-zA-Z0-9]{4,12}$", message="Login ID must be 4 to 12 characters (Alphanumeric only)")
	private String loginId;

	@NotBlank(message = "Password cannot be empty")
	@Size(min=8, max=16, message="Password must be between 8 and 16 characters long")
	@Pattern(regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
		message="Password must contain at least one uppercase letter, one lowercase letter, one digit and a special character.")
	private String password;

	@NotBlank(message = "Username cannot be empty")
	@Size(min=2,max=8,message="Username should have at least 2 characters and maximum of 8 characters.")
	@Pattern(regexp="[가-힣a-zA-Z0-9]*$", message="Username should only contain Korean alphabets or English alphabets or numbers.")
	private String username;
}