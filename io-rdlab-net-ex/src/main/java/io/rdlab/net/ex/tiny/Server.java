package io.rdlab.net.ex.tiny;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final InetSocketAddress address;
    private final Executor executor;
    private final Handler handler;

    public Server(
            InetSocketAddress address,
            Executor executor,
            Handler handler
    ) {
        this.address = address;
        this.executor = executor;
        this.handler = handler;
    }

    public void start() {
        executor.execute(() -> {
            try (
                    ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
                    ServerSocket serverSocket = new ServerSocket()
            ) {
                serverSocket.bind(address);
                logInfo("Server started: " + serverSocket);
                while (true) {
                    Socket socket = serverSocket.accept();
                    executor.execute(new RequestHandler(socket, handler));
                }
            } catch (IOException e) {
                logError(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });
    }

    private void logInfo(String message) {
        System.out.println(message);
    }

    private void logError(String message, Throwable e) {
        System.out.println(message);
        e.printStackTrace();
    }

    static class RequestHandler implements Runnable {
        private final Socket socket;
        private final Handler handler;

        public RequestHandler(Socket socket, Handler handler) {
            this.socket = socket;
            this.handler = handler;
        }

        @Override
        public void run() {
            try {
                logInfo("Handling socket: " + socket);
                while (true) {
                    InputStream inputStream = socket.getInputStream();
                    OutputStream outputStream = socket.getOutputStream();

                    Exchange exchange = new Exchange(inputStream, outputStream);
                    handler.handle(exchange);

                    if (!isConnectionKeepAlive(exchange)) {
                        break;
                    }
                }
            } catch (IOException e) {
                logError(e.getMessage(), e);
                throw new RuntimeException(e);
            } catch (Exception e) {
                logError(e.getMessage(), e);
                throw e;
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    logError(e.getMessage(), e);
                }
            }
        }

        private void logInfo(String message) {
            System.out.println(message);
        }

        private void logError(String message, Throwable e) {
            System.out.println(message);
            e.printStackTrace();
        }

        private boolean isConnectionKeepAlive(Exchange exchange) {
            String connectionHeader = exchange.getHeader("Connection");
            return connectionHeader != null && connectionHeader.equalsIgnoreCase("keep-alive");
        }
    }

    public static class Exchange {
        private final BufferedReader reader;
        private final Map<String, String> headers = new HashMap<>();
        private final OutputStream outputStream;
        private final PrintWriter writer;

        Exchange(InputStream inputStream, OutputStream outputStream) {
            this.reader = new BufferedReader(new InputStreamReader(inputStream));
            this.outputStream = outputStream;
            this.writer = new PrintWriter(outputStream, true);
            parseHeaders();
        }

        private void parseHeaders() {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isEmpty()) {
                        break;
                    }
                    String[] headerParts = line.split(": ", 2);
                    if (headerParts.length == 2) {
                        headers.put(headerParts[0], headerParts[1]);
                    }
                }
            } catch (IOException e) {
                logError(e.getMessage(), e);
            }
        }

        public String getHeader(String headerName) {
            return headers.get(headerName);
        }

        public void sendResponse(int status, String contentType, String body) {
            byte[] bodyBytes = body.getBytes();
            writer.println("HTTP/1.1 " + status + " OK");
            writer.println("Content-Type: " + contentType);
            writer.println("Content-Length: " + bodyBytes.length);
            writer.println();
            writer.flush();
            try {
                outputStream.write(bodyBytes, 0, bodyBytes.length);
            } catch (IOException e) {
                logError(e.getMessage(), e);
                throw new RuntimeException(e);
            }
            writer.close();
        }

        private void logError(String message, Throwable e) {
            System.out.println(message);
            e.printStackTrace();
        }
    }

    public interface Handler {
        void handle(Exchange exchange);
    }
}
