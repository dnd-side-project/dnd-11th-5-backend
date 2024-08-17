package com.odiga.fiesta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odiga.fiesta.festival.controller.FestivalController;
import com.odiga.fiesta.festival.controller.FestivalStaticDataController;
import com.odiga.fiesta.festival.service.CategoryService;
import com.odiga.fiesta.festival.service.CompanionService;
import com.odiga.fiesta.festival.service.FestivalBookmarkService;
import com.odiga.fiesta.festival.service.FestivalService;
import com.odiga.fiesta.festival.service.MoodService;
import com.odiga.fiesta.festival.service.PriorityService;
import com.odiga.fiesta.log.controller.LogController;
import com.odiga.fiesta.log.service.LogService;

@ActiveProfiles("test")
@WithMockUser
@WebMvcTest(controllers = {
	// 사용하는 컨트롤러 여기에 추가
	FestivalController.class,
	FestivalStaticDataController.class,
	LogController.class
})
public abstract class ControllerTestSupport {

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
	protected LogService logService;

	@MockBean
	protected FestivalService festivalService;

	@MockBean
	protected FestivalBookmarkService festivalBookmarkService;
}
