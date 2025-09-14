package io.github.sekelenao.smallyaml.api.line.provider;

import io.github.sekelenao.smallyaml.internal.parsing.line.Line;
import io.github.sekelenao.smallyaml.internal.parsing.parser.LineParser;
import io.github.sekelenao.smallyaml.internal.util.Assertions;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;

public class InputStreamLineProvider implements LineProvider {

    private final LinkedList<Integer> nextNewlineIndices = new LinkedList<>();

    private final StringBuilder stringBuilder = new StringBuilder();

    private final LineParser lineParser = new LineParser();

    private final ReadableByteChannel channel;

    private final CharsetDecoder decoder;

    private final CharBuffer charBuffer;

    private final ByteBuffer buffer;

    public InputStreamLineProvider(InputStream inputStream) {
        this(inputStream, StandardCharsets.UTF_8);
    }

    public InputStreamLineProvider(InputStream inputStream, Charset charset) {
        this(inputStream, charset, 8_192);
    }

    public InputStreamLineProvider(InputStream inputStream, Charset charset, int bufferSize) {
        Objects.requireNonNull(inputStream);
        Objects.requireNonNull(charset);
        Assertions.isStrictlyPositive(bufferSize);
        this.channel = Channels.newChannel(inputStream);
        this.decoder = charset.newDecoder()
            .onMalformedInput(CodingErrorAction.REPLACE)
            .onUnmappableCharacter(CodingErrorAction.REPLACE);
        this.buffer = ByteBuffer.allocate(bufferSize);
        this.charBuffer = CharBuffer.allocate(bufferSize * 2);
    }

    private boolean read() throws IOException {
        // WRITE MODE
        int amountOfBytesRead = channel.read(buffer);
        return switch (amountOfBytesRead) {
            case -1 -> false;
            case 0 -> true;
            default -> {
                buffer.flip(); // READ MODE
                var result = decoder.decode(buffer, charBuffer, false);
                if (result.isError()) {
                    result.throwException();
                }
                buffer.compact(); // WRITE MODE
                charBuffer.flip(); // READ MODE
                while (charBuffer.hasRemaining()) {
                    var currentChar = charBuffer.get();
                    if (currentChar == '\n') {
                        nextNewlineIndices.add(stringBuilder.length());
                    }
                    stringBuilder.append(currentChar);
                }
                charBuffer.compact(); // WRITE MODE
                yield true;
            }
        };
    }

    @Override
    public Optional<Line> nextLine() throws IOException {
        while (nextNewlineIndices.isEmpty()) {
            if (!read()) {
                if (stringBuilder.isEmpty()) {
                    return Optional.empty();
                }
                var lastLine = stringBuilder.toString();
                stringBuilder.setLength(0);
                return Optional.of(lineParser.parse(lastLine));
            }
        }
        int nextNewlineIndex = nextNewlineIndices.removeFirst();
        var line = stringBuilder.substring(0, nextNewlineIndex);
        stringBuilder.delete(0, nextNewlineIndex + 1);
        nextNewlineIndices.replaceAll(i -> i - (nextNewlineIndex + 1));
        return Optional.of(lineParser.parse(line));
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
