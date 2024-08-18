package com.odiga.fiesta.festival.service;

import static com.odiga.fiesta.common.error.ErrorCode.*;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.festival.domain.FestivalBookmark;
import com.odiga.fiesta.festival.dto.response.FestivalBookmarkResponse;
import com.odiga.fiesta.festival.repository.FestivalBookmarkRepository;
import com.odiga.fiesta.festival.repository.FestivalRepository;
import com.odiga.fiesta.user.domain.User;
import com.odiga.fiesta.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FestivalBookmarkService {

	private final UserRepository userRepository;
	private final FestivalRepository festivalRepository;
	private final FestivalBookmarkRepository festivalBookmarkRepository;

	@Transactional
	public FestivalBookmarkResponse updateFestivalBookmark(User user, Long festivalId) {
		validateUser(user);
		validateFestival(festivalId);

		Optional<FestivalBookmark> optionalFestivalBookmark = festivalBookmarkRepository.findByUserIdAndFestivalId(
			user.getId(), festivalId);
		boolean isBookmarked = optionalFestivalBookmark.isPresent();

		optionalFestivalBookmark.ifPresentOrElse(festivalBookmarkRepository::delete,
			() -> festivalBookmarkRepository.save(FestivalBookmark.of(user.getId(), festivalId))
		);

		Long bookmarkCount = festivalBookmarkRepository.countByFestivalId(festivalId);

		return FestivalBookmarkResponse.builder()
			.festivalId(festivalId)
			.isBookmarked(!isBookmarked)
			.bookmarkCount(bookmarkCount)
			.build();
	}

	private void validateUser(User user) {
		userRepository.findById(user.getId())
			.orElseThrow(() -> new CustomException(USER_NOT_FOUND));
	}

	private void validateFestival(Long festivalId) {
		festivalRepository.findById(festivalId)
			.orElseThrow(() -> new CustomException(FESTIVAL_NOT_FOUND));
	}

}
