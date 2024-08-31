package com.odiga.fiesta.user.domain;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import com.odiga.fiesta.common.domain.BaseEntity;
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
import lombok.ToString;

@Entity
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "user_type")
@Builder
@ToString
public class UserType extends BaseEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "user_type_id")
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "profile_image", length = 2048)
	private String profileImage;

	@Column(name = "card_image", length = 2048)
	private String cardImage;

	public FestivalUserType toFestivalUserType(Festival festival) {
		return FestivalUserType.builder()
			.festivalId(festival.getId())
			.userTypeId(this.id)
			.build();
	}
}
