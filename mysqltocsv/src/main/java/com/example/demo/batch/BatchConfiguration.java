package com.example.demo.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost/reservation?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
        dataSource.setUsername("root");
        dataSource.setPassword("");

        return dataSource;
    }

    @Bean
    public JdbcCursorItemReader<Evenement> reader(){
        JdbcCursorItemReader<Evenement> reader = new JdbcCursorItemReader();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT * FROM evenement");
        reader.setRowMapper(new UserRowMapper());

        return reader;
    }

    public class UserRowMapper implements RowMapper<Evenement> {

        @Override
        public Evenement mapRow(ResultSet rs, int rowNum) throws SQLException {
            Evenement evenement=new Evenement();
            evenement.setId(rs.getString(1));
            evenement.setCodeclient(rs.getString(2));
            evenement.setCodesejour(rs.getString(3));
            evenement.setSomme(rs.getString(5));
            evenement.setDateEvent(rs.getString(6));
            evenement.setEvenementa(rs.getString(4));

            return evenement;
        }

    }



    @Bean
    public FlatFileItemWriter<Evenement> writer(){
        FlatFileItemWriter<Evenement> writer = new FlatFileItemWriter<Evenement>();
        writer.setResource(new ClassPathResource("event.csv"));
        writer.setLineAggregator(new DelimitedLineAggregator<Evenement>() {{
            setDelimiter(",");
            setFieldExtractor(new BeanWrapperFieldExtractor<Evenement>() {{
                setNames(new String[] { "id", "codeclient","codesejour","evenementa","somme","dateEvent" });
            }});
        }});

        return writer;
    }

    @Bean
    public EvenementItemProcessor processor(){
        return new EvenementItemProcessor();
    }


    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1").<Evenement, Evenement> chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }


    @Bean
    public Job exportUserJob() {
        return jobBuilderFactory.get("exportUserJob")
                .incrementer(new RunIdIncrementer())
                .flow(step1())
                .end()
                .build();
    }

}