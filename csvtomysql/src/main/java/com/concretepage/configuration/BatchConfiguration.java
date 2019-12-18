package com.concretepage.configuration;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.concretepage.listener.JobCompletionListener;
import com.concretepage.model.Marksheet;
import com.concretepage.model.Student;
import com.concretepage.processor.StudentItemProcessor;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class BatchConfiguration extends DefaultBatchConfigurer {
	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Bean
	public LineMapper<Student> lineMapper() {
		DefaultLineMapper<Student> lineMapper = new DefaultLineMapper<Student>();
		lineMapper.setLineTokenizer(new DelimitedLineTokenizer() {
			{
				setNames(new String[] { "rollNum", "stdName", "subjectAMark", "subjectBMark" });
			}
		});
		lineMapper.setFieldSetMapper(new BeanWrapperFieldSetMapper<Student>() {
			{
				setTargetType(Student.class);
			}
		});
		return lineMapper;
	}

	@Bean
	public FlatFileItemReader<Student> reader() {
	    return new FlatFileItemReaderBuilder<Student>()
		  .name("studentItemReader")		
		  .resource(new ClassPathResource("student-marks.csv"))
		  .lineMapper(lineMapper())
		  .linesToSkip(1)
		  .build();
	}


	//inscrire ou update
	//lancer tous les jour a la meme heure
	//gestion des conflits
	//plusieur jobs

	@Transactional(propagation= Propagation.REQUIRED, rollbackFor=Exception.class)
	@Bean
	public JdbcBatchItemWriter<Marksheet> writer(DataSource dataSource) {


		return new JdbcBatchItemWriterBuilder<Marksheet>()
		   .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Marksheet>())
		   .sql("INSERT INTO marksheet (rollNum, studentName, totalMarks) VALUES (:rollNum, :stdName,:totalMarks)")
		   .dataSource(dataSource)
		   .build();
	}

	@Bean
	public ItemProcessor<Student, Marksheet> processor() {
		return new StudentItemProcessor();
	}

	@Bean
	public Job createMarkSheetJob(JobCompletionListener listener, Step step1) {
		return jobBuilderFactory
		  .get("createMarkSheetJob")
		  .incrementer(new RunIdIncrementer())
		  .listener(listener)
		  .flow(step1)
		  .end()
		  .build();
	}


	@Bean
	public Step step2(ItemReader<Student> reader, ItemWriter<Marksheet> writer,
	ItemProcessor<Student, Marksheet> processor) {
		return stepBuilderFactory
				.get("step2")
				.<Student, Marksheet>chunk(5)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.build();
	}

	@Bean
	public Step step1(ItemReader<Student> reader, ItemWriter<Marksheet> writer,
			ItemProcessor<Student, Marksheet> processor) {
		 return stepBuilderFactory
		   .get("step1")
		   .<Student, Marksheet>chunk(5)
		   .reader(reader)
		   .processor(processor)
		   .writer(writer)
		   .build();
	}

	@Bean
	public DataSource getDataSource() {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/testbatch");
		dataSource.setUsername("root");
		dataSource.setPassword("");
		return dataSource;
	}

	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Override
	public void setDataSource(DataSource dataSource) {
	}
}