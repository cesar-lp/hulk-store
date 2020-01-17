package com.todo1.hulkstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// TODO: create missing entity (contains dc, marvel, etc.)
// TODO: enable lombok's val in tests
// TODO: verify how to mock methods that implements spring conversion interface

@SpringBootApplication
public class HulkStoreApplication {
	public static void main(String[] args) {
		SpringApplication.run(HulkStoreApplication.class, args);
	}
}
