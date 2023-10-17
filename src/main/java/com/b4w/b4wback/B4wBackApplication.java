package com.b4w.b4wback;

import com.b4w.b4wback.service.interfaces.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
public class B4wBackApplication {
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(B4wBackApplication.class, args);
		initializeUpdateAuctionStatusThread(context.getBean(AuctionService.class));
	}

	private static void initializeUpdateAuctionStatusThread(AuctionService service){
		Thread thread = new Thread(()->{
			while (true) {
				service.updateAuctionStatus();
				try {Thread.sleep(10000);}
				catch (InterruptedException e) {throw new RuntimeException(e);}
			}
		});
		thread.start();
	}
}
