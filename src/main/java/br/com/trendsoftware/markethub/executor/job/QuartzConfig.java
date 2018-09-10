package br.com.trendsoftware.markethub.executor.job;

import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

@Configuration
public class QuartzConfig {

	private final Logger logger = LogManager.getLogger(this.getClass());

	@Autowired
	private ApplicationContext applicationContext;

	@PostConstruct
	public void init() {
		logger.debug("QuartzConfig initialized.");
	}

	@Bean
	public SchedulerFactoryBean quartzScheduler() {
		
		SchedulerFactoryBean quartzScheduler = new SchedulerFactoryBean();
		Properties p = new Properties();
		p.put("org.quartz.threadPool.threadCount", "1");
		
		quartzScheduler.setOverwriteExistingJobs(true);
		quartzScheduler.setQuartzProperties(p);
		quartzScheduler.setSchedulerName("jelies-quartz-scheduler");

		// custom job factory of spring with DI support for @Autowired!
		AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
		jobFactory.setApplicationContext(applicationContext);
		quartzScheduler.setJobFactory(jobFactory);

		Trigger[] triggers = { processTrigger().getObject() };
		quartzScheduler.setTriggers(triggers);

		return quartzScheduler;
	}

	@Bean
	public JobDetailFactoryBean processJob() {
		JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
		jobDetailFactory.setJobClass(B2WQueueConsumerJob.class);
		jobDetailFactory.setGroup("spring3-quartz");
		return jobDetailFactory;
	}

	@Bean
	public SimpleTriggerFactoryBean processTrigger() {
		SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
		trigger.setJobDetail(processJob().getObject());
		trigger.setStartDelay(10000);
    	trigger.setRepeatInterval(10000);
		trigger.setGroup("spring3-quartz");
		return trigger;
	}
	
}
