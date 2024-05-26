package com.example.vblogserver.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ResponseDto {
	private boolean result;
	private String message;

	public ResponseDto(boolean result, String message) {
		this.result = result;
		this.message = message;
	}

	public static ResponseDto createCheckIdResponse(boolean isDuplicated) {
		boolean result = !isDuplicated;
		String message = isDuplicated ? "해당 ID는 이미 사용 중입니다." : "해당 ID는 사용 가능합니다.";
		return new ResponseDto(result, message);
	}
}