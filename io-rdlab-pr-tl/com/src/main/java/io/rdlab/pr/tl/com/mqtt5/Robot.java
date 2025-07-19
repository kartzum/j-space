package io.rdlab.pr.tl.com.mqtt5;

import java.util.Map;

public interface Robot {
    Map<String, Object> calculate(Map<String, Object> data);
}
