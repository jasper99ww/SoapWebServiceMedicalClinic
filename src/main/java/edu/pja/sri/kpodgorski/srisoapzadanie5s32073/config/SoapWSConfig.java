package edu.pja.sri.kpodgorski.srisoapzadanie5s32073.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;


@EnableWs
@Configuration
public class SoapWSConfig extends WsConfigurerAdapter {

    public static final String NAMESPACE_PATIENTS = "http://services.xyzclinic.com/patients";
    public static final String NAMESPACE_APPOINTMENTS = "http://services.xyzclinic.com/appointments";
    public static final String NAMESPACE_MEDICAL_RECORDS = "http://services.xyzclinic.com/medicalRecords";

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext context) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(context);
        servlet.setTransformSchemaLocations(true);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    @Bean(name="patients")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema patientsSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("PatientsPort");
        wsdl11Definition.setLocationUri("/ws/patients");
        wsdl11Definition.setTargetNamespace(NAMESPACE_PATIENTS);
        wsdl11Definition.setSchema(patientsSchema);
        return wsdl11Definition;
    }

    @Bean(name="appointments")
    public DefaultWsdl11Definition appointmentsWsdl(XsdSchema appointmentsSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("AppointmentsPort");
        wsdl11Definition.setLocationUri("/ws/appointments");
        wsdl11Definition.setTargetNamespace(NAMESPACE_APPOINTMENTS);
        wsdl11Definition.setSchema(appointmentsSchema);
        return wsdl11Definition;
    }

    @Bean(name="medicalRecords")
    public DefaultWsdl11Definition medicalRecordsWsdl(XsdSchema medicalRecordsSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("MedicalRecordsPort");
        wsdl11Definition.setLocationUri("/ws/medicalRecords");
        wsdl11Definition.setTargetNamespace(NAMESPACE_MEDICAL_RECORDS);
        wsdl11Definition.setSchema(medicalRecordsSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema patientsSchema() {
        return new SimpleXsdSchema(new ClassPathResource("patients.xsd"));
    }

    @Bean
    public XsdSchema appointmentsSchema() {
        return new SimpleXsdSchema(new ClassPathResource("appointments.xsd"));
    }

    @Bean
    public XsdSchema medicalRecordsSchema() {
        return new SimpleXsdSchema(new ClassPathResource("medicalRecords.xsd"));
    }
}

