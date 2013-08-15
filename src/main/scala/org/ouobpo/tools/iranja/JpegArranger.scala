package org.ouobpo.tools.iranja

import java.io.{ File, FileFilter }
import java.util.Date

import scala.Array.canBuildFrom

import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.{ DirectoryFileFilter, SuffixFileFilter }
import org.apache.commons.lang.time.{ DateFormatUtils, DateUtils }
import org.slf4j.LoggerFactory

import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.exif.ExifSubIFDDirectory

object JpegArranger {
  private val logger = LoggerFactory.getLogger(getClass)
  private val jpegFilter: FileFilter = new SuffixFileFilter(Array(".jpeg", ".jpg", ".JPEG", ".JPG"))
  def main(args: Array[String]) {
    logger.info("=== START =========================================")
    run(new File(if (args.isEmpty) "." else args(0)))
    logger.info("=== END ===========================================")
  }

  //----------------------------------------------------------------------------

  def run(rootDir: File) {
    jpegFilesIn(rootDir) foreach { jpeg =>
      try { arrange(rootDir, jpeg) }
      catch { case e: Throwable => logger.error("failed to arrange jpeg '%s'".format(jpeg), e) }
    }
  }

  private[iranja] def jpegFilesIn(rootDir: File) = rootDir.listFiles(jpegFilter) ++
    rootDir.listFiles(DirectoryFileFilter.DIRECTORY.asInstanceOf[FileFilter]).foldLeft(List[File]()) {
      _ ++ _.listFiles(jpegFilter)
    }

  private[iranja] def arrange(rootDir: File, jpeg: File) {
    logger.info(jpeg.toString)
    val toDir = directoryOf(rootDir, originalDateOf(jpeg))
    val destination = new File(toDir, jpeg.getName())
    if (destination.equals(jpeg)) {
      logger.info("-> jpeg already arranged."); return
    }
    logger.info("-> {}", destination)
    FileUtils.moveFileToDirectory(jpeg, toDir, true)
  }

  private[iranja] def originalDateOf(jpeg: File) = {
    val metadata = ImageMetadataReader.readMetadata(jpeg)
    val exif = metadata.getDirectory(classOf[ExifSubIFDDirectory])
    DateUtils.parseDate(
      exif.getDescription(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL),
      Array("yyyy:MM:dd HH:mm:ss"))
  }

  private[iranja] def directoryOf(rootDir: File, date: Date) = new File(rootDir, DateFormatUtils.format(date, "yyyyMMdd"))
}
