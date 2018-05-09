package com.go2wheel.mysqlbackup.jooq;

import javax.sql.DataSource;

import org.jooq.SQLDialect;
import org.jooq.TransactionListener;
import org.jooq.TransactionListenerProvider;
import org.jooq.TransactionProvider;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jooq.SpringTransactionProvider;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

//@Configuration
//@AutoConfigureAfter({ DataSourceAutoConfiguration.class,
//	HibernateJpaAutoConfiguration.class })
public class JooqConfiguration {
	
	@Autowired
	private DataSource dataSource;
	
	@Bean
	public DefaultConfiguration jooqDefaultConfiguration() {
	    DefaultConfiguration jooqConfiguration = new DefaultConfiguration();
	    jooqConfiguration.set(connectionProvider());
	    jooqConfiguration.set(new DefaultExecuteListenerProvider(exceptionTransformer()));
//	    jooqConfiguration.setTransactionProvider(transactionProvider());
	 
//	    String sqlDialectName = environment.getRequiredProperty("jooq.sql.dialect");
//	    SQLDialect dialect = SQLDialect.valueOf();
	    jooqConfiguration.set(SQLDialect.HSQLDB);
	 
	    return jooqConfiguration;
	}
	
	@Bean
	public DefaultDSLContext dsl() {
		return new DefaultDSLContext(jooqDefaultConfiguration());
	}
	
	@Bean
	public ExceptionTranslator exceptionTransformer() {
	    return new ExceptionTranslator();
	}
	
//	@Bean
//	public TransactionAwareDataSourceProxy transactionAwareDataSource() {
//	    return ;
//	}
	
//	@Bean
//	public TransactionProvider transactionProvider() {
//		return new SpringTransactionProvider(transactionManager());
//	}
	 
//	@Bean
//	public DataSourceTransactionManager transactionManager() {
//	    return new DataSourceTransactionManager(dataSource);
//	}
	 
	@Bean
	public DataSourceConnectionProvider connectionProvider() {
	    return new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy(dataSource));
	}
	 
	
//	ConnectionProvider
//	TransactionProvider
//	RecordMapperProvider
//	RecordUnmapperProvider
//	RecordListenerProvider
//	ExecuteListenerProvider
//	VisitListenerProvider
	
}