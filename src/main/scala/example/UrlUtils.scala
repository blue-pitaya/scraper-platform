package example

import io.lemonlabs.uri.Url
import io.lemonlabs.uri.Host

object UrlUtils {
  def isUrlMatchingBase(baseUrlStr: String)(urlStr: String): Boolean = {
    val result = for {
      baseUrlHost <- Url.parseOption(baseUrlStr).flatMap(u => u.apexDomain)
      currUrlHost <- Url.parseOption(urlStr).flatMap(u => u.apexDomain)
    } yield currUrlHost == baseUrlHost

    result.getOrElse(false)
  }
}
