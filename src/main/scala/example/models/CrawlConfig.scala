package example.models

import cats._
import scala.util.matching.Regex
import io.lemonlabs.uri.Url
import io.lemonlabs.uri.Host
import io.lemonlabs.uri.DomainName
import io.lemonlabs.uri.Url.unordered._
import example.models._
import example.UrlUtils

final case class CrawlConfig(
    startUrl: Url,
    linkFilter: String => Boolean,
    maxDepth: Int,
    outputCsvFilename: String
)

object CrawlConfig {
  def fromUrl(url: String, maxDepth: Int = 3, outputCsvFilename: String): CrawlConfig = CrawlConfig(
    startUrl = Url.parse(url),
    linkFilter = UrlUtils.isUrlMatchingBase(url),
    maxDepth = maxDepth,
    outputCsvFilename = outputCsvFilename
  )
}
