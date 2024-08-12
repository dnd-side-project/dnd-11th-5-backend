package com.odiga.fiesta.log.domain;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import java.time.LocalDateTime;

import com.odiga.fiesta.common.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Log extends BaseEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "log_id")
	private Long id;

	@Column(name = "user_id")
	@NotNull
	private Long userId; // 작성한 유저 아이디

	@NotNull
	private String address;

	@NotNull
	@Column(length = 30)
	private String title;

	@NotNull
	@Column(length = 300)
	private String content;

	@NotNull
	private LocalDateTime date; // 다녀온 날짜

	@Column(name = "is_public")
	@NotNull
	private Boolean isPublic;

	@Builder
	private Log(Long userId, String address, String title, String content, LocalDateTime date, Boolean isPublic) {
		this.userId = userId;
		this.address = address;
		this.title = title;
		this.content = content;
		this.date = date;
		this.isPublic = isPublic;
	}

	public static Log of(Long userId, String address, String title, String content, LocalDateTime date,
		Boolean isPublic) {
		return new Log(userId, address, title, content, date, isPublic);
	}
}
