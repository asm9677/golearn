package com.golearn.service;

import java.util.List;

import com.golearn.dto.Tag;

public interface TagService {

	public int insertTag(int cosNo, List<Integer> list);
	public List<Tag> findByCosNo(int cosNo);
	public int deleteTag(int cosNo, List<Integer> list);
}
