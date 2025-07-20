package io.rdlab.pr.tl.com.util;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SourceUtils {
    public static String loadTextFromFile(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?> compileAndLoad(String className, String sourceCode) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        StandardJavaFileManager stdFileManager =
                compiler.getStandardFileManager(null, null, null);
        InMemoryClassFileManager fileManager = new InMemoryClassFileManager(stdFileManager);

        JavaFileObject sourceFile = new InMemoryJavaFileObject(className, sourceCode);

        JavaCompiler.CompilationTask task = compiler.getTask(
                null,
                fileManager,
                null,
                null,
                null,
                Collections.singletonList(sourceFile)
        );

        Boolean result = task.call();
        if (!Boolean.TRUE.equals(result)) {
            throw new RuntimeException("Compilation failed");
        }

        InMemoryClassLoader classLoader = new InMemoryClassLoader(
                fileManager.getCompiledClasses()
        );

        return classLoader.loadClass(className);
    }

    public static class InMemoryJavaFileObject extends SimpleJavaFileObject {
        private final String source;
        private byte[] byteCode;

        public InMemoryJavaFileObject(String className, String source) {
            super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension),
                    Kind.SOURCE);
            this.source = source;
        }

        public InMemoryJavaFileObject(String className, Kind kind) {
            super(URI.create("mem:///" + className.replace('.', '/') + kind.extension), kind);
            this.source = null;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            if (source == null) {
                throw new IllegalStateException("Not a source file");
            }
            return source;
        }

        public byte[] getByteCode() {
            return byteCode;
        }

        @Override
        public OutputStream openOutputStream() throws IOException {
            return new ByteArrayOutputStream() {
                @Override
                public void close() throws IOException {
                    super.close();
                    byteCode = toByteArray();
                }
            };
        }
    }

    public static class InMemoryClassFileManager extends ForwardingJavaFileManager<JavaFileManager> {
        private final Map<String, InMemoryJavaFileObject> compiled = new ConcurrentHashMap<>();

        public InMemoryClassFileManager(JavaFileManager fileManager) {
            super(fileManager);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(
                Location location,
                String className,
                JavaFileObject.Kind kind,
                FileObject sibling
        ) throws IOException {
            InMemoryJavaFileObject file = new InMemoryJavaFileObject(className, kind);
            compiled.put(className, file);
            return file;
        }

        public Map<String, InMemoryJavaFileObject> getCompiledClasses() {
            return compiled;
        }
    }

    public static class InMemoryClassLoader extends ClassLoader {
        private final Map<String, InMemoryJavaFileObject> compiled;

        public InMemoryClassLoader(Map<String, InMemoryJavaFileObject> compiled) {
            super(ClassLoader.getSystemClassLoader());
            this.compiled = compiled;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            InMemoryJavaFileObject file = compiled.get(name);
            if (file != null) {
                byte[] bytes = file.getByteCode();
                return defineClass(name, bytes, 0, bytes.length);
            }
            return super.findClass(name);
        }
    }
}
