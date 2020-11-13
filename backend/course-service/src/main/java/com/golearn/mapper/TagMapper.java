package com.golearn.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.golearn.dto.Tag;

@Mapper
public interface TagMapper {

	public int insertTag(@Param("cosNo") int cosNo, @Param("list") List<Integer> list);
	public List<Tag> findByCosNo(int cosNo);
	public int deleteTag(@Param("cosNo") int cosNo, @Param("list") List<Integer> list);
}
