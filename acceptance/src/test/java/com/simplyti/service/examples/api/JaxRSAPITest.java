package com.simplyti.service.examples.api;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.google.common.base.Joiner;
import com.simplyti.server.http.api.builder.jaxrs.JaxRsApiContext;

@Path("/jaxrs")
public class JaxRSAPITest {
	
	@GET
	public void getHello(JaxRsApiContext<String> ctx) {
		ctx.send("Hello!");
	}
	
	@GET
	@Path("blocking")
	public String getHello() {
		return "Hello!";
	}
	
	@POST
	@Path("echo")
	public void echo(JaxRsApiContext<APITestDTO> ctx, APITestDTO body) {
		ctx.send(body);
	}
	
	@GET
	@Path("/pathparams/{name}")
	public void getPathParams(JaxRsApiContext<String> ctx, @PathParam("name") String name) {
		ctx.send(name);
	}

	@GET
	@Path("queryparams/primitives")
	public void getQueryParamsPromitives(JaxRsApiContext<String> ctx, 
			@QueryParam("string") String string,
			@QueryParam("short") short shortValue,
			@QueryParam("int") int intValue,
			@QueryParam("double") double doubleValue,
			@QueryParam("long") long longValue,
			@QueryParam("float") float floatValue) {
		ctx.send(Joiner.on('|').join(string,shortValue,intValue,doubleValue,longValue,floatValue));
	}
	
	@GET
	@Path("queryparams")
	public void getQueryParams(JaxRsApiContext<String> ctx, 
			@QueryParam("string") String string,
			@QueryParam("short") Short shortValue,
			@QueryParam("int") Integer intValue,
			@QueryParam("double") Double doubleValue,
			@QueryParam("long") Long longValue,
			@QueryParam("float") Float floatValue) {
		ctx.send(Joiner.on('|').join(string,shortValue,intValue,doubleValue,longValue,floatValue));
	}
	
	@GET
	@Path("queryparams/list")
	public void getQueryParams(JaxRsApiContext<String> ctx, 
			@QueryParam("string") List<String> names) {
		ctx.send(Joiner.on(',').join(names));
	}
	
	@GET
	@Path("queryparams/default")
	public void getQueryParamDefault(JaxRsApiContext<String> ctx, 
			@QueryParam("string") @DefaultValue("Hello Default!") String name) {
		ctx.send(name);
	}
	
	@GET
	@Path("throwexception")
	public void throwException(JaxRsApiContext<String> ctx) {
		throw new RuntimeException("This is a test a error");
	}
	
	@GET
	@Path("failure")
	public void failure(JaxRsApiContext<String> ctx,@QueryParam("msg") String msg) {
		ctx.failure(new RuntimeException(msg));
	}
	
	@GET
	@Path("blocking/throwexception")
	public void throwException() {
		throw new RuntimeException("This is a test a error");
	}
	
	@GET
	@Path("headerparam")
	public void headerParam(JaxRsApiContext<String> ctx, @HeaderParam("x-my-header") String myHeader) {
		ctx.send(myHeader);
	}

}
