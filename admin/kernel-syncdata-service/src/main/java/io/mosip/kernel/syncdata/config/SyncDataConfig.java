package io.mosip.kernel.syncdata.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.mosip.kernel.syncdata.constant.HibernatePersistenceConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
/**
 * SyncDataConfig
 * 
 * @author Bal Vikash Sharma
 * @author Raj Jha
 * @since 1.0.0
 * 
 *
 */
@Configuration
@EnableJpaRepositories(basePackages = {"io.mosip.kernel.syncdata.repository", "io.mosip.kernel.keymanagerservice.repository"},
		entityManagerFactoryRef = "syncDataEntityManager", transactionManagerRef = "syncDataTransactionManager")
public class SyncDataConfig {

	@Autowired
	private Environment env;
	
	private static final Logger logger = LoggerFactory.getLogger(SyncDataConfig.class);

	@Value("${hikari.maximumPoolSize:100}")
	private int maximumPoolSize;
	@Value("${hikari.validationTimeout:3000}")
	private int validationTimeout;
	@Value("${hikari.connectionTimeout:60000}")
	private int connectionTimeout;
	@Value("${hikari.idleTimeout:200000}")
	private int idleTimeout;
	@Value("${hikari.minimumIdle:0}")
	private int minimumIdle;

	@Bean
	public LocalContainerEntityManagerFactoryBean syncDataEntityManager() {
		LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactory.setDataSource(syncDataSource());
		entityManagerFactory.setPackagesToScan("io.mosip.kernel.syncdata.entity","io.mosip.kernel.keymanagerservice.entity");
		entityManagerFactory.setJpaPropertyMap(jpaProperties());
		entityManagerFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		return entityManagerFactory;
	}

	@Bean
	public DataSource syncDataSource() {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setDriverClassName(env.getProperty("spring.datasource.driverClassName"));
		hikariConfig.setJdbcUrl(env.getProperty("spring.master-datasource.jdbcUrl"));
		hikariConfig.setUsername(env.getProperty("spring.master-datasource.username"));
		hikariConfig.setPassword(env.getProperty("spring.master-datasource.password"));
		hikariConfig.setMaximumPoolSize(maximumPoolSize);
		hikariConfig.setValidationTimeout(validationTimeout);
		hikariConfig.setConnectionTimeout(connectionTimeout);
		hikariConfig.setIdleTimeout(idleTimeout);
		hikariConfig.setMinimumIdle(minimumIdle);
		HikariDataSource dataSource = new HikariDataSource(hikariConfig);
		return dataSource;
	}

	@Bean
	public PlatformTransactionManager syncDataTransactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(syncDataEntityManager().getObject());
		return transactionManager;
	}

	public Map<String, Object> jpaProperties() {
		HashMap<String, Object> jpaProperties = new HashMap<>();
		getProperty(jpaProperties, HibernatePersistenceConstant.HIBERNATE_HBM2DDL_AUTO,
				HibernatePersistenceConstant.UPDATE);
		getProperty(jpaProperties, HibernatePersistenceConstant.HIBERNATE_DIALECT, null);
		getProperty(jpaProperties, HibernatePersistenceConstant.HIBERNATE_SHOW_SQL, HibernatePersistenceConstant.TRUE);
		getProperty(jpaProperties, HibernatePersistenceConstant.HIBERNATE_FORMAT_SQL,
				HibernatePersistenceConstant.TRUE);
		getProperty(jpaProperties, HibernatePersistenceConstant.HIBERNATE_CONNECTION_CHAR_SET,
				HibernatePersistenceConstant.UTF8);
		getProperty(jpaProperties, HibernatePersistenceConstant.HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE,
				HibernatePersistenceConstant.FALSE);
		getProperty(jpaProperties, HibernatePersistenceConstant.HIBERNATE_CACHE_USE_QUERY_CACHE,
				HibernatePersistenceConstant.FALSE);
		getProperty(jpaProperties, HibernatePersistenceConstant.HIBERNATE_CACHE_USE_STRUCTURED_ENTRIES,
				HibernatePersistenceConstant.FALSE);
		getProperty(jpaProperties, HibernatePersistenceConstant.HIBERNATE_GENERATE_STATISTICS,
				HibernatePersistenceConstant.FALSE);
		getProperty(jpaProperties, HibernatePersistenceConstant.HIBERNATE_NON_CONTEXTUAL_CREATION,
				HibernatePersistenceConstant.FALSE);
		getProperty(jpaProperties, HibernatePersistenceConstant.HIBERNATE_CURRENT_SESSION_CONTEXT,
				HibernatePersistenceConstant.JTA);
		getProperty(jpaProperties, HibernatePersistenceConstant.HIBERNATE_EJB_INTERCEPTOR,
				HibernatePersistenceConstant.EMPTY_INTERCEPTOR);
		return jpaProperties;
	}

	private HashMap<String, Object> getProperty(HashMap<String, Object> jpaProperties, String property,
			String defaultValue) {
		/**
		 * if property found in properties file then add that interceptor to the jpa
		 * properties.
		 */
		if (property.equals(HibernatePersistenceConstant.HIBERNATE_EJB_INTERCEPTOR)) {
			try {
				if (env.containsProperty(property)) {
					jpaProperties.put(property, BeanUtils.instantiateClass(Class.forName(env.getProperty(property))));
				}
				/**
				 * We can add a default interceptor whenever we require here.
				 */
			} catch (BeanInstantiationException | ClassNotFoundException e) {
				logger.error(e.getMessage(),e);
			}
		} else {
			jpaProperties.put(property, env.containsProperty(property) ? env.getProperty(property) : defaultValue);
		}
		return jpaProperties;
	}

}
