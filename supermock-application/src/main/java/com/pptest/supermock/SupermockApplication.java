package com.pptest.supermock;

import com.pptest.supermock.resouces.IndexResouce;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class SupermockApplication extends Application<SupermockConfiguration> {

	public static void main(String[] args) throws Exception {
		new SupermockApplication().run(args);
	}
	
	@Override
	public String getName(){
		return "supermock";
	}

	@Override
	public void initialize(Bootstrap<SupermockConfiguration> bootstrap) {
		bootstrap.addBundle(new AssetsBundle("/assets"));

	}

	@Override
	public void run(SupermockConfiguration configuration, Environment env)
			throws Exception {
		env.jersey().register(IndexResouce.class);
	}

	

}
