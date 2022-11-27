package com.batchprocess.batch.demo.config;

import com.batchprocess.batch.demo.entity.Customer;
import org.springframework.batch.item.ItemProcessor;


public class CustomerProcessor implements ItemProcessor<Customer,Customer> {

    @Override
    public Customer process(Customer customer) throws Exception {

        // u can filter customer data you want insert into db
//        if (customer.getCountry().equalsIgnoreCase("United States")){
//            return  customer;
//        }
//        else
//        {
//            return  null;
//        }
        return customer;
    }
}
