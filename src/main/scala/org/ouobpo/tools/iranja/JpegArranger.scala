package org.ouobpo.tools.iranja

import java.io.{ FileInputStream, FileFilter, File }
import java.util.Date

import org.apache.commons.io.filefilter.{ SuffixFileFilter, DirectoryFileFilter }
import org.apache.commons.io.FileUtils
import org.apache.commons.lang.time.{ DateUtils, DateFormatUtils }
import org.slf4j.LoggerFactory

import com.drew.imaging.jpeg.JpegMetadataReader
import com.drew.metadata.exif.ExifDirectory

object JpegArranger {
  private val logger = LoggerFactory.getLogger(getClass)
  private val currentDir = new File(".")
  private val jpegFilter: FileFilter = new SuffixFileFilter(Array(".jpeg", ".jpg", ".JPEG", ".JPG"))
  def main(args: Array[String]) {
    logger.info("=== START =========================================")
    run
    logger.info("=== END ===========================================")
  }

  //----------------------------------------------------------------------------

  def run {
    jpegFilesIn(currentDir) foreach { jpeg =>
      try { arrange(jpeg) }
      catch { case e => logger.error("failed to arrange jpeg '%s'".format(jpeg), e) }
    }
  }

  private[iranja] def jpegFilesIn(rootDir: File) = rootDir.listFiles(jpegFilter) ++
    rootDir.listFiles(DirectoryFileFilter.DIRECTORY.asInstanceOf[FileFilter]).foldLeft(List[File]()) {
      _ ++ _.listFiles(jpegFilter)
    }

  private[iranja] def arrange(jpeg: File) {
    logger.info(jpeg.toString)
    val toDir = directoryOf(originalDateOf(jpeg))
    val destination = new File(toDir, jpeg.getName())
    if (destination.equals(jpeg)) {
      logger.info("-> jpeg already arranged."); return
    }
    logger.info("-> {}", destination)
    FileUtils.moveFileToDirectory(jpeg, toDir, true)
  }

  private[iranja] def originalDateOf(jpeg: File) = {
    val metadata = JpegMetadataReader.readMetadata(new FileInputStream(jpeg))
    val exif = metadata.getDirectory(classOf[ExifDirectory])
    DateUtils.parseDate(
      exif.getDescription(ExifDirectory.TAG_DATETIME_ORIGINAL),
      Array("yyyy:MM:dd HH:mm:ss"))
  }

  private[iranja] def directoryOf(date: Date) = new File(currentDir, DateFormatUtils.format(date, "yyyyMMdd"))
}
