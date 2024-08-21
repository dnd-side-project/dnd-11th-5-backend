package com.odiga.fiesta.user.domain;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import com.odiga.fiesta.festival.domain.Festival;
import com.odiga.fiesta.festival.domain.FestivalUserType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "`user_type`")
@Builder
public class UserType {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "user_type_id")
	private Long id;

	private String name;

	@Column(name = "profile_image", length = 1024)
	private String profileImage;

	public FestivalUserType toFestivalUserType(Festival festival) {
		return FestivalUserType.builder()
			.festivalId(festival.getId())
			.userTypeId(this.id)
			.build();
	}
}
