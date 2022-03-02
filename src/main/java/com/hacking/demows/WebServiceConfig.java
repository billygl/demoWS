package com.hacking.demows;

import java.util.Properties;

import com.hacking.demows.exception.DetailSoapFaultDefinitionExceptionResolver;
import com.hacking.demows.exception.ServiceFaultException;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {
	@Bean
	public ServletRegistrationBean<MessageDispatcherServlet> 
        messageDispatcherServlet(ApplicationContext applicationContext) {
		MessageDispatcherServlet servlet = new MessageDispatcherServlet();
		servlet.setApplicationContext(applicationContext);
		servlet.setTransformWsdlLocations(true);
		return new ServletRegistrationBean<>(servlet, "/ws/*");
	}

	@Bean(name = "bank")
	public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema bankSchema) {
		DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
		wsdl11Definition.setPortTypeName("BankPort");
		wsdl11Definition.setLocationUri("/ws");
		wsdl11Definition.setTargetNamespace("http://hacking.com/demows");
		wsdl11Definition.setSchema(bankSchema);
		return wsdl11Definition;
	}

	@Bean
	public XsdSchema bankSchema() {
		return new SimpleXsdSchema(new ClassPathResource("bank.xsd"));
	}

	@Bean(name = "bankjwt")
	public DefaultWsdl11Definition bankjwtWsdl11Definition(XsdSchema bankjwtSchema) {
		DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
		wsdl11Definition.setPortTypeName("BankJWTPort");
		wsdl11Definition.setLocationUri("/ws");
		wsdl11Definition.setTargetNamespace("http://hacking.com/demows");
		wsdl11Definition.setSchema(bankjwtSchema);
		return wsdl11Definition;
	}
	
	@Bean(name="bankjwtSchema")
	public XsdSchema bankjwt() {
		return new SimpleXsdSchema(new ClassPathResource("bankjwt.xsd"));
	}

	@Bean
	public SoapFaultMappingExceptionResolver exceptionResolver() {
		SoapFaultMappingExceptionResolver exceptionResolver = 
			new DetailSoapFaultDefinitionExceptionResolver();

		SoapFaultDefinition faultDefinition = new SoapFaultDefinition();
		faultDefinition.setFaultCode(SoapFaultDefinition.SERVER);
		exceptionResolver.setDefaultFault(faultDefinition);

		Properties errorMappings = new Properties();
		errorMappings.setProperty(
			Exception.class.getName(), SoapFaultDefinition.SERVER.toString()
		);
		errorMappings.setProperty(
			ServiceFaultException.class.getName(), 
			SoapFaultDefinition.SERVER.toString()
		);
		exceptionResolver.setExceptionMappings(errorMappings);
		exceptionResolver.setOrder(1);
		return exceptionResolver;
	}
}
