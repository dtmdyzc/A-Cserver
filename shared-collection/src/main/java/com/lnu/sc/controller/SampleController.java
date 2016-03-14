package com.lnu.sc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lnu.sc.service.HelloWorldService;

@RestController
public class SampleController {

	@Autowired
	private HelloWorldService helloWorldService;

	@RequestMapping("/home")
	public String helloWorld() {
		return this.helloWorldService.getHelloMessage();
	}
}
