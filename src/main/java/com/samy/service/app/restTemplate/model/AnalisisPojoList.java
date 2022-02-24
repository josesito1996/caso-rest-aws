package com.samy.service.app.restTemplate.model;

import java.util.ArrayList;
import java.util.List;

import com.samy.service.app.aws.AnalisisRiesgoPojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AnalisisPojoList {

	public List<AnalisisRiesgoPojo> list;

	public AnalisisPojoList() {
		list = new ArrayList<>();
	}

	public AnalisisPojoList(List<AnalisisRiesgoPojo> list) {
		this.list = list;
	}

}
