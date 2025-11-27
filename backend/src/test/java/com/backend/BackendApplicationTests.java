package com.backend;

import com.backend.domain.member.member.entity.Member;
import com.backend.domain.member.member.service.MemberService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class BackendApplicationTests {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private MemberService memberService;

	@Test
	@DisplayName("회원가입 테스트")
	void t1() throws Exception {
		// given
		long before = memberService.count();

		// when
		ResultActions resultActions = mvc.perform(
				post("/api/v1/member/signup")
					.param("username", "user3")
					.param("password", "1234")
					.param("nickname", "user3")
					.param("email", "user3@test.com")
					.characterEncoding("UTF-8")
					.with(csrf())
			)
			.andDo(print());

		// then
		resultActions.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"));

		long after = memberService.count();
		assertThat(after).isEqualTo(before + 1);

		Member newMember = memberService.findByUsername("user3");
		assertThat(newMember).isNotNull();
		assertThat(newMember.getUsername()).isEqualTo("user3");
		assertThat(newMember.getEmail()).isEqualTo("user3@test.com");
	}
}

