package io.rdlab.hn;

import io.rdlab.hn.item.Item;
import io.rdlab.hn.item.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class HnApplicationTests {

    @Autowired
    private ItemService itemService;

    @Test
    void contextLoads() {
        List<Item> items = StreamSupport.stream(itemService.findAll().spliterator(), false).toList();
        assertThat(items).isNotNull();
    }

}
