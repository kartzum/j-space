package io.rdlab.hn;

import io.rdlab.hn.dbqueue.SimpleTask;
import io.rdlab.hn.item.Item;
import io.rdlab.hn.item.ItemService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yoomoney.tech.dbqueue.api.EnqueueParams;
import ru.yoomoney.tech.dbqueue.api.QueueConsumer;
import ru.yoomoney.tech.dbqueue.api.QueueProducer;
import ru.yoomoney.tech.dbqueue.config.QueueService;

import java.time.Duration;
import java.util.List;
import java.util.stream.StreamSupport;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class HnApplicationTests {

    @Autowired
    private ItemService itemService;

    @Autowired
    private QueueService queueService;

    @Autowired
    private QueueProducer<SimpleTask> simpleTaskQueueProducer;

    @Autowired
    private QueueConsumer<SimpleTask> simpleTaskQueueConsumer;

    @Test
    @Disabled
    void itemServiceTest() {
        List<Item> items = StreamSupport.stream(itemService.findAll().spliterator(), false).toList();
        assertThat(items).isNotNull();
    }

    @Test
    void dbQueueTest() throws InterruptedException {
        queueService.registerQueue(simpleTaskQueueConsumer);
        queueService.start();
        simpleTaskQueueProducer.enqueue(EnqueueParams.create(new SimpleTask()));
        sleep(500);
        queueService.awaitTermination(Duration.ofSeconds(1));
    }
}
