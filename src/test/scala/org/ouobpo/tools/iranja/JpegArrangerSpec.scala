package org.ouobpo.tools.iranja

import java.io.File

import org.apache.commons.lang.time.{ DateUtils, DateFormatUtils }
import org.junit.runner.RunWith
import org.specs.runner.JUnit
import org.specs.runner.JUnitSuiteRunner
import org.specs.Specification

@RunWith(classOf[JUnitSuiteRunner])
class JpegArrangerSpec extends Specification with JUnit {
  "JpegArranger" should {
    "collect jpeg files" in {
      val result1 = JpegArranger.jpegFilesIn(new File("src/test/resources"))
      result1.size must be equalTo 1
      result1 must contain(new File("src/test/resources/sample.jpg"))

      val result2 = JpegArranger.jpegFilesIn(new File("src/test"))
      result2.size must be equalTo 1
      result2 must contain(new File("src/test/resources/sample.jpg"))

      val result3 = JpegArranger.jpegFilesIn(new File("src"))
      result3 must beEmpty
    }

    "extract original date of jpeg file" in {
      val originalDateTime = JpegArranger.originalDateOf(new File("src/test/resources/sample.jpg"))
      DateFormatUtils.format(originalDateTime, "yyyy/MM/dd HH:mm:ss") must be equalTo "2008/03/09 12:48:51"
    }

    "return directory of date" in {
      val dir = JpegArranger.directoryOf(DateUtils.parseDate("2008/01/01", Array("yyyy/MM/dd")))
      dir.getName() must be equalTo "20080101"
    }
  }

}
