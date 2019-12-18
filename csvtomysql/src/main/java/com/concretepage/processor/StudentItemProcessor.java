package com.concretepage.processor;

import org.springframework.batch.item.ItemProcessor;

import com.concretepage.model.Marksheet;
import com.concretepage.model.Student;

public class StudentItemProcessor implements ItemProcessor<Student, Marksheet> {
	@Override
	public Marksheet process(final Student student) throws Exception {
		int totalMarks = student.getSubjectAMark() + student.getSubjectBMark();
		System.out.println("Student name:" + student.getStdName() + " and Total marks:" + totalMarks);
		Marksheet marksheet = new Marksheet(student.getRollNum(), student.getStdName(), totalMarks);
		return marksheet;
	}
}
