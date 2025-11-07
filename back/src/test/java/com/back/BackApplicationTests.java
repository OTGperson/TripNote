package com.back;

import com.back.domain.user.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles({"test"})
class BackApplicationTests {

	@Autowired
	private MockMvc mvc;
	@Autowired
	private UserService userService;

	@Test
	@DisplayName("회원가입 테스트")
	void t1() throws Exception {
		ResultActions resultActions = mvc.perform(
			multipart("/api/v1/user/signup")   // ← 슬래시 추가
				.param("username", "user1")
				.param("password", "1234")
				.param("email", "user5@test.com")
				.characterEncoding("UTF-8")
		).andDo(print());

	}

}
