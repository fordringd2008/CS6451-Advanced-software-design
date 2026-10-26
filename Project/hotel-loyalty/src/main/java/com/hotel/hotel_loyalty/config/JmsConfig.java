package com.hotel.hotel_loyalty.config;

import org.springframework.boot.artemis.autoconfigure.ArtemisConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.JacksonJsonMessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

/**
 * JMS and Artemis configuration.
 *
 * @author wukewei
 */
@Configuration
public class JmsConfig {

	/**
	 * JMS message converter: Java objects <-> JSON text messages.
	 * Uses Spring 7.0's new JacksonJsonMessageConverter (based on Jackson 3.x),
	 * replacing the deprecated MappingJackson2MessageConverter.
	 */
	@Bean
	public MessageConverter jacksonJmsMessageConverter() {
		JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_type");
		return converter;
	}

	/**
	 * Disables Artemis JMX management to fix the JDK 25 startup crash;
	 * also pins data directories under ./data/artemis so persistent: true takes effect.
	 * <p>Reason 1: Artemis 2.43.0 AuditLogger calls Subject.getSubject() when
	 * registering JMX MBeans, which is removed in JDK 25 and throws
	 * UnsupportedOperationException. Disabling JMX does not affect messaging.
	 * <p>Reason 2: Spring Boot embedded Artemis puts data directories in a random
	 * temp folder (...\Temp\D3B56.../artemis-data/), which changes on each restart
	 * and breaks persistence, so we explicitly set journal/bindings/large-messages/paging.
	 */
	@Bean
	public ArtemisConfigurationCustomizer artemisConfigurationCustomizer() {
		return configuration -> {
			configuration.setJMXManagementEnabled(false);
			configuration.setJournalDirectory("./data/artemis/journal");
			configuration.setBindingsDirectory("./data/artemis/bindings");
			configuration.setLargeMessagesDirectory("./data/artemis/largemessages");
			configuration.setPagingDirectory("./data/artemis/paging");
		};
	}
}