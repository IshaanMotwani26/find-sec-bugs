package com.h3xstream.findsecbugs;

import com.h3xstream.findbugs.test.BaseDetectorTest;
import com.h3xstream.findbugs.test.EasyBugReporter;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public class MyXmlDecoderDetectorTest extends BaseDetectorTest {

    @Test
    public void detectMyXmlDecoder() throws Exception {
        String[] files = {
                getClassFilePath("testcode/mydecoder/MyXmlDecodeUtil")
        };

        EasyBugReporter reporter = spy(new SecurityReporter());
        analyze(files, reporter);

        verify(reporter).doReportBug(
                bugDefinition()
                        .bugType("MY_XML_DECODER")
                        .inClass("MyXmlDecodeUtil").atLine(9)
                        .build()
        );
    }
}
