package com.golearn.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.golearn.dto.Tag;
import com.golearn.mapper.TagMapper;

@Service
public class TagServiceImpl implements TagService {

	@Autowired
	private TagMapper tagMapper;
	
	@Override
	public int insertTag(int cosNo, List<Integer> list) {
		return tagMapper.insertTag(cosNo, list);
	}

	@Override
	public List<Tag> findByCosNo(int cosNo) {
		return tagMapper.findByCosNo(cosNo);
	}

	@Override
	public int deleteTag(int cosNo, List<Integer> list) {
		return tagMapper.deleteTag(cosNo, list);
	}

}
