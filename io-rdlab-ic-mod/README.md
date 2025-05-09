# io-rdlab-ic-mod

## langchain4j + langchain4j-ollama

### pom.xml
```
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j</artifactId>
    <version>1.0.0-beta3</version>
</dependency>

<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-ollama</artifactId>
    <version>1.0.0-beta3</version>
</dependency>
```

### docker
```
services:
  ollama:
    image: ollama/ollama:0.6.8
    container_name: ollama_con
    ports:
      - 11434:11434
    volumes:
      - ./ollama:/root/.ollama
```

```
docker exec -it ollama_con ollama pull tinyllama
```

### java
```
ChatLanguageModel chatModel = OllamaChatModel.builder()
        .baseUrl("http://localhost:11434")
        .modelName("tinyllama")
        .logRequests(true)
        .build();
String answer = chatModel.chat("Provide 3 short bullet points explaining why Java is awesome");
System.out.println(answer);

assertThat(answer).isNotBlank();
```

## spring-ai + ollama-chat

```
docker exec -it ollama_con ollama pull llama3.2:1b
```
