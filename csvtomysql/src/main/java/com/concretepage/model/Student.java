package com.concretepage.model;
public class Student {
	private long rollNum;
	private String stdName;
	private int subjectAMark;
	private int subjectBMark;
	public long getRollNum() {
		return rollNum;
	}
	public void setRollNum(long rollNum) {
		this.rollNum = rollNum;
	}
	public String getStdName() {
		return stdName;
	}
	public void setStdName(String stdName) {
		this.stdName = stdName;
	}
	public int getSubjectAMark() {
		return subjectAMark;
	}
	public void setSubjectAMark(int subjectAMark) {
		this.subjectAMark = subjectAMark;
	}
	public int getSubjectBMark() {
		return subjectBMark;
	}
	public void setSubjectBMark(int subjectBMark) {
		this.subjectBMark = subjectBMark;
	}
}
