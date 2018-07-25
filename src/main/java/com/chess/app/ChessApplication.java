/**
 * 
 */
package com.chess.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author OSVALDIMAR
 *
 */
@SpringBootApplication
@ComponentScan({"com.chess.app.rest", "com.chess.app"})
public class ChessApplication {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(ChessApplication.class, args);
	}

}
