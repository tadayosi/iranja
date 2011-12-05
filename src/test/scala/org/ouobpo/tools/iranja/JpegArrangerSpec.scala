package org.ouobpo.tools.iranja

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import java.io.File
import org.apache.commons.lang.time.{ DateUtils, DateFormatUtils }

@RunWith(classOf[JUnitRunner])
class JpegArrangerSpec extends Specification {
  "JpegArranger" should {
    "sort jpeg files into date directories" in {
      skipped
    }

    "collect jpeg files in current directory and its child directories" in {
      val result1 = JpegArranger.jpegFilesIn(new File("src/test/resources"))
      result1 must have size 1
      result1 must be equalTo Array(new File("src/test/resources/sample.jpg"))

      val result2 = JpegArranger.jpegFilesIn(new File("src/test"))
      result2 must have size 1
      result2 must be equalTo Array(new File("src/test/resources/sample.jpg"))

      val result3 = JpegArranger.jpegFilesIn(new File("src"))
      result3 must be empty
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
