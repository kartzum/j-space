package io.rdlab.hn.dbqueue;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import ru.yoomoney.tech.dbqueue.api.*;
import ru.yoomoney.tech.dbqueue.api.impl.MonitoringQueueProducer;
import ru.yoomoney.tech.dbqueue.api.impl.ShardingQueueProducer;
import ru.yoomoney.tech.dbqueue.api.impl.SingleQueueShardRouter;
import ru.yoomoney.tech.dbqueue.config.*;
import ru.yoomoney.tech.dbqueue.config.impl.LoggingTaskLifecycleListener;
import ru.yoomoney.tech.dbqueue.config.impl.LoggingThreadLifecycleListener;
import ru.yoomoney.tech.dbqueue.settings.*;
import ru.yoomoney.tech.dbqueue.spring.dao.SpringDatabaseAccessLayer;

import javax.sql.DataSource;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;

import static java.util.Collections.singletonList;

@Configuration
public class DbQueueConfiguration {

    @Bean
    public QueueConsumer<SimpleTask> simpleTaskQueueConsumer(
            QueueSettings queueSettings,
            @Qualifier("simpleTaskQueueId") QueueId queueId,
            TaskPayloadTransformer<SimpleTask> transformer
    ) {
        QueueConfig config = new QueueConfig(
                QueueLocation.builder().withTableName("queue_tasks").withQueueId(queueId).build(),
                queueSettings
        );
        return new QueueConsumer<>() {
            @Override
            public TaskExecutionResult execute(Task<SimpleTask> task) {
                return TaskExecutionResult.finish();
            }

            @Override
            public QueueConfig getQueueConfig() {
                return config;
            }

            @Override
            public TaskPayloadTransformer<SimpleTask> getPayloadTransformer() {
                return transformer;
            }
        };
    }

    @Bean
    public QueueProducer<SimpleTask> simpleTaskQueueProducer(
            QueueSettings queueSettings,
            @Qualifier("simpleTaskQueueId") QueueId queueId,
            @Qualifier("queueShardMain") QueueShard<SpringDatabaseAccessLayer> queueShard,
            TaskPayloadTransformer<SimpleTask> transformer
    ) {
        QueueConfig config =
                new QueueConfig(QueueLocation.builder().withTableName("queue_tasks").withQueueId(queueId).build(),
                        queueSettings
                );
        ShardingQueueProducer<SimpleTask, SpringDatabaseAccessLayer> shardingQueueProducer =
                new ShardingQueueProducer<>(
                        config, transformer, new SingleQueueShardRouter<>(queueShard));
        return new MonitoringQueueProducer<>(shardingQueueProducer, queueId);
    }

    @Bean
    public TaskPayloadTransformer<SimpleTask> simpleTaskTaskPayloadTransformer() {
        return new TaskPayloadTransformer<>() {
            @Override
            public SimpleTask toObject(String s) {
                return new SimpleTask();
            }

            @Override
            public String fromObject(SimpleTask createItem) {
                return "";
            }
        };
    }

    @Bean(name = "simpleTaskQueueId")
    public QueueId simpleTaskQueueId() {
        return new QueueId("simpleTaskQueueId");
    }

    @Bean
    public QueueService queueService(List<QueueShard<?>> queueShards) {
        return new QueueService(queueShards,
                new LoggingThreadLifecycleListener(),
                new LoggingTaskLifecycleListener()
        );
    }

    @Bean(name = "queueShardMain")
    public QueueShard<SpringDatabaseAccessLayer> queueShardMain(
            SpringDatabaseAccessLayer springDatabaseAccessLayer
    ) {
        return new QueueShard<>(new QueueShardId("main"), springDatabaseAccessLayer);
    }

    @Bean
    public QueueSettings queueSettings() {
        return QueueSettings.builder()
                .withProcessingSettings(ProcessingSettings.builder()
                        .withProcessingMode(ProcessingMode.SEPARATE_TRANSACTIONS)
                        .withThreadCount(1).build())
                .withPollSettings(PollSettings.builder()
                        .withBetweenTaskTimeout(Duration.ofMillis(100))
                        .withNoTaskTimeout(Duration.ofMillis(100))
                        .withFatalCrashTimeout(Duration.ofSeconds(1)).build())
                .withFailureSettings(FailureSettings.builder()
                        .withRetryType(FailRetryType.GEOMETRIC_BACKOFF)
                        .withRetryInterval(Duration.ofMinutes(1)).build())
                .withReenqueueSettings(ReenqueueSettings.builder()
                        .withRetryType(ReenqueueRetryType.MANUAL).build())
                .withExtSettings(ExtSettings.builder().withSettings(new LinkedHashMap<>()).build())
                .build();
    }

    @Bean
    public SpringDatabaseAccessLayer springDatabaseAccessLayer(DataSource dataSource) {
        JdbcTemplate pgJdbcTemplate = new JdbcTemplate(dataSource);
        TransactionTemplate pgTransactionTemplate =
                new TransactionTemplate(new DataSourceTransactionManager(dataSource));
        pgTransactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        pgTransactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return new SpringDatabaseAccessLayer(
                DatabaseDialect.POSTGRESQL, QueueTableSchema.builder()
                .withExtFields(singletonList("trace_info")).build(),
                pgJdbcTemplate,
                pgTransactionTemplate);
    }
}
