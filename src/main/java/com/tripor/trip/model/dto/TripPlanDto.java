package com.tripor.trip.model.dto;

import java.util.List;

public class TripPlanDto {
	int planId;
	String planName;
	List<String> tripList;
	String memberId;
	String planRegisterDate;

	public int getPlanId() {
		return planId;
	}

	public void setPlanId(int planId) {
		this.planId = planId;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public List<String> getTripList() {
		return tripList;
	}

	public void setTripList(List<String> tripList) {
		this.tripList = tripList;
	}

	public String getPlanRegisterDate() {
		return planRegisterDate;
	}

	public void setPlanRegisterDate(String planRegisterDate) {
		this.planRegisterDate = planRegisterDate;
	}

	public TripPlanDto() {
		super();
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public TripPlanDto(int planId, String planName, List<String> tripList, String memberId, String planRegisterDate) {
		super();
		this.planId = planId;
		this.planName = planName;
		this.tripList = tripList;
		this.memberId = memberId;
		this.planRegisterDate = planRegisterDate;
	}

	@Override
	public String toString() {
		return "TripPlanDto [planId=" + planId + ", planName=" + planName + ", tripList=" + tripList + ", memberId="
				+ memberId + ", planRegisterDate=" + planRegisterDate + "]";
	}

}
