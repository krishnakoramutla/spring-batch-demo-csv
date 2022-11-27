package com.batchprocess.batch.demo.config;

import com.batchprocess.batch.demo.entity.Customer;
import com.batchprocess.batch.demo.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class SpringBatchConfig {

    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    private CustomerRepository customerRepository;

    @Bean
    public FlatFileItemReader<Customer> reader() {
        FlatFileItemReader<Customer> fileItemReader = new FlatFileItemReader<>();
        fileItemReader.setResource(new FileSystemResource("src/main/resources/customers.csv"));
        fileItemReader.setLinesToSkip(1);
        fileItemReader.setName("csvReader");
        fileItemReader.setLineMapper(lineMapper());

        return  fileItemReader;

    }

    private LineMapper<Customer> lineMapper() {

        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob");

        BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Customer.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public  CustomerProcessor processor(){
        return  new CustomerProcessor();
    }

    @Bean
    public RepositoryItemWriter<Customer> writer(){
        RepositoryItemWriter<Customer> repositoryItemWriter = new RepositoryItemWriter<>();
        repositoryItemWriter.setRepository(customerRepository);
        repositoryItemWriter.setMethodName("save");
        return  repositoryItemWriter;
    }

    //asynchronous
//    @Bean
//    public Step step1(){
//        return  stepBuilderFactory.get("csv-step").
//                <Customer,Customer>chunk(10).reader(reader()).processor(processor()).writer(writer()).taskExecutor(taskExecutor()).build();
//    }

    //synchronous
    @Bean
    public Step step1(){
        return  stepBuilderFactory.get("csv-step").
                <Customer,Customer>chunk(10).reader(reader()).processor(processor()).writer(writer()).build();
    }

    @Bean
    public Job importJob(){
        return  jobBuilderFactory.get("import-customers").flow(step1()).end().build();
    }

    // use if you want in async (fast compare to sync)
    @Bean
    public TaskExecutor taskExecutor(){
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10);
        return  asyncTaskExecutor;
    }
}
