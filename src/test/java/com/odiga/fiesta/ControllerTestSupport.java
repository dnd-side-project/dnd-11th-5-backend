package com.odiga.fiesta;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odiga.fiesta.auth.domain.UserAccount;
import com.odiga.fiesta.common.util.FileUtils;
import com.odiga.fiesta.festival.controller.FestivalController;
import com.odiga.fiesta.festival.controller.FestivalStaticDataController;
import com.odiga.fiesta.festival.service.CategoryService;
import com.odiga.fiesta.festival.service.CompanionService;
import com.odiga.fiesta.festival.service.FestivalBookmarkService;
import com.odiga.fiesta.festival.service.FestivalService;
import com.odiga.fiesta.festival.service.MoodService;
import com.odiga.fiesta.festival.service.PriorityService;
import com.odiga.fiesta.review.controller.ReviewController;
import com.odiga.fiesta.review.service.ReviewLikeService;
import com.odiga.fiesta.review.service.ReviewService;
import com.odiga.fiesta.user.domain.User;

@ActiveProfiles("test")
@WithAnonymousUser
@WebMvcTest(controllers = {
	// 사용하는 컨트롤러 여기에 추가
	FestivalController.class,
	FestivalStaticDataController.class,
	ReviewController.class
})
public abstract class ControllerTestSupport {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	// 모킹할 빈 추가
	@MockBean
	protected CategoryService categoryService;

	@MockBean
	protected CompanionService companionService;

	@MockBean
	protected MoodService moodService;

	@MockBean
	protected PriorityService priorityService;

	@MockBean
	protected FestivalService festivalService;

	@MockBean
	protected FestivalBookmarkService festivalBookmarkService;

	@MockBean
	protected FileUtils fileUtils;

	@MockBean
	protected ReviewService reviewService;

	@MockBean
	protected ReviewLikeService reviewLikeService;

	private User user;
	private UserAccount userAccount;

	@BeforeEach
	public void beforeEach() {
		this.mockMvc = MockMvcBuilders
			.webAppContextSetup(webApplicationContext)
			.addFilter(new CharacterEncodingFilter("utf-8", true))
			.build();

		SecurityContext context = SecurityContextHolder.getContext();

		user = mock(User.class);
		userAccount = mock(UserAccount.class);
		when(user.getId())
			.thenReturn(1L);
		when(user.getEmail())
			.thenReturn("fiesta@odiga.com");
		when(userAccount.getAccount())
			.thenReturn(user);

		Authentication authentication = new UsernamePasswordAuthenticationToken(userAccount, null,
			userAccount.getAuthorities());
		context.setAuthentication(authentication);
	}
}
