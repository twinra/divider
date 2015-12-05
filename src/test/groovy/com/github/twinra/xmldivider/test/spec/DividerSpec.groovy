package com.github.twinra.xmldivider.test.spec

import com.github.twinra.xmldivider.Consumer
import com.github.twinra.xmldivider.Divider
import com.github.twinra.xmldivider.Stream
import spock.lang.Specification


class DividerSpec extends Specification {

    def 'divides xml'() {
        when:
        def collector = new Collector()
        new Divider(createStream(testcase.source), collector).process()

        then:
        collector.list == testcase.result

        where:
        testcase << [
            [
                source: '<pkg1>some data</pkg1><pkg2><marker/><link>pkg1</link></pkg2><pkg3/>',
                result: ['<pkg1>some data</pkg1>', '<pkg2><marker/><link>pkg1</link></pkg2>', '<pkg3/>']
            ],
            [
                source: '<a><b><a>strange but valid</a></b></a><b><a>and inversely</a></b>',
                result: ['<a><b><a>strange but valid</a></b></a>', '<b><a>and inversely</a></b>']
            ],
        ]
    }

    //helpers
    def createStream(String content) {
        new Stream() {
            def chars = content.toCharArray()
            def pos = 0

            @Override
            char next() { chars[pos++] }

            @Override
            boolean hasNext() { pos < chars.length }
        }
    }

    class Collector implements Consumer {
        List<String> list = []

        @Override
        void consume(String content) { list.add(content) }
    }
}
