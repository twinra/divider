package com.github.twinra.xmldivider

import java.util.regex.Pattern

interface Stream {
    char next()
    boolean hasNext()
}

interface Consumer {
    void consume(String content)
}


class Divider {

    private enum TagType { OPENING, ENCLOSING, COMPLETED }

    private static def tagPattern = '\\<[^\\>]+\\>'
    private static def openingTagPattern = '\\<[^/\\>]+\\>'
    private static def enclosingTagPattern = '\\</[^/\\>]+\\>'
    private static def completedTagPattern = '\\<[^/\\>]+/\\>'

    private Stream stream
    private Consumer consumer
    private StringBuilder buffer

    Divider(Stream stream, Consumer consumer) {
        this.stream = stream
        this.consumer = consumer
        this.buffer = new StringBuilder()
    }

    void process() {
        while(stream.hasNext()) {
            def ch = stream.next()
            buffer.append(ch)
            if(ch == '>' as char)
                analyze()
        }
    }

    void analyze() {
        def content = buffer.toString()

        List<String> tags = []
        def matcher = Pattern.compile(tagPattern).matcher(content)
        while(matcher.find())
            tags.add(matcher.group())

        def opening = 0
        def enclosing = 0
        tags.each{
            if(it.matches(openingTagPattern))
                opening += 1
            else if(it.matches(enclosingTagPattern))
                enclosing += 1
            // otherwise it is completed tag <tag/>
        }
        if(opening - enclosing == 0) {
            consumer.consume(content)
            buffer.delete(0, buffer.length())
        }
    }
}
