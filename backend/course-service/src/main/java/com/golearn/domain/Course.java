package com.golearn.domain;

import com.golearn.dto.Tag;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name = "gl_course")
@Table(name = "gl_course")
public class Course {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cos_no")
	private long cosNo;
	@Column(name = "mbr_no")
	private long mbrNo;
	@Column(name = "cos_title")
	private String cosTitle;
	@Column(name = "cos_content")
	private String cosContent;
	@Column(name = "cos_thumbnail")
	private String cosThumbnail;
	@Column(name = "cos_banner")
	private String cosBanner;
	@Column(name = "reg_dt")
	private Date regDt;
	@Column(name = "chg_dt")
	private Date chgDt;

	@Transient
	private int likeCount;
	@Transient
	private int viewerCount;
	@Transient
	private List<Tag> tags;
	@Transient
	private String mbrNickname;

	public Course() {

	}

	public long getCosNo() {
		return cosNo;
	}

	public void setCosNo(long cosNo) {
		this.cosNo = cosNo;
	}

	public long getMbrNo() {
		return mbrNo;
	}

	public void setMbrNo(long mbrNo) {
		this.mbrNo = mbrNo;
	}

	public String getCosTitle() {
		return cosTitle;
	}

	public void setCosTitle(String cosTitle) {
		this.cosTitle = cosTitle;
	}

	public String getCosContent() {
		return cosContent;
	}

	public void setCosContent(String cosContent) {
		this.cosContent = cosContent;
	}

	public String getCosThumbnail() {
		return cosThumbnail;
	}

	public void setCosThumbnail(String cosThumbnail) {
		this.cosThumbnail = cosThumbnail;
	}

	public String getCosBanner() {
		return cosBanner;
	}

	public void setCosBanner(String cosBanner) {
		this.cosBanner = cosBanner;
	}

	public Date getRegDt() {
		return regDt;
	}

	public void setRegDt(Date regDt) {
		this.regDt = regDt;
	}

	public Date getChgDt() {
		return chgDt;
	}

	public void setChgDt(Date chgDt) {
		this.chgDt = chgDt;
	}

	public int getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}

	public int getViewerCount() {
		return viewerCount;
	}

	public void setViewerCount(int viewerCount) {
		this.viewerCount = viewerCount;
	}

	public List<Tag> getTags(){
		return tags;
	}

	public void setTags(List<Tag> tags){
		this.tags =tags;
	}

	public String getMbrNickname(){return mbrNickname;}

	public void setMbrNickname(String mbrNickname){this.mbrNickname = mbrNickname}

	@PrePersist
	void prePersist() {
		this.regDt = this.chgDt = new Date();
		this.cosThumbnail = "https://go-learn.s3.ap-northeast-2.amazonaws.com/course/thumbnail/course_thumbnail_default.png";
		this.cosBanner = "https://go-learn.s3.ap-northeast-2.amazonaws.com/course/banner/course_banner_default.png";
	}

	@PreUpdate
	void preUpdate() {
		this.chgDt = new Date();
	}

	@Override
	public String toString() {
		return "Course [cosNo=" + cosNo + ", mbrNo=" + mbrNo + ", cosTitle=" + cosTitle + ", cosContent=" + cosContent
				+ ", cosThumbnail=" + cosThumbnail + ", cosBanner=" + cosBanner + ", regDt=" + regDt + ", chgDt="
				+ chgDt + "]";
	}

}
